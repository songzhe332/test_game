package com.game.item.equipment;

import com.game.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Armor extends Equipment{
    private static final Logger LOGGER = LoggerFactory.getLogger(Armor.class);
    private Entity armored;
    private double defense;
    public Armor(String name,double defense) {
        super(ItemType.ARMOR, name);
        this.defense = defense;
    }

    @Override
    public void equip(Entity needArmor) {
        this.armored = needArmor;
        armored.setArmor(armored.getArmor() + defense);
        LOGGER.info("{}装备了{}获得了{}的防御力",armored,name,defense);
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public Entity getArmored() {
        return armored;
    }
}
