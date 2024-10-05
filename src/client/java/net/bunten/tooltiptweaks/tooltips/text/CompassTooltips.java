package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;

public class CompassTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private int roundedHorizontalDistance(BlockPos pos, BlockPos pos2) {
        float x = pos.getX() - pos2.getX();
        float z = pos.getZ() - pos2.getZ();
        return (int) MathHelper.sqrt(x * x + z * z);
    }

    public void register(ItemStack stack, List<Text> lines) {
        ClientWorld world = client.world;
        ClientPlayerEntity player = client.player;
        if (world == null || player == null) return;

        Optional<GlobalPos> target = Optional.empty();

        if (stack.contains(DataComponentTypes.LODESTONE_TRACKER)) {
            LodestoneTrackerComponent component = stack.get(DataComponentTypes.LODESTONE_TRACKER);
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
                        MutableText value = Text.translatable("tooltiptweaks.ui.distance.value", int_distance, Text.translatable("tooltiptweaks.ui.distance.append"));
                        MutableText unknown = Text.translatable("tooltiptweaks.ui.unknown");

                        lines.add(Text.translatable("tooltiptweaks.ui.distance", global.dimension() == world.getRegistryKey() ? value : unknown).formatted(Formatting.DARK_GREEN));
                    } else if (config.compassDisplay == 1) {
                        MutableText position = Text.translatable("tooltiptweaks.ui.position.coordinates", pos.getX(), pos.getY(), pos.getZ());
                        MutableText unknown_position = Text.translatable("tooltiptweaks.ui.unknown");

                        lines.add(Text.translatable("tooltiptweaks.ui.position", global.dimension() == world.getRegistryKey() ? position : unknown_position).formatted(Formatting.DARK_GREEN));
                    }
                } else {
                    lines.add(Text.translatable("tooltiptweaks.ui.unshifted").formatted(Formatting.GRAY));
                }
            }
        });
    }
}
