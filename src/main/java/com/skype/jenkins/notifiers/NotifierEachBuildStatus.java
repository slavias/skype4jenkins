package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javafx.util.Pair;

import com.skype.jenkins.dto.*;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyStatusDto;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.rest.JenkinsRestHelper;

import static com.skype.jenkins.RunNotification.getConfiguration;
import static com.skype.jenkins.dto.NotifyTypeEnum.statusOfEachBuild;

public class NotifierEachBuildStatus {
    private ConfigJobDTO jobConfig;
    private String jobName;
    private Pair<String, JobResultEnum> watchedBuildInfo;
    private JenkinsRestHelper jenkinsApi = JenkinsRestHelper.getInstance(getConfiguration().get(0).getJenkinsUrl());

    public void setJobConfig(final ConfigJobDTO jobConfig) {
        this.jobConfig = jobConfig;
        this.jobName = jobConfig.getInfo().getJobName();
    }

    public List<String> composeNotifications() {
        List<String> messages = new ArrayList<>();
        JenkinsJobDTO jenkinsJobDTO = jenkinsApi.getJobInfo(jobName);
        Pair<String, JobResultEnum> currentBuildInfo = new Pair<>(jenkinsJobDTO.getNumber(), jenkinsJobDTO.getResult());
        if (Objects.isNull(watchedBuildInfo)) {
            watchedBuildInfo = currentBuildInfo;
        }
        if (!watchedBuildInfo.getKey().equals(currentBuildInfo.getKey())) {
            JenkinsJobDTO previousJenkinsJobDto = jenkinsApi.getJobInfo(jobName, watchedBuildInfo.getKey());
            if (Objects.nonNull(getNotifyStatus(previousJenkinsJobDto.getResult()))) {
                messages.add(compose(previousJenkinsJobDto));
            }
            if (Objects.nonNull(getNotifyStatus(currentBuildInfo.getValue()))) {
                messages.add(compose(jenkinsJobDTO));
            }

        } else if (Objects.nonNull(getNotifyStatus(currentBuildInfo.getValue()))
                && !watchedBuildInfo.getValue().equals(currentBuildInfo.getValue())) {
            messages.add(compose(jenkinsJobDTO));
        }
        watchedBuildInfo = currentBuildInfo;
        return messages;
    }

    public NotifyStatusDto getNotifyStatus(JobResultEnum type) {
        return jobConfig.getNotifierByType(NotifyTypeEnum.statusOfEachBuild).getNotifyStatusByType(type);
    }

    protected String compose(JenkinsJobDTO jobResult) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(publishBuildMessage(getNotifyStatus(jobResult.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(jobResult)));
        textOutput.append(publishConsole(getNotifyStatus(jobResult.getResult()).getLineFromLog()));
        return textOutput.toString();
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
        parameters.addAll(jobConfig.getNotifierByType(statusOfEachBuild).getParameters());
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

}