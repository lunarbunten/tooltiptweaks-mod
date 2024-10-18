package net.bunten.tooltiptweaks.config;


import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.bunten.tooltiptweaks.config.options.*;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.id;

public class TooltipTweaksConfig {

    public static ConfigClassHandler<TooltipTweaksConfig> HANDLER = ConfigClassHandler.createBuilder(TooltipTweaksConfig.class)
            .id(id("config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("tooltiptweaks.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public static TooltipTweaksConfig getInstance() {
        return HANDLER.instance();
    }

    @SerialEntry public boolean displayAxolotlVariants = true;
    @SerialEntry public boolean displayDayNumber = false;
    @SerialEntry public boolean displayMaps = true;
    @SerialEntry public boolean displayMoonPhase = false;
    @SerialEntry public boolean displayPaintings = true;
    @SerialEntry public boolean displayRepairCost = false;
    @SerialEntry public boolean displaySpawnEgg = false;
    @SerialEntry public boolean displayUsesLeft = false;
    @SerialEntry public boolean updateEnchantmentTooltips = true;
    @SerialEntry public boolean updatePotionTooltips = true;
    @SerialEntry public boolean updateTippedArrowTooltips = true;

    @SerialEntry public int containerEntries = 6;
    @SerialEntry public int percentageDigits = 0;

    @SerialEntry public ClockTimeDisplay clockTimeDisplay = ClockTimeDisplay.TWELVE_HOUR;
    @SerialEntry public CompassDisplay compassDisplay = CompassDisplay.DISTANCE;
    @SerialEntry public ContainerStyle containerStyle = ContainerStyle.LIST_PER_ITEM;
    @SerialEntry public CrossbowDisplay updateCrossbowTooltips = CrossbowDisplay.WHITE_ITEM_TEXT;
    @SerialEntry public DurabilityStyle durabilityStyle = DurabilityStyle.PERCENTAGE;
    @SerialEntry public EffectDisplay foodEffectDisplay = EffectDisplay.POSITIVE_EFFECTS_ONLY;
    @SerialEntry public EffectDisplay stewEffectDisplay = EffectDisplay.CREATIVE_ONLY;
    @SerialEntry public IconLocation nourishmentIconLocation = IconLocation.BELOW;
    @SerialEntry public InstrumentDisplay instrumentDisplay = InstrumentDisplay.WHILE_CARRYING_NOTE_BLOCKS;
    @SerialEntry public NourishmentDisplay nourishmentDisplay = NourishmentDisplay.NUTRITION_ONLY;
    @SerialEntry public NourishmentStyle nourishmentStyle = NourishmentStyle.TEXT;
    @SerialEntry public OtherEffectDisplay modifierDisplay = OtherEffectDisplay.ENABLED;
    @SerialEntry public OtherEffectDisplay otherEffectDisplay = OtherEffectDisplay.ENABLED;

    private static ConfigCategory toolsCategory(TooltipTweaksConfig config) {

        Option<?> CLOCK_DISPLAY_TIME = Option.<ClockTimeDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.clock_time_display"))
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.clock_time_display.desc")).webpImage(id("textures/gui/previews/time_display.webp")).build())
                .binding(ClockTimeDisplay.TWELVE_HOUR, () -> config.clockTimeDisplay, value -> config.clockTimeDisplay = value)
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ClockTimeDisplay.class))
                .build();

        Option<?> CLOCK_DISPLAY_DAY_NUMBER = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.day_number"))
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.day_number.desc")).webpImage(id("textures/gui/previews/day_number.webp")).build())
                .binding(false, () -> config.displayDayNumber, value -> config.displayDayNumber = value)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> CLOCK_DISPLAY_MOON_PHASE = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.moon_phase"))
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.moon_phase.desc")).webpImage(id("textures/gui/previews/moon_phase.webp")).build())
                .binding(false, () -> config.displayMoonPhase, value -> config.displayMoonPhase = value)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> CONTAINER_DISPLAY_STYLE = Option.<ContainerStyle>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.container_display_style"))
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.container_display_style.desc")).webpImage(id("textures/gui/previews/container_display_style.webp")).build())
                .binding(ContainerStyle.LIST_PER_ITEM, () -> config.containerStyle, value -> config.containerStyle = value)
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(ContainerStyle.class))
                .build();

