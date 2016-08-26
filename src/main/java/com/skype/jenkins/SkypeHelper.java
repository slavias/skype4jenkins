package com.skype.jenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.samczsun.skype4j.internal.StreamUtils;
import com.skype.jenkins.logger.Logger;

import static com.skype.jenkins.logger.Logger.stackTrace;

public class SkypeHelper {

    private static Skype skype;

    private SkypeHelper() {
    }

    public synchronized static Skype getSkype() {
        if (Objects.isNull(skype)) {
            try {
                //TODO change credentials input
                String[] data = StreamUtils.readFully(new FileInputStream("credentials")).split(":");
                skype = new SkypeBuilder(data[0], data[1]).withAllResources()
                        .withLogger(java.util.logging.Logger.getLogger("skypeLogger")).build();
                skype.login();
            } catch (InvalidCredentialsException | ConnectionException | NotParticipatingException | IOException e) {
               Logger.out.error("Caught exception getSkype()  " + stackTrace(e));
            }
        }
        return skype;
    }

    private synchronized static void reinitializeSkype() {
            Logger.out.info("Reinitialize Skype");
            skype = null;
            getSkype();
          Logger.out.info("Reinitialize Skype Finished");
    }

    public synchronized static void sendSkype(final String message, final String chatName) {
        IllegalStateException illegalStateException;
        int resendTry = 5;
        int i = 0;
        do {
            i++;
            illegalStateException = null;
            try {
                GroupChat groupChat = (GroupChat) getSkype().getOrLoadChat(chatName);
                Logger.out.info("SEND TO SKYPE: " + chatName + "(" + groupChat.getTopic() + ")\n" + message);
                groupChat.sendMessage(message);
            } catch (IllegalStateException ex) {
                illegalStateException = ex;
                reinitializeSkype();
            } catch (ConnectionException | ChatNotFoundException e) {
                Logger.out.error("sendSkype Exception " + stackTrace(e));
            }
        } while ((illegalStateException != null) && (i < resendTry));
    }

}
