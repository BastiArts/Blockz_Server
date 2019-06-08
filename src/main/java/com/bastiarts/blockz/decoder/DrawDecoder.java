package com.bastiarts.blockz.decoder;

import com.bastiarts.blockz.entities.draw.Requests.DrawGameViewRequest;
import com.bastiarts.blockz.entities.draw.Requests.DrawLobbyRequest;
import com.bastiarts.blockz.entities.draw.Requests.DrawLoginRequest;
import com.bastiarts.blockz.entities.draw.Requests.DrawRequest;
import org.json.JSONObject;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class DrawDecoder implements Decoder.Text<DrawRequest> {
    @Override
    public DrawRequest decode(String s) throws DecodeException {
        JSONObject jso = new JSONObject(s);
        DrawRequest request;
        switch (jso.getString("type")) {
            case "login":
                request = new DrawLoginRequest(jso.getString("type"), jso.getString("username"));
                break;
            case "getGames":
                request = new DrawGameViewRequest(jso.getString("type"));
                break;
            case "createGame":
                // Example: {"type": "createGame", "lobbyID": "BastiGame", "maxPlayers": "2" --> 0 for unlimited Players
                request = new DrawLobbyRequest(jso.getString("type"), jso.getString("lobbyID"));
                break;
            case "joinLobby":
                request = new DrawLobbyRequest(jso.getString("type"), jso.getString("lobbyID"));
                break;
            case "leaveGame":
                request = new DrawLobbyRequest(jso.getString("type"), jso.getString("lobbyID"));
                break;
            default:
                System.out.println("not Decode");
                throw new DecodeException(s, "Could not decode!");
        }
        return request;
    }

    @Override
    public boolean willDecode(String s) {
        JSONObject jso = new JSONObject(s);
        return jso.has("type");
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
