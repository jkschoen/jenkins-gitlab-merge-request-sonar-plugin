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

import org.joda.time.DateTime;

/**
 *
 * @author jacob.schoen@ge.com
 */
public class SonarIssue {

    private String key;
    private String component;
    private int line;
    private String message;
    private String severity;
    private String rule;
    private String status;
    private boolean isNew;
    private DateTime creationDate;
    private DateTime updateDate;

    public SonarIssue() {
    }

    public SonarIssue(String key, String component, int line, String message, String severity, String rule, String status, boolean isNew, DateTime creationDate, DateTime updateDate) {
        this.key = key;
        this.component = component;
        this.line = line;
        this.message = message;
        this.severity = severity;
        this.rule = rule;
        this.status = status;
        this.isNew = isNew;
        this.creationDate = creationDate;
        this.updateDate = updateDate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    public DateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(DateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 71 * hash + (this.component != null ? this.component.hashCode() : 0);
        hash = 71 * hash + this.line;
        hash = 71 * hash + (this.message != null ? this.message.hashCode() : 0);
        hash = 71 * hash + (this.severity != null ? this.severity.hashCode() : 0);
        hash = 71 * hash + (this.rule != null ? this.rule.hashCode() : 0);
        hash = 71 * hash + (this.status != null ? this.status.hashCode() : 0);
        hash = 71 * hash + (this.isNew ? 1 : 0);
        hash = 71 * hash + (this.creationDate != null ? this.creationDate.hashCode() : 0);
        hash = 71 * hash + (this.updateDate != null ? this.updateDate.hashCode() : 0);
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
        final SonarIssue other = (SonarIssue) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.component == null) ? (other.component != null) : !this.component.equals(other.component)) {
            return false;
        }
        if (this.line != other.line) {
            return false;
        }
        if ((this.message == null) ? (other.message != null) : !this.message.equals(other.message)) {
            return false;
        }
        if ((this.severity == null) ? (other.severity != null) : !this.severity.equals(other.severity)) {
            return false;
        }
        if ((this.rule == null) ? (other.rule != null) : !this.rule.equals(other.rule)) {
            return false;
        }
        if ((this.status == null) ? (other.status != null) : !this.status.equals(other.status)) {
            return false;
        }
        if (this.isNew != other.isNew) {
            return false;
        }
        if (this.creationDate != other.creationDate && (this.creationDate == null || !this.creationDate.equals(other.creationDate))) {
            return false;
        }
        return this.updateDate == other.updateDate || (this.updateDate != null && this.updateDate.equals(other.updateDate));
    }

    @Override
    public String toString() {
        return "SonarIssue{" + "key=" + key + ", component=" + component + ", line=" + line + ", message=" + message + ", severity=" + severity + ", rule=" + rule + ", status=" + status + ", isNew=" + isNew + ", creationDate=" + creationDate + ", updateDate=" + updateDate + '}';
    }

}
