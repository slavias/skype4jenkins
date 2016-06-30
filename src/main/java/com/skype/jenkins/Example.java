package com.skype.jenkins;

import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import com.samczsun.skype4j.Skype;
import com.samczsun.skype4j.SkypeBuilder;
import com.samczsun.skype4j.chat.GroupChat;
import com.samczsun.skype4j.events.EventHandler;
import com.samczsun.skype4j.events.Listener;
import com.samczsun.skype4j.events.chat.ChatEvent;
import com.samczsun.skype4j.events.chat.message.MessageEvent;
import com.samczsun.skype4j.events.chat.sent.PictureReceivedEvent;
import com.samczsun.skype4j.events.chat.user.action.OptionUpdateEvent;
import com.samczsun.skype4j.events.chat.user.action.PictureUpdateEvent;
import com.samczsun.skype4j.events.chat.user.action.RoleUpdateEvent;
import com.samczsun.skype4j.events.chat.user.action.TopicUpdateEvent;
import com.samczsun.skype4j.events.contact.ContactRequestEvent;
import com.samczsun.skype4j.exceptions.ConnectionException;
import com.samczsun.skype4j.formatting.Message;
import com.samczsun.skype4j.internal.StreamUtils;
/**
 * Skype example
 */
public class Example {
    public static void main(String[] args) throws Exception {
        System.out.println(String.valueOf(null));
        try {
            String[] data = StreamUtils.readFully(new FileInputStream("credentials")).split(":");
            Skype skype = new SkypeBuilder(data[0], data[1]).withAllResources().build();
            skype.login();
            System.out.println("Logged in");
            
            skype.getEventDispatcher().registerListener(new Listener() {
                @EventHandler
                public void onMessage(MessageEvent e) throws ConnectionException {
                    //System.out.println("Message: " + e.getMessage().getContent() + " sent by " + e.getMessage().getSender().getDisplayName());
                    if (MessageEvent.class.isInstance(e)){
                        System.out.println("Message: " + e.getMessage().getContent() + " sent in chat" + ((GroupChat) e.getChat()).getTopic() + " by " + e.getChat().getAllMessages().get(0).getSender().getDisplayName());
                        Message.fromHtml(e.getMessage().getContent().toString());
                    }
                }
                
                @EventHandler
                public void onMessage(ChatEvent e) throws ConnectionException {
                    System.out.println(((GroupChat) e.getChat()).getIdentity() + " Message: " + e.getChat().getAllMessages().get(e.getChat().getAllMessages().size()-1).getContent().write() + " sent in chat" + ((GroupChat) e.getChat()).getTopic() + " by " + e.getChat().getAllMessages().get(0).getSender().getDisplayName());
                }

                @EventHandler
                public void onMessage(PictureReceivedEvent e) {
                    try {
                        System.out.println("Picture: " + e.getOriginalName() + " sent by " + e.getSender().getDisplayName());
                        System.out.println("Saving to " + new File(e.getOriginalName()).getCanonicalPath());
                        ImageIO.write(e.getSentImage(), "png", new File(e.getOriginalName()));
                    } catch (Exception e1) {
                    }
                }

                @EventHandler
                public void onPicture(PictureUpdateEvent event) {
                    System.out.println("Picture for " + event.getChat().getIdentity() + " was set to " + event.getPictureURL() + " at " + event.getEventTime() + " by " + event.getUser().getUsername());
                }

                @EventHandler
                public void onTopic(TopicUpdateEvent event) {
                    System.out.println("Topic for " + event.getChat().getIdentity() + " was set to " + event.getNewTopic() + " at " + event.getEventTime() + " by " + event.getUser().getUsername());
                }

                @EventHandler
                public void onOption(OptionUpdateEvent event) {
                    System.out.println(event.getOption() + " was set to " + event.isEnabled() + " at " + event.getEventTime());
                }

                @EventHandler
                public void onRole(RoleUpdateEvent event) {
                    System.out.println("Role for " + event.getTarget().getUsername() + " was set to " + event.getNewRole() + " at " + event.getEventTime() + " by " + event.getUser().getUsername());
                }

                @EventHandler
                public void onContact(ContactRequestEvent event) throws ConnectionException {
                    System.out.println("New contact request from " + event.getRequest().getSender().getUsername() + " at " + event.getRequest().getTime() + " with message " + event.getRequest().getMessage());
                }

                /*@EventHandler
                public void onError(MinorErrorEvent error) {
                    System.out.println("Uh oh. A minor error occured");
                    error.getError().printStackTrace();
                }*/

            });
            skype.subscribe();
            System.out.println("Subscribed");
            GroupChat cc = (GroupChat) skype.loadChat("19:ddd3efae2f054251a58c1bc1ed935e41@thread.skype");

            /*long ms = System.currentTimeMillis();

            JsonObject obj = new JsonObject();
            obj.add("content", "(y) text\ntext");
            obj.add("messagetype", "60");
            obj.add("contenttype", "text");
            obj.add("clientmessageid", String.valueOf(ms));

            
            Endpoints.SEND_MESSAGE_URL
            .open((SkypeImpl) skype, cc.getIdentity()).expect(201, "While sending message").post(obj);*/
            
            /*JsonObject obj = new JsonObject();
            obj.add("me", obj1);
            (false ? Endpoints.CONVERSATION_PROPERTY_GLOBAL : Endpoints.CONVERSATION_PROPERTY_SELF)
                    .open((SkypeImpl) skype, cc.getIdentity(), "me")
                    .expect(200, "While updating option")
                    .put(obj);*/
            
            
            cc.sendMessage("(y) text\ntext");
            //skype.loadChat("19:4fe5b03a54ab429f9e561cc2f969564d@thread.skype").getAllUsers().iterator().next().getContact().getUsername();
            //skype.getContact("klimapro").getPrivateConversation().sendImage(skype.getContact("klimapro").getAvatarPicture(), "jpg", "your profile image");
            //skype.getContact("klimapro").getPrivateConversation().sendMessage(Message.create().with(Text.rich("Send your profile image with Skype4J").withBold()));
            //GroupChat groupChat = (GroupChat) skype.createGroupChat(skype.getOrLoadContact("echo123"));
            //groupChat.sendMessage("Hello!");
           // groupChat.sendMessage(Message.create().with(Text.rich("Created with Skype4J").withBold()));
            //groupChat.setTopic("Topic!");
            //groupChat.setOptionEnabled(OptionUpdateEvent.Option.HISTORY_DISCLOSED, true);
            //groupChat.setOptionEnabled(OptionUpdateEvent.Option.JOINING_ENABLED, true);
            //groupChat.getUser("echo123").setRole(User.Role.ADMIN);
            //Thread.sleep(1000);
            //groupChat.kick("echo123");
            //System.out.println("Join url: " + groupChat.getJoinUrl());
            //Thread.sleep(1000);
            skype.logout();
            System.out.println("Logged out");
        } catch (ConnectionException e) {
            System.out.println(e.getMessage() + ", " + e.getResponseCode() + ", " + e.getResponseMessage());
            e.printStackTrace();
        }
        
    }
}
