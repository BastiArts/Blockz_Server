package com.bastiarts.blockz.entities.draw;

public class DrawPlayer {
    private String sessionID;
    private String username;

    public DrawPlayer(String sessionID) {

    }

    public DrawPlayer(String sessionID, String username) {
        this.sessionID = sessionID;
        this.username = username;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
