package net.bunten.tooltiptweaks.config.options;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.creative;

public enum OtherEffectDisplay implements NameableEnum {
    ENABLED,
    CREATIVE_ONLY,
    DISABLED;

    public static boolean canDisplay(OtherEffectDisplay style) {
        if (style == DISABLED) return false;
        if (style == CREATIVE_ONLY) return creative();
        return true;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("tooltiptweaks.value." + name().toLowerCase());
    }
}