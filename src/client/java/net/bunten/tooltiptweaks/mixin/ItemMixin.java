package net.bunten.tooltiptweaks.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static net.bunten.tooltiptweaks.tooltips.text.ConvertibleTooltips.CONVERTIBLE_TOOLTIP_DATA_REGISTRY;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    public void getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> info) {
        CONVERTIBLE_TOOLTIP_DATA_REGISTRY.forEach((data) -> {
            if (data.canDisplay(stack)) info.setReturnValue(Optional.of(data.withStack(stack)));
        });
    }
}