package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.OminousBottleItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(OminousBottleItem.class)
public abstract class OminousBottleItemMixin extends Item {

    public OminousBottleItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo info) {
        if (TooltipTweaksConfig.getInstance().updatePotionTooltips) {
            info.cancel();
            super.appendTooltip(stack, context, tooltip, type);
        }
    }
}