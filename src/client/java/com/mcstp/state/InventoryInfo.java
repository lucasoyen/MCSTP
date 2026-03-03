package com.mcstp.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mcstp.item.ItemCategorizer;
import com.mcstp.item.ItemCategory;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class InventoryInfo {
    private final JsonArray slots;

    public InventoryInfo(ClientPlayerEntity player) {
        this.slots = new JsonArray();
        PlayerInventory inventory = player.getInventory();

        // Slots 0-8: Hotbar, 9-35: Main inventory, 36-39: Armor, 40: Offhand
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;

            JsonObject slot = new JsonObject();
            slot.addProperty("slot", i);
            slot.addProperty("name", Registries.ITEM.getId(stack.getItem()).toString());
            slot.addProperty("category", ItemCategorizer.categorize(stack).name());
            slot.addProperty("count", stack.getCount());
            if (stack.getMaxDamage() > 0) {
                slot.addProperty("maxDurability", stack.getMaxDamage());
                slot.addProperty("durability", stack.getMaxDamage() - stack.getDamage());
            }
            slots.add(slot);
        }
    }

    public JsonArray toJson() {
        return slots;
    }
}
