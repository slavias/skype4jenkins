package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.annotations.SerializedName;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.dto.ParametersDTO;
import com.skype.jenkins.logger.Logger;

public abstract class Notifier {

    public abstract String composeNotification();

    protected String compose(){
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput.append(
                publishParameters(prepareAllParameters(ConfigJobDTO.NotifyTypeEnum.buildStatusChanged, jobResult)));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobResult).getLineFromLog()));
        return textOutput.toString();
    }

    private String publishBuildMessage(String message) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(message);
        Logger.out.debug(message);
        return textOutput.append("\n").toString();
    }

    private List<ParametersDTO> prepareAllParameters(ConfigJobDTO.NotifyTypeEnum notifyType, JobResultEnum notifyStatusType) {
        List<ParametersDTO> parameters = new ArrayList<>();
        parameters.addAll(jobConfig.getDefaultParameters());
        if (null != notifyType) {
            parameters.addAll(jobConfig.getNotifierByType(notifyType).getParameters());
            if (null != notifyStatusType) {
                parameters.addAll(jobConfig.getNotifierByType(notifyType).getNotifyStatusByType(notifyStatusType).getParameters());
            }
        }
        parameters.stream().forEach(par -> par.setValue(jobDTO.getParameterByName(par.getName()).getValue()));

        return parameters.stream().filter(par -> null != par.getValue()).collect(Collectors.toList());
    }

    private String publishParameters(List<ParametersDTO> parameters) {
        StringBuilder textOutput = new StringBuilder();
        for (ParametersDTO param : parameters) {
            String paramMessage = null == param.getMessage() ? param.getName() + " : " + param.getValue() : String.format(param.getMessage(), param.getValue());
            textOutput.append(paramMessage).append("\n");
            Logger.out.debug(paramMessage);
        }
        return textOutput.toString();
    }

    private String publishConsole(String text) {
        StringBuilder textOutput = new StringBuilder();
        for (String finded: consoleLog.stream().filter(line -> line.contains(text)).collect(Collectors.toList())){
            textOutput.append(finded).append("\n");
        }
        Logger.out.debug(textOutput);
        return textOutput.toString();
    }
}
