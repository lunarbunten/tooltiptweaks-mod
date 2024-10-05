package net.bunten.tooltiptweaks.tooltips.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltips.data.CustomTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class FoodTooltipGUI extends CustomTooltip {
    private ItemStack stack;
    private FoodComponent component;

    private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
    private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");

    @Override
    public CustomTooltip withStack(ItemStack stack) {
        this.stack = stack;
        this.component = stack.get(DataComponentTypes.FOOD);
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        if (TooltipTweaksConfig.getInstance().foodDisplayStyle == 0) return false;
        if (TooltipTweaksConfig.getInstance().foodNourishmentDisplay > 1) return false;
        if (stack.isOf(Items.OMINOUS_BOTTLE)) return false;
        return stack.getComponents().contains(DataComponentTypes.FOOD) || stack.isOf(Items.CAKE);
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return textRenderer.getWidth(stack.getName()) + (getNutrition() * 4) + 4;
    }

    private int getNutrition() {
        return stack.isOf(Items.CAKE) ? 14 : component.nutrition();
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        RenderSystem.enableBlend();

        int rx = x + textRenderer.getWidth(stack.getName()) + 2;
        int ry = y - 12;

        for (int index = 0; index < 10; index++) {

            if (index * 2 + 1 < getNutrition()) {
                context.drawGuiTexture(FOOD_FULL_TEXTURE, rx + index * 8, ry, 9, 9);
            }

            if (index * 2 + 1 == getNutrition()) {
                context.drawGuiTexture(FOOD_HALF_TEXTURE, rx + index * 8, ry, 9, 9);
            }
        }

        RenderSystem.disableBlend();
    }
}