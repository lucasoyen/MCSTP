package com.mcstp.state;

import com.google.gson.JsonObject;
import com.mcstp.item.ItemCategory;
import com.mcstp.item.ItemCategorizer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class HeldItemInfo {
    public final String name;
    public final String category;
    public final int stackCount;
    public final int maxDurability;
    public final int currentDurability;

    public HeldItemInfo(ItemStack stack) {
        if (stack.isEmpty()) {
            this.name = "minecraft:air";
            this.category = ItemCategory.EMPTY.name();
            this.stackCount = 0;
            this.maxDurability = 0;
            this.currentDurability = 0;
        } else {
            this.name = Registries.ITEM.getId(stack.getItem()).toString();
            this.category = ItemCategorizer.categorize(stack).name();
            this.stackCount = stack.getCount();
            this.maxDurability = stack.getMaxDamage();
            this.currentDurability = stack.getMaxDamage() - stack.getDamage();
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("category", category);
        json.addProperty("stackCount", stackCount);
        json.addProperty("maxDurability", maxDurability);
        json.addProperty("currentDurability", currentDurability);
        return json;
    }
}
