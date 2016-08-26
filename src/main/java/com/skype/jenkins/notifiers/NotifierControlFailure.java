package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;

import com.offbytwo.jenkins.model.BuildResult;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.rest.JenkinsRestHelper;

public abstract class NotifierControlFailure extends NotifierCompleted {

    //if true ABORTED build not affects notifiers
    protected boolean ignoreAborted = true;

    protected NotifierControlFailure(final String jenkinsUrl, final ConfigJobDTO jobConfig){
        super(jenkinsUrl, jobConfig);
    }

    @Override
    protected List<String> prepareMessages (final BuildResult expectedBuildResult) {
        List<String> messages = new ArrayList<>();
        int currentNumber = JenkinsRestHelper.getInstance(jenkinsUrl).getJob(jobConfig.getInfo().getJobName()).getLastCompletedBuild().getNumber();

        while (buildNumber < currentNumber){
            buildNumber++;
            BuildWithDetails watchedBuild = JenkinsRestHelper.getInstance(jenkinsUrl).getJobInfo(super.jobConfig.getInfo().getJobName(), buildNumber);
            if (watchedBuild.getResult().equals(expectedBuildResult) && buildResult.equals(BuildResult.FAILURE)) {
                addJenkinsResponseToSkypeBotMessages(watchedBuild, messages);
            }
            if (!ignoreAborted){
                buildResult = watchedBuild.getResult();
            }
        }

        return messages;
    }



}
