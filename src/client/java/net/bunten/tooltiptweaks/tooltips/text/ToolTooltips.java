package net.bunten.tooltiptweaks.tooltips.text;

import com.ibm.icu.text.DecimalFormat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.creative;

public class ToolTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    public record Variant(AxolotlEntity.Variant variant) {
        public static final Codec<Variant> CODEC = Codec.INT.xmap(Variant::new, Variant::getId);

        public Variant(int id) {
            this(AxolotlEntity.Variant.byId(id));
        }

        public int getId() {
            return variant.getId();
        }

        public String getName() {
            return variant().getName();
        }
    }

    private static final MapCodec<Variant> AXOLOTL_VARIANT_MAP_CODEC = Variant.CODEC.fieldOf("Variant");

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

    private void addDurabilityTooltip(ItemStack stack, List<Text> lines) {
        if (!stack.isDamageable() || !stack.isDamaged()) return;

        float max = stack.getMaxDamage();
        float damage = stack.getDamage();

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

        if (config.displayUsesLeft == 1 && percent <= 25) {
            message = Text.translatable("tooltiptweaks.ui.uses_left", new DecimalFormat("#").format(durability));
            lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
        }
    }

    private int getRepairCostTextColor(int repairCost) {
        float max = 36;
        float damage = Math.min(repairCost, max);

        float f = Math.max(0, (max - damage) / max);
        return MathHelper.hsvToRgb(f / 3, 1, 1);
    }

    private void addRepairCostTooltip(ItemStack stack, List<Text> lines) {
        if (!stack.contains(DataComponentTypes.REPAIR_COST)) return;
        Integer repairCost = stack.get(DataComponentTypes.REPAIR_COST);
        if (repairCost < 1) return;
        MutableText message = Text.translatable("tooltiptweaks.ui.repair_cost", repairCost);

        if (!creative() && repairCost + 1 >= 40)
            message = Text.translatable("tooltiptweaks.ui.cannot_repair");

        lines.add(message.setStyle(message.getStyle().withColor(getRepairCostTextColor(repairCost))));
    }

    public static MutableText getClockTime() {
        boolean twelveHour = TooltipTweaksConfig.getInstance().clockTimeDisplay == 1;
        long time = MinecraftClient.getInstance().world.getTimeOfDay();

        int hour = (int) ((time / 1000L + 6L) % 24L);
        int minute = (int) (60L * (time % 1000L) / 1000L);

        int displayedHour = twelveHour ? (hour % 12 == 0 ? 12 : hour % 12) : hour;

        String hourDisplay = String.format("%02d", displayedHour);
        String minuteDisplay = String.format("%02d", minute);

        MutableText suffix = twelveHour ? Text.translatable("tooltiptweaks.ui.clock." + (hour >= 12 ? "pm" : "am")) : Text.literal("");

        return Text.translatable("tooltiptweaks.ui.clock.time", hourDisplay, minuteDisplay, suffix);
    }

    private void addClockTooltip(ItemStack stack, List<Text> lines) {
        @Nullable ClientWorld world = client.world;
        if (!stack.isOf(Items.CLOCK) || world == null) return;

        if (config.clockTimeDisplay > 0) {
            MutableText message = !world.getDimension().natural() ? Text.translatable("tooltiptweaks.ui.unknown") : getClockTime();
            lines.add(message.formatted(Formatting.GRAY));
        }

        if (config.clockMoonPhaseDisplay == 0) {
            MutableText message = !world.getDimension().natural() ? Text.translatable("tooltiptweaks.ui.unknown") : Text.translatable("tooltiptweaks.ui.clock.moon_phase", Text.translatable("tooltiptweaks.ui.clock.moon_phase_" + world.getMoonPhase()));
            lines.add(message.formatted(Formatting.GRAY));
        }
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

    private void addAxolotlVariantTooltip(ItemStack stack, List<Text> lines) {
        if (config.axolotlVariants == 1) return;

        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT);
        if (nbt.isEmpty()) return;

        Optional<Variant> optional = nbt.get(AXOLOTL_VARIANT_MAP_CODEC).result();

        optional.ifPresent((variant) -> {
            Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};

            MutableText text = Text.translatable("tooltiptweaks.ui.axolotl." + variant.getName());
            MutableText message = text.formatted(Formatting.GRAY);

            message.formatted(formattings);

            lines.add(message);
        });
    }

    public void register(ItemStack stack, List<Text> lines) {
        addDurabilityTooltip(stack, lines);
        addRepairCostTooltip(stack, lines);
        addClockTooltip(stack, lines);
        addCompassTooltips(stack, lines);
        addAxolotlVariantTooltip(stack, lines);
    }
}