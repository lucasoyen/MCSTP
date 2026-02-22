package com.mcstp;

import com.mcctp.action.ActionDispatcher;
import com.mcctp.api.MCCTPApi;
import com.mcctp.api.StateProviderRegistry;
import com.mcstp.action.handlers.ClickHandler;
import com.mcstp.action.handlers.CursorHandler;
import com.mcstp.config.MCSTPConfig;
import com.mcstp.state.MCSTPStateProvider;
import net.fabricmc.api.ClientModInitializer;

public class MCSTPClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MCSTPConfig config = MCSTPConfig.load();

        // Register module with MCCTP
        MCCTPApi.registerModule("mcstp");

        // Register state provider
        StateProviderRegistry.register(new MCSTPStateProvider());

        // Register action handlers
        ActionDispatcher dispatcher = MCCTPApi.getActionDispatcher();
        dispatcher.registerHandler("cursor", new CursorHandler());
        dispatcher.registerHandler("click", new ClickHandler());

        // Set tick interval
        MCCTPApi.setTickInterval(config.getTickInterval());

        MCSTPMod.LOGGER.info("MCSTP client initialized (tickInterval={})", config.getTickInterval());
    }
}
