package com.game.item;

import com.game.func.ExceptionUtils;
import com.game.item.equipment.Armor;
import com.game.server.GameRoll;

public abstract class Item {
    public final String name;
    private final ItemType type;

    public Item(ItemType type, String name) {
        this.name = name;
        this.type = type;
        ExceptionUtils.nullPointerExceptionTrigger(type);
    }

    public enum ItemType {
        ARMOR,
        NULL,
        OTHER
    }

    @Override
    public String toString() {
        return name;
    }

    public static Item create(ItemType type, String name) {
        return switch (type){
            case ARMOR -> new Armor(name,5.0);
            case NULL -> new NullItem(name);
            default -> new Armor("物品创建失败",0.0);
        };
    }

    public static void remove(Item item) {
        GameRoll.getItems().remove(item);
    }

    public ItemType getType() {
        return type;
    }
}
