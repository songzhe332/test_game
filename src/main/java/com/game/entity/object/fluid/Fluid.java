package com.game.entity.object.fluid;

import com.game.entity.func.Direction;
import com.game.entity.func.Move;
import com.game.entity.object.GameObject;

public class Fluid extends GameObject {
    private int flows;
    private static final int MAX_FLOWS = 7;
    public Fluid(String id) {
        super(id);
    }

    public void flow(Direction.Directions direction){
        if(flows <= MAX_FLOWS) {
            Move move = getMove();
            switch (direction) {
                case DOWN -> placeObject(getId(), move.getX(), move.getY() - 1, move.getZ());
                case LEFT -> placeObject(getId(), move.getX() - 1, move.getY(), move.getZ());
                case RIGHT -> placeObject(getId(), move.getX() + 1, move.getY(), move.getZ());
                case FORWARD -> placeObject(getId(), move.getX(), move.getY(), move.getZ() + 1);
                case BACKWARD -> placeObject(getId(), move.getX(), move.getY(), move.getZ() - 1);
            }
            flows += 1;
        }
    }
}
