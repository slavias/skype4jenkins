package com.skype.jenkins.rest;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.skype.jenkins.RunNotification;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.logger.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
            JobWithDetails singleJob = jenkinsServer.getJob(jobName);
            String viewName = getConfigurationForJob(singleJob).getInfo().getJobView();
            // Jenkins API could return different job values for single job and for same job in view.
            JobWithDetails jobInView = jenkinsServer.getJob(new FolderJob("", String.format("view/%s/", viewName)), jobName);
            if (jobInView != null) {
                job = singleJob.getNextBuildNumber() > jobInView.getNextBuildNumber() ? singleJob : jobInView;
            } else {
                job = singleJob;
            }
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return job;
    }

    private ConfigJobDTO getConfigurationForJob(JobWithDetails job) {
        try {
            String jobHost = new URI(job.getLastBuild().getUrl()).getHost();
            return RunNotification.getConfiguration().stream()
                    .filter(file ->
                            file.getJenkinsUrl().contains(jobHost)).findFirst().get().getJobs().stream()
                    .filter(j -> j.getInfo().getJobName().equals(job.getName())).findFirst().get();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
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
            jobConsole = getJob(jobName).getLastBuild().details().getConsoleOutputText();
        } catch (IOException e) {
            Logger.out.error(e);
        }
        return Arrays.asList(jobConsole.split("\\n"));
    }

    public synchronized String getJenkinsJobSerenity(final String jobName, final int buildNumber) {
        return getJenkinsJobSerenity(jobName, buildNumber, "thucydidesReport");
    }

    public synchronized String getJenkinsJobSerenity(final String jobName, final int buildNumber,
                                                     final String reportName) {
        String response = "";
        try {
            response = jenkinsClient.getSerenityReport(getSerenityUrl(jobName, buildNumber, reportName));
        } catch (IOException e) {
            Logger.out.error(e);
        }

        return response;
    }

    public String getSerenityUrl(final String jobName, final int buildNumber, final String reportName) {
        return getJob(jobName).getBuildByNumber(buildNumber).getUrl() + reportName;
    }

}
