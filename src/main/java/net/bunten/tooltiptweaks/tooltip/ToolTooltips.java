package net.bunten.tooltiptweaks.tooltip;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.util.ClockUtil;
import net.minecraft.block.BeehiveBlock;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

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
        if (CompassItem.hasLodestone(stack)) {
            if (config.lodestoneCompassDisplay) {
                var nbt = stack.getNbt();

                boolean bl = nbt.contains("LodestonePos");
                boolean bl2 = nbt.contains("LodestoneDimension");
                
                var dimension = World.CODEC.parse(NbtOps.INSTANCE, nbt.get("LodestoneDimension")).result();;
                Optional<RegistryKey<World>> optional;

                @Nullable var world = client.world;
                if (bl && bl2 && (optional = dimension).isPresent() && world.getRegistryKey() == optional.get()) {
                    var pos = NbtHelper.toBlockPos(nbt.getCompound("LodestonePos"));

                    if (Screen.hasShiftDown()) {
                        lines.add(Text.translatable("tooltiptweaks.ui.lodestone_compass.position", pos.getX(), pos.getY(), pos.getZ()).formatted(Formatting.DARK_GREEN));
                        lines.add(Text.translatable("tooltiptweaks.ui.lodestone_compass.dimension", nbt.getString("LodestoneDimension")).formatted(Formatting.DARK_GREEN));
                    } else {
                        lines.add(Text.translatable("tooltiptweaks.ui.lodestone_compass.unshifted").formatted(Formatting.GRAY));
                    }
                }
            }
        }
    }

    private void addBeehiveTooltips(BlockItem item, ItemStack stack, List<Text> lines) {
        if (item.getBlock() instanceof BeehiveBlock) {
            var nbt = BlockItem.getBlockEntityNbt(stack);
            if (nbt != null) {
                if (nbt.contains(BeehiveBlockEntity.BEES_KEY, NbtElement.LIST_TYPE)) {
                    var bees = nbt.getList(BeehiveBlockEntity.BEES_KEY, NbtElement.COMPOUND_TYPE).size();
                    var honey = stack.getSubNbt("BlockStateTag").getInt("honey_level");

                    if (config.beehiveBeeDisplay) {
                        lines.add(Text.translatable("tooltiptweaks.ui.beehive.bees", bees).formatted(Formatting.GRAY));
                    }
                    if (config.beehiveHoneyDisplay) {
                        lines.add(Text.translatable("tooltiptweaks.ui.beehive.honey", honey).formatted(Formatting.GRAY));
                    }
                }
            }
        }
    }

    public void addTooltips(ItemStack stack, TooltipContext context, List<Text> lines) {
        @Nullable var world = client.world;
        if (world != null) {
            if (stack.isOf(Items.CLOCK) && config.clockTimeDisplay > 0) {
                addClockTooltips(stack, lines);
            }

            if (stack.getItem() instanceof CompassItem) {
                addCompassTooltips(stack, lines);
            }

            if (stack.getItem() instanceof BlockItem item) {
                new ShulkerBoxTooltips().addTooltips(stack, lines);
                addBeehiveTooltips(item, stack, lines);
            }
        }
    }
}