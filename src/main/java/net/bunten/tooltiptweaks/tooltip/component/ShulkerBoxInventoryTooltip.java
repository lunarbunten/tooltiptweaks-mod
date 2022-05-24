package net.bunten.tooltiptweaks.tooltip.component;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public class ShulkerBoxInventoryTooltip extends CustomTooltip {
    private final DefaultedList<ItemStack> inventory;

    public ShulkerBoxInventoryTooltip(DefaultedList<ItemStack> inventory) {
        this.inventory = inventory;
    }

	private boolean isEmpty() {
		var emptySlots = 0;
		for (var item : inventory) {
			if (item.isEmpty()) {
				emptySlots++;
			}
		}

		return emptySlots == 27;
	}

	@Override
	public int getHeight() {
		return isEmpty() ? 0 : 59;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		return isEmpty() ? 0 : 164;
	}

	@Override
	public void drawItems(TextRenderer textRenderer, int x, int y, MatrixStack matrix, ItemRenderer itemRenderer, int z) {
		if (isEmpty()) {
			return;
		}

        RenderSystem.setShaderTexture(0, TooltipTweaksMod.id("textures/gui/shulker_box.png"));
		RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);

		drawTexture(matrix, x, y, 0, 0, 172, 64, 256, 128);

		x += 2;
		y += 2;

		var xOffset = 0;
		var yOffset = -18;

		for (var i = 0; i < inventory.size(); i++) {
			var stack = inventory.get(i);
			xOffset += 18;

			if (i % 9 == 0) {
				xOffset = 0;
				yOffset+= 18;
			}

			itemRenderer.renderInGuiWithOverrides(stack, x + xOffset, y + yOffset, i);
			itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + xOffset, y + yOffset);
		}
	}
}