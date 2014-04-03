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
package jenkins.plugins.sonarparser.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jacob.schoen@ge.com
 */
public class SonarReport {

    private String version;

    private List<SonarIssue> issues;

    private List<SonarComponent> components;

    private List<SonarRule> rules;

    public SonarReport() {
    }

    public SonarReport(String version) {
        this.version = version;
    }

    public SonarReport(String version, List<SonarIssue> issues, List<SonarComponent> components, List<SonarRule> rules) {
        this.version = version;
        this.issues = issues;
        this.components = components;
        this.rules = rules;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<SonarIssue> getIssues() {
        return issues;
    }

    public void setIssues(List<SonarIssue> issues) {
        this.issues = issues;
    }

    public List<SonarComponent> getComponents() {
        return components;
    }

    public void setComponents(List<SonarComponent> components) {
        this.components = components;
    }

    public List<SonarRule> getRules() {
        return rules;
    }

    public void setRules(List<SonarRule> rules) {
        this.rules = rules;
    }

    public List<SonarIssue> getNewIssues() {
        ArrayList<SonarIssue> newIssues = new ArrayList<SonarIssue>();
        for (SonarIssue issue : issues) {
            if (issue.isIsNew()) {
                newIssues.add(issue);
            }
        }
        return newIssues;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.version != null ? this.version.hashCode() : 0);
        hash = 17 * hash + (this.issues != null ? this.issues.hashCode() : 0);
        hash = 17 * hash + (this.components != null ? this.components.hashCode() : 0);
        hash = 17 * hash + (this.rules != null ? this.rules.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SonarReport other = (SonarReport) obj;
        if ((this.version == null) ? (other.version != null) : !this.version.equals(other.version)) {
            return false;
        }
        if (this.issues != other.issues && (this.issues == null || !this.issues.equals(other.issues))) {
            return false;
        }
        if (this.components != other.components && (this.components == null || !this.components.equals(other.components))) {
            return false;
        }
        return this.rules == other.rules || (this.rules != null && this.rules.equals(other.rules));
    }

    @Override
    public String toString() {
        return "SonarReport{" + "version=" + version + '}';
    }

}
