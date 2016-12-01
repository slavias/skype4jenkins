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
    private static ScheduledExecutorService threadService;

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

    public static ScheduledExecutorService getThreadService() {
        return Optional.ofNullable(threadService)
                .map(srv -> srv.isShutdown() ? null : srv)
                .orElseGet(() -> {
                            threadService = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
                            return threadService;
                        });
    }

    //TODO implement message on time
    public static void main(final String[] args){
        SkypeHelper.getSkype();
        getThreadService().shutdown();
        start();
    }

    public static String start() {
        if (threadService.isShutdown()){
            getConfiguration().stream().forEach(
                    allConf -> allConf.getJobs().forEach(
                            jobConf -> getThreadService().scheduleWithFixedDelay(new JobThread(allConf.getJenkinsUrl(), jobConf), 1,
                                    jobConf.getInfo().getTimeout(), TimeUnit.SECONDS)));
            return "Start listener";
        } else {
            return "Listener already started. Can't start";
        }
    }

    public static String stop() {
        if (threadService.isShutdown()){
            return "Listener not running. Can't stop";
        } else{
            configData = null;
            threadService.shutdown();
            return "Stop Listener";
        }
    }

    public static String status() {
        return threadService.isShutdown() ?"Listener stopped" : "Listener Started";
    }

    public static ConfigDTO parseConfigFile(final String configName) {
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
