package com.skype.jenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.samczsun.skype4j.internal.StreamUtils;
import com.skype.jenkins.dto.ConfigDTO;
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
                if (Boolean.parseBoolean(System.getProperty("remote.control"))) {
                    skype.getEventDispatcher().registerListener(new MessageListener());
                    skype.subscribe();
                }
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

    private static class MessageListener implements Listener {

        @EventHandler
        public void onMessage(final MessageReceivedEvent e) throws ConnectionException  {
            if (e.getMessage().getContent().asPlaintext().startsWith("//")) {
                Logger.out.info("COMMAND FROM "+e.getMessage().getSender().getDisplayName()+": " + e.getChat().getIdentity() + "(" + ((GroupChat) e.getChat()).getTopic() + ")\n" + e.getMessage().getContent());
                sendSkype(execCommands(e.getMessage().getContent().asPlaintext().substring(2)), e.getChat().getIdentity());
            }

        }

        private String execCommands(final String command) {
            StringBuilder output = new StringBuilder();
            switch (command) {
            case "help":
                output.append("//jobs - list of job to listeners").append("\n");
                output.append("//jenkins - jenkins host(s) to listen").append("\n");
                output.append("//stop").append("\n");
                output.append("//start").append("\n");
                output.append("//reload(restart) - reload config file").append("\n");
                output.append("//status - reload config file").append("\n");
                break;
            case "jobs":
                output.append(RunNotification.getConfiguration().stream().flatMap(conf -> conf.getJobs().stream()).map(jobConf -> jobConf.getInfo().getJobName()).collect(Collectors.joining("\n")));
                break;
            case "jenkins":
                output.append(RunNotification.getConfiguration().stream().map(ConfigDTO::getJenkinsUrl).collect(Collectors.joining("\n")));
                break;
            case "reload":
            case "restart":
                output.append(RunNotification.stop());
                output.append("\n");
                output.append(RunNotification.start());
                break;
            case "stop":
                output.append(RunNotification.stop());
                break;
            case "start":
                output.append(RunNotification.start());
                break;
            case "status":
                output.append(RunNotification.status());
                break;
            default: output.append("No such command. Use:\n").append(execCommands("help"));
            }

            return "".equals(output.toString())?"No results found":output.toString();
        }
    }



}
