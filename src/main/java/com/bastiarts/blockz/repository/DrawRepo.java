package com.bastiarts.blockz.repository;

import com.bastiarts.blockz.entities.draw.DrawUser;
import com.bastiarts.blockz.entities.draw.Requests.*;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawRepo {
    private static DrawRepo instance = null;
    private List<DrawUser> users = Collections.synchronizedList(new ArrayList<>());

    public static DrawRepo getInstance() {
        if (instance == null) {
            instance = new DrawRepo();
        }
        return instance;
    }

    public void handleRequest(DrawRequest request, Session session) {
        if (request instanceof DrawLoginRequest) {
            this.updateUser((DrawLoginRequest) request, session);
        } else if (request instanceof DrawLobbyRequest) {
            // LOBBY
        } else if (request instanceof DrawGameRequest) {
            // GAME (For the current drawer)
        } else if (request instanceof DrawChatRequest) {
            // Chat for the guesses of the current game
        }
    }

    public void addUser(Session session) {
        this.users.add(new DrawUser(session));
    }

    public void removeUser(Session session) {
        this.users.removeIf(u -> u.getSession() == session);
    }

    private void updateUser(DrawLoginRequest dlr, Session s) {

        for (DrawUser du : this.users) {
            if (du.getSession() == s) {
                du.setUsername(dlr.getUsername());
                System.out.println("Username set: " + dlr.getUsername());
            }
        }
    }
}
