package com.bastiarts.blockz.entities;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private String gameID;
    private List<GameSettings> settings = new ArrayList<>();

    public Game(String gameID) {
        this.gameID = gameID;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public List<GameSettings> getSettings() {
        return settings;
    }

    public void setSettings(List<GameSettings> settings) {
        this.settings = settings;
    }
}
