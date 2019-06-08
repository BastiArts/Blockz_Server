package com.bastiarts.blockz.entities.draw;

import javax.websocket.Session;

public class DrawUser {
    private String username;
    private Session session;
    private String gameID;

    public DrawUser(Session session) {
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public boolean hasGame() {
        return this.gameID != null;
    }

}
