package com.mcstp.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcstp.MCSTPMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MCSTPConfig {
    private int tickInterval = 1;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int getTickInterval() {
        return tickInterval;
    }

    public static MCSTPConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("mcstp.json");
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                MCSTPConfig config = GSON.fromJson(json, MCSTPConfig.class);
                MCSTPMod.LOGGER.info("Loaded MCSTP config: tickInterval={}", config.tickInterval);
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
