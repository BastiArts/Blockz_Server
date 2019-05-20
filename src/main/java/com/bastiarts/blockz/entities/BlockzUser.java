package com.bastiarts.blockz.entities;

import javax.websocket.Session;

public class BlockzUser {

    private String username;
    private Session session;
    private Game game;

    public BlockzUser() {

    }

    /**
     * @param username - Username of the Player
     * @param session  - current game session
     * @param game     - contains the current Server the Player is playing
     */
    public BlockzUser(final String username, final Session session, final Game game) {
        this.username = username;
        this.session = session;
        this.game = game;
    }

    public BlockzUser(final Session session) {
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

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    // Returns true/false if the player is in a game
    public boolean hasGame() {
        return this.game != null;
    }
}
