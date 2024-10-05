package net.bunten.tooltiptweaks.tooltips.text;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;
import java.util.Optional;

public class AxolotlVariantTooltip {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    public record Variant(AxolotlEntity.Variant variant) {
        public static final Codec<Variant> CODEC = Codec.INT.xmap(Variant::new, Variant::getId);

        public Variant(int id) {
            this(AxolotlEntity.Variant.byId(id));
        }

        public int getId() {
            return variant.getId();
        }

        public String getName() {
            return variant().getName();
        }
    }

    private static final MapCodec<Variant> AXOLOTL_VARIANT_MAP_CODEC = Variant.CODEC.fieldOf("Variant");

    public void register(ItemStack stack, List<Text> lines) {
        if (config.axolotlVariants == 1) return;

        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.BUCKET_ENTITY_DATA, NbtComponent.DEFAULT);
        if (nbt.isEmpty()) return;

        Optional<Variant> optional = nbt.get(AXOLOTL_VARIANT_MAP_CODEC).result();

        optional.ifPresent((variant) -> {
            Formatting[] formattings = new Formatting[]{Formatting.ITALIC, Formatting.GRAY};

            MutableText text = Text.translatable("tooltiptweaks.ui.axolotl." + variant.getName());
            MutableText message = text.formatted(Formatting.GRAY);

            message.formatted(formattings);

            lines.add(message);
        });
    }
}
