package com.bastiarts.blockz.entities.draw.Requests;

import com.bastiarts.blockz.entities.draw.DrawPlayer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DrawGameRequest extends DrawRequest {
    private String gameID;
    private Set<DrawPlayer> players = Collections.synchronizedSet(new HashSet<>());

    public DrawGameRequest(String type) {
        super(type);
    }

    public DrawGameRequest(String type, String gameID, Set<DrawPlayer> players) {
        super(type);
        this.gameID = gameID;
        this.players = players;
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

    public Set<DrawPlayer> getPlayers() {
        return players;
    }

    public void setPlayers(Set<DrawPlayer> players) {
        this.players = players;
    }
}
