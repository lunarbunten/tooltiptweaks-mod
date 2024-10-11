package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.NourishmentDisplay;
import net.bunten.tooltiptweaks.config.options.NourishmentStyle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.text.DecimalFormat;
import java.util.List;

import static net.bunten.tooltiptweaks.tooltips.CommonText.NUTRITION_COLOR;
import static net.bunten.tooltiptweaks.tooltips.CommonText.addConsumedHeader;

public class NutritionTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private void addNutrition(ItemStack stack, List<Text> lines, int nutrition) {
        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.nutrition", nutrition).formatted(NUTRITION_COLOR)));
    }

    private void addSaturation(ItemStack stack, List<Text> lines, float saturation) {
        String formattedSaturation = new DecimalFormat("#.#").format(saturation);
        lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.saturation", formattedSaturation).formatted(NUTRITION_COLOR)));
    }

    public void register(ItemStack stack, List<Text> lines) {
        if (stack.isOf(Items.OMINOUS_BOTTLE) || config.nourishmentStyle != NourishmentStyle.TEXT) return;

        if (stack.isOf(Items.CAKE) && config.nourishmentDisplay != NourishmentDisplay.DISABLED) {
            addConsumedHeader(stack, lines, true);
            addNutrition(stack, lines, 14);
            if (config.nourishmentDisplay == NourishmentDisplay.NUTRITION_AND_SATURATION) addSaturation(stack, lines, 2.4F);
        }

        if (stack.contains(DataComponentTypes.FOOD)) {
            FoodComponent food = stack.get(DataComponentTypes.FOOD);
            if (food == null) return;

            if (config.nourishmentDisplay != NourishmentDisplay.DISABLED) {
                addConsumedHeader(stack, lines, false);
                addNutrition(stack, lines, food.nutrition());
            }

            if (config.nourishmentDisplay == NourishmentDisplay.NUTRITION_AND_SATURATION) addSaturation(stack, lines, food.saturation());
        }
    }
}