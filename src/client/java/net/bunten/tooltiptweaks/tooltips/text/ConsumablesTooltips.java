package net.bunten.tooltiptweaks.tooltips.text;

import com.mojang.datafixers.util.Pair;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
        if (stack.isOf(Items.CAKE) && config.foodDisplay <= 1) {
            addWhenConsumed(stack, lines, true);
            addFoodPoints(stack, lines, 14);
            if (config.foodDisplay == 1) addSaturation(stack, lines, 2.4F);
        }

        if (stack.contains(DataComponentTypes.FOOD)) {
            FoodComponent food = stack.get(DataComponentTypes.FOOD);
            if (food == null) return;

            if (config.foodDisplay <= 1 || (stack.isOf(Items.HONEY_BOTTLE)) && config.otherEffectDisplay < 1)
                addWhenConsumed(stack, lines, false);

            if (config.foodDisplay <= 1)
                addFoodPoints(stack, lines, food.nutrition());

            if (config.foodDisplay == 1)
                addSaturation(stack, lines, food.saturation());
        }
    }

    private void addEffects(ItemStack stack, List<Text> lines, List<StatusEffectInstance> effects, int displayConfig) {
        if (effects.isEmpty()) return;

        int i = 0;
        float durationMultiplier = stack.isOf(Items.LINGERING_POTION) ? 0.25F : 1.0F;

        for (StatusEffectInstance instance : effects) {
            StatusEffectCategory category = instance.getEffectType().value().getCategory();
            MutableText mutableText = Text.translatable(instance.getTranslationKey());

            if (instance.getAmplifier() > 0)
                mutableText = Text.translatable("potion.withAmplifier", mutableText, Text.translatable("potion.potency." + instance.getAmplifier()));

            if (instance.getDuration() > 20)
                mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(instance, durationMultiplier, client.world.getTickManager().getTickRate()));

            if (displayConfig == 0 || category != StatusEffectCategory.HARMFUL || creative()) {
                if (i == 0) lines.add(STATUS_EFFECTS_HEADER);
                Formatting formatting;
                switch (category) {
                    case BENEFICIAL:
                        formatting = BENEFICIAL_STATUS_EFFECT_COLOR;
                        break;
                    case NEUTRAL:
                        formatting = NEUTRAL_STATUS_EFFECT_COLOR;
                        break;
                    default:
                        formatting = HARMFUL_STATUS_EFFECT_COLOR;
                        break;
                }
                lines.add(Text.literal(" ").append(mutableText.formatted(formatting)));
                i++;
            }
        }
    }

    private void addModifiers(ItemStack stack, List<Text> lines, ComponentType<?> component) {
        List<Pair<RegistryEntry<EntityAttribute>, EntityAttributeModifier>> modifiers = new ArrayList<>();
        getStatusEffects(stack, component).forEach((instance) -> instance.getEffectType().value().forEachAttributeModifier(instance.getAmplifier(), (attribute, modifier) -> modifiers.add(new Pair<>(attribute, modifier))));

        if (modifiers.isEmpty()) return;

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
        ComponentType<?> component = DataComponentTypes.FOOD;

        byte displayConfig = config.foodEffectDisplay;
        if (stack.isOf(Items.SUSPICIOUS_STEW)) {
            component = DataComponentTypes.SUSPICIOUS_STEW_EFFECTS;
            displayConfig = config.stewEffectDisplay;
        }
        if (stack.getItem() instanceof PotionItem) {
            component = DataComponentTypes.POTION_CONTENTS;
            displayConfig = config.potionEffectDisplay;
        }
        if (stack.isOf(Items.OMINOUS_BOTTLE)) {
            component = DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER;
            displayConfig = config.potionEffectDisplay;
        }

        if (!stack.contains(component)) return;

        if (!stack.isOf(Items.OMINOUS_BOTTLE)) addNutritionInfo(stack, lines);

        if (displayConfig < 2 || creative()) addEffects(stack, lines, getStatusEffects(stack, component), displayConfig);
        if (config.modifierDisplay == 0 || creative()) addModifiers(stack, lines, component);
    }

    public void register(ItemStack stack, List<Text> lines) {
        addConsumableTooltips(stack, lines);

        // Add Honey Bottle Effects
        if (stack.isOf(Items.HONEY_BOTTLE) && (config.otherEffectDisplay < 1 || (config.otherEffectDisplay == 1 && creative()))) {
            lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.honey_bottle_effect").formatted(NUTRITION_COLOR)));
        }

        // Add Milk Bucket Effects
        if (stack.isOf(Items.MILK_BUCKET)) {
            if (config.otherEffectDisplay < 1 || (config.otherEffectDisplay == 1 && creative())) {
                addWhenConsumed(stack, lines, false);
                lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.milk_bucket_effect").formatted(NUTRITION_COLOR)));
            }
        }
    }
}
