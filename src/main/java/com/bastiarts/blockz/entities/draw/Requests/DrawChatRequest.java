package com.bastiarts.blockz.entities.draw.Requests;

public class DrawChatRequest extends DrawRequest {
    private String sender;
    private String message;
    public DrawChatRequest(String type) {
        super(type);
    }

    public DrawChatRequest(String type, String sender, String message) {
        super(type);
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
