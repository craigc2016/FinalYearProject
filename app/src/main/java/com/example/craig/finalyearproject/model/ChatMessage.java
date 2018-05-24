package com.example.craig.finalyearproject.model;

import java.util.Date;

/**
 * This acts as the model class for the messages sent in
 * the message page. It has instance variables that are set
 * when an instance of the class is created and the fields
 * are set. It will contain the getter and setter methods needed.
 */

public class ChatMessage {
    /**
     * Declase the instance variables
     */
    private String messageText;
    private String messageUser;
    private long messageTime;

    /**
     * Constructor which takes arguments and sets to
     * instance variables of the class
     * @param messageText
     * @param messageUser
     */
    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
    
}
