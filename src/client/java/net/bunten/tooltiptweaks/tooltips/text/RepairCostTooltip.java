package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.bunten.tooltiptweaks.config.options.RepairCostDisplay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

import static net.bunten.tooltiptweaks.TooltipTweaksMod.creative;

public class RepairCostTooltip {

    private final MinecraftClient client = MinecraftClient.getInstance();
    private final TooltipTweaksConfig config = TooltipTweaksConfig.getInstance();

    private int getRepairCostTextColor(int repairCost) {
        float max = 36;
        float damage = Math.min(repairCost, max);

        float f = Math.max(0, (max - damage) / max);
        return MathHelper.hsvToRgb(f / 5, 1, 1);
    }

    private boolean canDisplay(ItemStack stack) {
        if (!stack.contains(DataComponentTypes.REPAIR_COST) || config.repairCostDisplay == RepairCostDisplay.DISABLED) return false;

        if (config.repairCostDisplay == RepairCostDisplay.ON_RELEVANT_MENUS) {
            return client.currentScreen instanceof AnvilScreen || client.currentScreen instanceof GrindstoneScreen || client.currentScreen instanceof SmithingScreen;
        }

        return config.repairCostDisplay == RepairCostDisplay.ENABLED;
    }

    public void register(ItemStack stack, List<Text> lines) {
        if (canDisplay(stack)) {
            Integer repairCost = stack.get(DataComponentTypes.REPAIR_COST);
            if (repairCost == null || repairCost < 1) return;
            MutableText message = Text.translatable("tooltiptweaks.ui.repair_cost", repairCost);

            if (!creative() && repairCost + 1 >= 40) message = Text.translatable("tooltiptweaks.ui.cannot_repair");

            lines.add(message.setStyle(message.getStyle().withColor(getRepairCostTextColor(repairCost))));
        }
    }
}