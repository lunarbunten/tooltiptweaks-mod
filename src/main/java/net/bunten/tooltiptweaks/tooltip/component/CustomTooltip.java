package net.bunten.tooltiptweaks.tooltip.component;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.tooltip.TooltipComponent;

public abstract class CustomTooltip extends DrawableHelper implements ConvertibleTooltipData, TooltipComponent {

    @Override
    public abstract int getHeight();

    @Override
    public abstract int getWidth(TextRenderer textRenderer);

    @Override
    public TooltipComponent getComponent() {
        return this;
    }
}