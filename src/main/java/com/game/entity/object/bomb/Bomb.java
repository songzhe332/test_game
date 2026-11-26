package com.game.entity.object.bomb;

import com.game.entity.object.GameObject;
import com.game.skill.Skills;

public class Bomb extends GameObject {
    public Bomb() {
        super("bomb");
    }

    public void explode(){
        Skills.explode(this.getMove());
    }
}
