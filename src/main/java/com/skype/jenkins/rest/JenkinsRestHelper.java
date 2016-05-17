package com.skype.jenkins.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.skype.jenkins.JsonUtil;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.logger.Logger;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

public class JenkinsRestHelper extends RestHelper {
    
    private final String jenkinsUrl;
    
    public JenkinsRestHelper(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }
    
    public JenkinsJobDTO getJenkinsJobInfo(String jobName){
        return  getJenkinsJobInfo(jobName, null);
    }
    
    public JenkinsJobDTO getJenkinsJobInfo(String jobName, String buildNumber){
        String url = prepareUrl(jobName, buildNumber, "info");
        JenkinsJobDTO jj = JsonUtil.fromJson(sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders(), HttpStatus.OK).getBody(), JenkinsJobDTO.class);
        if (jj.isBuilding()) {
            jj.setResult(JobResultEnum.IN_PROGRESS) ;
        }
        
        return jj;
    }
    
    public List<String> getJenkinsJobConsole(String jobName){
        String buildNumber=null;
        String url = prepareUrl(jobName, buildNumber, "console");
        String jjc = sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders(), HttpStatus.OK).getBody();
        return Arrays.asList(jjc.split("\\n"));
        
    }
    public String getJenkinsJobThucydides(String jobName, String buildNumber) {
        String url = prepareUrl(jobName, buildNumber, "thucydides");
        String jt = "";
        try {
            jt = send(getUriFromString(url), HttpMethod.GET, getHttpEntityWithHeaders()).getBody();
        } catch (final HttpClientErrorException | HttpServerErrorException e) {
            Logger.out.error(e);
        }
        return jt;
    }
    

    public String prepareUrl(String jobName, Integer buildNumber, String type) {
        return prepareUrl(jobName, Optional.ofNullable(buildNumber).map(obj -> obj.toString()).orElse(null), type);
    }
    
    public String prepareUrl(String jobName, String buildNumber, String type) {
        if (null == buildNumber){
            buildNumber="lastBuild";
        }
        
        StringBuilder url = new StringBuilder("http://" + jenkinsUrl);
        url.append("/job/");
        url.append(jobName);
        url.append("/" + buildNumber + "/");
        switch (type) {
        case "info":url.append("api/json");break;
        case "console":url.append("consoleText");break;
        case "thucydides":url.append("thucydidesReport");break;
        }
        return url.toString();
    }
    
}
