/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bastiarts.blockz.endpoint;

import com.bastiarts.blockz.repository.BlockzRepo;
import com.bastiarts.blockz.util.ConsoleColor;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


@ServerEndpoint("/blockz")
public class GameEndpoint {
    private BlockzRepo repo = BlockzRepo.getInstance();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println(ConsoleColor.yellow() + session.getId() + ConsoleColor.green() + " connected" + ConsoleColor.reset());
    }

    @OnMessage
    public void handleRequests(String request, Session session) {
        try {
            this.repo.handleRequest(new JSONObject(request), session);
        } catch (JSONException e) {
            System.out.println(ConsoleColor.SERVER + "Invalid JSON Format!");
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println(ConsoleColor.SERVER + session.getId() + " Connection closed...");
    }
}