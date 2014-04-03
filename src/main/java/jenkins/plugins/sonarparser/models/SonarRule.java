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

/**
 *
 * @author jacob.schoen@ge.com
 */
public class SonarRule {

    private String key;
    private String rule;
    private String repository;
    private String name;

    public SonarRule() {
    }

    public SonarRule(String key, String rule, String repository, String name) {
        this.key = key;
        this.rule = rule;
        this.repository = repository;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.key != null ? this.key.hashCode() : 0);
        hash = 59 * hash + (this.rule != null ? this.rule.hashCode() : 0);
        hash = 59 * hash + (this.repository != null ? this.repository.hashCode() : 0);
        hash = 59 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final SonarRule other = (SonarRule) obj;
        if ((this.key == null) ? (other.key != null) : !this.key.equals(other.key)) {
            return false;
        }
        if ((this.rule == null) ? (other.rule != null) : !this.rule.equals(other.rule)) {
            return false;
        }
        if ((this.repository == null) ? (other.repository != null) : !this.repository.equals(other.repository)) {
            return false;
        }
        return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
    }

    @Override
    public String toString() {
        return "SonarRule{" + "key=" + key + ", rule=" + rule + ", repository=" + repository + ", name=" + name + '}';
    }

}
