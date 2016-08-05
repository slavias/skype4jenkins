package com.skype.jenkins.notifiers.types;

import java.util.ArrayList;
import java.util.List;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.notifiers.Notifier;
import com.skype.jenkins.rest.JenkinsRestHelper;

public class NotifierStart extends Notifier {

    private int buildNumber;

    public NotifierStart(final String jenkinsUrl, final ConfigJobDTO jobConfig) {
        super(jenkinsUrl, jobConfig);
        buildNumber = JenkinsRestHelper.getInstance(super.jenkinsUrl).getJob(super.jobConfig.getInfo().getJobName()).getLastBuild().getNumber();
    }

    @Override
    public void composeSendNotifications() {
        List<String> messages = new ArrayList<>();
        int currentNumber = JenkinsRestHelper.getInstance(super.jenkinsUrl).getJob(super.jobConfig.getInfo().getJobName()).getLastBuild().getNumber();
        while (buildNumber < currentNumber){
            buildNumber++;
            addJenkinsResponseToSkypeBotMessages(JenkinsRestHelper.getInstance(super.jenkinsUrl).getJobInfo(super.jobConfig.getInfo().getJobName(), buildNumber), messages);
        }
        sendNotifications(messages);
    }

}
