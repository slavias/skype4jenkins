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

    public void setInfo(final InfoDTO info) {
        this.info = info;
    }

    public List<NotifyDTO> getNotify() {
        return notify;
    }

    public void setNotify(final List<NotifyDTO> notify) {
        this.notify = notify;
    }

    public List<ParametersDTO> getDefaultParameters() {
        return Optional.ofNullable(defaultParameters).orElseGet(ArrayList::new);
    }

    public void setDefaultParameters(final List<ParametersDTO> defaultParameters) {
        this.defaultParameters = defaultParameters;
    }

    public NotifyDTO getNotifierByType(final NotifyTypeEnum type) {
        return getNotify().stream().filter(notifier -> type.equals(notifier.getType())).findAny().orElseGet(null);
    }

    public class InfoDTO {

        @SerializedName("name")
        private String name;
        @SerializedName("jobName")
        private String jobName;
        @SerializedName("serenityReportName")
        private String serenityReportName;
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

        public void setName(final String name) {
            this.name = name;
        }

        public String getJobName() {
            return jobName;
        }

        public void setJobName(final String jobName) {
            this.jobName = jobName;
        }

        public String getSerenityReportName() {
            return Optional.ofNullable(serenityReportName).orElse("thucydidesReport");
        }

        public void setSerenityReportName(final String serenityReportName) {
            this.serenityReportName = serenityReportName;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(final int timeout) {
            this.timeout = timeout;
        }

        public String getOnTime() {
            return onTime;
        }

        public void setOnTime(final String onTime) {
            this.onTime = onTime;
        }

        public String getOnDate() {
            return onDate;
        }

        public void setOnDate(final String onDate) {
            this.onDate = onDate;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(final String chatId) {
            this.chatId = chatId;
        }
    }

    public class NotifyDTO {
        @SerializedName("type")
        private NotifyTypeEnum type;
        @SerializedName("parameters")
        private List<ParametersDTO> parameters;
        @SerializedName("message")
        private String message;
        //proposed to use with "Aborted by" or "Started by" value
        @SerializedName("lineFromLog")
        private String lineFromLog;
        //if true - do not notify of dublicated status (only for success, failure, aborted)
        @SerializedName("once")
        private boolean once;

        public NotifyTypeEnum getType() {
            return type;
        }

        public void setType(final NotifyTypeEnum type) {
            this.type = type;
        }

        public List<ParametersDTO> getParameters() {
            return Optional.ofNullable(parameters).orElseGet(ArrayList::new);
        }

        public void setParameters(final List<ParametersDTO> parameters) {
            this.parameters = parameters;
        }

        public String getLineFromLog() {
            return lineFromLog;
        }

        public void setLineFromLog(final String lineFromLog) {
            this.lineFromLog = lineFromLog;
        }

        public boolean isOnce() {
            return once;
        }

        public void setOnce(final boolean once) {
            this.once = once;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

    }

}
