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
    private List<DrawGame> games = new ArrayList<>();
    private List<DrawGame> availableGames = new ArrayList<>();

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

    // -- HELPER METHODS --

    // Returns the User found by the Session
    private DrawUser findUserBySession(Session s) {
        DrawUser tmpUser = new DrawUser(s);
        for (DrawUser du : this.users) {
            if (du.getSession() == s) {
                tmpUser = du;
                break;
            }
        }
        return tmpUser;
    }

    // Returns the Game found by the Name
    private DrawGame findGameByID(String gameID) {
        DrawGame tmpGame = new DrawGame("");
        for (DrawGame g : this.games) {
            if (g.getGameID().equalsIgnoreCase(gameID)) {
                tmpGame = g;
                break;
            }
        }
        return !tmpGame.getGameID().equalsIgnoreCase("") ? tmpGame : new DrawGame(gameID);
    }

    // Chooses the Drawer once a game has started
    private DrawPlayer chooseRandomDrawer(String gameID) {
        DrawGame game = findGameByID(gameID);
        return game.getPlayers().get(randomIndex(0, game.getPlayers().size() - 1));
    }

    private int randomIndex(final int from, final int to) {
        return (int) (Math.random() * ((to - from) + 1)) + from;
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
                        this.games.remove(g);
                        this.games.add(g);
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
        for (DrawUser u : this.users) {
            if (u.getGameID().equalsIgnoreCase(findUserBySession(session).getGameID())) {
                u.getSession().getAsyncRemote().sendObject(hcr);
               /* if(hcr.getMessage().equalsIgnoreCase(this.findGameByID(findUserBySession(session).getGameID()).getTopic())){
                    // Im DrawGame eine Variable Topic anlegen, die nach jeder Runde neu belegt wird. das Topic wird auch mitgeschickt.
                    // Topic = das zu erratene Wort

                    // WICHTIG!! DAS TOPIC NUR DEM DRAWER MITSCHICKEN (SERVERSIDE -> CLIENT)
                }*/
            }
        }
    }

    // -= GameList-Handler =-
    private void handleGameList(DrawGameViewRequest dgvr, Session session) {
        this.refreshGameList(session);

    }

    // Sends the Status message
    private void sendStatusMessage(StatusMessage statusMessage, Session session) {
        session.getAsyncRemote().sendText(new JSONObject().put("type", "status").put("code", statusMessage.getStatusCode()).put("message", statusMessage.getStatusMessage()).toString());
    }

    // Creates the Lobby of a Game
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
            this.availableGames.addAll(games);
            this.refreshGameList(session);
            this.notifyToGame(tmpU, "JOIN", null);
        } else {
            this.sendStatusMessage(new StatusMessage(499, "Game already exists. Try another GameID"), session);
        }

    }

    // Broadcasts all available Games to the connected Users
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

    // Notifies Events to a specific Game
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
                DrawPlayer drawPlayer = this.chooseRandomDrawer(user.getGameID());
                System.out.println(ConsoleColor.GAME + ConsoleColor.purple() + user.getGameID() + ConsoleColor.reset() + " " + "The new Drawer is: " + ConsoleColor.green() + drawPlayer.getUsername() + ConsoleColor.reset());
                for (DrawUser u : this.users) {
                    if (u.getGameID().equalsIgnoreCase(user.getGameID())) {
                        u.getSession().getAsyncRemote().sendText(new JSONObject().put("type", "start").put("drawer", drawPlayer.getSessionID()).toString());
                    }
                }
                break;
            default:
                break;
        }
    }

}
