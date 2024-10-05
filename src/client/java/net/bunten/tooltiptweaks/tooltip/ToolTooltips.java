package net.bunten.tooltiptweaks.tooltip;

import com.ibm.icu.text.DecimalFormat;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.util.ClockUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ToolTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private DecimalFormat getDurabilityDecimalFormat() {
        String string = "#";
        for (int i = 0; i < config.percentageDecimalCount; i++) {
            if (i == 0) {
                string += ".";
            }
            string += "#";
        }

        return new DecimalFormat(string);
    }

    private int getDurabilityTextColor(float max, float damage) {
        float f = Math.max(0, (max - damage) / max);
        return MathHelper.hsvToRgb(f / 3, 1, 1);
    }

    private void addDurabilityTooltip(ItemStack stack, List<Text> lines, float max, float damage) {
        float durability = max - damage;
        float percent = (durability / max) * 100;

        MutableText message;
        switch (config.durabilityDisplay) {
            case 1:
                message = Text.translatable("tooltiptweaks.ui.durability", getDurabilityDecimalFormat().format(percent) + "%");
                lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
                break;
            case 2:
                message = Text.translatable("tooltiptweaks.ui.durability", (int) durability + " / " + (int) max);
                lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
                break;
            default:
                break;
        }

        if ((config.toolUsesLeft == 2 ? percent <= config.lowDurabilityThreshold : config.toolUsesLeft > 0) && percent > 0) {
            message = Text.translatable("tooltiptweaks.ui.uses_left", new DecimalFormat("#").format(durability));
            lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
        }
    }

    private void addClockTooltips(ItemStack stack, List<Text> lines) {
        @Nullable ClientWorld world = client.world;
        if (world == null) return;

        MutableText text = !world.getDimension().natural() ? Text.translatable("tooltiptweaks.ui.unknown") : Text.literal(ClockUtil.getClockTime());
        MutableText message = text.formatted(Formatting.GRAY);

        if (message != null) lines.add(message);
    }

    private int roundedHorizontalDistance(BlockPos pos, BlockPos pos2) {
        float x = pos.getX() - pos2.getX();
        float z = pos.getZ() - pos2.getZ();
        return (int) MathHelper.sqrt(x * x + z * z);
    }

    private void addCompassTooltips(ItemStack stack, List<Text> lines) {
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null) return;

        Optional<GlobalPos> target = Optional.empty();

        if (stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER)) {
            LodestoneTrackerComponent component = stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER);
            if (component != null) target = component.target();
        } else {
            if (stack.isOf(Items.COMPASS)) target = Optional.ofNullable(CompassItem.createSpawnPos(world));
            if (stack.isOf(Items.RECOVERY_COMPASS)) target = client.player.getLastDeathPos();
        }

        target.ifPresent(global -> {
            if (config.compassDisplay < 2) {
                BlockPos pos = global.pos();
                if (Screen.hasShiftDown()) {
                    if (config.compassDisplay == 0) {
                        int int_distance = roundedHorizontalDistance(player.getBlockPos(), pos);
                        MutableText value = Text.translatable("tooltiptweaks.ui.compass.distance.value", int_distance, Text.translatable("tooltiptweaks.ui.compass.distance.append"));
                        MutableText unknown = Text.translatable("tooltiptweaks.ui.unknown");

                        lines.add(Text.translatable("tooltiptweaks.ui.compass.distance", global.dimension() == world.getRegistryKey() ? value : unknown).formatted(Formatting.DARK_GREEN));
                    } else if (config.compassDisplay == 1) {
                        MutableText position = Text.translatable("tooltiptweaks.ui.compass.position.coordinates", pos.getX(), pos.getY(), pos.getZ());
                        MutableText unknown_position = Text.translatable("tooltiptweaks.ui.unknown");

                        lines.add(Text.translatable("tooltiptweaks.ui.compass.position", global.dimension() == world.getRegistryKey() ? position : unknown_position).formatted(Formatting.DARK_GREEN));
                    }
                } else {
                    lines.add(Text.translatable("tooltiptweaks.ui.compass.unshifted").formatted(Formatting.GRAY));
                }
            }
        });
    }

    public void addTooltips(ItemStack stack, TooltipType type, List<Text> lines) {
        if (stack.isDamageable() && stack.isDamaged()) addDurabilityTooltip(stack, lines, stack.getMaxDamage(), stack.getDamage());
        if (stack.isOf(Items.CLOCK) && config.clockTimeDisplay > 0) addClockTooltips(stack, lines);
        addCompassTooltips(stack, lines);
    }
}