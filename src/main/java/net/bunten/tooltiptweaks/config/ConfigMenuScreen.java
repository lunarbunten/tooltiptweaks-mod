package net.bunten.tooltiptweaks.config;

import java.util.List;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class ConfigMenuScreen {
    
    TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    public Screen getConfigScreen(Screen parent, boolean isTransparent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.translatable("tooltiptweaks.menu.options"));

        builder.setSavingRunnable(TooltipTweaksMod::saveSettings);

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        SubCategoryBuilder durability = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.durability"));
        addDurabilityOptions(entryBuilder, durability);
        general.addEntry(durability.build());

        SubCategoryBuilder food = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.food"));
        addFoodOptions(entryBuilder, food);
        general.addEntry(food.build());

        SubCategoryBuilder tools = entryBuilder.startSubCategory(Text.translatable("tooltiptweaks.section.tools"));
        addToolOptions(entryBuilder, tools);
        general.addEntry(tools.build());
        
        return builder.setTransparentBackground(isTransparent).build();
    }

    private void addDurabilityOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        // Percentage / Fraction Display
        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.durability.display"), new Byte [] {
            0, 1, 2
        }, config.durabilityDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.off");
            case 1 -> Text.translatable("tooltiptweaks.options.value.percentage");
            default -> Text.translatable("tooltiptweaks.options.value.fraction");
        }).setSaveConsumer((newValue)->config.durabilityDisplay = newValue).build());

        category.add(entryBuilder.startIntSlider(Text.translatable("tooltiptweaks.options.durability.percentage.decimals"), config.percentageDecimalCount, 0, 4).setDefaultValue(0).setSaveConsumer(newValue -> config.percentageDecimalCount = newValue).build());
        
        // Remaining Uses
        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.durability.remaininguses"), new Byte [] {
            0, 1, 2
        }, config.toolUsesLeft).setDefaultValue((byte) 0).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.off");
            case 1 -> Text.translatable("tooltiptweaks.options.value.on");
            default -> Text.translatable("tooltiptweaks.options.value.low_durability");
        }).setSaveConsumer((newValue)->config.toolUsesLeft = newValue).build());
                
        category.add(entryBuilder.startIntSlider(Text.translatable("tooltiptweaks.options.durability.remaininguses.threshold"), config.lowDurabilityThreshold, 0, 100).setDefaultValue(25).setSaveConsumer(newValue -> config.lowDurabilityThreshold = newValue).build());
    }

    private void addFoodOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.food.hunger"), config.showHungerRestoration).setDefaultValue(true).setSaveConsumer(newValue -> config.showHungerRestoration = newValue).build());
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.food.saturation"), config.showSaturationRestoration).setDefaultValue(false).setSaveConsumer(newValue -> config.showSaturationRestoration = newValue).build());
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.food.effects"), config.showFoodStatusEffects).setDefaultValue(true).setSaveConsumer(newValue -> config.showFoodStatusEffects = newValue).build());
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.food.negative"), config.hideNegativeFoodEffects).setDefaultValue(true).setSaveConsumer(newValue -> config.hideNegativeFoodEffects = newValue).build());
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.food.stew"), config.hideStewEffects).setDefaultValue(false).setSaveConsumer(newValue -> config.hideStewEffects = newValue).build());
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.food.stew.negative"), config.hideNegativeStewEffects).setDefaultValue(false).setSaveConsumer(newValue -> config.hideNegativeStewEffects = newValue).build());
    }

    private void addToolOptions(ConfigEntryBuilder entryBuilder, SubCategoryBuilder category) {
        // Beehive Data
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.tools.beehive.bees"), config.beehiveBeeDisplay).setDefaultValue(true).setSaveConsumer(newValue -> config.beehiveBeeDisplay = newValue).build());
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.tools.beehive.honey"), config.beehiveHoneyDisplay).setDefaultValue(false).setSaveConsumer(newValue -> config.beehiveHoneyDisplay = newValue).build());
        
        // Clock Time
        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.clock.display"), new Byte [] {
            0, 1, 2
        }, config.clockTimeDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.off");
            case 1 -> Text.translatable("tooltiptweaks.options.value.12hour");
            default -> Text.translatable("tooltiptweaks.options.value.24hour");
        }).setSaveConsumer((newValue)->config.clockTimeDisplay = newValue).build());

        // Lodestone Compass
        category.add(entryBuilder.startBooleanToggle(Text.translatable("tooltiptweaks.options.tools.compass.lodestone"), config.lodestoneCompassDisplay).setDefaultValue(false).setSaveConsumer(newValue -> config.lodestoneCompassDisplay = newValue).build());

        category.add(entryBuilder.startSelector(Text.translatable("tooltiptweaks.options.tools.shulker_box.display"), new Byte [] {
            0, 1, 2, 3
        }, config.shulkerBoxDisplay).setDefaultValue((byte) 1).setNameProvider((value)-> switch (value) {
            case 0 -> Text.translatable("tooltiptweaks.options.value.shulker_box.vanilla");
            case 1 -> Text.translatable("tooltiptweaks.options.value.shulker_box.enhanced");
            case 2 -> Text.translatable("tooltiptweaks.options.value.shulker_box.inventory");
            default -> Text.translatable("tooltiptweaks.options.value.shulker_box.compact");
        }).setSaveConsumer((newValue)->config.shulkerBoxDisplay = newValue).build());

        category.add(entryBuilder.startIntSlider(Text.translatable("tooltiptweaks.options.tools.shulker_box.entries"), config.shulkerBoxEntries, 4, 8).setDefaultValue(6).setSaveConsumer(newValue -> config.shulkerBoxEntries = newValue).build());
    }

    public Text[] wrapLines(Text text) {
        final MinecraftClient client = MinecraftClient.getInstance();
        List<StringVisitable> lines = client.textRenderer.getTextHandler().wrapLines(text, Math.max(client.getWindow().getWidth() / 2 - 43, 170), Style.EMPTY);
        lines.get(0).getString();
        Text[] textLines = new Text[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            textLines[i] = Text.literal(lines.get(i).getString());
        }
        return textLines;
    }
}