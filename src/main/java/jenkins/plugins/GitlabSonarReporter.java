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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
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
        if (build.getResult() == Result.SUCCESS) {
            Map variables = build.getBuildVariables();
            LOGGER.log(Level.SEVERE, "Build Variables");
            for (Object o : variables.keySet()) {
                LOGGER.log(Level.SEVERE, "  Key: ''{0}'' Value: ''{1}''", new Object[]{o, variables.get(o)});
            }
        }
        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
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

        private String _botUsername = "jenkins";
        private String _gitlabHostUrl;
        private String _botApiToken;
        private boolean _ignoreCertificateErrors = false;

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> type) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Gitlab Merge Requests Builder";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            _botUsername = formData.getString("botUsername");
            _botApiToken = formData.getString("botApiToken");
            _gitlabHostUrl = formData.getString("gitlabHostUrl");
            _ignoreCertificateErrors = formData.getBoolean("ignoreCertificateErrors");

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
            return _ignoreCertificateErrors;
        }

        public String getBotApiToken() {
            return _botApiToken;
        }

        public String getGitlabHostUrl() {
            return _gitlabHostUrl;
        }

        public String getBotUsername() {
            return _botUsername;
        }

    }
}
