package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.tooltips.gui.PriceTooltipGUI;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onSetTradeOffers", at = @At(value = "TAIL"))
    public void onSetTradeOffers(SetTradeOffersS2CPacket packet, CallbackInfo ci) {
        PriceTooltipGUI.updateOffers(packet.getOffers());
    }
}
