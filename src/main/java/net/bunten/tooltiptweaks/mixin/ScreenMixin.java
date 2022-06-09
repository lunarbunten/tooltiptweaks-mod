package net.bunten.tooltiptweaks.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.bunten.tooltiptweaks.tooltip.component.ConvertibleTooltipData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.item.TooltipData;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Inject(method = "method_32635", at = @At("HEAD"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD, remap = false)
	private static void onComponentConstruct(List<TooltipComponent> list, TooltipData data, CallbackInfo info) {
		if (data instanceof ConvertibleTooltipData convertible) {
			list.add(convertible.getComponent());
			info.cancel();
		}
	}
}