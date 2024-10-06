package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.ContainerStyle;
import net.minecraft.block.Block;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShulkerBoxBlock.class)
public abstract class ShulkerBoxBlockMixin extends Block {
    public ShulkerBoxBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    private void appendToolTip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options, CallbackInfo info) {
        if (TooltipTweaksConfig.getInstance().containerStyle != ContainerStyle.VANILLA) {
            info.cancel();
            super.appendTooltip(stack, context, tooltip, options);
        }
    }
}