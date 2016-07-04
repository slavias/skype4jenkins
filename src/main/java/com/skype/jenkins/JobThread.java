package com.skype.jenkins;

import java.util.List;

import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.notifiers.INotifier;
import com.skype.jenkins.notifiers.NotifierFactory;

import static com.skype.jenkins.logger.Logger.stackTrace;

public class JobThread implements Runnable {

    private final String jobName;
    private List<INotifier> notifiers;

    public JobThread(String jobName) {
        this.jobName = jobName;
        notifiers = NotifierFactory.registerNotifiersForJob(jobName);
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(jobName + " id " + Thread.currentThread().getId());
            Logger.out.debug("---triggered---");
            notifiers.forEach(INotifier::composeSendNotifications);
        } catch (Throwable e) {
            Logger.out.error("Caught exception in ScheduledExecutorService, see stacktrace" + stackTrace(e));
        }
    }
}
