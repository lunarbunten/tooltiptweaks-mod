package net.bunten.tooltiptweaks;

import com.google.gson.Gson;
import net.bunten.tooltiptweaks.config.ConfigMenuScreen;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltip.ContainerTooltips;
import net.bunten.tooltiptweaks.tooltip.DurabilityTooltips;
import net.bunten.tooltiptweaks.tooltip.FoodTooltips;
import net.bunten.tooltiptweaks.tooltip.ToolTooltips;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TooltipTweaksMod implements ClientModInitializer {
    public static final String MOD_ID = "tooltiptweaks";

    private static TooltipTweaksConfig config;
    public static ConfigMenuScreen configMenu;

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
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

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            new ContainerTooltips().addTooltips(stack, type, lines);
            new DurabilityTooltips().addTooltips(stack, type, lines);
            new FoodTooltips().addTooltips(stack, type, lines);
            new ToolTooltips().addTooltips(stack, type, lines);
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