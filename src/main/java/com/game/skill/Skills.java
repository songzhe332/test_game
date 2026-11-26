package com.game.skill;

import com.game.entity.Entity;
import com.game.entity.func.Move;
import com.game.server.GameRoll;

public class Skills {
    public static void magic(Entity entity,float damage){
        entity.setHealth(entity.getHealth() - (damage > 0 ? damage : 0));
    }

    public static void explode(Move move){
        float x = move.getX();
        float y = move.getY();
        float z = move.getZ();
        for(Entity entity : GameRoll.getEntities()){
            float entityX = entity.getMove().getX();
            float entityY = entity.getMove().getY();
            float entityZ = entity.getMove().getZ();
            double distance = Math.sqrt(Math.pow((entityX - x),2) + Math.pow((entityY - y),2) + Math.pow((entityZ - z),2));
            if(distance <= 15){
                for(float damage = 30;damage > distance;damage --) {
                    magic(entity, damage);
                }
            }
        }
    }
}
