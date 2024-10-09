package net.bunten.tooltiptweaks.tooltips.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltips.AbstractTooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class PaintingTooltipGUI extends AbstractTooltip {

    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();
    private PaintingVariant variant;

    MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public AbstractTooltip withStack(ItemStack stack) {
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        if (!config.displayPaintings || !stack.isOf(Items.PAINTING) || client.world == null) return false;
        if (!stack.contains(DataComponentTypes.ENTITY_DATA)) return false;

        NbtComponent nbtComponent = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        if (!nbtComponent.isEmpty()) {
            Identifier identifier = Identifier.of(nbtComponent.getNbt().getString("variant"));
            PaintingVariant variant = client.world.getRegistryManager().get(RegistryKeys.PAINTING_VARIANT).get(identifier);
            if (variant == null) return false;
            this.variant = variant;
            return true;
        }

        return true;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return (variant.width() * 16) + 6;
    }

    @Override
    public int getHeight() {
        return (variant.height() * 16) + 6;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        RenderSystem.enableBlend();

        Identifier assetId = Identifier.of(variant.assetId().getNamespace(), "textures/painting/" + variant.assetId().getPath() + ".png");

        context.drawTexture(assetId, x, y, 0, 0, variant.width() * 16, variant.height() * 16, variant.width() * 16, variant.height() * 16);

        RenderSystem.disableBlend();
    }
}