package com.game.entity;

import com.game.item.equipment.Armor;
import com.game.server.GameRoll;

public interface CanEquipArmor {
    default void equip(Armor armor, Entity entity){
        armor.equip(entity);
        armor.setEquipped(true);
        GameRoll.getItems().remove(armor);
    }
}
