package com.bastiarts.blockz.entities.draw.Requests;

public class DrawLoginRequest extends DrawRequest {
    private String username;

    public DrawLoginRequest(String type) {
        super(type);
    }

    public DrawLoginRequest(String type, String username) {
        super(type);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
