package net.bunten.tooltiptweaks.tooltip;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.util.ClockUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Environment(EnvType.CLIENT)
public class HudRenderer extends DrawableHelper {

    private final MinecraftClient client = MinecraftClient.getInstance();

    private int width;
    private int height;

    public static void init() {
        HudRenderer renderer = new HudRenderer();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!client.isPaused()) {
                renderer.tick();
            }
        });

        HudRenderCallback.EVENT.register(renderer::render);
    }

    public void tick() {
        width = client.getWindow().getScaledWidth();
        height = client.getWindow().getScaledHeight();

        if (client.player != null) {
            ClientPlayerEntity player = client.player;
        }
    }

    private void drawBackground(MatrixStack matrices, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, TooltipTweaksMod.id("textures/gui/widgets.png"));
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        drawTexture(matrices,
        x,
        y,
        0,
        0,
        width / 2,
        height / 2);
        
        // Top right corner
        drawTexture(matrices,
        x + width / 2,
        y,
        256 - width / 2,
        0,
        width / 2,
        height / 2);

        // Bottom left corner
        drawTexture(matrices,
        x,
        y + height / 2,
        0,
        128 - (height / 2),
        width / 2,
        height / 2);
        
        // Bottom right corner
        drawTexture(matrices,
        x + width / 2,
        y + height / 2,
        256 - width / 2,
        128 - (height / 2),
        width / 2,
        height / 2);
    }

    public void render(MatrixStack matrix, float delta) {
        if (client.player != null && 2 + 2 == 5) {
            matrix.push();
            float sc = 1F;
            matrix.scale(sc, sc, sc);
            var inventory = client.player.getInventory();

            int i = 8;

            ItemStack clock = new ItemStack(Items.CLOCK);
            if (inventory.contains(clock)) {
                var time = ClockUtil.getClockTime();
                var renderer = client.textRenderer;

                drawBackground(matrix, i, i, (renderer.getWidth(time)) + 16, 29);
                renderer.drawWithShadow(matrix, time, i + 9, i + 10, 0xFFFFFF);
            }
            matrix.pop();
        }
    }
}