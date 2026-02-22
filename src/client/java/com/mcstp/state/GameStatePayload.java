package com.mcstp.state;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class GameStatePayload {

    public static JsonObject collect(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return null;

        int selectedSlot = player.getInventory().getSelectedSlot();
        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getStackInHand(Hand.OFF_HAND);

        JsonObject payload = new JsonObject();
        payload.addProperty("selectedSlot", selectedSlot);
        payload.add("heldItem", new HeldItemInfo(mainHand).toJson());
        payload.add("offhandItem", new HeldItemInfo(offHand).toJson());
        payload.add("playerState", new PlayerStateInfo(player).toJson());
        payload.add("combatContext", new CombatContextInfo(player).toJson());
        payload.add("playerInput", new PlayerInputInfo(player).toJson());
        payload.add("screenState", new ScreenStateInfo(client).toJson());
        payload.add("statusEffects", new StatusEffectInfo(player).toJson());
        payload.add("threat", new ThreatInfo(player).toJson());
        return payload;
    }
}
