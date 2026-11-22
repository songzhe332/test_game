package com.game.item.equipment;

import com.game.entity.CanEquipArmor;
import com.game.entity.Entity;
import com.game.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Equipment extends Item implements Equipable{
    private static final Logger LOGGER = LoggerFactory.getLogger(Equipment.class);
    public Equipment(ItemType type, String name) {
        super(type, name);
    }

    @Override
    public abstract void equip(Entity needArmor);

    public static void quickEquip(Entity entity){
        for(Item armor : Entity.getLootTable()){
            if(armor instanceof Armor && entity instanceof CanEquipArmor){
                ((CanEquipArmor) entity).equip((Armor)armor,entity);
            }
        }
    }
}
