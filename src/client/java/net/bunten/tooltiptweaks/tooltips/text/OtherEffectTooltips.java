package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.OtherEffectDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

import static net.bunten.tooltiptweaks.tooltips.CommonText.*;

public class OtherEffectTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    public void register(ItemStack stack, List<Text> lines) {
        if (OtherEffectDisplay.canDisplay(config.modifierDisplay)) {
            if (stack.isOf(Items.HONEY_BOTTLE)) {
                if (!lines.contains(WHEN_CONSUMED_HEADER)) addConsumedHeader(stack, lines, false);
                lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.honey_bottle_effect").formatted(NUTRITION_COLOR)));
            }

            if (stack.isOf(Items.MILK_BUCKET)) {
                if (!lines.contains(WHEN_CONSUMED_HEADER)) addConsumedHeader(stack, lines, false);
                lines.add(Text.literal(" ").append(Text.translatable("tooltiptweaks.ui.milk_bucket_effect").formatted(NUTRITION_COLOR)));
            }
        }
    }
}