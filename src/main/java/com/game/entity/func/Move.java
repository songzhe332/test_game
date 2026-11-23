package com.game.entity.func;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public class Move {
    private static final Logger LOGGER = LoggerFactory.getLogger(Move.class);
    private float x;
    private float y;
    private float z;

    public Move(float x,float y,float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void move(Direction.Directions directions,String id){
        switch (directions) {
            case DOWN:
                y -= 1;
                break;
            case UP:
                y += 1;
                break;
            case LEFT:
                x -= 1;
                break;
            case RIGHT:
                x += 1;
                break;
            case FORWARD:
                z += 1;
                break;
            case BACKWARD:
                z -= 1;
                break;
            case NONE:
                break;
            default:
                LOGGER.warn("{}未移动", id);
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }
}
