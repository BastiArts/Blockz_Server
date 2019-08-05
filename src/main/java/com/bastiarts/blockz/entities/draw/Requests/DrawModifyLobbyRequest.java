package com.bastiarts.blockz.entities.draw.Requests;

public class DrawModifyLobbyRequest extends DrawRequest {
    private int maxPlayers;

    public DrawModifyLobbyRequest(String type) {
        super(type);
    }

    public DrawModifyLobbyRequest(String type, int maxPlayers) {
        super(type);
        this.maxPlayers = maxPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
