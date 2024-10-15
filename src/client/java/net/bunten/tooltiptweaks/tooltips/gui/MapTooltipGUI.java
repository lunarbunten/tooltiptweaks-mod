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
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MapTooltipGUI extends AbstractTooltip {

    MinecraftClient client = MinecraftClient.getInstance();

    private MapIdComponent component;

    @Nullable
    private MapState getMapState() {
        ClientWorld world = client.world;
        if (world != null) return world.getMapState(component);
        return null;
    }

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
        return getMapState() != null ? 66 : 0;
    }

    @Override
    public int getHeight() {
        return getMapState() != null ? 69 : 0;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        if (getMapState() == null) return;

        context.drawGuiTexture(Identifier.ofVanilla("container/cartography_table/map"), x, y, 66, 66);

        MatrixStack matrices = context.getMatrices();

        matrices.push();

        matrices.translate(x + 4, y + 4, 1);
        matrices.scale(0.45F, 0.45F, 1.0F);

        client.gameRenderer.getMapRenderer().draw(matrices, context.getVertexConsumers(), component, getMapState(), true, 15728880);

        matrices.pop();
    }
}