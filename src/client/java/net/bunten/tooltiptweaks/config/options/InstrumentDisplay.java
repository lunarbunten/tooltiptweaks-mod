package net.bunten.tooltiptweaks.config.options;

import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum InstrumentDisplay implements NameableEnum {
    ENABLED,
    WHILE_CARRYING_NOTE_BLOCKS,
    DISABLED;

    @Override
    public Text getDisplayName() {
        return Text.translatable("tooltiptweaks.value." + name().toLowerCase());
    }
}