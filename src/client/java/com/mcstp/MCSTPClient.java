package com.mcstp;

import com.mcctp.api.MCCTPApi;
import com.mcctp.api.StateProviderRegistry;
import com.mcstp.config.MCSTPConfig;
import com.mcstp.state.MCSTPStateProvider;
import net.fabricmc.api.ClientModInitializer;

public class MCSTPClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MCSTPConfig config = MCSTPConfig.load();

        // Register module with MCCTP
        MCCTPApi.registerModule("mcstp");

        // Register state provider (telemetry only - controls live in MCCTP)
        StateProviderRegistry.register(new MCSTPStateProvider(config));

        // Set tick interval
        MCCTPApi.setTickInterval(config.getTickInterval());

        MCSTPMod.LOGGER.info("MCSTP client initialized (tickInterval={})", config.getTickInterval());
    }
}
