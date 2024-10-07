package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.InstrumentDisplay;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Set;

import static net.minecraft.block.SkullBlock.Type.DRAGON;
import static net.minecraft.block.SkullBlock.Type.PLAYER;

public class InstrumentTooltip {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    public void register(ItemStack stack, List<Text> lines) {
        if (config.instrumentDisplay == InstrumentDisplay.DISABLED) return;
        if (config.instrumentDisplay == InstrumentDisplay.WHILE_CARRYING_NOTE_BLOCKS) {
            ClientPlayerEntity player = client.player;
            if (player == null) return;
            if (!player.getInventory().containsAny(Set.of(Items.NOTE_BLOCK))) return;
        }

        if (stack.getItem() instanceof BlockItem blockItem) {
            NoteBlockInstrument instrument = blockItem.getBlock().getDefaultState().getInstrument();
            MutableText value = Text.translatable("tooltiptweaks.ui.instrument." + instrument.asString());

            if (blockItem.getBlock() instanceof SkullBlock skull) {
                value = switch (skull.getSkullType()) {
                    case DRAGON -> EntityType.ENDER_DRAGON.getName().copy();
                    case PLAYER -> Text.translatable("tooltiptweaks.ui.instrument.custom_head");
                    default -> {
                        var entity = Registries.ENTITY_TYPE.getOrEmpty(Identifier.of(skull.getSkullType().asString()));
                        yield entity.isPresent() ? entity.get().getName().copy() : Text.literal("ERROR");
                    }
                };
            }

            lines.add(Text.translatable("tooltiptweaks.ui.instrument", value).formatted(Formatting.GRAY));
        }
    }
}