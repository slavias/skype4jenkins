package com.skype.jenkins.dto;

import java.lang.reflect.Type;
import java.util.Arrays;

import com.skype.jenkins.notifiers.NotifierAborted;
import com.skype.jenkins.notifiers.NotifierBackNormal;
import com.skype.jenkins.notifiers.NotifierFailure;
import com.skype.jenkins.notifiers.NotifierStart;
import com.skype.jenkins.notifiers.NotifierStillRed;
import com.skype.jenkins.notifiers.NotifierSuccess;

public enum NotifyTypeEnum {
    start(NotifierStart.class),
    success(NotifierSuccess.class),
    failure(NotifierFailure.class),
    aborted(NotifierAborted.class),
    stillRed(NotifierStillRed.class),
    backNormal(NotifierBackNormal.class);

    private Type type;

    NotifyTypeEnum(final Type notifier) {
        this.type = notifier;
    }

    public Type getImplementingClass() {
        return type;
    }

    public static <T> NotifyTypeEnum getNotifierByClass(final Class<T> clazz) {
        return Arrays.asList(NotifyTypeEnum.values()).stream()
                .filter(notifier -> clazz.equals(notifier.getImplementingClass())).findFirst().orElse(null);

    }

}