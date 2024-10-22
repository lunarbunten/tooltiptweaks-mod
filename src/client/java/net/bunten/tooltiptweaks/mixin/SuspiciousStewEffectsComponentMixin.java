package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(SuspiciousStewEffectsComponent.class)
public abstract class SuspiciousStewEffectsComponentMixin {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, CallbackInfo info) {
        if (TooltipTweaksConfig.getInstance().updatePotionTooltips) info.cancel();
    }
}