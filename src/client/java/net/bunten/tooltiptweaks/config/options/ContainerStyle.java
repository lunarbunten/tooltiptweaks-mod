package net.bunten.tooltiptweaks.config.options;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum ContainerStyle implements NameableEnum {
    LIST_PER_ITEM,
    LIST_PER_STACK,
    INVENTORY,
    VANILLA;

    @Override
    public Text getDisplayName() {
        return Text.translatable("tooltiptweaks.value." + name().toLowerCase());
    }
}