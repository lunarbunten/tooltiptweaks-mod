package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.ClockTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ClockTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    public static MutableText getClockTime() {
        boolean twelveHour = TooltipTweaksConfig.getInstance().clockTime == ClockTime.TWELVE_HOUR;
        long time = MinecraftClient.getInstance().world.getTimeOfDay();

        int hour = (int) ((time / 1000L + 6L) % 24L);
        int minute = (int) (60L * (time % 1000L) / 1000L);

        int displayedHour = twelveHour ? (hour % 12 == 0 ? 12 : hour % 12) : hour;

        String hourDisplay = String.format("%02d", displayedHour);
        String minuteDisplay = String.format("%02d", minute);

        MutableText suffix = twelveHour ? Text.translatable("tooltiptweaks.ui.clock." + (hour >= 12 ? "pm" : "am")) : Text.literal("");

        return Text.translatable("tooltiptweaks.ui.clock.time", hourDisplay, minuteDisplay, suffix);
    }

    public void register(ItemStack stack, List<Text> lines) {
        @Nullable ClientWorld world = client.world;
        if (!stack.isOf(Items.CLOCK) || world == null) return;

        if (config.clockTime != ClockTime.DISABLED) {
            MutableText message = !world.getDimension().natural() ? Text.translatable("tooltiptweaks.ui.unknown") : getClockTime();
            lines.add(message.formatted(Formatting.GRAY));
        }

        if (config.showMoonPhase) {
            MutableText message = !world.getDimension().natural() ? Text.translatable("tooltiptweaks.ui.unknown") : Text.translatable("tooltiptweaks.ui.clock.moon_phase", Text.translatable("tooltiptweaks.ui.clock.moon_phase_" + world.getMoonPhase()));
            lines.add(message.formatted(Formatting.GRAY));
        }
    }
}
