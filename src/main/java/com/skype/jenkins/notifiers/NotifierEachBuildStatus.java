package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javafx.util.Pair;

import com.skype.jenkins.Configuration;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

public class NotifierEachBuildStatus extends Notifier implements INotifier {
    private Pair<String, JobResultEnum> watchedBuildInfo;

    public NotifierEachBuildStatus(Configuration configuration) {
        super(configuration);
    }

    public void composeSendNotifications() {
        List<String> messages = new ArrayList<>();
        JenkinsJobDTO jenkinsJobDTO = super.jenkinsApi.getJobInfo(super.jobName);
        Pair<String, JobResultEnum> currentBuildInfo = new Pair<>(jenkinsJobDTO.getNumber(), jenkinsJobDTO.getResult());
        if (Objects.isNull(this.watchedBuildInfo)) {
            this.watchedBuildInfo = currentBuildInfo;
        }
        if (!this.watchedBuildInfo.getKey().equals(currentBuildInfo.getKey())) {
            JenkinsJobDTO previousJenkinsJobDto = super.jenkinsApi.getJobInfo(super.jobName,
                    this.watchedBuildInfo.getKey());
            Optional.ofNullable(previousJenkinsJobDto).filter(dto -> dto.getResult().equals(JobResultEnum.IN_PROGRESS))
                    .ifPresent(dto -> addJenkinsResponseToSkypeBotMessages(dto, messages));
            addJenkinsResponseToSkypeBotMessages(jenkinsJobDTO, messages);

        } else if (!Objects.equals(this.watchedBuildInfo.getValue(), currentBuildInfo.getValue())) {
            addJenkinsResponseToSkypeBotMessages(jenkinsJobDTO, messages);
        }
        watchedBuildInfo = currentBuildInfo;
        sendNotifications(messages);
    }
}