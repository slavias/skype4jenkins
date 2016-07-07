package com.skype.jenkins.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.offbytwo.jenkins.JenkinsServer;
import com.skype.jenkins.JsonUtil;
import com.skype.jenkins.dto.JenkinsJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.logger.Logger;

public class JenkinsRestHelper {

    private static Map<String, JenkinsRestHelper> jenkinsApi = new HashMap<>();

    private JenkinsServer jenkinsServer;
    private JenkinsExtendedHttpClient jenkinsClient;

    private JenkinsRestHelper(String jenkinsHostName) {
        this.jenkinsClient = new JenkinsExtendedHttpClient("jenkins.fpos.kyiv.epam.com");
        this.jenkinsServer = new JenkinsServer(jenkinsClient);
    }

    public static synchronized JenkinsRestHelper getInstance(String jenkinsHost) {
        if (Objects.isNull(jenkinsApi.get(jenkinsHost))) {
            jenkinsApi.put(jenkinsHost, new JenkinsRestHelper(jenkinsHost));
        }
        return jenkinsApi.get(jenkinsHost);
    }
    

    public synchronized JenkinsJobDTO getJobInfo(String jobName) {
        int number = 0;
        try {
            number = jenkinsServer.getJob(jobName).getLastBuild().getNumber();
        } catch (IOException e) {
            Logger.out.error(e);
        }
        
        return getJobInfo(jobName, number);
    }

    @Deprecated
    public synchronized JenkinsJobDTO getJobInfo(String jobName, String buildNumber) {
        return getJobInfo(jobName, Integer.parseInt(buildNumber));
    }

    public synchronized JenkinsJobDTO getJobInfo(String jobName, int buildNumber) {

        JenkinsJobDTO jj = null;
        try {
            jj = JsonUtil.fromJson(
                    jenkinsClient.get(jenkinsServer.getJob(jobName).getBuildByNumber(buildNumber).getUrl()),
                    JenkinsJobDTO.class);
        } catch (IOException e) {
            Logger.out.error(e);
        }
        if (jj.isBuilding()) {
            jj.setResult(JobResultEnum.IN_PROGRESS);
        }
        return jj;
    }

    public synchronized List<String> getJobConsole(String jobName) {
        String jjc = "";
        try {
            jjc = jenkinsServer.getJob(jobName).getLastBuild().details().getConsoleOutputText();
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return Arrays.asList(jjc.split("\\n"));
    }

    @Deprecated
    public synchronized String getJenkinsJobThucydides(String jobName, String buildNumber) {
        return getJenkinsJobThucydides(jobName, Integer.parseInt(buildNumber));
    }
    
    public synchronized String getJenkinsJobThucydides(String jobName, int buildNumber) {
        
        String response = "";
        try {
            response = jenkinsClient.get(jenkinsServer.getJob(jobName).getBuildByNumber(buildNumber).getUrl()+"/thucydidesReport");
        } catch (IOException e) {
            Logger.out.error(e);
        }
        
        return response;
    }
    
    public String getThucydidesUrl(String jobName, String buildNumber) {
        try {
            return jenkinsServer.getJob(jobName).getBuildByNumber(Integer.parseInt(buildNumber)).getUrl()+"thucydidesReport";
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return "";
    }

}
