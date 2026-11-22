package com.game.item.equipment;

import com.game.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Armor extends Equipment{
    private static final Logger LOGGER = LoggerFactory.getLogger(Armor.class);
    private Entity armored;
    private double testAddHealth = 5.0;
    public Armor(String name) {
        super(ItemType.ARMOR, name);
    }

    @Override
    public void equip(Entity needArmor) {
        this.armored = needArmor;
        armored.setHealth(armored.getHealth() + testAddHealth);//测试效果
        LOGGER.info("{}装备了{}获得了{}的防御力",armored,name,testAddHealth);
    }

    public Entity getArmored() {
        return armored;
    }
}
