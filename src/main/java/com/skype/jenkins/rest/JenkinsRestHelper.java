package com.skype.jenkins.rest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.skype.jenkins.logger.Logger;

public class JenkinsRestHelper {

    private static Map<String, JenkinsRestHelper> jenkinsApi = new HashMap<>();

    private JenkinsServer jenkinsServer;
    private JenkinsExtendedHttpClient jenkinsClient;

    private JenkinsRestHelper(final String jenkinsHostName) {
        this.jenkinsClient = new JenkinsExtendedHttpClient(jenkinsHostName);
        this.jenkinsServer = new JenkinsServer(jenkinsClient);
    }

    public static synchronized JenkinsRestHelper getInstance(final String jenkinsHost) {
        if (Objects.isNull(jenkinsApi.get(jenkinsHost))) {
            jenkinsApi.put(jenkinsHost, new JenkinsRestHelper(jenkinsHost));
        }
        return jenkinsApi.get(jenkinsHost);
    }

    public synchronized JobWithDetails getJob(final String jobName) {
        JobWithDetails job = null;
        try {
            job = jenkinsServer.getJob(jobName);
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return job;
    }

    public synchronized BuildWithDetails getJobInfo(final String jobName) {
        BuildWithDetails job = null;
        try {
            job = getJob(jobName).getLastBuild().details();
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return job;
    }

    public synchronized BuildWithDetails getJobInfo(final String jobName, final int buildNumber) {
        BuildWithDetails job = null;
        try {
            job = getJob(jobName).getBuildByNumber(buildNumber).details();
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return job;
    }

    public synchronized List<String> getJobConsole(final String jobName) {
        String jobConsole = "";
        try {
            jobConsole = jenkinsServer.getJob(jobName).getLastBuild().details().getConsoleOutputText();
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return Arrays.asList(jobConsole.split("\\n"));
    }

    public synchronized String getJenkinsJobThucydides(final String jobName, final int buildNumber) {

        String response = "";
        try {
            response = jenkinsClient.get(getThucydidesUrl(jobName, buildNumber));
        } catch (IOException e) {
            Logger.out.error(e);
        }

        return response;
    }

    public String getThucydidesUrl(final String jobName, final int buildNumber) {
        return getJob(jobName).getBuildByNumber(buildNumber).getUrl() + "thucydidesReport";
    }

}
