package com.mcstp;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCSTPMod implements ModInitializer {
    public static final String MOD_ID = "mcstp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("MCSTP initialized");
    }
}
