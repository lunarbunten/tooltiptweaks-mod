package net.bunten.tooltiptweaks.tooltips;

import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommonText {

    public static final Formatting NUTRITION_COLOR = Formatting.GOLD;
    
    public static final Formatting BENEFICIAL_STATUS_EFFECT_COLOR = Formatting.BLUE;
    public static final Formatting NEUTRAL_STATUS_EFFECT_COLOR = Formatting.BLUE;
    public static final Formatting HARMFUL_STATUS_EFFECT_COLOR = Formatting.DARK_PURPLE;

    public static final Formatting POSITIVE_MODIFIER_COLOR = Formatting.DARK_GREEN;
    public static final Formatting NEGATIVE_MODIFIER_COLOR = Formatting.RED;

    public static final MutableText WHEN_CONSUMED_HEADER = Text.translatable("tooltiptweaks.ui.when_consumed").formatted(Formatting.GRAY);
    public static final MutableText WHEN_FULLY_CONSUMED_HEADER = Text.translatable("tooltiptweaks.ui.when_fully_consumed").formatted(Formatting.GRAY);
    public static final MutableText STATUS_EFFECTS_HEADER = Text.translatable("tooltiptweaks.ui.status_effects").formatted(Formatting.GRAY);
    public static final MutableText MODIFIERS_HEADER = Text.translatable("tooltiptweaks.ui.modifiers").formatted(Formatting.GRAY);

    public static final List<MutableText> HEADER_TEXTS = Arrays.asList(WHEN_CONSUMED_HEADER, WHEN_FULLY_CONSUMED_HEADER, STATUS_EFFECTS_HEADER, MODIFIERS_HEADER);

    public static void tryAddSpace(List<Text> lines) {
        for (MutableText header : HEADER_TEXTS) if (lines.contains(header)) return;
        lines.add(Text.literal(" "));
    }

    public static void addConsumedHeader(List<Text> lines, boolean whenFullyConsumed) {
        if (lines.contains(WHEN_FULLY_CONSUMED_HEADER) || lines.contains(WHEN_CONSUMED_HEADER)) return;

        tryAddSpace(lines);
        lines.add(whenFullyConsumed ? WHEN_FULLY_CONSUMED_HEADER : WHEN_CONSUMED_HEADER);
    }

    public static void addStatusEffectHeader(List<Text> lines) {
        if (lines.contains(STATUS_EFFECTS_HEADER)) return;

        tryAddSpace(lines);
        lines.add(STATUS_EFFECTS_HEADER);
    }

    public static void addModifiersHeader(List<Text> lines) {
        if (lines.contains(MODIFIERS_HEADER)) return;

        tryAddSpace(lines);
        lines.add(MODIFIERS_HEADER);
    }
}
