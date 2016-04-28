package com.skype.jenkins.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ConfigDTO {

    @SerializedName("jenkinsUrl")
    private String jenkinsUrl;
    @SerializedName("jobs")
    private List<ConfigJobDTO> jobs;
    
    public String getJenkinsUrl() {
        return jenkinsUrl;
    }
    public void setJenkinsUrl(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }
    public List<ConfigJobDTO> getJobs() {
        return jobs;
    }
    public void setJobs(List<ConfigJobDTO> jobs) {
        this.jobs = jobs;
    }
    
    
}
