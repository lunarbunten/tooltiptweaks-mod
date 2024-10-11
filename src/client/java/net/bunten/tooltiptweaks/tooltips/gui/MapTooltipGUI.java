package net.bunten.tooltiptweaks.tooltips.gui;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltips.AbstractTooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class MapTooltipGUI extends AbstractTooltip {

    MinecraftClient client = MinecraftClient.getInstance();

    private MapIdComponent component;

    @Override
    public AbstractTooltip withStack(ItemStack stack) {
        component = stack.get(DataComponentTypes.MAP_ID);
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        return TooltipTweaksConfig.getInstance().displayMaps && stack.contains(DataComponentTypes.MAP_ID);
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 66;
    }

    @Override
    public int getHeight() {
        return 69;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        ClientWorld world = client.world;
        if (world != null) {
            MatrixStack matrices = context.getMatrices();

            context.drawGuiTexture(Identifier.ofVanilla("container/cartography_table/map"), x, y, 66, 66);

            matrices.push();
            matrices.translate(x + 4, y + 4, 1);
            matrices.scale(0.45F, 0.45F, 1.0F);
            client.gameRenderer.getMapRenderer().draw(matrices, context.getVertexConsumers(), component, world.getMapState(component), true, 15728880);
            matrices.pop();
        }
    }
}