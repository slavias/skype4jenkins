package com.skype.jenkins.notifiers;

import com.skype.jenkins.dto.ConfigJobDTO;

/**
 * Created by Anastasiia_Tamazlyka on 6/30/2016.
 */
public class NotifierBuildStillRed implements INotifier {

    @Override
    public String composeNotification(ConfigJobDTO.NotifyDTO notifier) {
        return null;
    }
}
