package com.game.entity;

import com.game.item.equipment.Armor;

public interface CanEquipArmor {
    default void equip(Armor armor, Entity entity){
        armor.equip(entity);
        armor.setEquipped(true);
    }
}
