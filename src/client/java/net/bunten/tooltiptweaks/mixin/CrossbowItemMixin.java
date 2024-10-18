package net.bunten.tooltiptweaks.mixin;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.CrossbowDisplay;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin extends RangedWeaponItem {

    public CrossbowItemMixin(Settings settings) {
        super(settings);
    }

    // I'm not sure if this causes any issues, but otherwise it just seems they left it out unintentionally
    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void reimplementSuper(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type, CallbackInfo ci) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    @ModifyArg(method = "appendTooltip", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private Object recolorProjectileText(Object object) {
        return TooltipTweaksConfig.getInstance().updateCrossbowTooltips != CrossbowDisplay.DISABLED ? ((Text) object).copy().formatted(Formatting.GRAY) : object;
    }

    @ModifyArg(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/MutableText;append(Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"))
    private Text recolorItemName(Text text) {
        return TooltipTweaksConfig.getInstance().updateCrossbowTooltips == CrossbowDisplay.GRAY_ITEM_TEXT ? text.copy().formatted(Formatting.GRAY) : text;
    }
}
