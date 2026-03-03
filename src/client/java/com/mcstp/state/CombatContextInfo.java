package com.mcstp.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.Registries;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class CombatContextInfo {
    public final boolean isUsingItem;
    public final boolean isBlocking;
    public final String activeHand;
    public final String crosshairTarget;
    public final String crosshairEntityType;
    public final int[] crosshairBlockPos;
    public final float crosshairEntityHealth;
    public final float crosshairEntityMaxHealth;
    public final double crosshairDistance;
    public final float attackCooldown;
    public final float itemUseProgress;
    public final boolean recentlyHurt;

    public CombatContextInfo(ClientPlayerEntity player) {
        this.isUsingItem = player.isUsingItem();
        this.isBlocking = player.isBlocking();
        this.activeHand = player.getActiveHand() == Hand.MAIN_HAND ? "MAIN_HAND" : "OFF_HAND";
        this.attackCooldown = player.getAttackCooldownProgress(0.0f);
        if (player.isUsingItem()) {
            ItemStack active = player.getActiveItem();
            int maxUse = active.getMaxUseTime(player);
            this.itemUseProgress = maxUse > 0 ? 1.0f - (float) player.getItemUseTimeLeft() / maxUse : 0.0f;
        } else {
            this.itemUseProgress = 0.0f;
        }
        this.recentlyHurt = player.hurtTime > 0;

        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitResult = client.crosshairTarget;

        if (hitResult instanceof EntityHitResult entityHit) {
            this.crosshairTarget = "ENTITY";
            Entity entity = entityHit.getEntity();
            this.crosshairEntityType = Registries.ENTITY_TYPE.getId(entity.getType()).toString();
            this.crosshairBlockPos = null;
            this.crosshairDistance = player.distanceTo(entity);
            if (entity instanceof LivingEntity living) {
                this.crosshairEntityHealth = living.getHealth();
                this.crosshairEntityMaxHealth = living.getMaxHealth();
            } else {
                this.crosshairEntityHealth = -1;
                this.crosshairEntityMaxHealth = -1;
            }
        } else if (hitResult instanceof BlockHitResult blockHit) {
            this.crosshairTarget = "BLOCK";
            this.crosshairEntityType = null;
            BlockPos pos = blockHit.getBlockPos();
            this.crosshairBlockPos = new int[]{pos.getX(), pos.getY(), pos.getZ()};
            this.crosshairDistance = Math.sqrt(player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
            this.crosshairEntityHealth = -1;
            this.crosshairEntityMaxHealth = -1;
        } else {
            this.crosshairTarget = "MISS";
            this.crosshairEntityType = null;
            this.crosshairBlockPos = null;
            this.crosshairDistance = -1;
            this.crosshairEntityHealth = -1;
            this.crosshairEntityMaxHealth = -1;
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("isUsingItem", isUsingItem);
        json.addProperty("isBlocking", isBlocking);
        json.addProperty("activeHand", activeHand);
        json.addProperty("crosshairTarget", crosshairTarget);
        if (crosshairEntityType != null) {
            json.addProperty("crosshairEntityType", crosshairEntityType);
        }
        if (crosshairBlockPos != null) {
            JsonArray posArr = new JsonArray();
            for (int v : crosshairBlockPos) posArr.add(v);
            json.add("crosshairBlockPos", posArr);
        }
        json.addProperty("crosshairDistance", Math.round(crosshairDistance * 10.0) / 10.0);
        if (crosshairEntityHealth >= 0) {
            json.addProperty("crosshairEntityHealth", crosshairEntityHealth);
            json.addProperty("crosshairEntityMaxHealth", crosshairEntityMaxHealth);
        }
        json.addProperty("attackCooldown", Math.round(attackCooldown * 100.0f) / 100.0f);
        json.addProperty("itemUseProgress", Math.round(itemUseProgress * 100.0f) / 100.0f);
        json.addProperty("recentlyHurt", recentlyHurt);
        return json;
    }
}
