package net.bunten.tooltiptweaks.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.tooltip.component.ShulkerBoxInventoryTooltip;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.item.TooltipData;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getTooltipData", at = @At("HEAD"), cancellable = true)
    public void getTooltipData(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> info) {
        var item = stack.getItem();
        if (item instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            var display = TooltipTweaksMod.getConfig().shulkerBoxDisplay;
            if (block instanceof ShulkerBoxBlock && display > 1 && display != 4) {
                var nbt = BlockItem.getBlockEntityNbt(stack);
                if (nbt != null) {
                    DefaultedList<ItemStack> inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
                    Inventories.readNbt(nbt, inventory);
                    info.setReturnValue(Optional.of(new ShulkerBoxInventoryTooltip(inventory, display == 3)));
                }
            }
        }
    }
}