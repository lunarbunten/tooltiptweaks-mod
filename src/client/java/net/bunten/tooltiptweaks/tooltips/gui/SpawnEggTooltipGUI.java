package net.bunten.tooltiptweaks.tooltips.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bunten.tooltiptweaks.tooltips.AbstractTooltip;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class SpawnEggTooltipGUI extends AbstractTooltip {
    private ItemStack stack;

    @Nullable
    private Item item;

    @Override
    public AbstractTooltip withStack(ItemStack stack) {
        this.stack = stack;
        return this;
    }

    @Override
    public boolean canDisplay(ItemStack stack) {
        String key = stack.isOf(Items.TRIAL_SPAWNER) ? "spawn_data" : "SpawnData";
        if (!stack.contains(DataComponentTypes.BLOCK_ENTITY_DATA)) return false;

        NbtCompound nbtCompound = stack.getOrDefault(DataComponentTypes.BLOCK_ENTITY_DATA, NbtComponent.DEFAULT).getNbt();
        if (!nbtCompound.contains(key)) return false;

        Identifier identifier = getSpawnedEntityId(nbtCompound, key);
        EntityType<?> type = Registries.ENTITY_TYPE.get(identifier);
        item = SpawnEggItem.forEntity(type);

        return item != null && (item != Items.PIG_SPAWN_EGG || type == EntityType.PIG);
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return 20;
    }

    @Nullable
    private static Identifier getSpawnedEntityId(NbtCompound nbt, String spawnDataKey) {
        if (nbt.contains(spawnDataKey, NbtElement.COMPOUND_TYPE)) {
            String string = nbt.getCompound(spawnDataKey).getCompound("entity").getString("id");
            return Identifier.tryParse(string);
        } else {
            return null;
        }
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        RenderSystem.enableBlend();

        if (item == null) return;

        context.drawItem(item.getDefaultStack(), x, y);

        int rx = x + textRenderer.getWidth(stack.getName()) + 2;
        int ry = y - 12;

        RenderSystem.disableBlend();
    }
}