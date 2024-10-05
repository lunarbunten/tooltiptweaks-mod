package net.bunten.tooltiptweaks.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.MinecraftClient;

public class TooltipModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return (parent)-> TooltipTweaksConfig.getMenu().build(parent, MinecraftClient.getInstance().world != null);
    }
}