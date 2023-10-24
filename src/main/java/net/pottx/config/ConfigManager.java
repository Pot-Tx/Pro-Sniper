package net.pottx.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.pottx.ProSniper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static ConfigData configData = null;

    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("prosniper.json");

    private static ConfigData correctConfigData(ConfigData configData) {
        ConfigData newConfigData = new ConfigData();
        double[][] chances = {configData.chance.skeletonChance, configData.chance.drownedChance, configData.chance.pillagerChance, configData.chance.ghastChance, configData.chance.snowGolemChance, configData.chance.witchChance};
        for(double[] chance : chances){
            for (int i=0; i<4; i++) {
                if (chance[i] < 0 || chance[i] > 1){
                    chance[i] = i * 0.25;
                }
            }
        }
        newConfigData.chance.skeletonChance = chances[0];
        newConfigData.chance.drownedChance = chances[1];
        newConfigData.chance.pillagerChance = chances[2];
        newConfigData.chance.ghastChance = chances[3];
        newConfigData.chance.snowGolemChance = chances[4];
        newConfigData.chance.witchChance = chances[5];
        return newConfigData;
    };

    public static void loadConfig() throws IOException {
        configData = null;
        FileWriter configWriter = new FileWriter(configPath.toString());
        try {
            ConfigData inputConfigData;
            if (Files.exists(configPath)) {
                List<String> lines = Files.readAllLines(configPath);
                String json = String.join("", lines);
                inputConfigData = gson.fromJson(json, ConfigData.class);
                if (inputConfigData != null) {
                    inputConfigData = correctConfigData(inputConfigData);
                } else {
                    inputConfigData = new ConfigData();
                }
            } else {
                Files.createFile(configPath);
                inputConfigData = new ConfigData();
            }
            configData = inputConfigData;
            configWriter.write(gson.toJson(inputConfigData));
            configWriter.close();
        } catch (Exception exception){
            ProSniper.LOGGER.error("Error occured while loading config!");
            exception.printStackTrace();
            if (configData == null){
                configData = new ConfigData();
            }
        }
    }
}
