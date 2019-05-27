package com.bastiarts.blockz.repository;

import com.bastiarts.blockz.entities.BlockzUser;
import com.bastiarts.blockz.entities.Game;
import com.bastiarts.blockz.entities.StatusMessage;
import com.bastiarts.blockz.util.ConsoleColor;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class BlockzRepo {

    // Singleton
    private static BlockzRepo instance = null;
    // To store all online Players & Games
    private List<BlockzUser> users = Collections.synchronizedList(new ArrayList<BlockzUser>());
    private List<Game> games = Collections.synchronizedList(new CopyOnWriteArrayList<Game>());
    private String[] colors = {"0xfeb74c", "0x1abc9c", "0x3b78a4"};

    public static BlockzRepo getInstance() {
        if (instance == null) {
            instance = new BlockzRepo();
        }
        return instance;
    }

    // Method to handle the income Requests e.g. Blockplacement etc.
    public void handleRequest(JSONObject request, Session session) {
        if (this.hasRequiredParams(request)) {
            switch (request.getString("type")) {
                case "createGame":
                    // Example: {"type": "createGame", "game": "MyFirstGame"}
                    // Check if the Game already exists
                    boolean check = false;
                    for (Game g : this.games) {
                        if (g.getGameID().equals(request.getString("game"))) {
                            check = true;
                            break;
                        }
                    }
                    if (check) {
                        this.sendStatusMessage(new StatusMessage(499, "Game already exists. Try another GameID"), session);
                    } else {
                        this.games.add(new Game(request.getString("game"))); // For status codes see StatusMessage class on the ClientSide
                        this.sendStatusMessage(new StatusMessage(199, "Game successfully created"), session);
                        System.out.println(ConsoleColor.SERVER + ConsoleColor.green() + "Game " + request.getString("game").toUpperCase() + " successfully created.");
                        this.joinGame(session, new Game(request.getString("game")));
                        this.refreshGameList(session);
                    }

                    break;
                // Request for updating the Movement, Playerposition etc.
                case "join":
                    this.joinGame(session, new Game(request.getString("game")));
                    break;
                case "update":
                    // TODO
                    this.notifyToGame(null, "UPDATE", request);
                    break;
                default:
                    break;
            }
        } else if (this.hasRequiredAuthParams(request)) {
            switch (request.getString("type")) {
                case "login":
                    // Example: {"type" : "login", "username": "BastiArts"}
                    this.updateUser(session, request.getString("username"));
                    break;
                default:
                    break;
            }
        } else {
            if (request.has("type") && request.getString("type").equalsIgnoreCase("getGames")) {
                // SEND GAME
                // RESPONSE {"type": "info", "games": ["ttt", "ttt"]}
                this.refreshGameList(session);
            }
        }
    }

    // Creates a new User and stores it
    public void addUser(BlockzUser user) {

        user.setColor(this.colors[(int) Math.floor(Math.random() * colors.length)]);
        System.out.println(user.getColor());
        this.users.add(user);
    }

    // Deletes a User, if disconnected
    public void removeUser(Session session) {
        this.users.removeIf(u -> u.getSession() == session);
    }

    // Updates a User --> Sets the Username
    private void updateUser(Session session, String username) {
        for (BlockzUser bu : this.users) {
            if (bu.getSession() == session) {
                bu.setUsername(username);
                System.out.println(ConsoleColor.SERVER + "Username successfully set! ");
            }
        }
    }

    private boolean hasRequiredParams(JSONObject object) {
        // Basic Params each Request should have
        // Example: {"type": "createGame", "game": "MyFirstGame"}
        return object.has("type") && object.has("game");
    }

    private boolean hasRequiredAuthParams(JSONObject object) {
        return object.has("type") && object.has("username");
    }

    private void joinGame(Session session, Game game) {
        for (BlockzUser bu : this.users) {
            if (bu.getSession() == session) {
                bu.setGame(game);
                this.notifyToGame(bu, "JOIN", null);
                System.out.println(ConsoleColor.GAME + bu.getUsername() + " joined the Game " + ConsoleColor.yellow() + game.getGameID() + ConsoleColor.reset());
            }
        }
    }

    /**
     * @param user       - User
     * @param notifyType - Can be JOIN, LEAVE, Maybe CHAT and most important UPDATE (if a Block is placed)
     */
    private void notifyToGame(BlockzUser user, String notifyType, JSONObject request) {
        JSONObject obj = new JSONObject();
        switch (notifyType) {
            case "JOIN":
                for (BlockzUser bu : this.users) {
                    if (bu.getGame() == user.getGame()) {
                        obj.put("type", "info");
                        obj.put("message", bu.getUsername() + " joined the Game.");
                        bu.getSession().getAsyncRemote().sendText(obj.toString());
                        break;
                    }
                }
                break;
            case "LEAVE":
                for (BlockzUser bu : this.users) {
                    if (bu.getGame() == user.getGame()) {
                        obj.put("type", "info");
                        obj.put("message", bu.getUsername() + " left the Game.");
                        bu.getSession().getAsyncRemote().sendText(obj.toString());
                        break;
                    }
                }
                break;
            case "UPDATE":
                if (request.get("players") != null && request.get("cubes") != null && request.get("game") != null) {
                    for (BlockzUser bu : this.users) {
                        if (bu != null) {
                            if (bu.getGame().getGameID().equalsIgnoreCase(request.getString("game"))) {
                                bu.getSession().getAsyncRemote().sendText(request.toString());
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void refreshGameList(Session session) {
        JSONObject obj = new JSONObject();
        obj.put("type", "games");
        obj.put("games", this.games.toArray());
        // session.getAsyncRemote().sendText(obj.toString());

        // Broadcast to all Users without a Game
        for (BlockzUser user : this.users) {
            if (!user.hasGame()) {
                user.getSession().getAsyncRemote().sendText(obj.toString());
            }
        }
    }

    private void sendStatusMessage(StatusMessage statusMessage, Session session) {
        session.getAsyncRemote().sendText(new JSONObject().put("type", "status").put("code", statusMessage.getStatusCode()).put("message", statusMessage.getStatusMessage()).toString());
    }
}
