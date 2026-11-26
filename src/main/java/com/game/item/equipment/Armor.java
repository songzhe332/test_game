package com.game.item.equipment;

import com.game.entity.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Armor extends Equipment{
    private static final Logger LOGGER = LoggerFactory.getLogger(Armor.class);
    private Entity armored;
    private double defense;
    private double defenseRate;
    public Armor(String name,double defense,double defenseRate) {
        super(ItemType.ARMOR, name);
        this.defense = defense;
        this.defenseRate = defenseRate;
    }

    @Override
    public void equip(Entity needArmor) {
        this.armored = needArmor;
        armored.setArmor(armored.getArmor() + defense);
        armored.setArmorRate(armored.getArmorRate() + defenseRate);
        LOGGER.info("{}装备了{}获得了{}点和{}%的防御力",armored,name,defense,defenseRate);
    }

    public double getDefense() {
        return defense;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public double getDefenseRate() {
        return defenseRate;
    }

    public void setDefenseRate(double defenseRate) {
        this.defenseRate = defenseRate;
    }

    public Entity getArmored() {
        return armored;
    }
}
