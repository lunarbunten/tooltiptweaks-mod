package net.bunten.tooltiptweaks.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Unique
    private List<Text> list;

    @Unique
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    @Shadow public abstract boolean hasEnchantments();

    @ModifyVariable(method = "getTooltip", at = @At("STORE"), ordinal = 0)
    private List<Text> setList(List<Text> list) {
        return this.list = list;
    }

    @ModifyVariable(method = "getTooltip", at = @At("STORE"), ordinal = 0)
    private Consumer<Text> setConsumer(Consumer<Text> consumer) {
        return consumer;
    }

    @WrapWithCondition(
            method = "method_57370",
            at = @At(value = "INVOKE", target = "java/util/function/Consumer.accept (Ljava/lang/Object;)V")
    )
    private boolean shouldDisplay(Consumer<Text> instance, Object object) {
        if (object == ScreenTexts.EMPTY && config.updateEnchantmentTooltips) return !hasEnchantments();
        return true;
    }

    @WrapWithCondition(
            method = "getTooltip",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V")
    )
    private boolean shouldCancelEnchantments(ItemStack instance, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
        return componentType == DataComponentTypes.ENCHANTMENTS;
    }

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", ordinal = 3, target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V", shift = At.Shift.BEFORE))
    private void addHeaderIfMissing(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info) {
        if (!config.updateEnchantmentTooltips || !hasEnchantments()) return;
        list.add(ScreenTexts.EMPTY);
    }
}
