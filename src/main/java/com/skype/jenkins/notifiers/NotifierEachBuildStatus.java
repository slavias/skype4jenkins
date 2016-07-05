package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.skype.jenkins.Configuration;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

public class NotifierEachBuildStatus extends Notifier implements INotifier {
    private JenkinsJobDTO watchedBuildDto;

    public NotifierEachBuildStatus(Configuration configuration) {
        super(configuration);
    }

    public void composeSendNotifications() {
        List<String> messages = new ArrayList<>();
        JenkinsJobDTO currentBuildDto = super.jenkinsApi.getJobInfo(super.jobName);
        if (Objects.isNull(this.watchedBuildDto)) {
            this.watchedBuildDto = currentBuildDto;
        }
        if (!this.watchedBuildDto.getNumber().equals(currentBuildDto.getNumber())) {
            if (watchedBuildDto.getResult().equals(JobResultEnum.IN_PROGRESS)) {
                Optional.ofNullable(update(watchedBuildDto))
                        .filter(dto -> !dto.getResult().equals(JobResultEnum.IN_PROGRESS))
                        .ifPresent(dto -> addJenkinsResponseToSkypeBotMessages(dto, messages));
            }
            addJenkinsResponseToSkypeBotMessages(currentBuildDto, messages);

        } else if (!Objects.equals(this.watchedBuildDto.getResult(), currentBuildDto.getResult())) {
            addJenkinsResponseToSkypeBotMessages(currentBuildDto, messages);
        }
        watchedBuildDto = currentBuildDto;
        sendNotifications(messages);
    }

    private JenkinsJobDTO update(JenkinsJobDTO dto) {
        return super.jenkinsApi.getJobInfo(super.jobName, dto.getNumber());
    }
}