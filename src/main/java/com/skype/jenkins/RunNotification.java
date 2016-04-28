package com.skype.jenkins;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.samczsun.skype4j.Skype;
import com.skype.jenkins.dto.ConfigDTO;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.JobResultEnum;
import com.skype.jenkins.logger.Logger;

import org.springframework.core.io.ClassPathResource;

public class RunNotification {
    
    public static void main(String[] args) throws Exception {
        
        ConfigDTO cj = parseConfigFile("example.json");
        ExecutorService service = Executors.newCachedThreadPool();
        Skype skype = SkypeHelper.getSkype();
        for (ConfigJobDTO jobConfig: cj.getJobs()){
            service.submit(new JobThread(jobConfig, cj.getJenkinsUrl()));
        }
        
        //service.shutdownNow();
        /*
        
        JenkinsRestHelper jenkinsApi= new JenkinsRestHelper(cj.getJenkinsUrl());
        //JenkinsJobDTO jj= jenkinsApi.getJenkinsJobInfo(cj.getJobs().get(0).getInfo().getJobName());
        List<String> jjc= jenkinsApi.getJenkinsJobConsole(cj.getJobs().get(0).getInfo().getJobName());
        */
        Logger.out.info("");
        
        
        
    }
    
    public static ConfigDTO parseConfigFile(String configName) {
        StringBuilder fileInline = new StringBuilder();
        try {
            URI path = new ClassPathResource("/config/"+configName).getURI();
            for (String line : Files.readAllLines(Paths.get(path), Charset.defaultCharset())) {
                fileInline.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return JsonUtil.fromJson(fileInline.toString(), ConfigDTO.class);
    }
    
    

}
