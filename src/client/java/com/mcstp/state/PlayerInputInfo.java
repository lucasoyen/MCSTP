package com.mcstp.state;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;

public class PlayerInputInfo {
    public final float movementForward;
    public final float movementSideways;
    public final boolean jump;
    public final boolean sprint;
    public final boolean sneak;
    public final boolean attack;
    public final boolean useItem;
    public final boolean drop;
    public final boolean swapOffhand;
    public final boolean openInventory;
    public final float yawDelta;
    public final float pitchDelta;

    private static float prevYaw = 0;
    private static float prevPitch = 0;

    public PlayerInputInfo(ClientPlayerEntity player) {
        GameOptions options = MinecraftClient.getInstance().options;

        // Read all inputs from key bindings directly — player.input fields
        // may be reset by the time the state provider runs in the tick cycle
        float fwd = options.forwardKey.isPressed() ? 1.0f : 0.0f;
        float back = options.backKey.isPressed() ? 1.0f : 0.0f;
        float left = options.leftKey.isPressed() ? 1.0f : 0.0f;
        float right = options.rightKey.isPressed() ? 1.0f : 0.0f;
        this.movementForward = fwd - back;
        this.movementSideways = left - right;
        this.jump = options.jumpKey.isPressed();
        this.sprint = options.sprintKey.isPressed();
        this.sneak = options.sneakKey.isPressed();
        this.attack = options.attackKey.isPressed();
        this.useItem = options.useKey.isPressed();
        this.drop = options.dropKey.isPressed();
        this.swapOffhand = options.swapHandsKey.isPressed();
        this.openInventory = options.inventoryKey.isPressed();

        float currentYaw = player.getYaw();
        float currentPitch = player.getPitch();
        this.yawDelta = currentYaw - prevYaw;
        this.pitchDelta = currentPitch - prevPitch;
        prevYaw = currentYaw;
        prevPitch = currentPitch;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("movementForward", movementForward);
        json.addProperty("movementSideways", movementSideways);
        json.addProperty("jump", jump);
        json.addProperty("sprint", sprint);
        json.addProperty("sneak", sneak);
        json.addProperty("attack", attack);
        json.addProperty("useItem", useItem);
        json.addProperty("drop", drop);
        json.addProperty("swapOffhand", swapOffhand);
        json.addProperty("openInventory", openInventory);
        json.addProperty("yawDelta", Math.round(yawDelta * 100.0f) / 100.0f);
        json.addProperty("pitchDelta", Math.round(pitchDelta * 100.0f) / 100.0f);
        return json;
    }
}
