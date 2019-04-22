package com.skype.jenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import com.eclipsesource.json.JsonObject;
import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.message.MessageReceivedEvent;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.samczsun.skype4j.exceptions.handler.ErrorHandler;
import com.samczsun.skype4j.exceptions.handler.ErrorSource;
import com.samczsun.skype4j.internal.Endpoints;
import com.samczsun.skype4j.internal.StreamUtils;
import com.samczsun.skype4j.internal.client.FullClient;
import com.samczsun.skype4j.internal.threads.AuthenticationChecker;
import com.samczsun.skype4j.internal.utils.Encoder;
import com.samczsun.skype4j.internal.utils.UncheckedRunnable;
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
                //temporary solution to ignore KeepaliveThread
                Set<String> res = new HashSet<String>();
                res.addAll(Arrays.asList("/v1/users/ME/conversations/ALL/properties",
                        "/v1/users/ME/conversations/ALL/messages", "/v1/users/ME/contacts/ALL", "/v1/threads/ALL"));
                skype = new Skype8(data[0], data[1], res, java.util.logging.Logger.getLogger("skypeLogger"), new ArrayList<>());
                //skype = new SkypeBuilder(data[0], data[1]).withAllResources()
                //        .withLogger(java.util.logging.Logger.getLogger("skypeLogger")).build();
                skype.login();
                //dead code. not verified for Skype8
                if ( false && Boolean.parseBoolean(System.getProperty("remote.control"))) {
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

    //temporary class to ignore KeepaliveThread
    public static class Skype8 extends FullClient {
        private final String password;

        public Skype8(final String username, final String password, final Set<String> resources,
                      final java.util.logging.Logger customLogger, final List<ErrorHandler> errorHandlers) {
            super(username, password, resources, customLogger, errorHandlers);
            this.password = password;

        }

        @Override
        public void login() throws InvalidCredentialsException, ConnectionException {
            Map<String, String> data = new HashMap<>();
            data.put("scopes", "client");
            data.put("clientVersion", "0/7.4.85.102/259/");
            data.put("username", username);
            data.put("passwordHash", hash());
            JsonObject loginData = Endpoints.LOGIN_URL.open(this).as(JsonObject.class).expect(200, "While logging in")
                    .post(Encoder.encode(data));
            this.setSkypeToken(loginData.get("skypetoken").asString());

            List<UncheckedRunnable> tasks = new ArrayList<>();
            tasks.add(this::registerEndpoint);
            tasks.add(() -> {
                HttpURLConnection asmResponse = getAsmToken();
                String[] setCookie = asmResponse.getHeaderField("Set-Cookie").split(";")[0].split("=");
                this.cookies.put(setCookie[0], setCookie[1]);
            });
            tasks.add(this::loadAllContacts);
            tasks.add(() -> this.getContactRequests(false));
            tasks.add(() -> {
                try {
                    this.registerWebSocket();
                } catch (Exception e) {
                    handleError(ErrorSource.REGISTERING_WEBSOCKET, e, false);
                }
            });

            try {
                ExecutorService executorService = Executors.newFixedThreadPool(5);
                tasks.forEach(executorService::submit);
                executorService.shutdown();
                executorService.awaitTermination(1, TimeUnit.DAYS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            loggedIn.set(true);
            // (sessionKeepaliveThread = new KeepaliveThread(this)).start();
            (reauthThread = new AuthenticationChecker(this)).start();
        }

        private String hash() {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                byte[] encodedMD = messageDigest.digest(String.format("%s\nskyper\n%s", username, password).getBytes(
                        StandardCharsets.UTF_8));
                return DatatypeConverter.printBase64Binary(encodedMD);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
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
                    output.append("//status").append("\n");
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
