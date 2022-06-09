package net.bunten.tooltiptweaks.tooltip;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FoodTooltips {

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private void addFoodTooltips(ItemStack stack, List<Text> lines) {
        var food = stack.getItem().getFoodComponent();
        var color = Formatting.DARK_GREEN;

        // Add Hunger / Saturation
        if (!(!config.showHungerRestoration && !config.showSaturationRestoration)) {
            lines.add(Text.literal(" "));
            lines.add(Text.translatable("tooltiptweaks.ui.food.when_consumed").formatted(Formatting.GRAY));
        }

        if (config.showHungerRestoration) {
            lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.food", food.getHunger()).formatted(color)));
        }

        if (config.showSaturationRestoration) {
            lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food.saturation", food.getSaturationModifier() * 2).formatted(color)));
        }

        if (config.showFoodStatusEffects) {
            // Add Status Effects
            int i = 0;
            for (Pair<StatusEffectInstance, Float> pair : food.getStatusEffects()) {
                var instance = pair.getFirst();
                MutableText mutableText = Text.translatable(instance.getTranslationKey());
                StatusEffect effect = instance.getEffectType();

                if (instance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.durationToString(instance, 1));
                }

                if (!config.hideNegativeFoodEffects || effect.getCategory() != StatusEffectCategory.HARMFUL) {
                    if (food.getStatusEffects().size() > 0 && i == 0) {
                        lines.add(Text.translatable("tooltiptweaks.ui.food.effects").formatted(Formatting.GRAY));
                    }

                    lines.add(Text.literal(" ").append(mutableText.formatted(effect.getCategory().getFormatting())));
                    i++;
                }
            }
        }
    }

    private void addStewTooltips(ItemStack stack, List<Text> lines) {
        NbtCompound nbt = stack.getNbt();
        if (nbt != null && nbt.contains("Effects", 9)) {
            NbtList effects = nbt.getList("Effects", 10);
            for (int i = 0; i < effects.size(); i++) {
                StatusEffect effect;
                int duration = 160;
                NbtCompound nbt2 = effects.getCompound(i);
                if (nbt2.contains("EffectDuration", 3)) {
                    duration = nbt2.getInt("EffectDuration");
                }

                if ((effect = StatusEffect.byRawId(nbt2.getByte("EffectId"))) == null) continue;

                var instance = new StatusEffectInstance(effect, duration);
                MutableText mutableText = Text.translatable(instance.getTranslationKey());

                if (instance.getAmplifier() > 0) {
                    mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.durationToString(instance, 1));
                }

                if (!config.hideNegativeStewEffects || effect.getCategory() != StatusEffectCategory.HARMFUL) {
                    if (effects.size() > 0) {
                        lines.add(Text.literal("Effects:").formatted(Formatting.GRAY));
                    }

                    lines.add(Text.literal(" ").append(mutableText.formatted(effect.getCategory().getFormatting())));
                }
            }
        }
    }

    public void addTooltips(ItemStack stack, TooltipContext context, List<Text> lines) {
        if (stack.isFood()) {
            addFoodTooltips(stack, lines);
        }

        if (stack.getItem() instanceof SuspiciousStewItem && !config.hideStewEffects) {
            addStewTooltips(stack, lines);
        }
    }
}