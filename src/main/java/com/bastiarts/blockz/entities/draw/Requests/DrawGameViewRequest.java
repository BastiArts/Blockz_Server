package com.bastiarts.blockz.entities.draw.Requests;

import com.bastiarts.blockz.entities.draw.DrawGame;

import java.util.List;

public class DrawGameViewRequest extends DrawRequest {
    private List<DrawGame> games;

    public DrawGameViewRequest(String type) {
        super(type);
    }

    public List<DrawGame> getGames() {
        return games;
    }

    public void setGames(List<DrawGame> games) {
        this.games = games;
    }
}
