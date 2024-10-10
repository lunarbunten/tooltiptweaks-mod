package net.bunten.tooltiptweaks.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemEnchantmentsComponent.class)
public abstract class ItemEnchantmentsComponentMixin {

    @Final
    @Shadow
    boolean showInTooltip;

    @Final
    @Shadow
    Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments;

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    public void addHeader(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, CallbackInfo info) {
        if (TooltipTweaksConfig.getInstance().updateEnchantmentTooltips && showInTooltip && !enchantments.isEmpty()) {
            tooltip.accept(Text.translatable("tooltiptweaks.ui.enchantments").formatted(Formatting.GRAY));
        }
    }

    @ModifyArg(method = "appendTooltip", at = @At(value = "INVOKE", target = "Ljava/util/function/Consumer;accept(Ljava/lang/Object;)V"), index = 0)
    private Object addSpacingToEntries(Object object) {
        if (!TooltipTweaksConfig.getInstance().updateEnchantmentTooltips) return object;
        return Text.literal(" ").append((Text) object);
    }
}