package com.bastiarts.blockz.repository;

import com.bastiarts.blockz.entities.StatusMessage;
import com.bastiarts.blockz.entities.draw.DrawGame;
import com.bastiarts.blockz.entities.draw.DrawPlayer;
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
    private List<DrawGame> availableGames = Collections.synchronizedList(new ArrayList<>());

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
        System.out.println(ConsoleColor.GAME + findUserBySession(session).getUsername() + ConsoleColor.red() + " left the Game " + ConsoleColor.yellow() + findUserBySession(session).getGameID() + ConsoleColor.reset());
        this.users.removeIf(u -> u.getSession() == session);
        //this.notifyToGame(findUserBySession(session), "LEAVE", null);
    }

    private void updateUser(DrawLoginRequest dlr, Session s) {

        for (DrawUser du : this.users) {
            if (du.getSession() == s) {
                du.setUsername(dlr.getUsername());
                System.out.println("Username set: " + dlr.getUsername());
            }
        }
        s.getAsyncRemote().sendText(new JSONObject().put("receiveSession", s.getId()).toString());
    }

    private DrawUser findUserBySession(Session s) {
        for (DrawUser du : this.users) {
            if (du.getSession() == s) {
                return du;
            }
        }
        return new DrawUser(s);
    }

    private DrawGame findGameByID(String gameID) {
        for (DrawGame g : this.games) {
            if (g.getGameID().equalsIgnoreCase(gameID)) {
                return g;
            }
        }
        return new DrawGame(gameID);
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
                        user.setGameID(g.getGameID());
                        this.users.removeIf(u -> u.getSession() == user.getSession());
                        this.users.add(user);
                        g.getPlayers().add(new DrawPlayer(user.getSession().getId(), user.getUsername()));
                        break;
                    }
                }

                // TODO Notify the join to others
                this.notifyToGame(user, "JOIN", null);
                System.out.println(ConsoleColor.GAME + user.getUsername() + " joined the Game " + ConsoleColor.yellow() + drl.getLobbyID() + ConsoleColor.reset());
                break;
            case "leaveGame":
                DrawUser us = this.findUserBySession(session);
                for (DrawGame g : this.games) {
                    if (g.getGameID().equalsIgnoreCase(drl.getLobbyID())) {
                        us.setGameID(g.getGameID());
                        g.getPlayers().removeIf(p -> p.getSessionID().equalsIgnoreCase(session.getId()));
                        break;
                    }
                }
                this.notifyToGame(us, "LEAVE", null);
                System.out.println(ConsoleColor.GAME + findUserBySession(session).getUsername() + ConsoleColor.red() + " left the Game " + ConsoleColor.yellow() + drl.getLobbyID() + ConsoleColor.reset());
                break;
            default:
                break;
        }

    }

    // -= Game-Handler =-
    private DrawGame gameToRemove = new DrawGame("");
    private void handleGameRequests(DrawGameRequest dgr, Session session) {

        if (dgr.getType().equalsIgnoreCase("startGame")) {
            for (DrawGame dg : this.games) {
                for (DrawPlayer dp : dg.getPlayers()) {
                    if (dp.getSessionID().equalsIgnoreCase(session.getId())) {
                        gameToRemove = dg;
                        break;
                    }
                }
            }
            this.availableGames.removeIf(g -> g.getGameID().equalsIgnoreCase(gameToRemove.getGameID()));
            notifyToGame(findUserBySession(session), "START", null);
            this.refreshGameList(session);
        }
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
            DrawUser tmpU = this.findUserBySession(session);
            tmpGame.getPlayers().add(new DrawPlayer(tmpU.getSession().getId(), tmpU.getUsername()));
            tmpU.setGameID(tmpGame.getGameID());
            this.users.removeIf(u -> u.getSession() == tmpU.getSession());
            this.users.add(tmpU);
            this.games.add(tmpGame);
            availableGames = games;
            this.refreshGameList(session);
            this.notifyToGame(tmpU, "JOIN", null);
        } else {
            this.sendStatusMessage(new StatusMessage(499, "Game already exists. Try another GameID"), session);
        }

    }

    private void refreshGameList(Session session) {
        JSONObject obj = new JSONObject();
        obj.put("type", "games");


        obj.put("games", this.availableGames.toArray());
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
                    if (u.getGameID().equalsIgnoreCase(user.getGameID())) {
                        u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "join").put("game", new JSONObject(findGameByID(u.getGameID()))).toString());
                        }
                }
                break;
            case "LEAVE":
                for (DrawUser u : this.users) {
                    if (u.getGameID().equalsIgnoreCase(user.getGameID())) {
                        this.users.removeIf(us -> us.getSession() == user.getSession());
                        u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "join").put("game", new JSONObject(findGameByID(u.getGameID()))).toString());
                    }
                }
                this.users.removeIf(u -> u.getSession() == user.getSession());
                break;
            case "START":
                for (DrawUser u : this.users) {
                    if (u.getGameID().equalsIgnoreCase(user.getGameID())) {
                        u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "start").toString());
                    }
                }
                break;
            default:
                break;
        }
    }

}
