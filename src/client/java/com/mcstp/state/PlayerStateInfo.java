package com.mcstp.state;

import com.google.gson.JsonObject;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerStateInfo {
    public final float health;
    public final float maxHealth;
    public final int hunger;
    public final float saturation;
    public final double x;
    public final double y;
    public final double z;
    public final float yaw;
    public final float pitch;
    public final boolean onGround;
    public final boolean sprinting;
    public final boolean sneaking;
    public final boolean swimming;
    public final boolean flying;
    public final boolean inWater;
    public final boolean onFire;
    public final int experienceLevel;
    public final float experienceProgress;
    public final int totalExperience;
    public final int armor;
    public final double fallDistance;
    public final double velocityY;
    public final boolean horizontalCollision;
    public final boolean climbing;
    public final boolean recentlyHurt;

    public PlayerStateInfo(ClientPlayerEntity player) {
        this.health = player.getHealth();
        this.maxHealth = player.getMaxHealth();
        this.hunger = player.getHungerManager().getFoodLevel();
        this.saturation = player.getHungerManager().getSaturationLevel();
        this.x = Math.round(player.getX() * 10.0) / 10.0;
        this.y = Math.round(player.getY() * 10.0) / 10.0;
        this.z = Math.round(player.getZ() * 10.0) / 10.0;
        this.yaw = player.getYaw();
        this.pitch = player.getPitch();
        this.onGround = player.isOnGround();
        this.sprinting = player.isSprinting();
        this.sneaking = player.isSneaking();
        this.swimming = player.isSwimming();
        this.flying = player.getAbilities().flying;
        this.inWater = player.isTouchingWater();
        this.onFire = player.isOnFire();
        this.experienceLevel = player.experienceLevel;
        this.experienceProgress = player.experienceProgress;
        this.totalExperience = player.totalExperience;
        this.armor = player.getArmor();
        this.fallDistance = player.fallDistance;
        Vec3d vel = player.getVelocity();
        this.velocityY = vel.y;
        this.horizontalCollision = player.horizontalCollision;
        this.climbing = player.isClimbing();
        this.recentlyHurt = player.hurtTime > 0;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("health", health);
        json.addProperty("maxHealth", maxHealth);
        json.addProperty("hunger", hunger);
        json.addProperty("saturation", saturation);
        json.addProperty("x", x);
        json.addProperty("y", y);
        json.addProperty("z", z);
        json.addProperty("yaw", yaw);
        json.addProperty("pitch", pitch);
        json.addProperty("onGround", onGround);
        json.addProperty("sprinting", sprinting);
        json.addProperty("sneaking", sneaking);
        json.addProperty("swimming", swimming);
        json.addProperty("flying", flying);
        json.addProperty("inWater", inWater);
        json.addProperty("onFire", onFire);
        json.addProperty("experienceLevel", experienceLevel);
        json.addProperty("experienceProgress", experienceProgress);
        json.addProperty("totalExperience", totalExperience);
        json.addProperty("armor", armor);
        json.addProperty("fallDistance", Math.round(fallDistance * 100.0) / 100.0);
        json.addProperty("velocityY", Math.round(velocityY * 1000.0) / 1000.0);
        json.addProperty("horizontalCollision", horizontalCollision);
        json.addProperty("climbing", climbing);
        json.addProperty("recentlyHurt", recentlyHurt);
        return json;
    }
}
