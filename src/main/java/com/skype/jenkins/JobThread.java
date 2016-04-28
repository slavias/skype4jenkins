package com.skype.jenkins;

import java.util.ArrayList;
import java.util.List;

import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.rest.JenkinsRestHelper;

public class JobThread implements Runnable{

    private final JenkinsRestHelper jenkins;
    private final ConfigJobDTO jobConfig;
    private List<JobResultEnum> currentStatus;
    
    public JobThread(ConfigJobDTO jobConfig, String jenkinsUrl) {
        this.jobConfig = jobConfig;
        jenkins = new JenkinsRestHelper(jenkinsUrl);
        currentStatus = new ArrayList<>();
    }
    
    @Override
    public void run() {
        Logger.out.info("thread start");
        Thread.currentThread().setName(jobConfig.getInfo().getName());
        while(!Thread.currentThread().isInterrupted()){
            try {
                Logger.out.info("---triggered---");
                JenkinsJobDTO jobInfo = jenkins.getJenkinsJobInfo(jobConfig.getInfo().getJobName());
                List<String> jobConsole = jenkins.getJenkinsJobConsole(jobConfig.getInfo().getJobName());
                if (!currentStatus.isEmpty()) {
                    NotifyHelper notifyHelper = new NotifyHelper(jobConfig, currentStatus, jobInfo, jobConsole);
                    for (NotifyDTO notifier : jobConfig.getNotify()) {
                        switch (notifier.getType()) {
                        case statusOfEachBuild:
                            Logger.out.info("statusOfEachBuild ");
                            sendSkype(notifyHelper.executeStatusOfEachBuild(notifier),jobConfig.getInfo().getChatId());
                            break;
                        case buildStatusChanged:
                            Logger.out.info("buildStatusChanged ");
                            sendSkype(notifyHelper.executeBuildStatusChanged(notifier),jobConfig.getInfo().getChatId());
                            break;
                        case buildStillRed:
                            Logger.out.info("buildStillRed ");
                            sendSkype(notifyHelper.executeBuildStillRed(notifier),jobConfig.getInfo().getChatId());
                            break;
                        case buildFrozen:
                            notifyHelper.executeBuildFrozen(notifier);
                            break;
                        case dailyReport:
                            notifyHelper.executeDailyReport(notifier);
                            break;
                        }

                    }
                }
                if (currentStatus.isEmpty() || !currentStatus.get(currentStatus.size()-1).equals(jobInfo.getResult())){
                    currentStatus.add(jobInfo.getResult());
                }
                Thread.sleep(jobConfig.getInfo().getTimeout() * 1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        Logger.out.info("thread ended");
    }
    
    private void sendSkype(String message, String chatName) {
        if ("".equals(message)) {
            return;
        }
        GroupChat groupChat = (GroupChat) SkypeHelper.getChat(chatName);
        try {
            groupChat.sendMessage(message);
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
