package com.mcstp.state;

import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class ThreatInfo {
    private static final double SCAN_RANGE = 16.0;

    public final boolean targetEntityHostile;
    public final double targetDistance;
    public final double nearestHostileDist;
    public final float nearestHostileYaw;
    public final int hostileCount;

    public ThreatInfo(ClientPlayerEntity player) {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hitResult = client.crosshairTarget;

        if (hitResult instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof HostileEntity) {
            this.targetEntityHostile = true;
            this.targetDistance = player.distanceTo(entityHit.getEntity());
        } else {
            this.targetEntityHostile = false;
            this.targetDistance = -1;
        }

        Box scanBox = player.getBoundingBox().expand(SCAN_RANGE);
        List<HostileEntity> hostiles = player.getEntityWorld().getEntitiesByClass(
                HostileEntity.class, scanBox, Entity::isAlive);

        this.hostileCount = hostiles.size();

        if (!hostiles.isEmpty()) {
            HostileEntity nearest = null;
            double minDist = Double.MAX_VALUE;
            for (HostileEntity hostile : hostiles) {
                double dist = player.distanceTo(hostile);
                if (dist < minDist) {
                    minDist = dist;
                    nearest = hostile;
                }
            }
            this.nearestHostileDist = minDist;
            double dx = nearest.getX() - player.getX();
            double dz = nearest.getZ() - player.getZ();
            this.nearestHostileYaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        } else {
            this.nearestHostileDist = -1;
            this.nearestHostileYaw = 0;
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("targetEntityHostile", targetEntityHostile);
        json.addProperty("targetDistance", Math.round(targetDistance * 10.0) / 10.0);
        json.addProperty("nearestHostileDist", Math.round(nearestHostileDist * 10.0) / 10.0);
        json.addProperty("nearestHostileYaw", Math.round(nearestHostileYaw * 10.0f) / 10.0f);
        json.addProperty("hostileCount", hostileCount);
        return json;
    }
}
