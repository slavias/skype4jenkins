package com.skype.jenkins.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.skype.jenkins.JsonUtil;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

import org.springframework.http.HttpMethod;

public class JenkinsRestHelper extends RestHelper {
    private static JenkinsRestHelper jenkinsRestHelper = null;

    private static String jenkinsHostName;

    public static void init(String jenkinsUrl) {
        jenkinsHostName = jenkinsUrl;
    }

    public static JenkinsRestHelper getJenkinsHelper() {
        return Optional.ofNullable(jenkinsRestHelper).orElseGet(JenkinsRestHelper::new);
    }

    public JenkinsJobDTO getJobInfo(String jobName) {
        return getJobInfo(jobName, null);
    }

    public JenkinsJobDTO getJobInfo(String jobName, String buildNumber) {
        String url = prepareUrl(jobName, buildNumber, "info");
        JenkinsJobDTO jj = JsonUtil.fromJson(
                sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders()).getBody(), JenkinsJobDTO.class);
        if (jj.isBuilding()) {
            jj.setResult(JobResultEnum.IN_PROGRESS);
        }

        return jj;
    }

    public List<String> getJobConsole(String jobName) {
        String buildNumber = null;
        String url = prepareUrl(jobName, buildNumber, "console");
        String jjc = sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders()).getBody();
        return Arrays.asList(jjc.split("\\n"));

    }

    public String getJenkinsJobThucydides(String jobName, String buildNumber) {
        String url = prepareUrl(jobName, buildNumber, "thucydides");
        if (Objects.isNull(sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders())))   return "";
        return sendAndGetResponse(url, HttpMethod.GET, getHttpEntityWithHeaders()).getBody();
    }

    public String prepareUrl(String jobName, String buildNumber, String type) {
        String ending = "";
        switch (type) {
            case "info": ending = "api/json";break;
            case "console": ending = "consoleText"; break;
            case "thucydides": ending = "thucydidesReport"; break;
        }
        return String.join("/", "http://" + jenkinsHostName, "job", jobName,
                Optional.ofNullable(buildNumber).orElse("lastBuild"), ending);
    }

}
