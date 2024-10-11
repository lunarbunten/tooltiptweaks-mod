package net.bunten.tooltiptweaks.tooltips.text;

import com.mojang.datafixers.util.Pair;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.EffectDisplay;
import net.bunten.tooltiptweaks.config.options.OtherEffectDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
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

    private boolean configAllows(ItemStack stack) {
        if (stack.getItem() instanceof PotionItem) return config.updatePotionTooltips;
        if (stack.isOf(Items.TIPPED_ARROW) || stack.isOf(Items.SPECTRAL_ARROW)) return config.updateTippedArrowTooltips;
        return true;
    }

    private List<StatusEffectInstance> getStatusEffects(ItemStack stack, ComponentType<?> component) {
        List<StatusEffectInstance> effects = new ArrayList<>();

        if (stack.isOf(Items.SPECTRAL_ARROW)) {
            effects.add(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0));
            return effects;
        }

        if (!stack.contains(component)) return effects;

        if (component == DataComponentTypes.FOOD) {
            stack.get(DataComponentTypes.FOOD).effects().forEach(entry -> effects.add(entry.effect()));
        }
        if (component == DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) {
            stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS).effects().forEach(effect -> effects.add(effect.createStatusEffectInstance()));
        }
        if (component == DataComponentTypes.POTION_CONTENTS) {
            stack.get(DataComponentTypes.POTION_CONTENTS).getEffects().forEach(effects::add);
        }
        if (component == DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER) {
            Integer integer = stack.getOrDefault(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, 0);
            return List.of(new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, integer, false, false, true));
        }

        return effects;
    }

    private void addEffects(ItemStack stack, List<Text> lines, List<StatusEffectInstance> effects, EffectDisplay style) {
        boolean isPotion = stack.getItem() instanceof PotionItem;
        boolean isWaterBottle = isPotion && stack.get(DataComponentTypes.POTION_CONTENTS).potion().get() == Potions.WATER;

        if ((!isPotion && effects.isEmpty()) || isWaterBottle) return;

        if (isPotion && effects.isEmpty()) {
            if (!lines.contains(WHEN_CONSUMED_HEADER))  lines.add(Text.literal(" "));
            lines.add(STATUS_EFFECTS_HEADER);

            lines.add(Text.literal(" ").append(Text.translatable("effect.none").formatted(Formatting.DARK_GRAY)));
            return;
        }

        List<StatusEffectInstance> filtered = effects.stream().filter((instance) -> style == EffectDisplay.POSITIVE_EFFECTS_ONLY && !creative() ? instance.getEffectType().value().getCategory() != StatusEffectCategory.HARMFUL : true).toList();

        if (!filtered.isEmpty()) {
            if (!lines.contains(WHEN_CONSUMED_HEADER))  lines.add(Text.literal(" "));
            lines.add(STATUS_EFFECTS_HEADER);

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

    private void addModifiers(List<Text> lines, List<StatusEffectInstance> effects) {
        List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> modifiers = new ArrayList<>();
        effects.forEach((instance) -> instance.getEffectType().value().forEachAttributeModifier(instance.getAmplifier(), (attribute, modifier) -> modifiers.add(new Pair<>(attribute, modifier))));

        if (modifiers.isEmpty()) return;

        if (!lines.contains(WHEN_CONSUMED_HEADER) && !lines.contains(STATUS_EFFECTS_HEADER)) lines.add(Text.literal(" "));

        lines.add(MODIFIERS_HEADER);

        modifiers.forEach((pair) -> {
            EntityAttributeModifier modifier = pair.getSecond();
            double value = (modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE && modifier.operation() != EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) ? modifier.value() : modifier.value() * 100.0;
            String modifierKey = modifier.value() > 0.0 ? "attribute.modifier.plus." : "attribute.modifier.take.";
            lines.add(Text.translatable(modifierKey + modifier.operation().getId(), AttributeModifiersComponent.DECIMAL_FORMAT.format(Math.abs(value)), Text.translatable(pair.getFirst().value().getTranslationKey())).formatted(modifier.value() > 0.0 ? POSITIVE_MODIFIER_COLOR : NEGATIVE_MODIFIER_COLOR));
        });
    }

    public void register(ItemStack stack, List<Text> lines) {
        ComponentType<?> component = DataComponentTypes.FOOD;
        EffectDisplay style = config.foodEffectDisplay;

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

        List<StatusEffectInstance> effects = getStatusEffects(stack, component);
        if (EffectDisplay.canDisplay(style) && configAllows(stack)) addEffects(stack, lines, effects, style);
        if (OtherEffectDisplay.canDisplay(config.modifierDisplay)) addModifiers(lines, effects);
    }
}