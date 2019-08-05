package com.bastiarts.blockz.entities;

public class BlockzPlayer extends BlockzUser {
    private String game;
    private double height;
    private double turnSpeed;
    private double speed;
    private Object position;
    private String color;

    public BlockzPlayer(BlockzUser user) {
        super.username = user.getUsername();
    }

    public BlockzPlayer() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGameID() {
        return game;
    }

    public void setGameID(String game) {
        this.game = game;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getTurnSpeed() {
        return turnSpeed;
    }

    public void setTurnSpeed(double turnSpeed) {
        this.turnSpeed = turnSpeed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Object getPosition() {
        return position;
    }

    public void setPosition(Object position) {
        this.position = position;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
