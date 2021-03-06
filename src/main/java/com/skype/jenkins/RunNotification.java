package com.skype.jenkins;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private static List<ConfigDTO> initConfData() {
        String confPath = Optional.ofNullable(System.getProperty("config.file"))
                .orElseThrow(() -> new RuntimeException("Specify config.file property"));
        configData = Arrays.asList(confPath.split(",")).stream().map(conf -> parseConfigFile(conf.trim()))
                .collect(Collectors.toList());
        return configData;
    }

    public static List<ConfigDTO> getConfiguration() {
        return Optional.ofNullable(configData).orElseGet(RunNotification::initConfData);
    }

    public static void main(String[] args) throws Exception {
        SkypeHelper.getSkype();
        ScheduledExecutorService service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        getConfiguration().stream().flatMap(allConfiguration -> allConfiguration.getJobs().stream())
                .forEach(jobConfig -> service.scheduleWithFixedDelay(new JobThread(jobConfig.getInfo().getJobName()), 1,
                        jobConfig.getInfo().getTimeout(), TimeUnit.SECONDS));
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
