package net.pottx;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.pottx.config.ConfigData;
import net.pottx.config.ConfigManager;
import net.pottx.mixin.AbstractSkeletonEntityMixin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class ProSniper implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("prosniper");
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Pro Sniper mod");

		try {
			ConfigManager.loadConfig();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}