package com.bastiarts.blockz.entities.draw;

public class DrawGame {
    private DrawUser host;
    private String gameID;
    private int maxPlayers;

    public DrawGame(String gameID) {
        this.gameID = gameID;
    }

    public DrawGame(String gameID, int maxPlayers, DrawUser host) {
        this.gameID = gameID;
        this.maxPlayers = maxPlayers;
        this.host = host;
    }

    public DrawGame(DrawUser host, String gameID) {
        this.host = host;
        this.gameID = gameID;
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

    public DrawUser getHost() {
        return host;
    }

    public void setHost(DrawUser host) {
        this.host = host;
    }
}
