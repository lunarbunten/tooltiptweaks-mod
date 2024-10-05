package net.bunten.tooltiptweaks.tooltips;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;

public interface ConvertibleTooltipData extends TooltipData {

    boolean canDisplay(ItemStack stack);

    ConvertibleTooltipData withStack(ItemStack stack);

    @Environment(EnvType.CLIENT)
    TooltipComponent getComponent();
}