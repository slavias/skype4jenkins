package com.skype.jenkins;

import java.util.List;
import java.util.Objects;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.dto.NotifyTypeEnum;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.notifiers.NotifierFactory;
import com.skype.jenkins.rest.JenkinsRestHelper;

import static com.skype.jenkins.logger.Logger.stackTrace;

public class JobThread implements Runnable {

    private final ConfigJobDTO jobConfig;
    private JobResultEnum currentStatus;
    private JenkinsRestHelper jenkinsApi;

    private NotifyHelper notifyHelper;
    private JenkinsJobDTO jobInfo;
    private List<String> jobConsole;

    public JobThread(ConfigJobDTO jobConfig, String jenkinsUrl) {
        this.jobConfig = jobConfig;
//        notifyHelper = new NotifyHelper(jobConfig, null);
        jenkinsApi = JenkinsRestHelper.getInstance(jenkinsUrl);
    }

    public ConfigJobDTO getJobConfig() {
        return jobConfig;
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(jobConfig.getInfo().getName() + " id " + Thread.currentThread().getId());
            Logger.out.debug("---triggered---");
            jobInfo = jenkinsApi.getJobInfo(jobConfig.getInfo().getJobName());
            if (Objects.isNull(jobInfo))
                return;
            jobConsole = jenkinsApi.getJobConsole(jobConfig.getInfo().getJobName());
//            notifyHelper.updateJenkinsResponce(jobInfo, jobConsole);
            List<String> jobMessages = NotifierFactory.getMessages(NotifyTypeEnum.statusOfEachBuild, jobConfig);
         /*   if (Objects.nonNull(currentStatus)) {
                for (NotifyDTO notifier : jobConfig.getNotify()) {
                    String jobMessage = null;
                    switch (notifier.getType()) {
                    case statusOfEachBuild:
                        jobMessage = notifyHelper.executeStatusOfEachBuild(notifier);
                        if (!jobMessage.isEmpty()) {
                            Logger.out.info("Start notify");
                            SkypeHelper.sendSkype(
                                    jobMessage + jobInfo.getUrl() + "\n" + notifyHelper.getThucydidesReport(jenkinsApi),
                                    jobConfig.getInfo().getChatId());
                        }
                        break;
                    case buildStatusChanged:
                        jobMessage = notifyHelper.executeBuildStatusChanged(notifier);
                        if (!jobMessage.isEmpty()) {
                            SkypeHelper.sendSkype(
                                    jobMessage + jobInfo.getUrl() + "\n" + notifyHelper.getThucydidesReport(jenkinsApi),
                                    jobConfig.getInfo().getChatId());
                        }
                        break;
                    case buildStillRed:
                        SkypeHelper.sendSkype(notifyHelper.executeBuildStillRed(notifier),
                                jobConfig.getInfo().getChatId());
                        break;
                    case buildFrozen:
                    case dailyReport:
                        break;
                    }

                }*/
//            }
            currentStatus = jobInfo.getResult();
        } catch (Throwable e) {
            Logger.out.error("Caught exception in ScheduledExecutorService, see stacktrace" + stackTrace(e));
        }
    }
}
