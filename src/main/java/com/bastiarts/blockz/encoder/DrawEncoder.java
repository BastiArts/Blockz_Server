package com.bastiarts.blockz.encoder;

import com.bastiarts.blockz.entities.draw.Requests.DrawRequest;
import org.json.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class DrawEncoder implements Encoder.Text<DrawRequest> {
    @Override
    public String encode(DrawRequest object) throws EncodeException {
        String jso = new JSONObject(object).toString();
        System.out.println("Encoded: " + jso);
        return jso;
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
