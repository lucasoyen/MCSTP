package com.mcstp.state;

import com.google.gson.JsonObject;
import com.mcstp.config.MCSTPConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class GameStatePayload {

    public static JsonObject collect(MinecraftClient client, MCSTPConfig config) {
        ClientPlayerEntity player = client.player;
        if (player == null) return null;

        JsonObject payload = new JsonObject();

        if (config.isModuleEnabled("heldItem")) {
            int selectedSlot = player.getInventory().getSelectedSlot();
            ItemStack mainHand = player.getMainHandStack();
            ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);
            payload.addProperty("selectedSlot", selectedSlot);
            payload.add("heldItem", new HeldItemInfo(mainHand).toJson());
            payload.add("offhandItem", new HeldItemInfo(offHand).toJson());
        }

        if (config.isModuleEnabled("playerState")) {
            payload.add("playerState", new PlayerStateInfo(player).toJson());
        }

        if (config.isModuleEnabled("combatContext")) {
            payload.add("combatContext", new CombatContextInfo(player).toJson());
        }

        if (config.isModuleEnabled("playerInput")) {
            payload.add("playerInput", new PlayerInputInfo(player).toJson());
        }

        if (config.isModuleEnabled("screenState")) {
            payload.add("screenState", new ScreenStateInfo(client).toJson());
        }

        if (config.isModuleEnabled("statusEffects")) {
            payload.add("statusEffects", new StatusEffectInfo(player).toJson());
        }

        if (config.isModuleEnabled("threat")) {
            payload.add("threat", new ThreatInfo(player).toJson());
        }

        if (config.isModuleEnabled("inventory")) {
            payload.add("inventory", new InventoryInfo(player).toJson());
        }

        if (config.isModuleEnabled("nearbyEntities")) {
            payload.add("nearbyEntities", new NearbyEntitiesInfo(
                    player, config.getNearbyEntitiesRadius(), config.getNearbyEntitiesMax()).toJson());
        }

        return payload;
    }
}
