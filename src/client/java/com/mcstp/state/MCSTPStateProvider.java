package com.mcstp.state;

import com.google.gson.JsonObject;
import com.mcctp.api.StateProvider;
import com.mcstp.config.MCSTPConfig;
import net.minecraft.client.MinecraftClient;

public class MCSTPStateProvider implements StateProvider {
    private final MCSTPConfig config;

    public MCSTPStateProvider(MCSTPConfig config) {
        this.config = config;
    }

    @Override
    public void collectState(MinecraftClient client, JsonObject root) {
        JsonObject payload = GameStatePayload.collect(client, config);
        if (payload != null) {
            for (String key : payload.keySet()) {
                root.add(key, payload.get(key));
            }
        }
    }
}
