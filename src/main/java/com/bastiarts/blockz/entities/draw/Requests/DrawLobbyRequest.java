package com.bastiarts.blockz.entities.draw.Requests;

import com.bastiarts.blockz.entities.draw.DrawUser;

import java.util.ArrayList;
import java.util.List;

public class DrawLobbyRequest extends DrawRequest {
    private String lobbyID;
    private List<DrawUser> lobbyMembers = new ArrayList<>();
    private Integer maxPlayers;
    public DrawLobbyRequest(String type) {
        super(type);
    }

    public DrawLobbyRequest(String type, String lobbyID) {
        super(type);
        this.lobbyID = lobbyID;
    }

    public String getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(String lobbyID) {
        this.lobbyID = lobbyID;
    }

    public List<DrawUser> getLobbyMembers() {
        return lobbyMembers;
    }

    public void setLobbyMembers(List<DrawUser> lobbyMembers) {
        this.lobbyMembers = lobbyMembers;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
