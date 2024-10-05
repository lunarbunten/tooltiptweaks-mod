package net.bunten.tooltiptweaks.tooltips.gui;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltips.AbstractTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ContainerTooltipGUI extends AbstractTooltip {
    private ContainerComponent component;

    @Override
    public AbstractTooltip withStack(ItemStack stack) {
        component = stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT);
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        return stack.getComponents().contains(DataComponentTypes.CONTAINER) && TooltipTweaksConfig.getInstance().containerDisplay == 2;
    }

    private boolean isEmpty() {
        return component.stream().filter(stack -> (!stack.isEmpty())).toList().isEmpty();
    }

    @Override
    public int getHeight() {
        if (isEmpty()) return 0;
        return 59;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        if (isEmpty()) return 0;
        return 164;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if (isEmpty()) return;

        context.drawTexture(TooltipTweaksMod.id("textures/gui/container.png"), x, y, 0, 0, 172, 64, 256, 128);

        int xOffset = 2;
        int yOffset = -16;

        List<ItemStack> list = component.stream().toList();

        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i);
            xOffset += 18;

            if (i % 9 == 0) {
                xOffset = 2;
                yOffset += 18;
            }

            context.drawItem(stack, x + xOffset, y + yOffset);
            context.drawItemInSlot(textRenderer, stack, x + xOffset, y + yOffset);
        }
    }
}