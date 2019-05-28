package com.bastiarts.blockz.entities.draw.Requests;

public class DrawRequest {
    private String type;

    public DrawRequest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
