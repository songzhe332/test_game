package com.game.skill;

import com.game.entity.Entity;
import com.game.entity.func.Move;
import com.game.server.GameRoll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Skills {
    private static final Logger LOGGER = LoggerFactory.getLogger(Skills.class);
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
                int cost = 1;
                if(distance > 10){
                    cost += 6;
                }
                if(distance > 5){
                    cost += 3;
                }
                if(distance < 3){
                    cost -= 2;
                }
                if (distance == 0){
                    cost = (int) (cost - entity.getHealth());
                }
                int damage = (int) (30 - cost - entity.getArmor());
                magic(entity, damage);
                LOGGER.info("{}被炸伤，距离：{}，伤害{}",entity,distance,damage);
            }
        }
    }
}
