package com.skype.jenkins;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.rest.JenkinsRestHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class NotifyHelper {
/*
    private static JenkinsJobDTO jobDTO;
    private final ConfigJobDTO jobConfig;
    private final JobResultEnum lastJobStatus;
    private List<String> consoleLog;

    public NotifyHelper(ConfigJobDTO jobConfig, JobResultEnum lastJobStatus) {
        this.jobConfig = jobConfig;
        this.lastJobStatus = lastJobStatus;
        this.jobDTO = null;
        this.consoleLog = null;

    }

    public static synchronized JenkinsJobDTO getJobDTO() {
        return jobDTO;
    }

    public NotifyHelper updateJenkinsResponce(JenkinsJobDTO jobDTO, List<String> consoleLog) {
        this.jobDTO = jobDTO;
        this.consoleLog = consoleLog;
        return this;
    }

    private List<JobResultEnum> getCurrentStatusIgnored(List<JobResultEnum> ignoredStatus) {
        List<JobResultEnum> statusesEnums = lastJobStatus.stream().collect(Collectors.toList());
        for (JobResultEnum status : ignoredStatus) {
            statusesEnums.removeIf(el -> status.equals(el));
        }
        return statusesEnums;
    }

    public String executeStatusOfEachBuild(NotifyDTO notifier) {
        // NotifyDTO notifier = jobConfig.getNotifierByType(NotifyTypeEnum.statusOfEachBuild);
        StringBuilder textOutput = new StringBuilder();
        if (lastJobStatus == jobDTO.getResult() || null == notifier.getNotifyStatusByType(jobDTO.getResult())) {
            return "";
        }
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput
                .append(publishParameters(prepareAllParameters(NotifyTypeEnum.statusOfEachBuild, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();
    }

    public String executeBuildStatusChanged(NotifyDTO notifier) {
        // NotifyDTO notify = jobConfig.getNotifierByType(NotifyTypeEnum.buildStatusChanged);
        StringBuilder textOutput = new StringBuilder();
        List<JobResultEnum> ignoredStatus = new ArrayList<>();
        for (JobResultEnum status : JobResultEnum.values()) {
            if (null == notifier.getNotifyStatusByType(status)) {
                ignoredStatus.add(status);
            }
        }
        List<JobResultEnum> statusFiltered = getCurrentStatusIgnored(ignoredStatus);
        if (statusFiltered.isEmpty() || statusFiltered.get(statusFiltered.size() - 1) == jobDTO.getResult()
                || null == notifier.getNotifyStatusByType(jobDTO.getResult())) {
            return "";
        }
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput
                .append(publishParameters(prepareAllParameters(NotifyTypeEnum.buildStatusChanged, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();
    }

    public String executeBuildStillRed(NotifyDTO notifier) {
        // NotifyDTO notify = jobConfig.getNotifierByType(NotifyTypeEnum.buildStillRed);
        StringBuilder textOutput = new StringBuilder("");
        if (lastJobStatus == jobDTO.getResult()) {
            return "";
        }
        try {
            if (JobResultEnum.IN_PROGRESS.equals(lastJobStatus) && JobResultEnum.FAILURE.equals(jobDTO.getResult())) {
                textOutput.append(publishBuildMessage(notifier.getMessage()));
                textOutput.append(publishParameters(prepareAllParameters(NotifyTypeEnum.buildStillRed, null)));

            }
        } catch (IndexOutOfBoundsException e) {

        }
        return textOutput.toString();
    }

    public String getThucydidesReport(JenkinsRestHelper jenkins) {
        StringBuilder thucydidesResult = new StringBuilder("");
        if (JobResultEnum.SUCCESS.equals(jobDTO.getResult()) || JobResultEnum.UNSTABLE.equals(jobDTO.getResult())) {
            String report = jenkins.getJenkinsJobThucydides(jobConfig.getInfo().getJobName(),
                    String.valueOf(jobDTO.getNumber()));
            if (report.isEmpty()) {
                return "";
            } else {
                thucydidesResult.append("Serenity Result\n");
            }
            Document doc = Jsoup.parse(report);
            Elements summary = doc.select(".summary-leading-column").get(0).parents();
            thucydidesResult.append("test passed: ").append(summary.select("td").get(2).text()).append("\n");
            thucydidesResult.append("test failed: ").append(summary.select("td").get(3).text()).append("\n");
            thucydidesResult.append("report Url: ")
                    .append(jenkins.prepareUrl(jobConfig.getInfo().getJobName(), jobDTO.getNumber(), "thucydides"))
                    .append("\n");
        }
        Logger.out.debug(thucydidesResult);
        return thucydidesResult.toString();
    }*/

}
