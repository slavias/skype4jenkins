package com.skype.jenkins.dto;

import com.google.gson.annotations.SerializedName;

public class ParametersDTO {

    @SerializedName("name")
    private String name;
    @SerializedName("value")
    private String value;
    @SerializedName("message")
    private String message;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
