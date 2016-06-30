package com.skype.jenkins.dto;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NotifyDTO {
    @SerializedName("type")
    private NotifyTypeEnum type;
    @SerializedName("status")
    private List<MessageForStatusDto> statuses;

    class MessageForStatusDto {
        @SerializedName("type")
        private JobResultEnum type;
        @SerializedName("message")
        private String message;
        @SerializedName("lineFromLog")
        private String lineFromLog;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

}