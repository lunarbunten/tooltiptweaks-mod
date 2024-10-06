package net.bunten.tooltiptweaks.tooltips.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.IconLocation;
import net.bunten.tooltiptweaks.config.options.NourishmentDisplay;
import net.bunten.tooltiptweaks.config.options.NourishmentStyle;
import net.bunten.tooltiptweaks.tooltips.AbstractTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.id;

public class FoodTooltipGUI extends AbstractTooltip {

    private ItemStack stack;
    private FoodComponent component;

    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private static final Identifier FOOD_HALF_TEXTURE = Identifier.ofVanilla("hud/food_half");
    private static final Identifier FOOD_FULL_TEXTURE = Identifier.ofVanilla("hud/food_full");

    private static final Identifier SATURATION_HALF_TEXTURE = id("hud/saturation_half");
    private static final Identifier SATURATION_FULL_TEXTURE = id("hud/saturation_full");

    @Override
    public AbstractTooltip withStack(ItemStack stack) {
        this.stack = stack;
        this.component = stack.get(DataComponentTypes.FOOD);
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        if (config.nourishmentStyle != NourishmentStyle.ICONS) return false;
        if (config.nourishmentDisplay == NourishmentDisplay.DISABLED) return false;
        if (stack.isOf(Items.OMINOUS_BOTTLE)) return false;
        return stack.getComponents().contains(DataComponentTypes.FOOD) || stack.isOf(Items.CAKE);
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        int offset = config.nourishmentIconLocation == IconLocation.BELOW ? 0 : textRenderer.getWidth(stack.getName());
        int width = getNutrition() * 4;

        if (config.nourishmentDisplay == NourishmentDisplay.FOOD_AND_SATURATION) width = Math.max(width, getSaturation() * 4);

        return offset + width + 4;
    }

    @Override
    public int getHeight() {
        return (config.nourishmentIconLocation == IconLocation.BELOW) ? 12 : 0;
    }

    private int getNutrition() {
        return stack.isOf(Items.CAKE) ? 14 : component.nutrition();
    }

    private int getSaturation() {
        float value = stack.isOf(Items.CAKE) ? 2.8F : component.saturation();
        return (int) value;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        RenderSystem.enableBlend();

        int xOffset = (config.nourishmentIconLocation == IconLocation.BESIDE) ? textRenderer.getWidth(stack.getName()) + 2 : 0;
        int yOffset = (config.nourishmentIconLocation == IconLocation.BESIDE) ? -12 : 0;

        int rx = x + xOffset;
        int ry = y + yOffset;

        for (int index = 0; index < 10; index++) {

            if (index * 2 + 1 < getNutrition()) {
                context.drawGuiTexture(FOOD_FULL_TEXTURE, rx + index * 8, ry, 9, 9);
            }

            if (index * 2 + 1 == getNutrition()) {
                context.drawGuiTexture(FOOD_HALF_TEXTURE, rx + index * 8, ry, 9, 9);
            }

            if (config.nourishmentDisplay == NourishmentDisplay.FOOD_AND_SATURATION) {
                if (index * 2 + 1 < getSaturation()) {
                    context.drawGuiTexture(SATURATION_FULL_TEXTURE, rx + index * 8, ry, 9, 9);
                }

                if (index * 2 + 1 == getSaturation()) {
                    context.drawGuiTexture(SATURATION_HALF_TEXTURE, rx + index * 8, ry, 9, 9);
                }
            }
        }

        RenderSystem.disableBlend();
    }
}