package net.bunten.tooltiptweaks.tooltip.component;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

public interface ConvertibleTooltipData extends TooltipData {
    TooltipComponent getComponent();
}