        Option<?> CONTAINER_MAXIMUM_LIST_LENGTH = Option.<Integer>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.container_entries"))
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.container_entries.desc")).webpImage(id("textures/gui/previews/container_entries.webp")).build())
                .binding(6, () -> config.containerEntries, value -> config.containerEntries = value)
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(1, 27).step(1))
                .build();

        Option<?> DISPLAY_MAPS = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_maps"))
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_maps.desc")).webpImage(id("textures/gui/previews/display_maps.webp")).build())
                .binding(true, () -> config.displayMaps, value -> config.displayMaps = value)
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> DURABILITY_DISPLAY_STYLE = Option.<DurabilityStyle>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.durability_display_style"))
                .binding(DurabilityStyle.PERCENTAGE, () -> config.durabilityStyle, value -> config.durabilityStyle = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.durability_display_style.desc")).webpImage(id("textures/gui/previews/durability_display_style.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(DurabilityStyle.class))
                .build();

        Option<?> DURABILITY_DIGIT_COUNT = Option.<Integer>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.percentage_digits"))
                .binding(0, () -> config.percentageDigits, value -> config.percentageDigits = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.percentage_digits.desc")).webpImage(id("textures/gui/previews/percentage_digits.webp")).build())
                .controller(opt -> IntegerSliderControllerBuilder.create(opt).range(0, 4).step(1))
                .build();

        Option<?> DURABILITY_DISPLAY_USES_LEFT = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_uses_left"))
                .binding(false, () -> config.displayUsesLeft, value -> config.displayUsesLeft = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_uses_left.desc")).webpImage(id("textures/gui/previews/display_uses_left.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> COMPASS_DISPLAY_LOCATION = Option.<CompassDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.compass_display"))
                .binding(CompassDisplay.DISTANCE, () -> config.compassDisplay, value -> config.compassDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.compass_display.desc")).webpImage(id("textures/gui/previews/compass_display.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(CompassDisplay.class))
                .build();

        Option<?> DISPLAY_AXOLOTL_VARIANTS = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_axolotl_variants"))
                .binding(true, () -> config.displayAxolotlVariants, value -> config.displayAxolotlVariants = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_axolotl_variants.desc")).webpImage(id("textures/gui/previews/display_axolotl_variants.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> DISPLAY_INSTRUMENT_TYPE = Option.<InstrumentDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_instrument"))
                .binding(InstrumentDisplay.WHILE_CARRYING_NOTE_BLOCKS, () -> config.instrumentDisplay, value -> config.instrumentDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_instrument.desc")).webpImage(id("textures/gui/previews/display_instrument.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(InstrumentDisplay.class))
                .build();

        Option<?> DISPLAY_PAINTINGS = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_paintings"))
                .binding(true, () -> config.displayPaintings, value -> config.displayPaintings = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_paintings.desc")).webpImage(id("textures/gui/previews/display_paintings.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> DISPLAY_REPAIR_COST = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_repair_cost"))
                .binding(false, () -> config.displayRepairCost, value -> config.displayRepairCost = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_repair_cost.desc")).webpImage(id("textures/gui/previews/display_repair_cost.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> DISPLAY_SPAWN_EGG = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.display_spawn_egg"))
                .binding(false, () -> config.displaySpawnEgg, value -> config.displaySpawnEgg = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.display_spawn_egg.desc")).webpImage(id("textures/gui/previews/display_spawn_egg.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> UPDATE_CROSSBOW_TOOLTIPS = Option.<CrossbowDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.update_crossbow_tooltips"))
                .binding(CrossbowDisplay.WHITE_ITEM_TEXT, () -> config.updateCrossbowTooltips, value -> config.updateCrossbowTooltips = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.update_crossbow_tooltips.desc")).webpImage(id("textures/gui/previews/update_crossbow_tooltips.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(CrossbowDisplay.class))
                .build();

        Option<?> UPDATE_ENCHANTMENT_TOOLTIPS = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.update_enchantment_tooltips"))
                .binding(true, () -> config.updateEnchantmentTooltips, value -> config.updateEnchantmentTooltips = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.update_enchantment_tooltips.desc")).webpImage(id("textures/gui/previews/update_enchantment_tooltips.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        Option<?> UPDATE_TIPPED_ARROW_TOOLTIPS = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.update_tipped_arrow_tooltips"))
                .binding(true, () -> config.updateTippedArrowTooltips, value -> config.updateTippedArrowTooltips = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.update_tipped_arrow_tooltips.desc")).webpImage(id("textures/gui/previews/update_tipped_arrow_tooltips.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        return ConfigCategory.createBuilder()
                .name(Text.translatable("tooltiptweaks.category.general"))

                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("tooltiptweaks.group.tools"))

                        .option(DURABILITY_DISPLAY_STYLE)
                        .option(DURABILITY_DIGIT_COUNT)
                        .option(DURABILITY_DISPLAY_USES_LEFT)
                        .option(DISPLAY_REPAIR_COST)
                        .option(UPDATE_CROSSBOW_TOOLTIPS)
                        .option(UPDATE_ENCHANTMENT_TOOLTIPS)
                        .option(UPDATE_TIPPED_ARROW_TOOLTIPS)

                        .build())

                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("tooltiptweaks.group.informational_tools"))

                        .option(CLOCK_DISPLAY_TIME)
                        .option(CLOCK_DISPLAY_DAY_NUMBER)
                        .option(CLOCK_DISPLAY_MOON_PHASE)
                        .option(COMPASS_DISPLAY_LOCATION)
                        .option(CONTAINER_DISPLAY_STYLE)
                        .option(CONTAINER_MAXIMUM_LIST_LENGTH)
                        .option(DISPLAY_MAPS)

                        .build())

                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("tooltiptweaks.group.other_items"))

                        .option(DISPLAY_AXOLOTL_VARIANTS)
                        .option(DISPLAY_SPAWN_EGG)
                        .option(DISPLAY_INSTRUMENT_TYPE)
                        .option(DISPLAY_PAINTINGS)

                        .build())
                .build();
    }

    private static ConfigCategory consumablesCategory(TooltipTweaksConfig config) {

        Option<?> NOURISHMENT_STYLE = Option.<NourishmentStyle>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.nourishment_style"))
                .binding(NourishmentStyle.TEXT, () -> config.nourishmentStyle, value -> config.nourishmentStyle = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.nourishment_style.desc")).webpImage(id("textures/gui/previews/nourishment_style.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(NourishmentStyle.class))
                .build();

        Option<?> NOURISHMENT_ICON_LOCATION = Option.<IconLocation>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.nourishment_icon_location"))
                .binding(IconLocation.BELOW, () -> config.nourishmentIconLocation, value -> config.nourishmentIconLocation = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.nourishment_icon_location.desc")).webpImage(id("textures/gui/previews/nourishment_icon_location.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(IconLocation.class))
                .build();

        Option<?> NOURISHMENT_DISPLAY = Option.<NourishmentDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.nourishment_display"))
                .binding(NourishmentDisplay.NUTRITION_ONLY, () -> config.nourishmentDisplay, value -> config.nourishmentDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.nourishment_display.desc")).webpImage(id("textures/gui/previews/nourishment_display.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(NourishmentDisplay.class))
                .build();

        Option<?> FOOD_EFFECT_DISPLAY = Option.<EffectDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.food_effect_display"))
                .binding(EffectDisplay.POSITIVE_EFFECTS_ONLY, () -> config.foodEffectDisplay, value -> config.foodEffectDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.food_effect_display.desc")).webpImage(id("textures/gui/previews/food_effect_display.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(EffectDisplay.class))
                .build();

        Option<?> STEW_EFFECT_DISPLAY = Option.<EffectDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.stew_effect_display"))
                .binding(EffectDisplay.CREATIVE_ONLY, () -> config.stewEffectDisplay, value -> config.stewEffectDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.stew_effect_display.desc")).webpImage(id("textures/gui/previews/stew_effect_display.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(EffectDisplay.class))
                .build();

        Option<?> MODIFIER_DISPLAY = Option.<OtherEffectDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.modifier_display"))
                .binding(OtherEffectDisplay.ENABLED, () -> config.modifierDisplay, value -> config.modifierDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.modifier_display.desc")).webpImage(id("textures/gui/previews/modifier_display.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(OtherEffectDisplay.class))
                .build();

        Option<?> OTHER_EFFECT_DISPLAY = Option.<OtherEffectDisplay>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.other_effect_display"))
                .binding(OtherEffectDisplay.ENABLED, () -> config.otherEffectDisplay, value -> config.otherEffectDisplay = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.other_effect_display.desc")).webpImage(id("textures/gui/previews/other_effect_display.webp")).build())
                .controller(opt -> EnumControllerBuilder.create(opt).enumClass(OtherEffectDisplay.class))
                .build();

        Option<?> UPDATE_POTION_TOOLTIPS = Option.<Boolean>createBuilder()
                .name(Text.translatable("tooltiptweaks.option.update_potion_tooltips"))
                .binding(true, () -> config.updatePotionTooltips, value -> config.updatePotionTooltips = value)
                .description(OptionDescription.createBuilder().text(Text.translatable("tooltiptweaks.option.update_potion_tooltips.desc")).webpImage(id("textures/gui/previews/update_potion_tooltips.webp")).build())
                .controller(TickBoxControllerBuilder::create)
                .build();

        return ConfigCategory.createBuilder()
                .name(Text.translatable("tooltiptweaks.category.consumables"))
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("tooltiptweaks.group.food"))

                        .option(NOURISHMENT_STYLE)
                        .option(NOURISHMENT_ICON_LOCATION)
                        .option(NOURISHMENT_DISPLAY)
                        .option(FOOD_EFFECT_DISPLAY)
                        .option(STEW_EFFECT_DISPLAY)
                        .option(MODIFIER_DISPLAY)
                        .option(OTHER_EFFECT_DISPLAY)

                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.translatable("tooltiptweaks.group.other_consumables"))

                        .option(UPDATE_POTION_TOOLTIPS)

                        .build())
                .build();
    }

    public static Screen buildMenu(Screen parent) {
        return YetAnotherConfigLib.create(TooltipTweaksConfig.HANDLER,
                (defaults, config, builder) -> {
                    return builder
                            .title(Text.translatable("tooltiptweaks.menu"))
                            .category(toolsCategory(config))
                            .category(consumablesCategory(config));
                }).generateScreen(parent);
    }

    static {
        HANDLER.load();
    }
}