package com.returno.dynasty.admin.models;

public class Message {
    private int messageId;
    private String message, timeSent;

    public Message(int messageId, String message, String timeSent) {
        this.messageId = messageId;
        this.message = message;
        this.timeSent = timeSent;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeSent() {
        return timeSent;
    }
}
