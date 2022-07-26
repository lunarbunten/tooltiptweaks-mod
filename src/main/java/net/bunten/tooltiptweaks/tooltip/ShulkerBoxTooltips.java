package net.bunten.tooltiptweaks.tooltip;

import java.util.LinkedHashMap;
import java.util.List;

import net.bunten.tooltiptweaks.TooltipTweaksMod;
import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;

public class ShulkerBoxTooltips {

    private final DefaultedList<ItemStack> INVENTORY = DefaultedList.ofSize(27, ItemStack.EMPTY);
    private final LinkedHashMap<Item, Integer> ITEM_COUNT_MAP = new LinkedHashMap<Item, Integer>();

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private void addEnhancedTooltips(NbtCompound nbt, List<Text> lines) {
        Inventories.readNbt(nbt, INVENTORY);

        // Go through the inventory and add items to a HashMap
        for (ItemStack stack : INVENTORY) {
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                int count = stack.getCount();

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

    private void addVanillaTooltips(NbtCompound nbt, List<Text> lines) {
        Inventories.readNbt(nbt, INVENTORY);

        int maxrenderedLines = config.shulkerBoxEntries;
        var renderedLines = 0;
        var moreItems = 0;

        for (ItemStack stack : INVENTORY) {
            var name = stack.getName().copyContentOnly();
            var count = stack.getCount();

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

    public void addTooltips(ItemStack stack, List<Text> lines) {
        var nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt != null) {
            if (nbt.contains("LootTable", 8)) {
                lines.add(Text.literal("???????"));
            }

            var display = config.shulkerBoxDisplay;
            if (nbt.contains("Items", 9) && display < 2) {
                if (display > 0 && display != 4) {
                    addEnhancedTooltips(nbt, lines);
                } else {
                    addVanillaTooltips(nbt, lines);
                }
            }
        }
    }
}