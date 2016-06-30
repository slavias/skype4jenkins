package com.skype.jenkins.notifiers;

import java.util.ArrayList;
import java.util.List;

import com.skype.jenkins.NotifyHelper;
import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.JobResultEnum;

public class NotifierBuildStatusChanged extends Notifier {

    public String composeNotification() {
        JobResultEnum jobResult = NotifyHelper.getJobDTO().getResult();

        JobResultEnum ignoredStatus = null;
        for (JobResultEnum status : JobResultEnum.values()) {
            if (null == this.getNotifyStatusByType(status)) {
            }
            List<JobResultEnum> statusFiltered = getCurrentStatusIgnored(ignoredStatus);
            if (statusFiltered.isEmpty() || statusFiltered.get(statusFiltered.size() - 1) == jobResult
                    || null == notifier.getNotifyStatusByType(jobDTO.getResult())) {
                return "";
            }

        }
    }

}