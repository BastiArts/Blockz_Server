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
                for (DrawGame g : this.games) {
                    if (g.getGameID().equalsIgnoreCase(drl.getLobbyID())) {
                        user.setGame(g);
                        break;
                    }
                }

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
        this.refreshGameList(session);

    }

    private void sendStatusMessage(StatusMessage statusMessage, Session session) {
        session.getAsyncRemote().sendText(new JSONObject().put("type", "status").put("code", statusMessage.getStatusCode()).put("message", statusMessage.getStatusMessage()).toString());
    }

    private void createLobby(DrawLobbyRequest drl, Session session) {
        boolean exists = false;
        for (DrawGame dg : this.games) {
            if (dg.getGameID().equalsIgnoreCase(drl.getLobbyID())) {
                this.sendStatusMessage(new StatusMessage(499, "Game creation failed"), session);
                exists = true;
            }
        }
        if (!exists) {
            DrawGame tmpGame = new DrawGame(session, drl.getLobbyID());

            this.sendStatusMessage(new StatusMessage(199, "Game successfully created"), session);
            System.out.println(ConsoleColor.SERVER + ConsoleColor.green() + "DRAW: Game " + drl.getLobbyID().toUpperCase() + " successfully created.");
            drl.getLobbyMembers().add(this.findUserBySession(session));
            this.findUserBySession(session).setGame(new DrawGame(session, drl.getLobbyID()));
            DrawUser tmpU = this.findUserBySession(session);
            this.games.add(tmpGame);
            this.refreshGameList(session);
            tmpU.setGame(tmpGame);
            this.notifyToGame(tmpU, "JOIN", null);
        } else {
            this.sendStatusMessage(new StatusMessage(499, "Game already exists. Try another GameID"), session);
        }
    }

    private void refreshGameList(Session session) {
        JSONObject obj = new JSONObject();
        obj.put("type", "games");
        obj.put("games", this.games.toArray());
        // session.getAsyncRemote().sendText(obj.toString());

        // Broadcast to all Users without a Game
        for (DrawUser user : this.users) {
            if (!user.hasGame()) {
                user.getSession().getAsyncRemote().sendText(obj.toString());
            }
        }
    }
    private void notifyToGame(DrawUser user, String notifyType, DrawRequest request) {
        switch (notifyType.toUpperCase()) {
            case "JOIN":
                for (DrawUser u : this.users) {
                    if (u.getSession() != user.getSession()) {
                        if (u.getGame() == user.getGame()) {
                            //  u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "join").put("username", user.getUsername()).put("sessionID", user.getSession().getId()).put("game", user.getGame().getGameID()).toString());
                            u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "join").put("game", new JSONObject(user.getGame()).toString()).toString());
                        }
                    }
                }
                break;
            default:
                break;
        }
    }
}
