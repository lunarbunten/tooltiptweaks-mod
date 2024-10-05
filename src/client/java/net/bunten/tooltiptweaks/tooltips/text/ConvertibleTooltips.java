package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.tooltips.gui.ContainerInventoryTooltip;
import net.bunten.tooltiptweaks.tooltips.data.ConvertibleTooltipData;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.id;

public class ConvertibleTooltips {

    public static final SimpleRegistry<ConvertibleTooltipData> CONVERTIBLE_TOOLTIP_DATA_REGISTRY = FabricRegistryBuilder.createSimple(ConvertibleTooltipData.class, id("convertible_tooltip")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ConvertibleTooltipData CONTAINER = register("container", new ContainerInventoryTooltip());

    private static ConvertibleTooltipData register(String name, ConvertibleTooltipData element) {
        return Registry.register(CONVERTIBLE_TOOLTIP_DATA_REGISTRY, id(name), element);
    }
}