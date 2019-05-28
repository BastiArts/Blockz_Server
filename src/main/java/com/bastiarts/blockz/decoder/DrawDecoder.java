package com.bastiarts.blockz.decoder;

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
            default:
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
