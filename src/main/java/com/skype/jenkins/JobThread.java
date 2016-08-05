package com.skype.jenkins;

import java.util.List;

import com.skype.jenkins.dto.ConfigJobDTO;
import com.skype.jenkins.logger.Logger;
import com.skype.jenkins.notifiers.Notifier;
import com.skype.jenkins.notifiers.NotifierFactory;

import static com.skype.jenkins.logger.Logger.stackTrace;

public class JobThread implements Runnable {

    private final String jenkinsUrl;
    private final ConfigJobDTO jobConfig;

    private List<Notifier> notifiers;

    public JobThread(final String jenkinsUrl, final ConfigJobDTO jobConfig) {
        this.jobConfig = jobConfig;
        this.jenkinsUrl = jenkinsUrl;
        notifiers = NotifierFactory.registerNotifiersForJob(jenkinsUrl,jobConfig);
    }

    @Override
    public void run() {
        try {
            Thread.currentThread().setName(jobConfig.getInfo().getName() + " id " + Thread.currentThread().getId());
            Logger.out.debug("---triggered---");
            notifiers.forEach(Notifier::composeSendNotifications);
        } catch (Throwable e) {
            Logger.out.error("Caught exception in ScheduledExecutorService, see stacktrace" + stackTrace(e));
        }
    }
}
