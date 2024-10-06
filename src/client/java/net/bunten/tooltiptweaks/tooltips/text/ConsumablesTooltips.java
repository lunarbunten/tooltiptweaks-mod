package net.bunten.tooltiptweaks.tooltips.text;

import com.mojang.datafixers.util.Pair;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.EffectDisplay;
import net.bunten.tooltiptweaks.config.options.NourishmentDisplay;
import net.bunten.tooltiptweaks.config.options.NourishmentStyle;
import net.bunten.tooltiptweaks.config.options.OtherEffectDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.creative;

public class ConsumablesTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private static final Formatting NUTRITION_COLOR = Formatting.GOLD;

    private static final Formatting BENEFICIAL_STATUS_EFFECT_COLOR = Formatting.BLUE;
    private static final Formatting NEUTRAL_STATUS_EFFECT_COLOR = Formatting.BLUE;
    private static final Formatting HARMFUL_STATUS_EFFECT_COLOR = Formatting.DARK_PURPLE;

    private static final Formatting POSITIVE_MODIFIER_COLOR = Formatting.DARK_GREEN;
    private static final Formatting NEGATIVE_MODIFIER_COLOR = Formatting.RED;

    private static final MutableText WHEN_CONSUMED_HEADER = Text.translatable("tooltiptweaks.ui.when_consumed").formatted(Formatting.GRAY);
    private static final MutableText WHEN_FULLY_CONSUMED_HEADER = Text.translatable("tooltiptweaks.ui.when_fully_consumed").formatted(Formatting.GRAY);
    private static final MutableText STATUS_EFFECTS_HEADER = Text.translatable("tooltiptweaks.ui.status_effects").formatted(Formatting.GRAY);
    private static final MutableText MODIFIERS_HEADER = Text.translatable("tooltiptweaks.ui.modifiers").formatted(Formatting.GRAY);

    private void addWhenConsumed(ItemStack stack, List<Text> lines, boolean whenFullyConsumed) {
        lines.add(Text.literal(" "));
        MutableText line = whenFullyConsumed ? WHEN_FULLY_CONSUMED_HEADER : WHEN_CONSUMED_HEADER;
        lines.add(line);
    }

    private void addFoodPoints(ItemStack stack, List<Text> lines, int nutrition) {
        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.food_points", nutrition).formatted(NUTRITION_COLOR)));
    }

    private void addSaturation(ItemStack stack, List<Text> lines, float saturation) {
        String formattedSaturation = new DecimalFormat("#.#").format(saturation);
        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.saturation", formattedSaturation).formatted(NUTRITION_COLOR)));
    }

    private void addNutritionInfo(ItemStack stack, List<Text> lines) {
        if (stack.isOf(Items.CAKE) && config.nourishmentDisplay != NourishmentDisplay.DISABLED) {
            addWhenConsumed(stack, lines, true);
            addFoodPoints(stack, lines, 14);
            if (config.nourishmentDisplay == NourishmentDisplay.FOOD_AND_SATURATION) addSaturation(stack, lines, 2.4F);
        }

        if (stack.contains(DataComponentTypes.FOOD)) {
            FoodComponent food = stack.get(DataComponentTypes.FOOD);
            if (food == null) return;

            if (config.nourishmentDisplay != NourishmentDisplay.DISABLED) {
                addWhenConsumed(stack, lines, false);
                addFoodPoints(stack, lines, food.nutrition());
            }

            if (config.nourishmentDisplay == NourishmentDisplay.FOOD_AND_SATURATION)
                addSaturation(stack, lines, food.saturation());
        }
    }

    private void addEffects(ItemStack stack, List<Text> lines, List<StatusEffectInstance> effects, EffectDisplay style) {
        boolean isPotion = stack.getItem() instanceof PotionItem;
        boolean isWaterBottle = isPotion && stack.get(DataComponentTypes.POTION_CONTENTS).potion().get() == Potions.WATER;

        if ((!isPotion && effects.isEmpty()) || isWaterBottle) return;

        if (isPotion && effects.isEmpty()) {
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

    private void addModifiers(ItemStack stack, List<Text> lines, ComponentType<?> component) {
        List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> modifiers = new ArrayList<>();
        getStatusEffects(stack, component).forEach((instance) -> instance.getEffectType().value().forEachAttributeModifier(instance.getAmplifier(), (attribute, modifier) -> modifiers.add(new Pair<>(attribute, modifier))));

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

    private List<StatusEffectInstance> getStatusEffects(ItemStack stack, ComponentType<?> component) {
        List<StatusEffectInstance> effects = new ArrayList<>();
        if (component == DataComponentTypes.FOOD) {
            stack.get(DataComponentTypes.FOOD).effects().forEach(entry -> effects.add(entry.effect()));
        } else if (component == DataComponentTypes.SUSPICIOUS_STEW_EFFECTS) {
            stack.get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS).effects().forEach(effect -> effects.add(effect.createStatusEffectInstance()));
        } else if (component == DataComponentTypes.POTION_CONTENTS) {
            stack.get(DataComponentTypes.POTION_CONTENTS).getEffects().forEach(effects::add);
        } else if (component == DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER) {
            Integer integer = stack.getOrDefault(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, 0);
            return List.of(new StatusEffectInstance(StatusEffects.BAD_OMEN, 120000, integer, false, false, true));
        }

        return effects;
    }

    private void addConsumableTooltips(ItemStack stack, List<Text> lines) {
        if (!stack.isOf(Items.OMINOUS_BOTTLE) && config.nourishmentStyle == NourishmentStyle.TEXT) addNutritionInfo(stack, lines);

        ComponentType<?> effectComponent = DataComponentTypes.FOOD;

        EffectDisplay style = config.foodEffectDisplay;
        
        if (stack.isOf(Items.SUSPICIOUS_STEW)) {
            effectComponent = DataComponentTypes.SUSPICIOUS_STEW_EFFECTS;
            style = config.stewEffectDisplay;
        }
        if (stack.getItem() instanceof PotionItem) {
            effectComponent = DataComponentTypes.POTION_CONTENTS;
            style = EffectDisplay.ALL_EFFECTS;
        }
        if (stack.isOf(Items.OMINOUS_BOTTLE)) {
            effectComponent = DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER;
            style = EffectDisplay.ALL_EFFECTS;
        }

        if (!stack.contains(effectComponent)) return;

        if (EffectDisplay.canDisplay(style)) addEffects(stack, lines, getStatusEffects(stack, effectComponent), style);
        if (config.modifierDisplay == OtherEffectDisplay.ENABLED || (config.modifierDisplay == OtherEffectDisplay.CREATIVE_ONLY && creative()))
            addModifiers(stack, lines, effectComponent);
    }

    public void register(ItemStack stack, List<Text> lines) {
        addConsumableTooltips(stack, lines);

        if (config.otherEffectDisplay == OtherEffectDisplay.ENABLED || (config.otherEffectDisplay == OtherEffectDisplay.CREATIVE_ONLY && creative())) {
            if (stack.isOf(Items.HONEY_BOTTLE)) {
                if (!lines.contains(WHEN_CONSUMED_HEADER)) addWhenConsumed(stack, lines, false);
                lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.honey_bottle_effect").formatted(NUTRITION_COLOR)));
            }

            if (stack.isOf(Items.MILK_BUCKET)) {
                if (!lines.contains(WHEN_CONSUMED_HEADER)) addWhenConsumed(stack, lines, false);
                lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.milk_bucket_effect").formatted(NUTRITION_COLOR)));
            }
        }
    }
}