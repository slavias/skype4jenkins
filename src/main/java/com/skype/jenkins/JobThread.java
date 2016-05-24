package com.skype.jenkins;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.logger.Logger;

import static com.skype.jenkins.logger.Logger.stackTrace;
import static com.skype.jenkins.rest.JenkinsRestHelper.getJenkinsHelper;

public class JobThread implements Runnable{

    private final ConfigJobDTO jobConfig;
    private List<JobResultEnum> currentStatus;

    private NotifyHelper notifyHelper;
    private JenkinsJobDTO jobInfo;
    private List<String> jobConsole;

    public JobThread(ConfigJobDTO jobConfig) {
        this.jobConfig = jobConfig;
        currentStatus = new ArrayList<>();
        notifyHelper = new NotifyHelper(jobConfig, currentStatus);
    }

    public ConfigJobDTO getJobConfig() {
        return jobConfig;
    }

    @Override
    public void run() {
        try{
            Thread.currentThread().setName(jobConfig.getInfo().getName()+" id "+ Thread.currentThread().getId());
            Logger.out.debug("---triggered---");
            jobInfo = getJenkinsHelper().getJobInfo(jobConfig.getInfo().getJobName());
            jobConsole = getJenkinsHelper().getJobConsole(jobConfig.getInfo().getJobName());
            notifyHelper.updateJenkinsResponce(jobInfo, jobConsole);
            if (!currentStatus.isEmpty()) {
                for (NotifyDTO notifier : jobConfig.getNotify()) {
                    String jobMessage = null;
                    switch (notifier.getType()) {
                        case statusOfEachBuild:
                            // Logger.out.info("statusOfEachBuild ");
                            jobMessage = notifyHelper.executeStatusOfEachBuild(notifier);
                            if (!jobMessage.isEmpty()) {
                                Logger.out.info("Start notify");
                                SkypeHelper.sendSkype(jobMessage + jobInfo.getUrl() + "\n" + notifyHelper.getThucydidesReport(getJenkinsHelper()), jobConfig.getInfo().getChatId());
                            }
                            break;
                        case buildStatusChanged:
                            // Logger.out.info("buildStatusChanged ");
                            jobMessage = notifyHelper.executeBuildStatusChanged(notifier);
                            if (!jobMessage.isEmpty()) {
                                SkypeHelper.sendSkype(jobMessage + jobInfo.getUrl() + "\n" + notifyHelper.getThucydidesReport(getJenkinsHelper()), jobConfig.getInfo().getChatId());
                            }
                            break;
                        case buildStillRed:
                            // Logger.out.info("buildStillRed ");
                            SkypeHelper.sendSkype(notifyHelper.executeBuildStillRed(notifier), jobConfig.getInfo().getChatId());
                            break;
                        case buildFrozen:
                            // notifyHelper.executeBuildFrozen(notifier);
                            break;
                        case dailyReport:
                            // notifyHelper.executeDailyReport(notifier);
                            break;
                    }

                }
            }
            if (currentStatus.isEmpty() || !currentStatus.get(currentStatus.size() - 1).equals(jobInfo.getResult())) {
                currentStatus.add(jobInfo.getResult());
            }
        }catch (Throwable e){
            Logger.out.error("Caught exception in ScheduledExecutorService, see stacktrace"+ stackTrace(e));
        }
    }
}
