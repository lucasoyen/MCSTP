package com.mcstp.state;

import com.google.gson.JsonObject;
import com.mcctp.api.StateProvider;
import net.minecraft.client.MinecraftClient;

public class MCSTPStateProvider implements StateProvider {

    @Override
    public void collectState(MinecraftClient client, JsonObject root) {
        JsonObject payload = GameStatePayload.collect(client);
        if (payload != null) {
            for (String key : payload.keySet()) {
                root.add(key, payload.get(key));
            }
        }
    }
}
