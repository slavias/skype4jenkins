package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.skype.jenkins.Configuration;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

public class NotifierBuildStillRed extends Notifier implements INotifier {

    private JenkinsJobDTO watchedBuildDto;

    public NotifierBuildStillRed(Configuration configuration) {
        super(configuration);
    }

    public void composeSendNotifications() {
        List<String> messages = new ArrayList<>();
        JenkinsJobDTO currentBuildDto = super.jenkinsApi.getJobInfo(super.jobName);
        if (currentBuildDto.getResult().equals(JobResultEnum.IN_PROGRESS))
            return;
        if (Objects.isNull(this.watchedBuildDto)) {
            this.watchedBuildDto = currentBuildDto;
        } else if (!this.watchedBuildDto.getNumber().equals(currentBuildDto.getNumber())
                && this.watchedBuildDto.getResult().equals(currentBuildDto.getResult())) {
            addJenkinsResponseToSkypeBotMessages(currentBuildDto, messages);
            watchedBuildDto = currentBuildDto;
        }
        sendNotifications(messages);
    }
}