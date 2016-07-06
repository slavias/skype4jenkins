package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.skype.jenkins.Configuration;
import com.skype.jenkins.SkypeHelper;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.dto.NotifyTypeEnum;
import com.skype.jenkins.dto.ParametersDTO;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.rest.JenkinsRestHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public abstract class Notifier {

    NotifyTypeEnum notifierType;
    String jobName;
    ConfigJobDTO jobConfig;
    JenkinsRestHelper jenkinsApi;

    public Notifier(Configuration configuration) {
        notifierType = configuration.getNotifierType();
        jobName = configuration.getJobName();
        jobConfig = configuration.getJobConfig();
        jenkinsApi = JenkinsRestHelper.getInstance(configuration.getJenkinsUrl());
    }

    public abstract void composeSendNotifications();

    protected void sendNotifications(final List<String> messages) {
        messages.forEach(notif -> SkypeHelper.sendSkype(notif, jobConfig.getInfo().getChatId()));
    }

    protected void addJenkinsResponseToSkypeBotMessages(JenkinsJobDTO jenkinsJobDTO, List<String> messages) {
        if (Objects.nonNull(getNotifyStatus(jenkinsJobDTO.getResult())))
            messages.add(composeMessageFromJenkinsResponse(jenkinsJobDTO));
    }

    protected String composeMessageFromJenkinsResponse(JenkinsJobDTO jobResult) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(publishBuildMessage(getNotifyStatus(jobResult.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(jobResult)));
        textOutput.append(publishConsole(getNotifyStatus(jobResult.getResult()).getLineFromLog()));
        textOutput.append(jenkinsApi.getJobInfo(jobName).getUrl() + "\n");
        textOutput.append(getThucydidesReport(jobResult.getNumber(), jobResult.getResult()));
        return textOutput.toString();
    }

    private ConfigJobDTO.NotifyStatusDto getNotifyStatus(JobResultEnum type) {
        return jobConfig.getNotifierByType(notifierType).getNotifyStatusByType(type);
    }

    private String publishBuildMessage(String message) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(message);
        Logger.out.debug(message);
        return textOutput.append("\n").toString();
    }

    private List<ParametersDTO> prepareAllParameters(JenkinsJobDTO jobResult) {
        List<ParametersDTO> parameters = new ArrayList<>();
        parameters.addAll(jobConfig.getDefaultParameters());
        parameters.addAll(jobConfig.getNotifierByType(notifierType).getParameters());
        parameters.addAll(getNotifyStatus(jobResult.getResult()).getParameters());
        parameters.stream().forEach(par -> par.setValue(jobResult.getParameterByName(par.getName()).getValue()));
        return parameters.stream().filter(par -> null != par.getValue()).collect(Collectors.toList());
    }

    private String publishParameters(List<ParametersDTO> parameters) {
        StringBuilder textOutput = new StringBuilder();
        for (ParametersDTO param : parameters) {
            String paramMessage = (null == param.getMessage()) ? param.getName() + " : " + param.getValue()
                    : String.format(param.getMessage(), param.getValue());
            textOutput.append(paramMessage).append("\n");
            Logger.out.debug(paramMessage);
        }
        return textOutput.toString();
    }

    private String publishConsole(String text) {
        StringBuilder textOutput = new StringBuilder();
        for (String finded : jenkinsApi.getJobConsole(jobName).stream().filter(line -> line.contains(text))
                .collect(Collectors.toList())) {
            textOutput.append(finded).append("\n");
        }
        Logger.out.debug(textOutput);
        return textOutput.toString();
    }

    private String getThucydidesReport(final String buildNumber, final JobResultEnum result) {
        StringBuilder thucydidesResult = new StringBuilder("");
        if (JobResultEnum.SUCCESS.equals(result) || JobResultEnum.UNSTABLE.equals(result)) {
            String report = jenkinsApi.getJenkinsJobThucydides(jobName, buildNumber);
            if (report.isEmpty()) {
                return "";
            } else {
                thucydidesResult.append("Serenity Result\n");
            }
            Document doc = Jsoup.parse(report);
            Elements summary = doc.select(".summary-leading-column").get(0).parents();
            thucydidesResult.append("test passed: ").append(summary.select("td").get(2).text()).append("\n");
            thucydidesResult.append("test failed: ").append(summary.select("td").get(3).text()).append("\n");
            thucydidesResult.append("report Url: ").append(jenkinsApi.getThucydidesUrl(jobName, buildNumber))
                    .append("\n");
        }
        Logger.out.debug(thucydidesResult);
        return thucydidesResult.toString();
    }
}
