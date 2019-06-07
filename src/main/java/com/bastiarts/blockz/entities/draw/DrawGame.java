package com.bastiarts.blockz.entities.draw;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

public class DrawGame {
    private String hostID;
    private String gameID;
    private int maxPlayers;
    private List<DrawUser> players = new ArrayList<>();

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

    public List<DrawUser> getPlayers() {
        return players;
    }

    public void setPlayers(List<DrawUser> players) {
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
}
