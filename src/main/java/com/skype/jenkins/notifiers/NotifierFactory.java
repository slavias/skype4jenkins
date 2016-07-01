package com.skype.jenkins.notifiers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.skype.jenkins.dto.ConfigJobDTO.NotifyDTO;

public class NotifierFactory {

    public static List<INotifier> registerNotifiersForJob(final String jobName) {
        List<INotifier> neededNotifiers = new ArrayList<>();
        Configuration conf = new Configuration(jobName);
        for (NotifyDTO dto : conf.getJobConfig().getNotify()) {
            conf.setNotifierType(dto.getType());
            try {
                Type clazz = dto.getType().getImplementingClass();
                Constructor<?> ctor = Class.forName(clazz.getTypeName()).getConstructor(Configuration.class);
                Object object = ctor.newInstance(conf);
                neededNotifiers.add((INotifier) object);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return neededNotifiers;
    }
}