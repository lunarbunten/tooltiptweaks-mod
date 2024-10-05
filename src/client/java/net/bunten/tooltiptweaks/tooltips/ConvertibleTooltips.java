package net.bunten.tooltiptweaks.tooltips;

import net.bunten.tooltiptweaks.tooltips.gui.ContainerTooltipGUI;
import net.bunten.tooltiptweaks.tooltips.gui.FoodTooltipGUI;
import net.bunten.tooltiptweaks.tooltips.gui.SpawnEggTooltipGUI;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.SimpleRegistry;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.id;

public class ConvertibleTooltips {

    public static final SimpleRegistry<ConvertibleTooltipData> CONVERTIBLE_TOOLTIP_DATA_REGISTRY = FabricRegistryBuilder.createSimple(ConvertibleTooltipData.class, id("convertible_tooltip")).attribute(RegistryAttribute.SYNCED).buildAndRegister();

    public static final ConvertibleTooltipData CONTAINER = register("container", new ContainerTooltipGUI());
    public static final ConvertibleTooltipData FOOD = register("food", new FoodTooltipGUI());
    public static final ConvertibleTooltipData SPAWN_EGG = register("spawn_egg", new SpawnEggTooltipGUI());

    private static ConvertibleTooltipData register(String name, ConvertibleTooltipData element) {
        return Registry.register(CONVERTIBLE_TOOLTIP_DATA_REGISTRY, id(name), element);
    }
}