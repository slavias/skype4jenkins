package com.skype.jenkins;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.skype.jenkins.dto.ConfigDTO;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.logger.Logger;

public class RunNotification {
    
    public static void main(String[] args) throws Exception {
        
        String configFiles = System.getProperty("config.file");
        if (null == configFiles || configFiles.isEmpty()){
            configFiles="config/example.json";
        }
        //Init and login to skype
        SkypeHelper.getSkype();
        List<JobThread> jobs = new ArrayList<>();
        Arrays.asList(configFiles.split(",")).forEach(file -> {
            ConfigDTO cj = parseConfigFile(file.trim());
            for (ConfigJobDTO jobConfig : cj.getJobs()) {
                jobs.add(new JobThread(jobConfig, cj.getJenkinsUrl()));
            }
        });
        ScheduledExecutorService  service = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        jobs.forEach(job -> service.scheduleAtFixedRate(job, 1, job.getJobConfig().getInfo().getTimeout(), TimeUnit.SECONDS));
        
        //service.shutdownNow();
        
    }
    
    public static ConfigDTO parseConfigFile(String configName) {
        Logger.out.info("parse config file: "+ configName);
        StringBuilder fileInline = new StringBuilder();
        try {
            for (String line : Files.readAllLines(Paths.get(configName), Charset.defaultCharset())) {
                fileInline.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonUtil.fromJson(fileInline.toString(), ConfigDTO.class);
    }
    
    

}
