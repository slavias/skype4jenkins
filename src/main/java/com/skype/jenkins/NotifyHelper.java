package com.skype.jenkins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.ParseException;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyTypeEnum;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.dto.ParametersDTO;
import com.skype.jenkins.logger.Logger;

public class NotifyHelper {
    
    private final ConfigJobDTO jobConfig;
    private final List<JobResultEnum> currentStatus;
    private final JenkinsJobDTO jobDTO;
    private final List<String> consoleLog;
    
    public NotifyHelper(ConfigJobDTO jobConfig, List<JobResultEnum> currentStatus, JenkinsJobDTO jobDTO, List<String> consoleLog) {
        this.jobConfig = jobConfig;
        this.currentStatus = currentStatus;
        this.jobDTO = jobDTO;
        this.consoleLog = consoleLog;
    }
    
    private List<JobResultEnum> getCurrentStatusIgnored(JobResultEnum... ignoredStatus){
        return getCurrentStatusIgnored(Arrays.asList(ignoredStatus));
    }
    
    private List<JobResultEnum> getCurrentStatusIgnored(List<JobResultEnum> ignoredStatus){
        List<JobResultEnum> statusesEnums = currentStatus.stream().collect(Collectors.toList());
        for (JobResultEnum status: ignoredStatus){
            statusesEnums.removeIf(el -> status.equals(el));
        }
        return statusesEnums;
    }
    
    public String executeStatusOfEachBuild(NotifyDTO notifier){
        //NotifyDTO notifier = jobConfig.getNotifierByType(NotifyTypeEnum.statusOfEachBuild);
        StringBuilder textOutput = new StringBuilder();
        if (currentStatus.get(currentStatus.size()-1 ) == jobDTO.getResult() || null == notifier.getNotifyStatusByType(jobDTO.getResult())){
            return "";
        }
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(NotifyTypeEnum.statusOfEachBuild, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();
    }
    
    public String executeBuildStatusChanged(NotifyDTO notifier){
        //NotifyDTO notify = jobConfig.getNotifierByType(NotifyTypeEnum.buildStatusChanged);
        StringBuilder textOutput = new StringBuilder();
        List <JobResultEnum> ignoredStatus = new ArrayList<>();
        for (JobResultEnum status: JobResultEnum.values()){
            if (null == notifier.getNotifyStatusByType(status)){
                ignoredStatus.add(status);
            }
        }
        List<JobResultEnum> statusFiltered = getCurrentStatusIgnored(ignoredStatus);
        if (statusFiltered.isEmpty() || statusFiltered.get(statusFiltered.size()-1 ) == jobDTO.getResult() || null == notifier.getNotifyStatusByType(jobDTO.getResult())){
            return "";
        }
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(NotifyTypeEnum.buildStatusChanged, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();
    }
    
    public String executeBuildStillRed(NotifyDTO notifier){
        //NotifyDTO notify = jobConfig.getNotifierByType(NotifyTypeEnum.buildStillRed);
        StringBuilder textOutput = new StringBuilder("");
        if (currentStatus.get(currentStatus.size()-1 ) == jobDTO.getResult()){
            return "";
        }
        try{
            if (JobResultEnum.IN_PROGRESS.equals(currentStatus.get(currentStatus.size()-1 )) 
                    && JobResultEnum.FAILURE.equals(jobDTO.getResult()) 
                    && JobResultEnum.FAILURE.equals(currentStatus.get(currentStatus.size()-2 ))){
                textOutput.append(publishBuildMessage(notifier.getMessage()));
                textOutput.append(publishParameters(prepareAllParameters(NotifyTypeEnum.buildStillRed, null)));
                
            }
        }catch (IndexOutOfBoundsException e) {
            
        }
        return textOutput.toString();
    }
    
    public void executeBuildFrozen(NotifyDTO notifier){
        //NotifyDTO notify = jobConfig.getNotifierByType(NotifyTypeEnum.buildFrozen);
        //TODO
        
    }
    
    public void executeDailyReport(NotifyDTO notifier){
        //NotifyDTO notify = jobConfig.getNotifierByType(NotifyTypeEnum.dailyReport);
        //TODO
        
    }
    
    private String publishBuildMessage(String message) {
        StringBuilder textOutput = new StringBuilder();
        textOutput.append(message);
        Logger.out.info(message);
        return textOutput.append("\n").toString();
    }

    private List<ParametersDTO> prepareAllParameters(NotifyTypeEnum notifyType, JobResultEnum notifyStatusType) {
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
            Logger.out.info(paramMessage);
        }
        return textOutput.toString();
    }
    
    private String publishConsole(String text) {
        StringBuilder textOutput = new StringBuilder();
        for (String finded: consoleLog.stream().filter(line -> line.contains(text)).collect(Collectors.toList())){
            textOutput.append(finded).append("\n");
            Logger.out.info(finded);
        }
        return textOutput.toString();
    }
    

}
