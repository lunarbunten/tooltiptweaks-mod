package net.bunten.tooltiptweaks.tooltip;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

import java.util.LinkedHashMap;
import java.util.List;

public class ContainerTooltips {

    private static final Text UNKNOWN_CONTENTS_TEXT = Text.translatable("container.shulkerBox.unknownContents");

    private final DefaultedList<ItemStack> INVENTORY = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private final LinkedHashMap<Item, Integer> ITEM_COUNT_MAP = new LinkedHashMap<Item, Integer>();

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private void addEnhancedTooltips(ItemStack stack, List<Text> lines) {

        // Go through the inventory and add items to a HashMap
        for (ItemStack itemStack : stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).iterateNonEmpty()) {
            if (!itemStack.isEmpty()) {
                Item item = itemStack.getItem();
                int count = itemStack.getCount();

                if (!ITEM_COUNT_MAP.containsKey(item)) ITEM_COUNT_MAP.put(item, count); else ITEM_COUNT_MAP.put(item, ITEM_COUNT_MAP.get(item) + count);
            }
        }

        int maxrenderedLines = config.shulkerBoxEntries;
        var renderedLines = 0;
        var moreItems = 0;

        // Go through the HashMap and render lines based on the item and count data
        for (var set : ITEM_COUNT_MAP.entrySet()) {
            var name = set.getKey().getName().copyContentOnly();
            var count = set.getValue();
            
            if (renderedLines < maxrenderedLines) {
                lines.add(name.formatted(Formatting.GRAY).append(Text.translatable("tooltiptweaks.ui.shulker_box.entry", count).formatted(Formatting.WHITE)));
                renderedLines++;
            } else {
                moreItems++;
            }
        }  
        
        if (renderedLines >= maxrenderedLines) {
            lines.add(Text.translatable("tooltiptweaks.ui.shulker_box.more", moreItems).formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }

    private void addVanillaTooltips(ItemStack stack, List<Text> lines) {

        int maxrenderedLines = config.shulkerBoxEntries;
        var renderedLines = 0;
        var moreItems = 0;

        for (ItemStack itemStack : stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).iterateNonEmpty()) {
            var name = itemStack.getName().copyContentOnly();
            var count = itemStack.getCount();

            if (renderedLines < maxrenderedLines) {
                lines.add(name.formatted(Formatting.GRAY).append(Text.translatable("tooltiptweaks.ui.shulker_box.entry", count).formatted(Formatting.WHITE)));
                renderedLines++;
            } else {
                moreItems++;
            }
        }
        
        if (moreItems > 0) {
            lines.add(Text.translatable("tooltiptweaks.ui.shulker_box.more", moreItems).formatted(Formatting.ITALIC, Formatting.GRAY));
        }
    }

    public void addTooltips(ItemStack stack, Item.TooltipContext context, TooltipType type, List<Text> lines) {
        if (stack.contains(DataComponentTypes.CONTAINER_LOOT)) {
            lines.add(UNKNOWN_CONTENTS_TEXT);
        }

        var display = config.shulkerBoxDisplay;
        if (stack.getComponents().contains(DataComponentTypes.CONTAINER) && display < 2) {
            if (display > 0 && display != 4) {
                addEnhancedTooltips(stack, lines);
            } else {
                addVanillaTooltips(stack, lines);
            }
        }
    }
}