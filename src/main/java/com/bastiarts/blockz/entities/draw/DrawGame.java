package com.bastiarts.blockz.entities.draw;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrawGame {
    private String hostID;
    private String gameID;
    private int maxPlayers;
    private List<DrawPlayer> players = new ArrayList<>();
    private String topic;
    private String drawer; // SESSION ID
    // User, who guessed the word
    private transient Set<String> guessedRight = new HashSet<>();

    public DrawGame(String gameID, int maxPlayers, Session host) {
        this.gameID = gameID;
        this.maxPlayers = maxPlayers;
        this.hostID = host.getId();
    }

    public DrawGame(Session host, String gameID) {
        this.hostID = host.getId();
        this.gameID = gameID;
    }

    public DrawGame(String gameID) {
        this.gameID = gameID;
    }

    public List<DrawPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(List<DrawPlayer> players) {
        this.players = players;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }


    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String host) {
        this.hostID = host;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDrawer() {
        return drawer;
    }

    public void setDrawer(String drawer) {
        this.drawer = drawer;
    }

    public Set<String> getGuessedRight() {
        return guessedRight;
    }

    public void setGuessedRight(Set<String> guessedRight) {
        this.guessedRight = guessedRight;
    }
}
