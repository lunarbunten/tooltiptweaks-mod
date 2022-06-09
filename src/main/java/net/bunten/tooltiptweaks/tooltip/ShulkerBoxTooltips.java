package net.bunten.tooltiptweaks.tooltip;

import java.util.HashMap;
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

    private final TooltipTweaksConfig config = TooltipTweaksMod.getConfig();

    private void addEnhancedTooltips(NbtCompound nbt, List<Text> lines) {
        // HashMap that will be used to keep track of the item type and the total count
        var map = new HashMap<Item, Integer>();

        var inventory = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.readNbt(nbt, inventory);

        // Go through the inventory and add items to a HashMap
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                Item item = stack.getItem();
                int count = stack.getCount();

                if (!map.containsKey(item)) {
                    // If the HashMap does not contain the item, add it along with the countof the item
                    map.put(item, count);
                } else { 
                    // If the HashMap already contains the item, readd the item but increment the existing count
                    map.put(item, map.get(item) + count);
                }
            }
        }

        int maxrenderedLines = config.shulkerBoxEntries;
        var renderedLines = 0;
        var moreItems = 0;

        // Go through the HashMap and render lines based on the item and count data
        for (var set : map.entrySet()) {
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
        DefaultedList<ItemStack> list = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.readNbt(nbt, list);

        int maxrenderedLines = config.shulkerBoxEntries;
        var renderedLines = 0;
        var moreItems = 0;

        for (ItemStack stack : list) {
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
                if (display > 0) {
                    addEnhancedTooltips(nbt, lines);
                } else {
                    addVanillaTooltips(nbt, lines);
                }
            }
        }
    }
}