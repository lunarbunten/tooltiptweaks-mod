package net.bunten.tooltiptweaks.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.MOD_ID;

public class TooltipTweaksConfig {

    private static TooltipTweaksConfig config;
    private static ConfigMenuScreen menu;

    public static TooltipTweaksConfig getInstance() {
        return config;
    }

    public static ConfigMenuScreen getMenu() {
        return menu;
    }

    private static String getConfigPath() {
        return "./config/" + MOD_ID + ".json";
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

    static {
        loadSettings();

        menu = new ConfigMenuScreen();
    }

    public byte clockTimeDisplay = 1;
    public byte clockMoonPhaseDisplay = 1;

    public byte compassDisplay = 0;
    public byte containerDisplay = 0;
    public int containerEntries = 6;

    public byte durabilityDisplay = 1;
    public int percentageDecimalCount = 0;
    public byte displayUsesLeft = 0;
    public byte repairCostDisplay = 0;

    public byte foodDisplayStyle = 0;
    public byte foodNourishmentDisplay = 0;
    public byte foodEffectDisplay = 1;
    public byte potionEffectDisplay = 0;
    public byte stewEffectDisplay = 2;
    public byte modifierDisplay = 0;
    public byte otherEffectDisplay = 0;

    public byte axolotlVariants = 0;
}