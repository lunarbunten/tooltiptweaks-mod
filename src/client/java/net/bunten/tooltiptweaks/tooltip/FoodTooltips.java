package net.bunten.tooltiptweaks.tooltip;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.List;

public class FoodTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private static final Formatting FOOD_COLOR = Formatting.GOLD;
    private static final Formatting OTHER_COLOR = Formatting.BLUE;

    private void addWhenConsumed(ItemStack stack, List<Text> lines, boolean whenFullyConsumed) {
        lines.add(Text.literal(" "));
        String append = whenFullyConsumed ? "when_fully_consumed" : "when_consumed";
        lines.add(Text.translatable("tooltiptweaks.ui.food." + append).formatted(Formatting.GRAY));
    }

    private void addFoodPoints(ItemStack stack, List<Text> lines, int nutrition) {
        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.food", nutrition).formatted(FOOD_COLOR)));
    }

    private void addSaturation(ItemStack stack, List<Text> lines, float saturation) {
        String formattedSaturation = new DecimalFormat("#.#").format(saturation);
        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.saturation", formattedSaturation).formatted(FOOD_COLOR)));
    }

    private void addPrimaryTooltips(ItemStack stack, TooltipType type, List<Text> lines) {
        if (stack.isOf(Items.CAKE) && config.foodDisplay <= 1) {
            addWhenConsumed(stack, lines, true);
            addFoodPoints(stack, lines, 14);
            if (config.foodDisplay == 0) addSaturation(stack, lines, 2.4F);
        }

        if (!stack.getComponents().contains(DataComponentTypes.FOOD)) return;

        FoodComponent food = stack.getComponents().get(DataComponentTypes.FOOD);
        if (food == null) return;

        if (config.foodDisplay <= 1 || (stack.isOf(Items.HONEY_BOTTLE)) && config.otherEffectDisplay < 1)
            addWhenConsumed(stack, lines, false);

        if (config.foodDisplay <= 1)
            addFoodPoints(stack, lines, food.nutrition());

        if (config.foodDisplay == 0)
            addSaturation(stack, lines, food.saturation());
    }

    private void addFoodEffectTooltips(ItemStack stack, TooltipType type, List<Text> lines) {
        if (!stack.getComponents().contains(DataComponentTypes.FOOD)) return;
        FoodComponent food = stack.getComponents().get(DataComponentTypes.FOOD);
        if (food == null) return;

        if (config.foodEffectDisplay < 2 || (config.foodEffectDisplay == 2 && type.isCreative())) {
            int i = 0;
            for (FoodComponent.StatusEffectEntry entry : food.effects()) {
                StatusEffectInstance instance = entry.effect();
                StatusEffectCategory category = instance.getEffectType().value().getCategory();

                MutableText mutableText = Text.translatable(instance.getTranslationKey());

                if (instance.getAmplifier() > 0)
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + instance.getAmplifier()));

                if (instance.getDuration() > 20)
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(instance, 1.0F, client.world.getTickManager().getTickRate()));

                if (config.foodEffectDisplay == 0 || category != StatusEffectCategory.HARMFUL) {
                    if (!food.effects().isEmpty() && i == 0) {
                        lines.add(Text.translatable("tooltiptweaks.ui.food.effects").formatted(Formatting.GRAY));
                    }

                    lines.add(Text.literal(" ").append(mutableText.formatted(category.getFormatting())));
                    i++;
                }
            }
        }
    }

    private void addStewTooltips(ItemStack stack, TooltipType type, List<Text> lines) {
        if (!stack.getComponents().contains(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)) return;
        SuspiciousStewEffectsComponent stewEffects = stack.getComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS);
        if (stewEffects == null) return;

        if (config.stewEffectDisplay < 2 || (config.stewEffectDisplay == 2 && type.isCreative())) {
            int i = 0;
            for (SuspiciousStewEffectsComponent.StewEffect effect : stewEffects.effects()) {
                StatusEffectInstance instance = effect.createStatusEffectInstance();
                StatusEffectCategory category = instance.getEffectType().value().getCategory();

                MutableText mutableText = Text.translatable(instance.getTranslationKey());

                if (instance.getAmplifier() > 0)
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + instance.getAmplifier()));

                if (instance.getDuration() > 20)
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(instance, 1.0F, client.world.getTickManager().getTickRate()));

                if (config.stewEffectDisplay == 0 || category != StatusEffectCategory.HARMFUL) {
                    if (!stewEffects.effects().isEmpty() && i == 0) {
                        lines.add(Text.translatable("tooltiptweaks.ui.food.effects").formatted(Formatting.GRAY));
                    }

                    lines.add(Text.literal(" ").append(mutableText.formatted(category.getFormatting())));
                    i++;
                }
            }
        }
    }

    public void addTooltips(ItemStack stack, TooltipType type, List<Text> lines) {
        addPrimaryTooltips(stack, type, lines);
        addFoodEffectTooltips(stack, type, lines);
        addStewTooltips(stack, type, lines);

        // Add Honey Bottle Effects
        if (stack.isOf(Items.HONEY_BOTTLE) && (config.otherEffectDisplay < 1 || (config.otherEffectDisplay == 1 && type.isCreative()))) {
            lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.honey_bottle").formatted(OTHER_COLOR)));
        }

        // Add Milk Bucket Effects
        if (stack.isOf(Items.MILK_BUCKET)) {
            if (config.otherEffectDisplay < 1 || (config.otherEffectDisplay == 1 && type.isCreative())) {
                addWhenConsumed(stack, lines, false);
                lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.milk_bucket").formatted(OTHER_COLOR)));
            }
        }

    }
}