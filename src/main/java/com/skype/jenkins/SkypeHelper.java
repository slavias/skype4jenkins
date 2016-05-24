package com.skype.jenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.chat.messages.ChatMessage;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.ChatEvent;
import com.samczsun.skype4j.exceptions.ChatNotFoundException;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.exceptions.InvalidCredentialsException;
import com.samczsun.skype4j.exceptions.NotParticipatingException;
import com.samczsun.skype4j.internal.SkypeImpl;
import com.samczsun.skype4j.internal.StreamUtils;
import com.skype.jenkins.logger.Logger;

import static com.skype.jenkins.logger.Logger.stackTrace;

public class SkypeHelper {

    private static Skype skype;

    private SkypeHelper() {
    }

    public synchronized static Skype getSkype() {
        if (Objects.isNull(skype))
            try {
                String[] data = StreamUtils.readFully(new FileInputStream("credentials")).split(":");
                skype = new SkypeBuilder(data[0], data[1]).withAllResources()
                        .withLogger(java.util.logging.Logger.getLogger("skypeLogger")).build();
                skype.login();
                if (Boolean.parseBoolean(System.getProperty("bot.active")))
                    subscribeBot();
            } catch (InvalidCredentialsException | ConnectionException | NotParticipatingException | IOException e) {
               Logger.out.error("Caught exception getSkype()  " + stackTrace(e));
            }

        return skype;

    }

    private synchronized static Skype reinitializeSkype() {
        try {
            Logger.out.info("Reinitialize Skype");
            ((SkypeImpl) skype).reauthenticate();
        } catch (InvalidCredentialsException | ConnectionException | NotParticipatingException e) {
            Logger.out.error("Caught exception reinitializeSkype()  " + stackTrace(e));
        }
        return skype;

    }

    public synchronized static void sendSkype(String message, String chatName) {
        IllegalStateException illegalStateException;
        int i = 0;
        do {
            i++;
            illegalStateException = null;
            try {
                GroupChat groupChat = (GroupChat) getSkype().getOrLoadChat(chatName);
                Logger.out.info("SEND TO SKYPE: " + chatName + "\n" + message);
                groupChat.sendMessage(message);
            } catch (IllegalStateException ex) {
                illegalStateException = ex;
                reinitializeSkype();
            } catch (ConnectionException | ChatNotFoundException e) {
                Logger.out.error("sendSkype Exception " + stackTrace(e));
            }
        } while (illegalStateException != null && (i < 10));
    }

    private static void subscribeBot() {
        skype.getEventDispatcher().registerListener(new Listener() {
            @EventHandler
            public void onMessage(ChatEvent e) throws ConnectionException {
                if (e.getChat().getAllMessages().isEmpty())
                    return;
                ChatMessage mess = e.getChat().getAllMessages().get(e.getChat().getAllMessages().size() - 1);
                String text = null;
                switch (mess.getContent().asPlaintext()) {
                case "Jenkins успокойся":
                case "успокойся":
                    text = mess.getSender().getUsername() + ", попух что ли?? (smoking)";
                    break;
                case "гори в аду":
                    text = mess.getSender().getUsername() + ", поджарь (poop) !!!";
                    break;
                case "проспись":
                case "иди спать":
                    text = mess.getSender().getUsername() + ", иди в (mooning) !!!";
                    break;
                case "сука":
                case "сучка":
                    text = mess.getSender().getUsername() + ",  хуй тебе в глаз (finger)";
                    break;
                case "падло":
                case "падла":
                    text = mess.getSender().getUsername() + ", мы же цивилизованные люди, хватит пиздеть (wait) !";
                    break;
                case "тварь":
                    text = mess.getSender().getUsername() + ", пшел ты к чертовой матери (neil)";
                    break;
                case "паскуда":
                    text = mess.getSender().getUsername() + ", щоб ти сказився (wtf)";
                    break;
                case "гамно":
                case "гавно":
                case "говно":
                    text = mess.getSender().getUsername() + ", курва (doh)";
                    break;
                // case "телега":text= mess.getSender().getUsername()+", ";break;
                case "нет":
                case "не":
                case "Нет":
                    text = mess.getSender().getUsername() + ", на \"нет\" и суда нет (ninja)";
                    break;
                case "сек":
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    text = mess.getSender().getUsername() + ", секунда прошла (emo)";
                    break;

                case "Вадим":
                case "Слава":
                    text = mess.getSender().getUsername() + ", я за него (drunk) !";
                    break;
                case "стоп":
                    text = "продолжаем, продолжаем, не слушаем " + mess.getSender().getUsername()
                            + ". вечно он что-то лишнее говорит (monkey)";
                    break;
                case "всем привет":
                case "Всем привет":
                case "привет":
                    text = "привет, " + mess.getSender().getUsername() + " (beer)";
                    break;
                case "всем пока":
                case "Всем пока":
                case "пока":
                    text = "пока, " + mess.getSender().getUsername() + " (wave)";
                    break;
                case "http://jenkins.marks.kyiv.epam.com/view/TAF/view/TAF_POS/job/08.2.3_run_test_suite_smoke_CI/thucydides/":
                case "http://jenkins.marks.kyiv.epam.com/view/TAF/view/TAF_POS/job/08.2.2_run_test_suite/thucydides/":
                    text = mess.getSender().getUsername() + ", сам смотри свой отчет (poolparty) !";
                    break;
                case "Спасибо":
                case "спс":
                case "спасибо)":
                case "(bow)":
                case "спасибо":
                    text = mess.getSender().getUsername() + ", всегда пожалуйста (fistbump) !";
                    break;
                case "done":
                case "доне":
                case "Доне":
                    text = mess.getSender().getUsername() + ", всегда знал что ты не подведешь (hug) !";
                    break;
                case "100 %":
                case "100%":
                    text = mess.getSender().getUsername() + ", сегодня тебе повезло... (slap) ";
                    break;

                default:
                    text = null;
                    break;

                }
                if (null != text) {
                    e.getChat().sendMessage(text);
                }
            }

        });
        try {
            skype.subscribe();
        } catch (ConnectionException e1) {
            Logger.out.error(e1);
        }
    }

}
