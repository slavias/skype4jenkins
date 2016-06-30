package com.skype.jenkins.notifiers;

import com.skype.jenkins.dto.ConfigJobDTO;

/**
 * Created by Anastasiia_Tamazlyka on 6/30/2016.
 */
public class NotifierEachBuildStatus implements INotifier {

    @Override
    public String composeNotification(ConfigJobDTO.NotifyDTO notifier) {
        StringBuilder textOutput = new StringBuilder();
        if (currentStatus.get(currentStatus.size()-1 ) == jobDTO.getResult() || null == notifier.getNotifyStatusByType(jobDTO.getResult())){
            return "";
        }
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(ConfigJobDTO.NotifyTypeEnum.statusOfEachBuild, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();

        StringBuilder textOutput = new StringBuilder();
        textOutput.append(publishBuildMessage(notifier.getNotifyStatusByType(jobDTO.getResult()).getMessage()));
        textOutput.append(publishParameters(prepareAllParameters(ConfigJobDTO.NotifyTypeEnum.statusOfEachBuild, jobDTO.getResult())));
        textOutput.append(publishConsole(notifier.getNotifyStatusByType(jobDTO.getResult()).getLineFromLog()));
        return textOutput.toString();
    }
}
