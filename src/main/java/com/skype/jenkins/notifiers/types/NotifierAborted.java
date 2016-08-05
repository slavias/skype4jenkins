package com.skype.jenkins.notifiers.types;

import java.util.ArrayList;
import java.util.List;

import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.NotifyTypeEnum;
import com.skype.jenkins.notifiers.Notifier;
import com.skype.jenkins.rest.JenkinsRestHelper;

public class NotifierAborted extends Notifier {

    private int buildNumber;
    private BuildResult buildResult;

    public NotifierAborted(final String jenkinsUrl, final ConfigJobDTO jobConfig){
        super(jenkinsUrl, jobConfig);
        buildNumber = JenkinsRestHelper.getInstance(super.jenkinsUrl).getJob(super.jobConfig.getInfo().getJobName()).getLastCompletedBuild().getNumber();
        buildResult = JenkinsRestHelper.getInstance(super.jenkinsUrl).getJobInfo(super.jobConfig.getInfo().getJobName(),buildNumber).getResult();
    }

    @Override
    public void composeSendNotifications() {
        List<String> messages = new ArrayList<>();
        int currentNumber = JenkinsRestHelper.getInstance(jenkinsUrl).getJob(jobConfig.getInfo().getJobName()).getLastCompletedBuild().getNumber();

        while (buildNumber < currentNumber){
            buildNumber++;
            BuildWithDetails watchedBuild = JenkinsRestHelper.getInstance(jenkinsUrl).getJobInfo(super.jobConfig.getInfo().getJobName(), buildNumber);
            if (watchedBuild.getResult().equals(BuildResult.ABORTED)) {
                if (!(buildResult.equals(BuildResult.ABORTED) && jobConfig.getNotifierByType(NotifyTypeEnum.getNotifierByClass(this.getClass())).isOnce())) {
                    addJenkinsResponseToSkypeBotMessages(watchedBuild, messages);
                }
            }
            buildResult=watchedBuild.getResult();
        }

        sendNotifications(messages);
    }

}
