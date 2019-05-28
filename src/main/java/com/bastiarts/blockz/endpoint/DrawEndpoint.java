package com.bastiarts.blockz.endpoint;

import com.bastiarts.blockz.decoder.DrawDecoder;
import com.bastiarts.blockz.encoder.DrawEncoder;
import com.bastiarts.blockz.entities.draw.Requests.DrawRequest;
import com.bastiarts.blockz.repository.DrawRepo;
import com.bastiarts.blockz.util.ConsoleColor;
import org.json.JSONException;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/draw", encoders = DrawEncoder.class, decoders = DrawDecoder.class)
public class DrawEndpoint {
    DrawRepo drawRepo = DrawRepo.getInstance();

    @OnOpen
    public void onOpen(Session session) throws IOException {
        this.drawRepo.addUser(session);
        System.out.println(ConsoleColor.yellow() + session.getId() + ConsoleColor.green() + " connected" + ConsoleColor.reset());
    }

    @OnMessage
    public void handleRequests(DrawRequest request, Session session) {
        try {
            this.drawRepo.handleRequest(request, session);
        } catch (JSONException e) {
            System.out.println(ConsoleColor.SERVER + "Invalid JSON Format!");
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Throwable t) {
        t.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
        this.drawRepo.removeUser(session);
        System.out.println(ConsoleColor.SERVER + session.getId() + " Connection closed...");
    }
}
