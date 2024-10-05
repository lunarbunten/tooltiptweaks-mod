package net.bunten.tooltiptweaks.util;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ClockUtil {
    
    public static MutableText getClockTime() {
        boolean twelveHour = TooltipTweaksConfig.getInstance().clockTimeDisplay == 1;

        var world = MinecraftClient.getInstance().world;

        long time = world.getTimeOfDay();

        int actualHour = (int) ((time / 1000L + 6L) % 24L);
        int actualMinute = (int) (60L * (time % 1000L) / 1000L);

        int displayedHour = actualHour;
        int displayedMinute = actualMinute;

        if (twelveHour) {
            if (actualHour > 12) {
                displayedHour -= 12;
            } else if (actualHour == 0) {
                displayedHour = 12;
            }
        }

        String hourDisplay = String.valueOf(displayedHour);
        String minuteDisplay = String.valueOf(displayedMinute);

        if (displayedHour < 10 && !twelveHour) hourDisplay = "0" + hourDisplay;
        if (displayedMinute < 10) minuteDisplay = "0" + minuteDisplay;

        MutableText suffix = twelveHour ? Text.translatable("tooltiptweaks.ui.clock." + (actualHour >= 12 ? "pm" : "am")) : Text.literal("");

        return Text.translatable("tooltiptweaks.ui.clock.time", hourDisplay, minuteDisplay, suffix);
    }
}