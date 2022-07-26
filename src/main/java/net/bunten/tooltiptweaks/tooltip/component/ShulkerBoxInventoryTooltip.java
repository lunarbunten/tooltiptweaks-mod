package net.bunten.tooltiptweaks.tooltip.component;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ShulkerBoxInventoryTooltip extends CustomTooltip {
	private final List<ItemStack> stacks;
    private final DefaultedList<ItemStack> inventory;
	private final boolean automatic;

    public ShulkerBoxInventoryTooltip(DefaultedList<ItemStack> inventory, boolean automatic) {
        this.inventory = inventory;
		this.automatic = automatic;

		stacks = inventory.stream().filter(stack -> (!stack.isEmpty())).toList();
    }

	private boolean isEmpty() {
		return stacks.isEmpty();
	}

	@Override
	public int getHeight() {
		if (isEmpty()) return 0;
		if (automatic) {
			return 24 + (Math.min(2, stacks.size() / 9)) * 20;
		}
		return 59;
	}

	@Override
	public int getWidth(TextRenderer textRenderer) {
		if (isEmpty()) return 0;
		if (automatic) {
			return Math.min(18 * 9 + 2, stacks.size() * 18);
		}
		return 164;
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

		if (!automatic) {
			drawTexture(matrix, x, y, 0, 0, 172, 64, 256, 128);

			var xOffset = 2;
			var yOffset = -16;

			for (var i = 0; i < inventory.size(); i++) {
				var stack = inventory.get(i);
				xOffset += 18;

				if (i % 9 == 0) {
					xOffset = 2;
					yOffset += 18;
				}

				itemRenderer.renderInGuiWithOverrides(stack, x + xOffset, y + yOffset, i);
				itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + xOffset, y + yOffset);
			}
		} else {
			int i = 0;

			var xOffset = 1;
			var yOffset = -19;

			for (ItemStack stack : stacks) {
				if (stack.isEmpty()) continue;

				xOffset += 18;
				if (i % 9 == 0) {
					xOffset = 1;
					yOffset += 20;
				}

				drawSlot(matrix, x + xOffset, y + yOffset, z);
				itemRenderer.renderInGuiWithOverrides(stack, x + xOffset + 1, y + yOffset + 1, i);
				itemRenderer.renderGuiItemOverlay(textRenderer, stack, x + xOffset + 1, y + yOffset + 1);

				i++;
			}
		}
	}

	private void drawSlot(MatrixStack matrix, int x, int y, int z) {
        RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.setShaderTexture(0, new Identifier("textures/gui/container/bundle.png"));
        DrawableHelper.drawTexture(matrix, x, y, z, 0, 0, 18, 20, 128, 128);
    }
}