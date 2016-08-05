package com.skype.jenkins.notifiers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;

public class NotifierFactory {

    public static List<Notifier> registerNotifiersForJob(final String jenkinsUrl, final ConfigJobDTO jobConfig) {
        List<Notifier> neededNotifiers = new ArrayList<>();
        for (NotifyDTO dto : jobConfig.getNotify()) {
            try {
                Type clazz = dto.getType().getImplementingClass();
                Constructor<?> ctor = Class.forName(clazz.getTypeName()).getConstructor(String.class, ConfigJobDTO.class);
                Object object = ctor.newInstance(jenkinsUrl, jobConfig);
                neededNotifiers.add((Notifier) object);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return neededNotifiers;
    }
}