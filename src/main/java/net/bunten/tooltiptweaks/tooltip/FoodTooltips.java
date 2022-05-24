package net.bunten.tooltiptweaks.tooltip;

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
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

import java.util.List;

public class FoodTooltips {

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private LiteralText literal(String key) {
        return new LiteralText(key);
    }

    private TranslatableText translatable(String key) {
        return new TranslatableText(key);
    }

    private TranslatableText translatable(String key, Object ... args) {
        return new TranslatableText(key, args);
    }

    private void addFoodTooltips(ItemStack stack, List<Text> lines) {
        var food = stack.getItem().getFoodComponent();
        var color = Formatting.DARK_GREEN;

        // Add Hunger / Saturation
        if (!(!config.showHungerRestoration && !config.showSaturationRestoration)) {
            lines.add(literal(" "));
            lines.add(translatable("tooltiptweaks.ui.food.when_consumed").formatted(Formatting.GRAY));
        }

        if (config.showHungerRestoration) {
            lines.add(literal(" ").append(translatable("tooltiptweaks.ui.food.food", food.getHunger()).formatted(color)));
        }

        if (config.showSaturationRestoration) {
            lines.add(literal(" ").append(translatable("tooltiptweaks.ui.food.saturation", food.getSaturationModifier() * 2).formatted(color)));
        }

        if (config.showFoodStatusEffects) {
            // Add Status Effects
            int i = 0;
            for (Pair<StatusEffectInstance, Float> pair : food.getStatusEffects()) {
                var instance = pair.getFirst();
                MutableText mutableText = new TranslatableText(instance.getTranslationKey());
                StatusEffect effect = instance.getEffectType();

                if (instance.getAmplifier() > 0) {
                    mutableText = new TranslatableText("potion.withAmplifier", mutableText, new TranslatableText("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    mutableText = new TranslatableText("potion.withDuration", mutableText, StatusEffectUtil.durationToString(instance, 1));
                }

                if (!config.hideNegativeFoodEffects || effect.getCategory() != StatusEffectCategory.HARMFUL) {
                    if (food.getStatusEffects().size() > 0 && i == 0) {
                        lines.add(translatable("tooltiptweaks.ui.food.effects").formatted(Formatting.GRAY));
                    }

                    lines.add(literal(" ").append(mutableText.formatted(effect.getCategory().getFormatting())));
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
                MutableText mutableText = new TranslatableText(instance.getTranslationKey());

                if (instance.getAmplifier() > 0) {
                    mutableText = new TranslatableText("potion.withAmplifier", mutableText, new TranslatableText("potion.potency." + instance.getAmplifier()));
                }

                if (instance.getDuration() > 20) {
                    mutableText = new TranslatableText("potion.withDuration", mutableText, StatusEffectUtil.durationToString(instance, 1));
                }

                if (!config.hideNegativeStewEffects || effect.getCategory() != StatusEffectCategory.HARMFUL) {
                    if (effects.size() > 0) {
                        lines.add(literal("Effects:").formatted(Formatting.GRAY));
                    }

                    lines.add(literal(" ").append(mutableText.formatted(effect.getCategory().getFormatting())));
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