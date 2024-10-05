package net.bunten.tooltiptweaks.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigMenuScreen {
    
    TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("tooltiptweaks.menu.options"));

        builder.setSavingRunnable(TooltipTweaksMod::saveSettings);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        SubCategoryBuilder tools = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.tools"));
        addToolOptions(entryBuilder, tools);
        general.addEntry(tools.build());

        SubCategoryBuilder durability = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.durability"));
        addDurabilityOptions(entryBuilder, durability);
        general.addEntry(durability.build());

        SubCategoryBuilder consumables = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.consumables"));
        addConsumablesOptions(entryBuilder, consumables);
        general.addEntry(consumables.build());

        SubCategoryBuilder otherItems = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.other_items"));
        addOtherItemOptions(entryBuilder, otherItems);
        general.addEntry(otherItems.build());
        
        return builder.setTransparentBackground(true).build();
    }

    private void addToolOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {

        // Clock Display Style

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.clock.display"), new Byte [] {
                0, 1, 2
        }, config.clockTimeDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.none");
            case 1 -> Text.translatable("tooltiptweaks.options.value.12hour");
            default -> Text.translatable("tooltiptweaks.options.value.24hour");
        }).setSaveConsumer((newValue)->config.clockTimeDisplay = newValue).build());

        // Compass Display Style

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.compass"), new Byte [] {
                0, 1, 2
        }, config.compassDisplay).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.compass.distance");
            case 1 -> Text.translatable("tooltiptweaks.options.value.compass.coordinates");
            default -> Text.translatable("tooltiptweaks.options.value.none");
        }).setSaveConsumer((newValue)->config.compassDisplay = newValue).build());

        // Container Display Style

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.container.display"), new Byte [] {
                0, 1, 2, 3
        }, config.containerDisplay).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.container.per_item");
            case 1 -> Text.translatable("tooltiptweaks.options.value.container.per_stack");
            case 2 -> Text.translatable("tooltiptweaks.options.value.container.inventory");
            default -> Text.translatable("tooltiptweaks.options.value.vanilla");
        }).setSaveConsumer((newValue)->config.containerDisplay = newValue).build());

        // Container Entry Count

        category.add(entryBuilder.startIntSlider(Text.translatable("tooltiptweaks.options.tools.container.entries"), config.containerEntries, 1, 27).setDefaultValue(6).setSaveConsumer(newValue -> config.containerEntries = newValue).build());

        // Repair Cost Display

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.repair_cost"), new Byte [] {
                0, 1
        }, config.repairCostDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.yes");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.repairCostDisplay = newValue).build());
    }

    private void addDurabilityOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {

        // Display Style

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.durability.display"), new Byte [] {
            0, 1, 2
        }, config.durabilityDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.none");
            case 1 -> Text.translatable("tooltiptweaks.options.value.percentage");
            default -> Text.translatable("tooltiptweaks.options.value.fraction");
        }).setSaveConsumer((newValue)->config.durabilityDisplay = newValue).build());

        // Decimal Count

        category.add(entryBuilder.startIntSlider(Text.translatable("tooltiptweaks.options.tools.durability.decimal_count"), config.percentageDecimalCount, 0, 4).setDefaultValue(0).setSaveConsumer(newValue -> config.percentageDecimalCount = newValue).build());
        
        // Display "Uses Left"

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.durability.uses_left"), new Byte [] {
            0, 1
        }, config.displayUsesLeft).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.no");
            default -> Text.translatable("tooltiptweaks.options.value.yes");
        }).setSaveConsumer((newValue)->config.displayUsesLeft = newValue).build());

    }

    private void addConsumablesOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {

        // Food Nourishment

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.consumables.display"), new Byte [] {
                0, 1, 2
        }, config.foodDisplay).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.food_points_only");
            case 1 -> Text.translatable("tooltiptweaks.options.value.food_and_saturation");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.foodDisplay = newValue).build());

        // Food Status Effects

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.consumables.effects"), new Byte [] {
                0, 1, 2
        }, config.foodEffectDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.all_effects");
            case 1 -> Text.translatable("tooltiptweaks.options.value.positive_effects");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.foodEffectDisplay = newValue).build());

        // Suspicious Stew Status Effects

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.consumables.stew_effects"), new Byte [] {
                0, 1, 2
        }, config.stewEffectDisplay).setDefaultValue((byte) 2).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.all_effects");
            case 1 -> Text.translatable("tooltiptweaks.options.value.positive_effects");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.stewEffectDisplay = newValue).build());

        // Show Modifiers

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.consumables.modifiers"), new Byte [] {
                0, 1
        }, config.modifierDisplay).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.yes");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.modifierDisplay = newValue).build());

        // Other Effects

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.consumables.other_effects"), new Byte [] {
                0, 1
        }, config.otherEffectDisplay).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.all_effects");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.otherEffectDisplay = newValue).build());
    }

    private void addOtherItemOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {

        // Axolotl Variants

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.other_items.axolotl_variants"), new Byte [] {
                0, 1
        }, config.axolotlVariants).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.yes");
            default -> Text.translatable("tooltiptweaks.options.value.no");
        }).setSaveConsumer((newValue)->config.axolotlVariants = newValue).build());}
}