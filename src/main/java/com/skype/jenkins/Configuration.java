package com.skype.jenkins;

import java.util.Objects;

import com.skype.jenkins.dto.ConfigDTO;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.NotifyTypeEnum;

import static com.skype.jenkins.RunNotification.getConfiguration;

public class Configuration {
    private NotifyTypeEnum notifierType;
    private String jobName;
    private int configFileIndex;

    public Configuration() {
    };

    public Configuration(NotifyTypeEnum notifierType, String jobName) {
        this.jobName = jobName;
        this.notifierType = notifierType;
    };

    public String getJobName() {
        return jobName;
    }

    public String getJenkinsUrl() {
        return getConfiguration().get(configFileIndex).getJenkinsUrl();
    }

    public ConfigJobDTO getJobConfig() {
        return getConfiguration().get(configFileIndex).getJobs().stream()
                .filter(job -> job.getInfo().getJobName().equals(jobName)).findFirst().orElse(null);
    }

    private int configFileIndex() {
        ConfigDTO conf = getConfiguration().stream().filter(config -> config.getJobs().stream()
                .map(job -> job.getInfo().getJobName()).anyMatch(name -> name.equals(jobName))).findFirst()
                .orElse(null);
        return Objects.nonNull(conf) ? getConfiguration().indexOf(conf) : -1;
    }

    public void setJobName(String jobName1) {
        jobName = jobName1;
        configFileIndex = configFileIndex();
    }

    public NotifyTypeEnum getNotifierType() {
        return notifierType;
    }

    public void setNotifierType(NotifyTypeEnum notifierType1) {
        notifierType = notifierType1;
    }
}
