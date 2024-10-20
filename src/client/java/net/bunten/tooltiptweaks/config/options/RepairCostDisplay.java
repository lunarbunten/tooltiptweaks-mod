package net.bunten.tooltiptweaks.config.options;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum RepairCostDisplay implements NameableEnum {
    ENABLED,
    ON_RELEVANT_MENUS,
    DISABLED;

    @Override
    public Text getDisplayName() {
        return Text.translatable("tooltiptweaks.value." + name().toLowerCase());
    }
}