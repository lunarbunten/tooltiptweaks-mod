package net.bunten.tooltiptweaks.tooltips.text;

import com.ibm.icu.text.DecimalFormat;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class DurabilityTooltips {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private DecimalFormat getDurabilityDecimalFormat() {
        String string = "#";
        for (int i = 0; i < config.percentageDecimalCount; i++) {
            if (i == 0) {
                string += ".";
            }
            string += "#";
        }

        return new DecimalFormat(string);
    }

    private int getDurabilityTextColor(float max, float damage) {
        float f = Math.max(0, (max - damage) / max);
        return MathHelper.hsvToRgb(f / 3, 1, 1);
    }

    public void register(ItemStack stack, List<Text> lines) {
        if (!stack.isDamageable() || !stack.isDamaged()) return;

        float max = stack.getMaxDamage();
        float damage = stack.getDamage();

        float durability = max - damage;
        float percent = (durability / max) * 100;

        MutableText message;
        switch (config.durabilityDisplay) {
            case 1:
                message = Text.translatable("tooltiptweaks.ui.durability", getDurabilityDecimalFormat().format(percent) + "%");
                lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
                break;
            case 2:
                message = Text.translatable("tooltiptweaks.ui.durability", (int) durability + " / " + (int) max);
                lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
                break;
            default:
                break;
        }

        if (config.displayUsesLeft == 1 && percent <= 25) {
            message = Text.translatable("tooltiptweaks.ui.uses_left", new DecimalFormat("#").format(durability));
            lines.add(message.setStyle(message.getStyle().withColor(getDurabilityTextColor(max, damage))));
        }
    }
}