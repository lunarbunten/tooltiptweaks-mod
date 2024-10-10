package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.tooltips.text.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static net.bunten.tooltiptweaks.tooltips.ConvertibleTooltips.CONVERTIBLE_TOOLTIP_DATA_REGISTRY;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    public void getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> info) {
        CONVERTIBLE_TOOLTIP_DATA_REGISTRY.forEach((data) -> {
            if (data.canDisplay(stack)) info.setReturnValue(Optional.of(data.withStack(stack)));
        });
    }

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true)
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        new DurabilityTooltips().register(stack, tooltip);
        new RepairCostTooltip().register(stack, tooltip);
    }
}