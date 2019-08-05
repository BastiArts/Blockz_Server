package com.bastiarts.blockz.entities;

public class Block {
    private BlockzPlayer owner;
    private Number color;
    private int x, y, z;

    public Block(BlockzPlayer owner, Number color, int x, int y, int z) {
        this.owner = owner;
        this.color = color;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BlockzPlayer getOwner() {
        return owner;
    }

    public void setOwner(BlockzPlayer owner) {
        this.owner = owner;
    }

    public Number getColor() {
        return color;
    }

    public void setColor(Number color) {
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
