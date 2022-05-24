package net.bunten.tooltiptweaks;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

import net.bunten.tooltiptweaks.config.ConfigMenuScreen;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltip.DurabilityTooltips;
import net.bunten.tooltiptweaks.tooltip.FoodTooltips;
import net.bunten.tooltiptweaks.tooltip.ToolTooltips;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class TooltipTweaksMod implements ClientModInitializer {
    public static final String MOD_ID = "tooltiptweaks";

    private static TooltipTweaksConfig config;
    public static ConfigMenuScreen configMenu;

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static boolean isDevelopment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static TooltipTweaksConfig getConfig() {
        return config;
    }

    public static String getConfigPath() {
        return "./config/" + MOD_ID + ".json";
    }
    
    @Override
    public void onInitializeClient() {
        loadSettings();
        configMenu = new ConfigMenuScreen();

        ItemTooltipCallback.EVENT.register((stack, context, lines) -> {
            new DurabilityTooltips().addTooltips(stack, context, lines);
            new FoodTooltips().addTooltips(stack, context, lines);
            new ToolTooltips().addTooltips(stack, context, lines);
        });
    }

    public static void loadSettings() {
        File file = new File(getConfigPath());
        Gson gson = new Gson();
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                config = gson.fromJson(fileReader, TooltipTweaksConfig.class);
                fileReader.close();
            } catch (IOException ignored) {
            }
        } else {
            config = new TooltipTweaksConfig();
        }
    }

    public static void saveSettings() {
        Gson gson = new Gson();
        File file = new File(getConfigPath());
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(config));
            fileWriter.close();
        } catch (IOException ignored) {
        }
    }
}