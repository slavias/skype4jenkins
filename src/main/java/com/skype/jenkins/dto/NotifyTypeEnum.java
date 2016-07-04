package com.skype.jenkins.dto;

import java.lang.reflect.Type;

import com.skype.jenkins.notifiers.NotifierBuildStatusChanged;
import com.skype.jenkins.notifiers.NotifierBuildStillRed;
import com.skype.jenkins.notifiers.NotifierEachBuildStatus;

public enum NotifyTypeEnum {
    statusOfEachBuild(NotifierEachBuildStatus.class),
    buildStatusChanged(NotifierBuildStatusChanged.class),
    buildStillRed(NotifierBuildStillRed.class),
    buildFrozen(null),
    dailyReport(null);

    private Type type;

    NotifyTypeEnum(Type notifier) {
        this.type = notifier;
    }

    public Type getImplementingClass() {
        return type;
    }

}