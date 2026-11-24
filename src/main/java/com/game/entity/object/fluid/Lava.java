package com.game.entity.object.fluid;

import com.game.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lava extends Fluid{
    private static final Logger LOGGER = LoggerFactory.getLogger(Lava.class);
    public Lava() {
        super("lava");
    }

    @Override
    public void attack(Entity target, double critValue) {
        target.setHealth(target.getHealth() - critValue);//熔岩伤害
    }

    public void lavaDamage(Entity target,double damage){
        attack(target, damage);
        LOGGER.info("{}被熔岩烫伤了",target);
    }
}
