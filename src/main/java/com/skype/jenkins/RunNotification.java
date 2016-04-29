package com.skype.jenkins;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.samczsun.skype4j.Skype;
import com.skype.jenkins.dto.ConfigDTO;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.logger.Logger;

public class RunNotification {
    
    public static void main(String[] args) throws Exception {
        
        String configFiles = System.getProperty("config.file");
        if (null == configFiles || configFiles.isEmpty()){
            configFiles="config/example.json";
        }
        ExecutorService service = Executors.newCachedThreadPool();
        Arrays.asList(configFiles.split(",")).forEach(file -> {
            ConfigDTO cj = parseConfigFile(file.trim());
            Skype skype = SkypeHelper.getSkype();
            for (ConfigJobDTO jobConfig : cj.getJobs()) {
                service.submit(new JobThread(jobConfig, cj.getJenkinsUrl()));
            }
        });
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
