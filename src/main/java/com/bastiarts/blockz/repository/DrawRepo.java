package com.bastiarts.blockz.repository;

import com.bastiarts.blockz.entities.StatusMessage;
import com.bastiarts.blockz.entities.draw.DrawGame;
import com.bastiarts.blockz.entities.draw.DrawUser;
import com.bastiarts.blockz.entities.draw.Requests.*;
import com.bastiarts.blockz.util.ConsoleColor;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawRepo {
    private static DrawRepo instance = null;
    private List<DrawUser> users = Collections.synchronizedList(new ArrayList<>());
    private List<DrawGame> games = Collections.synchronizedList(new ArrayList<>());

    public static DrawRepo getInstance() {
        if (instance == null) {
            instance = new DrawRepo();
        }
        return instance;
    }

    public void handleRequest(DrawRequest request, Session session) {
        // Set the username
        if (request instanceof DrawLoginRequest) {
            this.updateUser((DrawLoginRequest) request, session);
        } else if (request instanceof DrawLobbyRequest) {
            // LOBBY
            this.handleLobbyRequest((DrawLobbyRequest) request, session);
        } else if (request instanceof DrawGameRequest) {
            // GAME (For the current drawer or the updating Views)
            this.handleGameRequests((DrawGameRequest) request, session);
        } else if (request instanceof DrawChatRequest) {
            // Chat for the guesses of the current game
            this.handleChatRequests((DrawChatRequest) request, session);
        } else if (request instanceof DrawGameViewRequest) {
            // Broadcast all the available Games
            this.handleGameList((DrawGameViewRequest) request, session);

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

    private DrawUser findUserBySession(Session s) {
        for (DrawUser du : this.users) {
            if (du.getSession() == s) {
                return du;
            }
        }
        return new DrawUser(s);
    }

    // -- REQUEST HANDLERS --

    // -= Lobby-Handler =-
    private void handleLobbyRequest(DrawLobbyRequest drl, Session session) {
        switch (drl.getType()) {
            case "createGame":
                this.createLobby(drl, session);
                break;
            case "modifyLobby":

                break;
            case "joinLobby":
                DrawUser user = this.findUserBySession(session);
                user.setGame(new DrawGame(drl.getLobbyID()));
                // TODO Notify the join to others
                this.notifyToGame(user, "JOIN", null);
                System.out.println(ConsoleColor.GAME + user.getUsername() + " joined the Game " + ConsoleColor.yellow() + drl.getLobbyID() + ConsoleColor.reset());
                break;
            default:
                break;
        }

    }

    // -= Game-Handler =-
    private void handleGameRequests(DrawGameRequest dgr, Session session) {

    }

    // -= Chat-Handler =-
    private void handleChatRequests(DrawChatRequest hcr, Session session) {

    }

    // -= GameList-Handler =-
    private void handleGameList(DrawGameViewRequest dgvr, Session session) {


        for (DrawUser du : this.users) {
            if (!du.hasGame()) {
                du.getSession().getAsyncRemote().sendObject(dgvr);
            }
        }
    }

    private void sendStatusMessage(StatusMessage statusMessage, Session session) {
        session.getAsyncRemote().sendText(new JSONObject().put("type", "status").put("code", statusMessage.getStatusCode()).put("message", statusMessage.getStatusMessage()).toString());
    }

    private void createLobby(DrawLobbyRequest drl, Session session) {
        boolean exists = false;
        for (DrawGame dg : this.games) {
            if (dg.getGameID().equalsIgnoreCase(drl.getLobbyID())) {
                exists = true;
            }
        }
        if (!exists) {
            this.games.add(new DrawGame(this.findUserBySession(session), drl.getLobbyID()));
            this.sendStatusMessage(new StatusMessage(199, "Game successfully created"), session);
            System.out.println(ConsoleColor.SERVER + ConsoleColor.green() + "DRAW: Game " + drl.getLobbyID().toUpperCase() + " successfully created.");
            drl.getLobbyMembers().add(this.findUserBySession(session));
        } else {
            this.sendStatusMessage(new StatusMessage(499, "Game already exists. Try another GameID"), session);
        }
    }

    private void notifyToGame(DrawUser user, String notifyType, DrawRequest request) {
        switch (notifyType) {
            case "JOIN":
                for (DrawUser u : this.users) {
                    if (u.getSession() != user.getSession()) {
                        if (u.getGame() == user.getGame()) {
                            u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "info").put("message", user.getUsername() + " joined the Game").toString());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
