package net.bunten.tooltiptweaks.config.options;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum DurabilityStyle implements NameableEnum {
    PERCENTAGE,
    FRACTION,
    DISABLED;

    @Override
    public Text getDisplayName() {
        return Text.translatable("tooltiptweaks.value." + name().toLowerCase());
    }
}