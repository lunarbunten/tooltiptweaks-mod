package net.bunten.tooltiptweaks.tooltips;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public abstract class AbstractTooltip implements ConvertibleTooltipData, TooltipComponent {

    @Override
    public abstract int getHeight(TextRenderer textRenderer);

    @Override
    public abstract int getWidth(TextRenderer textRenderer);

    @Override
    public TooltipComponent getComponent() {
        return this;
    }
}