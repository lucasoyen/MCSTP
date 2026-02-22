package com.mcstp.state;

import com.google.gson.JsonObject;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class StatusEffectInfo {
    public final boolean speed;
    public final boolean slowness;
    public final boolean strength;
    public final boolean fireResistance;
    public final boolean poison;
    public final boolean wither;
    public final boolean regeneration;
    public final boolean resistance;
    public final boolean invisibility;
    public final boolean waterBreathing;
    public final boolean absorption;
    public final int activeEffectCount;

    public StatusEffectInfo(ClientPlayerEntity player) {
        this.speed = player.hasStatusEffect(StatusEffects.SPEED);
        this.slowness = player.hasStatusEffect(StatusEffects.SLOWNESS);
        this.strength = player.hasStatusEffect(StatusEffects.STRENGTH);
        this.fireResistance = player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
        this.poison = player.hasStatusEffect(StatusEffects.POISON);
        this.wither = player.hasStatusEffect(StatusEffects.WITHER);
        this.regeneration = player.hasStatusEffect(StatusEffects.REGENERATION);
        this.resistance = player.hasStatusEffect(StatusEffects.RESISTANCE);
        this.invisibility = player.hasStatusEffect(StatusEffects.INVISIBILITY);
        this.waterBreathing = player.hasStatusEffect(StatusEffects.WATER_BREATHING);
        this.absorption = player.hasStatusEffect(StatusEffects.ABSORPTION);
        this.activeEffectCount = player.getStatusEffects().size();
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("speed", speed);
        json.addProperty("slowness", slowness);
        json.addProperty("strength", strength);
        json.addProperty("fireResistance", fireResistance);
        json.addProperty("poison", poison);
        json.addProperty("wither", wither);
        json.addProperty("regeneration", regeneration);
        json.addProperty("resistance", resistance);
        json.addProperty("invisibility", invisibility);
        json.addProperty("waterBreathing", waterBreathing);
        json.addProperty("absorption", absorption);
        json.addProperty("activeEffectCount", activeEffectCount);
        return json;
    }
}
