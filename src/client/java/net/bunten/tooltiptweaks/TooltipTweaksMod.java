package net.bunten.tooltiptweaks;

import com.google.common.reflect.Reflection;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.tooltips.ConvertibleTooltips;
import net.bunten.tooltiptweaks.tooltips.text.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TooltipTweaksMod implements ClientModInitializer {
    public static final String MOD_ID = "tooltiptweaks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static boolean creative() {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return false;
        return player.getAbilities().creativeMode;
    }

    @Override
    public void onInitializeClient() {
        Reflection.initialize(TooltipTweaksConfig.class, ConvertibleTooltips.class);

        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            new AxolotlVariantTooltip().register(stack, lines);
            new ClockTooltips().register(stack, lines);
            new CompassTooltips().register(stack, lines);
            new ConsumablesTooltips().register(stack, lines);
            new ContainerTooltips().register(stack, lines);
            new DurabilityTooltips().register(stack, lines);
            new InstrumentTooltip().register(stack, lines);
            new RepairCostTooltip().register(stack, lines);
        });
    }
}