package com.mcstp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcstp.MCSTPMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class MCSTPConfig {
    private int tickInterval = 1;
    private Map<String, Boolean> enabledModules = createDefaults();
    private int nearbyEntitiesRadius = 16;
    private int nearbyEntitiesMax = 32;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Map<String, Boolean> createDefaults() {
        Map<String, Boolean> modules = new LinkedHashMap<>();
        modules.put("playerState", true);
        modules.put("heldItem", true);
        modules.put("combatContext", true);
        modules.put("inventory", false);
        modules.put("nearbyEntities", false);
        modules.put("playerInput", false);
        modules.put("screenState", false);
        modules.put("statusEffects", false);
        modules.put("threat", false);
        return modules;
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    public boolean isModuleEnabled(String name) {
        return enabledModules.getOrDefault(name, false);
    }

    public void setModuleEnabled(String name, boolean enabled) {
        enabledModules.put(name, enabled);
    }

    public Map<String, Boolean> getEnabledModules() {
        return enabledModules;
    }

    public int getNearbyEntitiesRadius() {
        return nearbyEntitiesRadius;
    }

    public void setNearbyEntitiesRadius(int radius) {
        this.nearbyEntitiesRadius = radius;
    }

    public int getNearbyEntitiesMax() {
        return nearbyEntitiesMax;
    }

    public void setNearbyEntitiesMax(int max) {
        this.nearbyEntitiesMax = max;
    }

    public static MCSTPConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("mcstp.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                MCSTPConfig config = GSON.fromJson(json, MCSTPConfig.class);
                // Ensure any new modules added in future updates have defaults
                Map<String, Boolean> defaults = createDefaults();
                for (Map.Entry<String, Boolean> entry : defaults.entrySet()) {
                    config.enabledModules.putIfAbsent(entry.getKey(), entry.getValue());
                }
                MCSTPMod.LOGGER.info("Loaded MCSTP config: tickInterval={}, modules={}", config.tickInterval, config.enabledModules);
                return config;
            } catch (IOException e) {
                MCSTPMod.LOGGER.error("Failed to load MCSTP config, using defaults", e);
            }
        }
        MCSTPConfig config = new MCSTPConfig();
        config.save();
        return config;
    }

    public void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("mcstp.json");
        try {
            Files.writeString(configPath, GSON.toJson(this));
        } catch (IOException e) {
            MCSTPMod.LOGGER.error("Failed to save MCSTP config", e);
        }
    }
}
