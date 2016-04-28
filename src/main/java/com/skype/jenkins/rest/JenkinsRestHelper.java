package com.skype.jenkins.rest;

import java.util.Arrays;
import java.util.List;

import com.skype.jenkins.JsonUtil;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class JenkinsRestHelper extends RestHelper {
    
    private final String jenkinsUrl;
    
    public JenkinsRestHelper(String jenkinsUrl) {
        this.jenkinsUrl = jenkinsUrl;
    }
    
    public JenkinsJobDTO getJenkinsJobInfo(String jobName){
        return  getJenkinsJobInfo(jobName, null);
    }
    
    public JenkinsJobDTO getJenkinsJobInfo(String jobName, String buildNumber){
        String url = prepareUrl(jobName, buildNumber, true);
        JenkinsJobDTO jj = JsonUtil.fromJson(sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders(), HttpStatus.OK).getBody(), JenkinsJobDTO.class);
        if (jj.isBuilding()) {
            jj.setResult(JobResultEnum.IN_PROGRESS) ;
        }
        
        return jj;
    }
    
    public List<String> getJenkinsJobConsole(String jobName){
        String buildNumber=null;
        String url = prepareUrl(jobName, buildNumber, false);
        String jjc = sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders(), HttpStatus.OK).getBody();
        return Arrays.asList(jjc.split("\\n"));
        
    }

    private String prepareUrl(String jobName, String buildNumber, boolean isInfo) {
        if (null == buildNumber){
            buildNumber="lastBuild";
        }
        
        StringBuilder url = new StringBuilder("http://" + jenkinsUrl);
        url.append("/job/");
        url.append(jobName);
        url.append("/lastBuild/");
        url.append(isInfo ? "api/json" : "consoleText");
        return url.toString();
    }
    
}
