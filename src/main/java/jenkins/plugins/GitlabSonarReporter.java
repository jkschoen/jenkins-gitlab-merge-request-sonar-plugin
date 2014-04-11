/*
 * Copyright (c) 2014 Jacob Schoen 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE. 
 */
package jenkins.plugins;

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.util.FormValidation;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.plugins.gitlab.Gitlab;
import jenkins.plugins.sonarparser.SonarReportParser;
import jenkins.plugins.sonarparser.models.SonarIssue;
import jenkins.plugins.sonarparser.models.SonarReport;
import net.sf.json.JSONObject;
import org.gitlab.api.models.GitlabMergeRequest;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link GitlabSonarReporter} is created. The created instance is persisted to
 * the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 *
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 *
 * @author Kohsuke Kawaguchi
 */
public class GitlabSonarReporter extends Notifier {

    private static final Logger LOGGER = Logger.getLogger(GitlabSonarReporter.class.getName());

    private final String projectPath;
    private final String sonarResults;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public GitlabSonarReporter(String projectPath, String sonarResults) {
        this.projectPath = projectPath;
        this.sonarResults = sonarResults;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getSonarResults() {
        return sonarResults;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        LOGGER.log(Level.FINER, "Build Result: {0}", build.getResult());
        Result result = build.getResult();
        if(result != null && result.isBetterOrEqualTo(Result.SUCCESS)){
            try {
                Map variables = build.getBuildVariables();
                String mrId = (String)variables.get("gitlabMergeRequestId");
                //get the merge request
                GitlabMergeRequest mergeRequest = Gitlab.getMergeRequest(this.projectPath, Integer.parseInt(mrId));
                //get the report results
                FilePath workspace = build.getWorkspace();
                if(workspace != null){
                    SonarReport report = getReport(workspace.absolutize());
                    //post the comments
                    postComments(mergeRequest, report);
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                //we want to make sure we never break the build
                LOGGER.log(Level.SEVERE, null, ex);
            } 
        }
        return true;
    }
    
    private SonarReport getReport(FilePath workspace) throws IOException{
        FilePath resultsPath = new FilePath(workspace, this.sonarResults);
        InputStream resultsStream = null;
        try {
            resultsStream = resultsPath.read();
            SonarReport report = SonarReportParser.parse(resultsStream);
            return report;
        } finally {
            if(resultsStream != null){
                resultsStream.close();
            }
        }
    }
    
    private void postComments(GitlabMergeRequest mergeRequest, SonarReport report){
        //we just care about the new issues
        List<SonarIssue> newIssues = report.getNewIssues();
        LOGGER.log(Level.SEVERE, "Number of new issues: {0}", newIssues.size());
        
        String comment = "";
        for (SonarIssue issue : newIssues){
            if(comment.length() > 0){
                //we need to a few lines
                comment = comment + "  \n";
            }
            comment = comment + issueMarkup(issue);
        }
        comment = headerFooterMarkup(report, getDescriptor().getMessageHeader()) 
                + comment
                + headerFooterMarkup(report, getDescriptor().getMessageFooter());
        Gitlab.createNote(mergeRequest, comment);
    }
    
    private String headerFooterMarkup(SonarReport report, String template){
        String message = template;
        message = message.replaceAll("\\$NEW_ISSUE_COUNT", ""+report.getNewIssues().size());
        message = message.replaceAll("\\$TOTAL_ISSUE_COUNT", ""+report.getIssues().size());
        return message;
    }
    
    private String issueMarkup(SonarIssue issue){
        String message = getDescriptor().getMessageIssue();
        message = message.replaceAll("\\$KEY", issue.getSeverity());
        message = message.replaceAll("\\$COMPONENT", issue.getComponent());
        message = message.replaceAll("\\$LINE", ""+issue.getLine());
        message = message.replaceAll("\\$MESSAGE", issue.getMessage());
        message = message.replaceAll("\\$SEVERITY", issue.getSeverity());
        message = message.replaceAll("\\$RULE", issue.getRule());
        return message;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * Descriptor for {@link GitlabSonarReporter}. Used as a singleton. The
     * class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See
     * <tt>src/main/resources/hudson/plugins/hello_world/GitlabSonarReporter/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String botUsername = "jenkins";
        private String gitlabHostUrl;
        private String botApiToken;
        private boolean ignoreCertificateErrors = false;
        private String messageHeader;
        private String messageIssue;
        private String messageFooter;
        

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> type) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Gitlab Merge Request Sonar Results Poster";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            botUsername = formData.getString("botUsername");
            botApiToken = formData.getString("botApiToken");
            gitlabHostUrl = formData.getString("gitlabHostUrl");
            ignoreCertificateErrors = formData.getBoolean("ignoreCertificateErrors");
            messageHeader = formData.getString("messageHeader");
            messageIssue = formData.getString("messageIssue");
            messageFooter = formData.getString("messageFooter");

            save();

            return super.configure(req, formData);
        }

        public FormValidation doCheckGitlabHostUrl(@QueryParameter String value) {
            if (!value.isEmpty()) {
                return FormValidation.ok();
            }

            return FormValidation.error("Gitlab Host Url needs to be set");
        }

        public FormValidation doCheckBotUsername(@QueryParameter String value) {
            if (value == null || value.isEmpty()) {
                return FormValidation.error("You must provide a username for the Jenkins user");
            }

            return FormValidation.ok();
        }

        public FormValidation doCheckBotApiToken(@QueryParameter String value) {
            if (value == null || value.isEmpty()) {
                return FormValidation.error("You must provide an API token for the Jenkins user");
            }

            return FormValidation.ok();
        }

        public boolean isIgnoreCertificateErrors() {
            return ignoreCertificateErrors;
        }

        public String getBotApiToken() {
            return botApiToken;
        }

        public String getGitlabHostUrl() {
            return gitlabHostUrl;
        }

        public String getBotUsername() {
            return botUsername;
        }

        public String getMessageHeader() {
            return messageHeader;
        }

        public String getMessageIssue() {
            return messageIssue;
        }

        public String getMessageFooter() {
            return messageFooter;
        }
        

    }
}
