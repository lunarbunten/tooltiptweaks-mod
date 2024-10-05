package net.bunten.tooltiptweaks.tooltip;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.util.ClockUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToolTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private void addClockTooltips(ItemStack stack, List<Text> lines) {
        @Nullable var world = client.world;

        var text = !world.getDimension().natural() ? Text.translatable("tooltiptweaks.ui.clock.unknown") : Text.literal(ClockUtil.getClockTime());
        var message = text.formatted(Formatting.GRAY);

        if (message != null) {
            lines.add(message);
        }
    }

    private void addCompassTooltips(ItemStack stack, List<Text> lines) {
        if (stack.getComponents().contains(DataComponentTypes.LODESTONE_TRACKER)) {
            if (config.lodestoneCompassDisplay) {
                var component = stack.getComponents().get(DataComponentTypes.LODESTONE_TRACKER);

                @Nullable var world = client.world;
                if (component.target().isPresent() && component.target().get().dimension() == world.getRegistryKey()) {
                    var pos = component.target().get().pos();

                    if (Screen.hasShiftDown()) {
                        lines.add(Text.translatable("tooltiptweaks.ui.lodestone_compass.position", pos.getX(), pos.getY(), pos.getZ()).formatted(Formatting.DARK_GREEN));
                        lines.add(Text.translatable("tooltiptweaks.ui.lodestone_compass.dimension", component.target().get().dimension().getValue().toTranslationKey()).formatted(Formatting.DARK_GREEN));
                    } else {
                        lines.add(Text.translatable("tooltiptweaks.ui.lodestone_compass.unshifted").formatted(Formatting.GRAY));
                    }
                }
            }
        }
    }

    public void addTooltips(ItemStack stack, TooltipType type, List<Text> lines) {
        @Nullable var world = client.world;
        
        if (world != null) {
            if (stack.isOf(Items.CLOCK) && config.clockTimeDisplay > 0) addClockTooltips(stack, lines);
            addCompassTooltips(stack, lines);
        }
    }
}