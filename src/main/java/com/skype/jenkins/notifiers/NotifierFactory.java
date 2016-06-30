package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.NotifyTypeEnum;

/**
 * Created by Anastasiia_Tamazlyka on 6/30/2016.
 */
public class NotifierFactory {
    private static NotifierEachBuildStatus notifierEachBuildStatus = new NotifierEachBuildStatus();
    
    public static List<String> getMessages(NotifyTypeEnum type, final ConfigJobDTO jobConfig) {
        List<String> listMessages = new ArrayList<>();
        switch (type) {
        case statusOfEachBuild:
            notifierEachBuildStatus.setJobConfig(jobConfig);
            listMessages = notifierEachBuildStatus.composeNotifications();
            break;
        case buildStatusChanged:
            // notifier = new NotifierBuildStatusChanged();
            // break;
        case buildStillRed:
            // notifier = new NotifierBuildStillRed();
            // break;
        case buildFrozen:
        case dailyReport:
            // TODO: need to implement
        }
        return listMessages;
    }
}
