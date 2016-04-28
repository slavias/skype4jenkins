package com.skype.jenkins.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class ConfigJobDTO {

    @SerializedName("info")
    private InfoDTO info;
    
    @SerializedName("notify")
    private List<NotifyDTO> notify;
    
    @SerializedName("defaultParameters")
    private List<ParametersDTO> defaultParameters;
    
    
    
    public InfoDTO getInfo() {
        return info;
    }

    public void setInfo(InfoDTO info) {
        this.info = info;
    }

    public List<NotifyDTO> getNotify() {
        return notify;
    }

    public void setNotify(List<NotifyDTO> notify) {
        this.notify = notify;
    }

    public List<ParametersDTO> getDefaultParameters() {
        return Optional.ofNullable(defaultParameters).orElseGet(ArrayList::new);
    }

    public void setDefaultParameters(List<ParametersDTO> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }
    
    public NotifyDTO getNotifierByType(NotifyTypeEnum type){
        return getNotify().stream().filter(notifier -> type.equals(notifier.getType())).findAny().orElseGet(null);
    }

    public class InfoDTO {
        
        @SerializedName("name")
        private String name;
        @SerializedName("jobName")
        private String jobName;
        @SerializedName("timeout")
        private int timeout;
        @SerializedName("onTime")
        private String onTime;
        @SerializedName("onDate")
        private String onDate;
        @SerializedName("chatId")
        private String chatId;
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getJobName() {
            return jobName;
        }
        public void setJobName(String jobName) {
            this.jobName = jobName;
        }
        public int getTimeout() {
            return timeout;
        }
        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
        public String getOnTime() {
            return onTime;
        }
        public void setOnTime(String onTime) {
            this.onTime = onTime;
        }
        public String getOnDate() {
            return onDate;
        }
        public void setOnDate(String onDate) {
            this.onDate = onDate;
        }
        public String getChatId() {
            return chatId;
        }
        public void setChatId(String chatId) {
            this.chatId = chatId;
        }
    }
    
    public class NotifyDTO {
        @SerializedName("type")
        private NotifyTypeEnum type;
        @SerializedName("parameters")
        private List<ParametersDTO> parameters;
        @SerializedName("status")
        private List<NotifyStatusDto> status;
        @SerializedName("message")
        private String message;
        
        public NotifyTypeEnum getType() {
            return type;
        }
        public void setType(NotifyTypeEnum type) {
            this.type = type;
        }
        public List<ParametersDTO> getParameters() {
            return Optional.ofNullable(parameters).orElseGet(ArrayList::new);
        }
        public void setParameters(List<ParametersDTO> parameters) {
            this.parameters = parameters;
        }
        public List<NotifyStatusDto> getStatus() {
            return status;
        }
        public void setStatus(List<NotifyStatusDto> status) {
            this.status = status;
        }
        
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        
        public NotifyStatusDto getNotifyStatusByType(JobResultEnum type){
            return getStatus().stream().filter(notifier -> type.equals(notifier.getType())).findAny().orElse(null);
        }
        
        
    }
    
    public class NotifyStatusDto {
        @SerializedName("type")
        private JobResultEnum type;
        @SerializedName("message")
        private String message;
        @SerializedName("parameters")
        private List<ParametersDTO> parameters;
        @SerializedName("timeout")
        private String timeout;
        @SerializedName("lineFromLog")
        private String lineFromLog;
        
        
        public JobResultEnum getType() {
            return type;
        }
        public void setType(JobResultEnum type) {
            this.type = type;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public List<ParametersDTO> getParameters() {
            return Optional.ofNullable(parameters).orElseGet(ArrayList::new);
        }
        public void setParameters(List<ParametersDTO> parameters) {
            this.parameters = parameters;
        }
        public String getTimeout() {
            return timeout;
        }
        public void setTimeout(String timeout) {
            this.timeout = timeout;
        }
        public String getLineFromLog() {
            return lineFromLog;
        }
        public void setLineFromLog(String lineFromLog) {
            this.lineFromLog = lineFromLog;
        }
        
        
    }
    
    public enum NotifyTypeEnum {
        statusOfEachBuild,
        buildStatusChanged,
        buildStillRed,
        buildFrozen,
        dailyReport;
        
    }
}


