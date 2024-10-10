package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {

    @ModifyArg(method = "getName", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Texts;setStyleIfAbsent(Lnet/minecraft/text/MutableText;Lnet/minecraft/text/Style;)Lnet/minecraft/text/MutableText;"), index = 1)
    private static Style changeNameColor(Style style) {
        if (!TooltipTweaksConfig.getInstance().updateEnchantmentTooltips) return style;
        return Style.EMPTY.withColor(Formatting.BLUE);
    }
}