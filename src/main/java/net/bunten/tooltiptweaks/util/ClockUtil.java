package net.bunten.tooltiptweaks.util;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;

public class ClockUtil {

    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();
    
    public static String getClockTime() {
        boolean twelveHour = config.clockTimeDisplay == 1;

        var world = client.world;
        
        long time = world.getTimeOfDay();

        int actualHour = (int) ((time / 1000L + 6L) % 24L);
        int actualMinute = (int) (60L * (time % 1000L) / 1000L);

        int displayedHour = actualHour;
        int displayedMinute = actualMinute;

        String suffix = "";

        if (twelveHour) {
            if (actualHour > 12) {
                displayedHour -= 12;
            } else if (actualHour == 0) {
                displayedHour = 12;
            }

            if (actualHour >= 12) {
                suffix = " PM";
            } else {
                suffix = " AM";
            }
        }

        String hourDisplay = String.valueOf(displayedHour);
        String minuteDisplay = String.valueOf(displayedMinute);

        if (displayedHour < 10 && !twelveHour) {
            hourDisplay = "0" + hourDisplay;
        }

        if (displayedMinute < 10) {
            minuteDisplay = "0" + minuteDisplay;
        }

        return hourDisplay + ":" + minuteDisplay + suffix;
    }
}