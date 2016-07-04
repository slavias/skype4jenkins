package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.util.Pair;

import com.skype.jenkins.Configuration;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

public class NotifierBuildStatusChanged extends Notifier implements INotifier {

    private Pair<String, JobResultEnum> watchedBuildInfo;

    public NotifierBuildStatusChanged(Configuration configuration) {
        super(configuration);
    }

    public void composeSendNotifications() {
        List<String> messages = new ArrayList<>();
        JenkinsJobDTO jenkinsJobDTO = super.jenkinsApi.getJobInfo(super.jobName);
        Pair<String, JobResultEnum> currentBuildInfo = new Pair<>(jenkinsJobDTO.getNumber(), jenkinsJobDTO.getResult());
        if (currentBuildInfo.getValue().equals(JobResultEnum.IN_PROGRESS))
            return;
        if (Objects.isNull(this.watchedBuildInfo)) {
            this.watchedBuildInfo = currentBuildInfo;
        } else if (!this.watchedBuildInfo.getKey().equals(currentBuildInfo.getKey())
                && !this.watchedBuildInfo.getValue().equals(currentBuildInfo.getValue())) {
            addJenkinsResponseToSkypeBotMessages(jenkinsJobDTO, messages);
            watchedBuildInfo = currentBuildInfo;
        }

        sendNotifications(messages);
    }
}