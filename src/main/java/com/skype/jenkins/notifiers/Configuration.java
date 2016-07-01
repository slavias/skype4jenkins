package com.skype.jenkins.notifiers;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.NotifyTypeEnum;

import static com.skype.jenkins.RunNotification.getConfiguration;

public class Configuration {
    private NotifyTypeEnum notifierType;
    private String jobName;
    private String jenkinsUrl = getConfiguration().get(0).getJenkinsUrl();

    public Configuration(NotifyTypeEnum notifierType, String jobName) {
        this.jobName = jobName;
        this.notifierType = notifierType;
    };

    public Configuration(String jobName) {
        this.jobName = jobName;
    };

    public String getJobName() {
        return jobName;
    }

    public String getJenkinsUrl() {
        return jenkinsUrl;
    }

    public ConfigJobDTO getJobConfig() {
        return getConfiguration().get(0).getJobs().stream().filter(job -> job.getInfo().getJobName().equals(jobName))
                .findFirst().orElse(null);
    }

    public void setJobName(String jobName1) {
        jobName = jobName1;
    }

    public NotifyTypeEnum getNotifierType() {
        return notifierType;
    }

    public void setNotifierType(NotifyTypeEnum notifierType1) {
        notifierType = notifierType1;
    }
}
