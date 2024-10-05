package net.bunten.tooltiptweaks.tooltips;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public abstract class CustomTooltip implements ConvertibleTooltipData, TooltipComponent {

    @Override
    public abstract int getHeight();

    @Override
    public abstract int getWidth(TextRenderer textRenderer);

    @Override
    public TooltipComponent getComponent() {
        return this;
    }
}