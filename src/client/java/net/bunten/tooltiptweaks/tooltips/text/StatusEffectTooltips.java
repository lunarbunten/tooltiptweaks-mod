package net.bunten.tooltiptweaks.tooltips.text;

import com.mojang.datafixers.util.Pair;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.EffectDisplay;
import net.bunten.tooltiptweaks.config.options.OtherEffectDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.OminousBottleAmplifierComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.TippedArrowItem;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.RemoveEffectsConsumeEffect;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.creative;
import static net.bunten.tooltiptweaks.tooltips.CommonText.*;

public class StatusEffectTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private List<StatusEffectInstance> statusEffects;
    private EffectDisplay style;

    private boolean configAllows(ItemStack stack) {
        if (stack.getItem() instanceof PotionItem || stack.isOf(Items.OMINOUS_BOTTLE)) return config.updatePotionTooltips;
        if (stack.isOf(Items.TIPPED_ARROW) || stack.isOf(Items.SPECTRAL_ARROW)) return config.updateTippedArrowTooltips;
        return true;
    }

    public void gatherEffects(ItemStack stack) {
        ComponentType<?> component = DataComponentTypes.CONSUMABLE;
        style = config.foodEffectDisplay;

        if (stack.isOf(Items.SUSPICIOUS_STEW)) {
            component = DataComponentTypes.SUSPICIOUS_STEW_EFFECTS;
            style = config.stewEffectDisplay;
        }
        if (stack.getItem() instanceof PotionItem || stack.getItem() instanceof TippedArrowItem) {
            component = DataComponentTypes.POTION_CONTENTS;
            style = EffectDisplay.ALL_EFFECTS;
        }
        if (stack.isOf(Items.OMINOUS_BOTTLE)) {
            component = DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER;
            style = EffectDisplay.ALL_EFFECTS;
        }

        statusEffects = getAppliedStatusEffects(stack, component);
    }

    private List<StatusEffectInstance> getAppliedStatusEffects(ItemStack stack, ComponentType<?> component) {
        List<StatusEffectInstance> effects = new ArrayList<>();

        if (stack.isOf(Items.SPECTRAL_ARROW)) {
            effects.add(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0));
            return effects;
        }

        if (!stack.contains(component)) return effects;

        if (component == DataComponentTypes.CONSUMABLE) {
            stack.get(DataComponentTypes.CONSUMABLE).onConsumeEffects().forEach(entry -> {
                if (entry instanceof ApplyEffectsConsumeEffect effect) effects.addAll(effect.effects());
            });
        }
        if (component == DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) {
            stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS).effects().forEach(effect -> effects.add(effect.createStatusEffectInstance()));
        }

        if (component == DataComponentTypes.POTION_CONTENTS) {
            stack.get(DataComponentTypes.POTION_CONTENTS).getEffects().forEach(effects::add);
        }

        if (component == DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER) {
            OminousBottleAmplifierComponent amplifier = stack.get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER);
            return List.of(new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, amplifier.value(), false, false, true));
        }

        return effects;
    }

    private void addAppliedStatusEffects(ItemStack stack, List<Text> lines) {
        boolean isPotion = stack.getItem() instanceof PotionItem;
        boolean isWaterBottle = isPotion && stack.get(DataComponentTypes.POTION_CONTENTS).potion().get() == Potions.WATER;

        if ((!isPotion && statusEffects.isEmpty()) || isWaterBottle) return;

        if (isPotion && statusEffects.isEmpty()) {
            addStatusEffectHeader(lines);

            lines.add(Text.literal(" ").append(Text.translatable("effect.none").formatted(Formatting.DARK_GRAY)));
            return;
        }

        List<StatusEffectInstance> filtered = statusEffects.stream().filter((instance) -> style == EffectDisplay.POSITIVE_EFFECTS_ONLY && !creative() ? instance.getEffectType().value().getCategory() != StatusEffectCategory.HARMFUL : true).toList();

        if (!filtered.isEmpty()) {
            addStatusEffectHeader(lines);

            filtered.forEach((instance) -> {
                StatusEffectCategory category = instance.getEffectType().value().getCategory();

                MutableText text = Text.translatable(instance.getTranslationKey());

                if (instance.getAmplifier() > 0)
                    text = Text.translatable("potion.withAmplifier", text, Text.translatable("potion.potency." + instance.getAmplifier()));

                if (instance.getDuration() > 20)
                    text = Text.translatable("potion.withDuration", text, StatusEffectUtil.getDurationText(instance, stack.isOf(Items.LINGERING_POTION) ? 0.25F : 1.0F, client.world.getTickManager().getTickRate()));

                Formatting formatting = switch (category) {
                    case BENEFICIAL -> BENEFICIAL_STATUS_EFFECT_COLOR;
                    case NEUTRAL -> NEUTRAL_STATUS_EFFECT_COLOR;
                    default -> HARMFUL_STATUS_EFFECT_COLOR;
                };
                lines.add(Text.literal(" ").append(text.formatted(formatting)));
            });
        }
    }

    public void addRemoveEffectTooltips(ItemStack stack, List<Text> lines) {
        if (OtherEffectDisplay.canDisplay(config.modifierDisplay) && stack.contains(DataComponentTypes.CONSUMABLE)) {
            ConsumableComponent component = stack.get(DataComponentTypes.CONSUMABLE);

            component.onConsumeEffects().forEach((effect) -> {
                if (effect instanceof RemoveEffectsConsumeEffect remove) {
                    remove.effects().forEach((entry) -> {
                        addStatusEffectHeader(lines);
                        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.removes_prefix", Text.translatable(entry.value().getTranslationKey())).formatted(NEUTRAL_STATUS_EFFECT_COLOR)));
                    });
                }

                if (effect instanceof ClearAllEffectsConsumeEffect) {
                    addStatusEffectHeader(lines);
                    lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.removes_all_effects").formatted(NEUTRAL_STATUS_EFFECT_COLOR)));
                }
            });
        }
    }

    private void addModifiers(List<Text> lines) {
        if (statusEffects.isEmpty()) return;

        List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> modifiers = new ArrayList<>();
        statusEffects.forEach((instance) -> instance.getEffectType().value().forEachAttributeModifier(instance.getAmplifier(), (attribute, modifier) -> modifiers.add(new Pair<>(attribute, modifier))));

        if (modifiers.isEmpty()) return;

        addModifiersHeader(lines);

        modifiers.forEach((pair) -> {
            EntityAttributeModifier modifier = pair.getSecond();
            double value = (modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) ? modifier.value() : modifier.value() * 100.0;
            String modifierKey = modifier.value() > 0.0 ? "attribute.modifier.plus." : "attribute.modifier.take.";
            lines.add(Text.translatable(modifierKey + modifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(Math.abs(value)), Text.translatable(pair.getFirst().value().getTranslationKey())).formatted(modifier.value() > 0.0 ? POSITIVE_MODIFIER_COLOR : NEGATIVE_MODIFIER_COLOR));
        });
    }

    public void register(ItemStack stack, List<Text> lines) {
        gatherEffects(stack);

        if (EffectDisplay.canDisplay(style) && configAllows(stack)) addAppliedStatusEffects(stack, lines);
        if (OtherEffectDisplay.canDisplay(config.otherEffectDisplay)) addRemoveEffectTooltips(stack, lines);
        if (OtherEffectDisplay.canDisplay(config.modifierDisplay)) addModifiers(lines);
    }
}