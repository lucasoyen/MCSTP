package com.mcstp.item;

import net.minecraft.item.*;
import net.minecraft.registry.Registries;

public class ItemCategorizer {
    public static ItemCategory categorize(ItemStack stack) {
        if (stack.isEmpty()) return ItemCategory.EMPTY;

        Item item = stack.getItem();
        String id = Registries.ITEM.getId(item).toString();

        if (id.contains("sword")) return ItemCategory.SWORD;
        if (id.contains("pickaxe")) return ItemCategory.PICKAXE;

        if (item instanceof BowItem) return ItemCategory.BOW;
        if (item instanceof CrossbowItem) return ItemCategory.CROSSBOW;
        if (item instanceof AxeItem) return ItemCategory.AXE;
        if (item instanceof ShovelItem) return ItemCategory.SHOVEL;
        if (item instanceof HoeItem) return ItemCategory.HOE;
        if (item instanceof ShieldItem) return ItemCategory.SHIELD;
        if (item instanceof TridentItem) return ItemCategory.TRIDENT;
        if (item instanceof FishingRodItem) return ItemCategory.FISHING_ROD;
        if (item instanceof BlockItem) return ItemCategory.BLOCK;
        if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem)
            return ItemCategory.THROWABLE;
        if (item.getComponents().contains(net.minecraft.component.DataComponentTypes.FOOD))
            return ItemCategory.FOOD;

        return ItemCategory.OTHER;
    }
}
