package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.MaceCompanion.Companion.MOD_ID
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader
import moe.pxe.macecompanion.resourceLoaders.AchievementResourceLoader.ACHIEVEMENT_DIRECTORY
import moe.pxe.macecompanion.resourceLoaders.EventResourceLoader
import moe.pxe.macecompanion.resourceLoaders.EventResourceLoader.EVENT_DIRECTORY
import moe.pxe.macecompanion.resourceLoaders.IconOverridesResourceLoader
import moe.pxe.macecompanion.resourceLoaders.IconOverridesResourceLoader.ICON_OVERRIDES_DIRECTORY
import moe.pxe.macecompanion.resourceLoaders.ModifierResourceLoader
import moe.pxe.macecompanion.resourceLoaders.ModifierResourceLoader.MODIFIER_DIRECTORY
import net.fabricmc.fabric.api.resource.v1.ResourceLoader
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.PackType

object ResourcePackManager {
    fun registerResourcePackListeners() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(
                Identifier.fromNamespaceAndPath(MOD_ID, ACHIEVEMENT_DIRECTORY),
                AchievementResourceLoader
            )
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(
                Identifier.fromNamespaceAndPath(MOD_ID, EVENT_DIRECTORY),
                EventResourceLoader
            )
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(
                Identifier.fromNamespaceAndPath(MOD_ID, MODIFIER_DIRECTORY),
                ModifierResourceLoader
            )
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
            .registerReloadListener(
                Identifier.fromNamespaceAndPath(MOD_ID, ICON_OVERRIDES_DIRECTORY),
                IconOverridesResourceLoader
            )
    }
    fun registerDefaultResourcePacks() {
        FabricLoader.getInstance().getModContainer("macecompanion").ifPresent { container ->
            ResourceLoader.registerBuiltinPack(
                Identifier.fromNamespaceAndPath("macecompanion", "custom_modifier_textures"),
                container,
                Component.translatable("mrc.resourcepack.custom_modifier_textures"),
                PackActivationType.NORMAL
            )
        }
    }
}

