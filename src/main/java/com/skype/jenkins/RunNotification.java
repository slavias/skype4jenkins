package com.skype.jenkins;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.skype.jenkins.dto.ConfigDTO;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.rest.JenkinsRestHelper;

public class RunNotification {

    private static ConfigDTO configData;
    private static List<JobThread> jobs = new ArrayList<>();

    private static ConfigDTO initConfData() {
        String confPath = Optional.ofNullable(System.getProperty("config.file"))
                .orElseThrow(() -> new RuntimeException("Specify config.file property"));
        Arrays.asList(confPath.split(",")).forEach(file -> {
            configData = parseConfigFile(file.trim());
        });
        return configData;
    }

    public static ConfigDTO getConfiguration() {
        return Optional.ofNullable(configData).orElseGet(RunNotification::initConfData);
    }

    private static void initializeJobThreads() {
        getConfiguration().getJobs().stream().forEach(jobConfiguration -> jobs.add(new JobThread(jobConfiguration)));
    }

    public static void main(String[] args) throws Exception {
        SkypeHelper.getSkype();
        JenkinsRestHelper.init(getConfiguration().getJenkinsUrl());
        initializeJobThreads();
        int numberProcessors = Runtime.getRuntime().availableProcessors();
        Logger.out.info("Number of available processors = " + numberProcessors);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(numberProcessors);
        //TODO:delete timeout for jobs at conf file
        jobs.forEach(job -> {
            int delay = 10 + (int) (Math.random() * ((20 - 10) + 1));
            Logger.out.info(
                    "Delay for job ".concat(job.getJobConfig().getInfo().getName()).concat(" is equal to " + delay));
            service.scheduleWithFixedDelay(job, 1, delay, TimeUnit.SECONDS);
        });
    }

    public static ConfigDTO parseConfigFile(String configName) {
        Logger.out.info("parse config file: " + configName);
        StringBuilder fileInline = new StringBuilder();
        try {
            for (String line : Files.readAllLines(Paths.get(configName), Charset.defaultCharset())) {
                fileInline.append(line);
            }
        } catch (IOException e) {
            Logger.out.error(e.getStackTrace());
        }
        return JsonUtil.fromJson(fileInline.toString(), ConfigDTO.class);
    }

}
