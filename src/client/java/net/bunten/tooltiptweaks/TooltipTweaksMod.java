package net.bunten.tooltiptweaks;

import com.google.gson.Gson;
import net.bunten.tooltiptweaks.config.ConfigMenuScreen;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltip.ConsumablesTooltips;
import net.bunten.tooltiptweaks.tooltip.ContainerTooltips;
import net.bunten.tooltiptweaks.tooltip.ToolTooltips;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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

    public static boolean creative() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return false;
        return player.getAbilities().creativeMode;
    }
    
    @Override
    public void onInitializeClient() {
        loadSettings();
        configMenu = new ConfigMenuScreen();

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            new ConsumablesTooltips().register(stack, lines);
            new ContainerTooltips().register(stack, lines);
            new ToolTooltips().register(stack, lines);
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