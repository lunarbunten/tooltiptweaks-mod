package net.bunten.tooltiptweaks.tooltips.text;

import net.bunten.tooltiptweaks.config.TooltipTweaksConfig;
import net.minecraft.client.MinecraftClient;
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
        return MathHelper.hsvToRgb(f / 3, 1, 1);
    }

    public void register(ItemStack stack, List<Text> lines) {
        if (!stack.contains(DataComponentTypes.REPAIR_COST)) return;
        Integer repairCost = stack.get(DataComponentTypes.REPAIR_COST);
        if (repairCost == null || repairCost < 1) return;
        MutableText message = Text.translatable("tooltiptweaks.ui.repair_cost", repairCost);

        if (!creative() && repairCost + 1 >= 40) message = Text.translatable("tooltiptweaks.ui.cannot_repair");

        lines.add(message.setStyle(message.getStyle().withColor(getRepairCostTextColor(repairCost))));
    }
}