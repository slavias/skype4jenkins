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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
        Thread.currentThread().setName(jobConfig.getInfo().getName());
        Logger.out.info("thread start");
        NotifyHelper notifyHelper;
        JenkinsJobDTO jobInfo;
        List<String> jobConsole;
        notifyHelper = new NotifyHelper(jobConfig, currentStatus);
        while(!Thread.currentThread().isInterrupted()){
            try {
                Logger.out.info("---triggered---");
                jobInfo = jenkins.getJenkinsJobInfo(jobConfig.getInfo().getJobName());
                jobConsole = jenkins.getJenkinsJobConsole(jobConfig.getInfo().getJobName());
                notifyHelper.updateJenkinsResponce(jobInfo, jobConsole);
                if (!currentStatus.isEmpty()) {
                    for (NotifyDTO notifier : jobConfig.getNotify()) {
                        String jobMessage = null;
                        switch (notifier.getType()) {
                        case statusOfEachBuild:
                            //Logger.out.info("statusOfEachBuild ");
                            jobMessage = notifyHelper.executeStatusOfEachBuild(notifier);
                            if (!jobMessage.isEmpty()){
                                sendSkype(jobMessage + jobInfo.getUrl() + "\n" + getThucydidesReport(jobInfo),jobConfig.getInfo().getChatId());
                            }
                            break;
                        case buildStatusChanged:
                            //Logger.out.info("buildStatusChanged ");
                            jobMessage = notifyHelper.executeBuildStatusChanged(notifier);
                            if (!jobMessage.isEmpty()){
                                sendSkype(jobMessage + jobInfo.getUrl() + "\n" + getThucydidesReport(jobInfo),jobConfig.getInfo().getChatId());
                            }
                            break;
                        case buildStillRed:
                            //Logger.out.info("buildStillRed ");
                            sendSkype(notifyHelper.executeBuildStillRed(notifier),jobConfig.getInfo().getChatId());
                            break;
                        case buildFrozen:
                            //notifyHelper.executeBuildFrozen(notifier);
                            break;
                        case dailyReport:
                            //notifyHelper.executeDailyReport(notifier);
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
        GroupChat groupChat = SkypeHelper.getChat(chatName);
        try {
            groupChat.sendMessage(message);
        } catch (ConnectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private String getThucydidesReport(JenkinsJobDTO jobInfo) {
        StringBuilder thucydidesResult = new StringBuilder("");
        if (JobResultEnum.SUCCESS.equals(jobInfo.getResult()) || JobResultEnum.UNSTABLE.equals(jobInfo.getResult())){
            String report = jenkins.getJenkinsJobThucydides(jobConfig.getInfo().getJobName(), String.valueOf(jobInfo.getNumber()));
            if (report.isEmpty()){
                return "";
            } else {
                thucydidesResult.append("Serenity Result\n");
            }
            Document doc = Jsoup.parse(report);
            Elements summary = doc.select(".summary-leading-column").get(0).parents();
            thucydidesResult.append("test passed: ").append(summary.select("td").get(2).text()).append("\n");
            thucydidesResult.append("test failed: ").append(summary.select("td").get(3).text()).append("\n");
            thucydidesResult.append("report Url: ").append(jenkins.prepareUrl(jobConfig.getInfo().getJobName(), null, "thucydides")).append("\n");
        }
        return thucydidesResult.toString();
    }

}
