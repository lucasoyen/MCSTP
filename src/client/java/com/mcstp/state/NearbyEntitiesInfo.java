package com.mcstp.state;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.Comparator;
import java.util.List;

public class NearbyEntitiesInfo {
    private final JsonArray entities;

    public NearbyEntitiesInfo(ClientPlayerEntity player, int radius, int maxEntities) {
        this.entities = new JsonArray();

        Box scanBox = player.getBoundingBox().expand(radius);
        List<LivingEntity> nearby = player.getEntityWorld().getEntitiesByClass(
                LivingEntity.class, scanBox, e -> e.isAlive() && e != player);

        nearby.sort(Comparator.comparingDouble(player::distanceTo));

        int count = Math.min(nearby.size(), maxEntities);
        for (int i = 0; i < count; i++) {
            LivingEntity entity = nearby.get(i);

            JsonObject obj = new JsonObject();
            obj.addProperty("type", Registries.ENTITY_TYPE.getId(entity.getType()).toString());
            obj.addProperty("x", Math.round(entity.getX() * 10.0) / 10.0);
            obj.addProperty("y", Math.round(entity.getY() * 10.0) / 10.0);
            obj.addProperty("z", Math.round(entity.getZ() * 10.0) / 10.0);
            obj.addProperty("distance", Math.round(player.distanceTo(entity) * 10.0) / 10.0);

            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            obj.addProperty("yaw", Math.round(yaw * 10.0f) / 10.0f);

            obj.addProperty("health", entity.getHealth());
            obj.addProperty("maxHealth", entity.getMaxHealth());
            obj.addProperty("hostile", entity instanceof HostileEntity);

            entities.add(obj);
        }
    }

    public JsonArray toJson() {
        return entities;
    }
}
