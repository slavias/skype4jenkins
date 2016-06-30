package com.skype.jenkins.notifiers;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.dto.ParametersDTO;
import com.skype.jenkins.logger.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Anastasiia_Tamazlyka on 6/30/2016.
 */
public class NotifierFabric {



    static INotifier getNotifier(NotifyTypeEnum type){
        INotifier notifier = null;
        switch(type) {
            case statusOfEachBuild:
                notifier = new NotifierEachBuildStatus();
                break;
            case buildStatusChanged:
                notifier = new NotifierBuildStatusChanged();
                break;
            case buildStillRed:
                notifier = new NotifierBuildStillRed();
                break;
            case buildFrozen:
            case dailyReport:
                //TODO: need to implement
        }
        return notifier;
    }
    default String mimi(){
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(ConfigJobDTO.NotifyTypeEnum.statusOfEachBuild, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();
    }

    default String publishBuildMessage(String message) {
        Logger.out.debug(message);
        return message.concat("\n");
    }

    default String publishParameters(List<ParametersDTO> parameters) {
        StringBuilder textOutput = new StringBuilder();
        for (ParametersDTO param : parameters) {
            String paramMessage = null == param.getMessage() ? param.getName() + " : " + param.getValue() : String.format(param.getMessage(), param.getValue());
            textOutput.append(paramMessage).append("\n");
            Logger.out.debug(paramMessage);
        }
        return textOutput.toString();
    }

    default List<ParametersDTO> prepareAllParameters(ConfigJobDTO.NotifyTypeEnum notifyType, JobResultEnum notifyStatusType) {
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

    default String publishConsole(String text) {
        StringBuilder textOutput = new StringBuilder();
        for (String finded: consoleLog.stream().filter(line -> line.contains(text)).collect(Collectors.toList())){
            textOutput.append(finded).append("\n");
        }
        Logger.out.debug(textOutput);
        return textOutput.toString();
    }

}
