package com.game.entity.object.glass;

import com.game.entity.Entity;
import com.game.entity.func.Move;
import com.game.entity.object.GameObject;

public class Glass extends GameObject {
    public Glass() {
        super("glass");
    }

    public boolean reflectAttack(Entity attacker,Entity target){
        Move attackerMove = attacker.getMove();
        Move targetMove = target.getMove();
        boolean hitGlass = false;
        if(attackerMove.getY() == targetMove.getY()){
            for(float dx = attackerMove.getX();dx <= attackerMove.getX() + 64.0;dx ++){
                for(float dz = attackerMove.getZ();dz <= attackerMove.getZ() + 64.0;dz ++) {
                    if (dx == getMove().getX() ) {
                        if(dz == getMove().getZ()){
                            if(targetMove.getX() >= 0 && targetMove.getZ() >= 0) {
                                hitGlass = targetMove.getX() - dx < 5 && targetMove.getZ() - dz < 5;
                            }
                        }
                    }
                }
            }
        }

        if(hitGlass){
            attacker.attack(attacker,attacker.getCritValue());
        }
        return hitGlass;
    }
}
