package net.bunten.tooltiptweaks.tooltip;

import com.ibm.icu.text.DecimalFormat;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class DurabilityTooltips {

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

    private DecimalFormat getDecimalFormat() {
        String string = "#";
        for (int i = 0; i < config.percentageDecimalCount; i++) {
            if (i == 0) {
                string += ".";
            }
            string += "#";
        }

        return new DecimalFormat(string);
    }

    private int getTextColor(float max, float damage) {
        float f = Math.max(0, (max - damage) / max);
        return MathHelper.hsvToRgb(f / 3, 1, 1);
    }

    private void addDurabilityTooltip(ItemStack stack, List<Text> lines, float max, float damage) {
        float durability = max - damage;
        float percent = (durability / max) * 100;

        MutableText message;
        switch (config.durabilityDisplay) {
            case 1:
                message = translatable("tooltiptweaks.ui.durability", getDecimalFormat().format(percent) + "%");
                lines.add(message.setStyle(message.getStyle().withColor(getTextColor(max, damage))));
                break;
            case 2:
                message = translatable("tooltiptweaks.ui.durability", (int) durability + " / " + (int) max);
                lines.add(message.setStyle(message.getStyle().withColor(getTextColor(max, damage))));
                break;
            default:
                break;
        }

        if ((config.toolUsesLeft == 2 ? percent <= config.lowDurabilityThreshold : config.toolUsesLeft > 0) && percent > 0) {
            message = translatable("tooltiptweaks.ui.uses_left", new DecimalFormat("#").format(durability));
            lines.add(message.setStyle(message.getStyle().withColor(getTextColor(max, damage))));
        }
    }
    
    public void addTooltips(ItemStack stack, TooltipContext context, List<Text> lines) {
        float max = stack.getMaxDamage();
        float damage = stack.getDamage();

        if (stack.isDamageable() && stack.isDamaged()) {
            addDurabilityTooltip(stack, lines, max, damage);
        }
    }
}