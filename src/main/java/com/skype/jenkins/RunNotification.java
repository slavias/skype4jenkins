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
import java.util.stream.Collectors;

import com.skype.jenkins.dto.ConfigDTO;
import com.skype.jenkins.logger.Logger;

public class RunNotification {

    private static List<ConfigDTO> configData;
    private static List<JobThread> jobs = new ArrayList<>();

    private static List<ConfigDTO> initConfData() {
        String confPath = Optional.ofNullable(System.getProperty("config.file"))
                .orElseThrow(() -> new RuntimeException("Specify config.file property"));
        configData = Arrays.asList(confPath.split(",")).stream().map(conf -> parseConfigFile(conf.trim())).collect(Collectors.toList());
        return configData;
    }

    public static List<ConfigDTO> getConfiguration() {
        return Optional.ofNullable(configData).orElseGet(RunNotification::initConfData);
    }

    private static void initializeJobThreads() {
        getConfiguration().forEach(
                allConfiguration -> allConfiguration.getJobs().forEach(
                        jobConfiguration -> jobs.add(new JobThread(jobConfiguration, allConfiguration
                                .getJenkinsUrl()))));
    }

    public static void main(String[] args) throws Exception {
        SkypeHelper.getSkype();
        initializeJobThreads();
        int numberProcessors = Runtime.getRuntime().availableProcessors();
        Logger.out.info("Number of available processors = " + numberProcessors);
        ScheduledExecutorService service = Executors.newScheduledThreadPool(numberProcessors);
        jobs.forEach(job -> {
            int initDelay = (int) (Math.random() % job.getJobConfig().getInfo().getTimeout());
            Logger.out.info(
                    "Initial delay for job ".concat(job.getJobConfig().getInfo().getName()).concat(" is equal to " + initDelay));
            service.scheduleWithFixedDelay(job, initDelay, job.getJobConfig().getInfo().getTimeout(), TimeUnit.SECONDS);
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
