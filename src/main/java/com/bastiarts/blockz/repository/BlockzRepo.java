package com.bastiarts.blockz.repository;

import com.bastiarts.blockz.entities.BlockzUser;
import com.bastiarts.blockz.entities.Game;
import com.bastiarts.blockz.util.ConsoleColor;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BlockzRepo {

    // Singleton
    private static BlockzRepo instance = null;
    // To store all online Players
    private List<BlockzUser> users = Collections.synchronizedList(new ArrayList<BlockzUser>());

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
                    // TODO: ADD GAME CODE
                    // Example: {"type": "createGame", "game": "MyFirstGame"}
                    System.out.println(ConsoleColor.SERVER + ConsoleColor.green() + "Game " + request.getString("game").toUpperCase() + " successfully created.");
                    this.joinGame(session, new Game(request.getString("game")));
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
        }
    }

    // Creates a new User and stores it
    public void addUser(BlockzUser user) {
        this.users.add(user);
    }

    // Deletes a User, if disconnected
    public void removeUser(Session session) {
        this.users.removeIf(u -> u.getSession() == session);
    }

    private void updateUser(Session session, String username) {
        for (BlockzUser bu : this.users) {
            if (bu.getSession() == session) {
                bu.setUsername(username);
                System.out.println(ConsoleColor.SERVER + "Username successful set! ");
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
                this.notifyToGame(bu, "JOIN");
                System.out.println(ConsoleColor.GAME + bu.getUsername() + " joined the Game " + ConsoleColor.yellow() + game.getGameID() + ConsoleColor.reset());
            }
        }
    }

    /**
     * @param user       - User
     * @param notifyType - Can be JOIN, LEAVE, Maybe CHAT and most important UPDATE (if a Block is placed)
     */
    private void notifyToGame(BlockzUser user, String notifyType) {
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
                break;
            default:
                break;
        }
    }
}
