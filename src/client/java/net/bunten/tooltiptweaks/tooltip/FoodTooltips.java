package net.bunten.tooltiptweaks.tooltip;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class FoodTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private void addFoodTooltips(ItemStack stack, List<Text> lines) {
        var food = stack.getComponents().get(DataComponentTypes.FOOD);
        var color = Formatting.DARK_GREEN;

        // Add Hunger / Saturation
        if (!(!config.showHungerRestoration && !config.showSaturationRestoration)) {
            lines.add(Text.literal(" "));
            lines.add(Text.translatable("tooltiptweaks.ui.food.when_consumed").formatted(Formatting.GRAY));
        }

        if (config.showHungerRestoration) {
            lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.food", food.nutrition()).formatted(color)));
        }

        if (config.showSaturationRestoration) {
            lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.saturation", food.saturation() * 2).formatted(color)));
        }

        if (config.showFoodStatusEffects) {

            if (!config.hideStewEffects && stack.isOf(Items.SUSPICIOUS_STEW)) return;

            // Add Status Effects
            int i = 0;
            for (FoodComponent.StatusEffectEntry entry : food.effects()) {
                var instance = entry.effect();
                MutableText mutableText = Text.translatable(instance.getTranslationKey());

                if (instance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(instance, 1.0F, client.world.getTickManager().getTickRate()));
                }

                StatusEffect effect = instance.getEffectType().value();

                if (!config.hideNegativeFoodEffects || effect.getCategory() != StatusEffectCategory.HARMFUL) {
                    if (!food.effects().isEmpty() && i == 0) {
                        lines.add(Text.translatable("tooltiptweaks.ui.food.effects").formatted(Formatting.GRAY));
                    }

                    lines.add(Text.literal(" ").append(mutableText.formatted(effect.getCategory().getFormatting())));
                    i++;
                }
            }
        }
    }

    public void addTooltips(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        if (stack.getComponents().contains(DataComponentTypes.FOOD)) addFoodTooltips(stack, lines);
    }
}