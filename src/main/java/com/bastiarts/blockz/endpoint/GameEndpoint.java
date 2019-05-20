/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bastiarts.blockz.endpoint;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;


@ServerEndpoint("/echo")
public class GameEndpoint {

    @OnOpen
    public void onOpen(Session session) throws IOException {
        //session.getBasicRemote().sendText("Server: onOpen");
        System.out.println("Server: Connection opened...");
    }

    @OnMessage
    public String echo(String message) {
        System.out.println("Server: Message received: >" + message + "<");
        // send echo to client
        return message;
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Server: Connection closed...");
    }
}