package com.bastiarts.blockz.entities.draw.Requests;

import com.bastiarts.blockz.entities.draw.DrawUser;

import java.util.ArrayList;
import java.util.List;

public class DrawLobbyRequest extends DrawRequest {
    private String lobbyID;
    private List<DrawUser> lobbyMembers = new ArrayList<>();

    public DrawLobbyRequest(String type) {
        super(type);
    }

    public DrawLobbyRequest(String type, String lobbyID, List<DrawUser> lobbyMembers) {
        super(type);
        this.lobbyID = lobbyID;
        this.lobbyMembers = lobbyMembers;
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
}
