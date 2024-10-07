package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.ClockTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
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

    private static final MutableText UNKNOWN_TEXT = Text.translatable("tooltiptweaks.ui.unknown");

    public static MutableText getClockText(ClientWorld world) {
        MutableText value = UNKNOWN_TEXT;

        if (world.getDimension().natural()) {
            boolean twelveHour = TooltipTweaksConfig.getInstance().clockTime == ClockTime.TWELVE_HOUR;
            long time = MinecraftClient.getInstance().world.getTimeOfDay();

            int hour = (int) ((time / 1000L + 6L) % 24L);
            int minute = (int) (60L * (time % 1000L) / 1000L);

            int displayedHour = twelveHour ? (hour % 12 == 0 ? 12 : hour % 12) : hour;

            String hourDisplay = twelveHour ? String.format("%d", displayedHour) : String.format("%02d", displayedHour);
            String minuteDisplay = String.format("%02d", minute);

            MutableText suffix = twelveHour ? Text.translatable("tooltiptweaks.ui.clock." + (hour >= 12 ? "pm" : "am")) : Text.literal("");
            value = Text.translatable("tooltiptweaks.ui.clock.time.value", hourDisplay, minuteDisplay, suffix);
        }

        return Text.translatable("tooltiptweaks.ui.clock.time", value);
    }

    public static MutableText getDayText(ClientWorld world) {
        MutableText value = !world.getDimension().natural() ? UNKNOWN_TEXT : Text.literal(String.valueOf(world.getTimeOfDay() / 24000L));
        return Text.translatable("tooltiptweaks.ui.clock.day_number", value);
    }

    public static MutableText getMoonPhaseText(ClientWorld world) {
        MutableText value = !world.getDimension().natural() ? UNKNOWN_TEXT : Text.translatable("tooltiptweaks.ui.clock.moon_phase.value_" + world.getMoonPhase());
        return Text.translatable("tooltiptweaks.ui.clock.moon_phase", value);
    }

    public void register(ItemStack stack, List<Text> lines) {
        @Nullable ClientWorld world = client.world;
        if (!stack.isOf(Items.CLOCK) || world == null) return;

        MutableText dayText = getDayText(world);
        MutableText timeText = getClockText(world);
        MutableText phaseText = getMoonPhaseText(world);

        if (config.displayDayNumber && config.clockTime == ClockTime.DISABLED) lines.add(dayText.formatted(Formatting.GRAY));

        if (config.clockTime != ClockTime.DISABLED) {
            MutableText value = config.displayDayNumber  ? Text.translatable("tooltiptweaks.ui.clock.multiple_values", dayText, timeText) : timeText;
            lines.add(value.formatted(Formatting.GRAY));
        }

        if (config.displayMoonPhase) lines.add(phaseText.formatted(Formatting.GRAY));
    }
}