package com.game.entity.object.fluid;

import com.game.entity.func.Direction;
import com.game.entity.func.Move;
import com.game.entity.object.GameObject;

import java.util.ArrayList;

public class Fluid extends GameObject {
    private static final int MAX_FLOWS = 7;
    private final ArrayList<Fluid> fluidsMadeBySelf = new ArrayList<>();
    private boolean hasFather = false;

    public Fluid(String id) {
        super(id);
    }

    public void add(Fluid fluid) {
        fluidsMadeBySelf.add(fluid);
    }

    public Fluid setHasFather(boolean hasFather) {
        this.hasFather = hasFather;
        return this;
    }

    public void flow(Direction.Directions direction) {
        if (fluidsMadeBySelf.size() <= MAX_FLOWS) {
            if(this.hasFather){return;}
            Move move = getMove();
            switch (direction) {
                case DOWN -> add(((Fluid) placeObject(getId(), move.getX(), move.getY() - 1, move.getZ())).setHasFather(true));
                case LEFT -> add(((Fluid) placeObject(getId(), move.getX() - 1, move.getY(), move.getZ())).setHasFather(true));
                case RIGHT -> add(((Fluid) placeObject(getId(), move.getX() + 1, move.getY(), move.getZ())).setHasFather(true));
                case FORWARD -> add(((Fluid) placeObject(getId(), move.getX(), move.getY(), move.getZ() + 1)).setHasFather(true));
                case BACKWARD -> add(((Fluid) placeObject(getId(), move.getX(), move.getY(), move.getZ() - 1)).setHasFather(true));
            }
        }
    }
}
