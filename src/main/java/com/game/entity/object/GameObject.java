package com.game.entity.object;

import com.game.entity.Entity;
import com.game.entity.func.Move;
import com.game.func.Crit;
import com.game.server.GameRoll;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameObject extends Entity {
    public GameObject(String id) {
        super(999,EntityType.DEFAULT, Double.POSITIVE_INFINITY, id);
        setMove(new Move(0,0,0));
    }

    @Override
    public void die() {
        //不会死亡
    }

    @Override
    public void attack(Entity target, double critValue) {
        //不会攻击
    }

    @Override
    public Crit getCrit() {
        return null;
    }

    public static void placeObject(String id,float x,float y,float z){
        GameObject gameObject = new GameObject(id);
        gameObject.add();
        Move move = gameObject.getMove();
        if(move != null) {
            move.setX(x);
            move.setY(y);
            move.setZ(z);
        }
    }

    @Override
    public void add() {
        GameRoll.getObjects().add(this);
    }

    public List<Entity> getEntityOn(){
        float entityX;
        float entityY;
        float entityZ;
        CopyOnWriteArrayList<Entity> entities = new CopyOnWriteArrayList<>();
        for(Entity entity : GameRoll.getEntities()){
            if(entity.getMove() == null){continue;}
            entityX = entity.getMove().getX();
            entityY = entity.getMove().getY();
            entityZ = entity.getMove().getZ();
            if(entityY == this.getMove().getY() + 1.0 && entityX == this.getMove().getX() && entityZ == this.getMove().getZ()){
                entities.add(entity);
            }
        }
        return entities;
    }
}
