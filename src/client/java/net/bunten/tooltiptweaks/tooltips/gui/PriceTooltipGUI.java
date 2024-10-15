package net.bunten.tooltiptweaks.tooltips.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltips.AbstractTooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class PriceTooltipGUI extends AbstractTooltip {

    MinecraftClient client = MinecraftClient.getInstance();

    private ItemStack stack;

    public static LinkedHashMap<Item, Float> lowestPrices = new LinkedHashMap<>();
    public static LinkedHashMap<Item, Float> highestPrices;

    public static void updateOffers(TradeOfferList newOffers) {
        for (TradeOffer offer : newOffers) {
            TooltipTweaksMod.LOGGER.info("try add something");

            ItemStack selling = offer.getFirstBuyItem().itemStack();
            ItemStack buying = offer.getSellItem();

            if (selling.isOf(Items.EMERALD)) continue;
            if (offer.getSecondBuyItem().isPresent()) continue;

            TooltipTweaksMod.LOGGER.info("added something");

            lowestPrices.put(selling.getItem(), (float) buying.getCount());
        }
    }

    @Override
    public AbstractTooltip withStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        return lowestPrices.containsKey(stack.getItem());
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 12;
    }

    @Override
    public int getHeight() {
        return 12;
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        context.drawText(textRenderer, "Price: " + lowestPrices.get(stack.getItem()), x, y, 0xFFFFFF, true);
    }
}