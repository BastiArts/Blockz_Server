package com.bastiarts.blockz.repository;

import com.bastiarts.blockz.entities.BlockzUser;
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

        }
    }

    // Creates a new User and stores it
    public void addUser(BlockzUser user) {
        this.users.add(user);
    }

    // Deletes a User, if disconnected
    public void removeUser(BlockzUser user) {
        this.users.remove(user);
    }

    private boolean hasRequiredParams(JSONObject object) {
        // Basic Params each Request should have
        // Example: {"type": "addGame", "game": "MyFirstGame"}
        return object.has("type") && object.has("game");
    }
}
