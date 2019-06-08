package com.bastiarts.blockz.entities.draw.Requests;

public class DrawGameRequest extends DrawRequest {
    private String gameID;

    public DrawGameRequest(String type) {
        super(type);
    }


    public DrawGameRequest(String type, String gameID) {
        super(type);
        this.gameID = gameID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}
