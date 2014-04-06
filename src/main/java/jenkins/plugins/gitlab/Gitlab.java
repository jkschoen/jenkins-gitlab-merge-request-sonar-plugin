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
package jenkins.plugins.gitlab;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.plugins.GitlabSonarReporter;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabMergeRequest;
import org.gitlab.api.models.GitlabNote;
import org.gitlab.api.models.GitlabProject;

/**
 *
 * @author jacob.schoen@ge.com
 */
public class Gitlab {

    private static final Logger LOGGER = Logger.getLogger(Gitlab.class.getName());

    private static GitlabAPI API;

    public static GitlabAPI get() {
        if (API == null) {
            String privateToken = GitlabSonarReporter.DESCRIPTOR.getBotApiToken();
            String apiUrl = GitlabSonarReporter.DESCRIPTOR.getGitlabHostUrl();
            API = GitlabAPI.connect(apiUrl, privateToken);
        }

        return API;
    }

    public static GitlabProject getProjectForPath(String path) {
        try {
            List<GitlabProject> projects = get().getAllProjects();
            for (GitlabProject project : projects) {
                if (project.getPathWithNamespace().equals(path)) {
                    return project;
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not retrieve Project with path: {0} (Have you properly configured the project path?)", path);
        }
        return null;
    }
    
    public static GitlabProject getProject(String projectPath) throws IOException{
        LOGGER.log(Level.FINEST, "Looking for Project Path with Namespace: ''{0}''", projectPath);
        
        List<GitlabProject> projects = get().getProjects();
        for (GitlabProject project : projects){
            LOGGER.log(Level.FINEST, "Project Path with Namespace: ''{0}''", project.getPathWithNamespace());
            if(project.getPathWithNamespace().equals(projectPath)){
                return project;
            }
        }
        return null; 
    }
    
    public static GitlabMergeRequest getMergeRequest(String projectPath, int mergeRequestId) throws IOException {
        GitlabProject project = Gitlab.getProject(projectPath);
        return Gitlab.getMergeRequest(project, mergeRequestId);
    }
    
    public static GitlabMergeRequest getMergeRequest(GitlabProject project, int mergeRequestId) throws IOException {
        return get().getMergeRequest(project, mergeRequestId);
    }

    public static GitlabNote createNote(GitlabMergeRequest mergeRequest, String message) {
        try {
            return get().createNote(mergeRequest, message);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create note for merge request " + mergeRequest.getId(), e);
            return null;
        }
    }

}
