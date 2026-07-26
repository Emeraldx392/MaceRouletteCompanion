package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.ConfigMenu
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.KeyMapping
import com.mojang.blaze3d.platform.InputConstants
import moe.pxe.macecompanion.stateManagers.PlotManager
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.resources.Identifier

object CustomKeybinds {
    lateinit var openProfileKeyBinding: KeyMapping
        private set
    lateinit var openCosmeticsKeyBinding: KeyMapping
        private set
    lateinit var openEventsKeyBinding: KeyMapping
        private set
    lateinit var openSummerKeyBinding: KeyMapping
        private set
    lateinit var openModOptionsKeyBinding: KeyMapping
        private set
    lateinit var toggleHudKeyBinding: KeyMapping
        private set
    lateinit var toggleAutoGLKeyBinding: KeyMapping
        private set
    lateinit var toggleAutoGGKeyBinding: KeyMapping
        private set

    fun registerKeybinds() {
        MaceCompanion.LOGGER.info("Registering mod keybinds")

        val maceRouletteCategory = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "title"))

        openProfileKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.open_profile",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openCosmeticsKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.open_cosmetics",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openEventsKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.open_events",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openSummerKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.open_summer",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        openModOptionsKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.open_mod_options",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        toggleHudKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.toggle_hud",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        toggleAutoGLKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.toggle_auto_gl",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )
        toggleAutoGGKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyMapping(
                "key.macecompanion.toggle_auto_gg",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.value,
                maceRouletteCategory
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (openProfileKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette) SendMessage.sendMessage("@profile")
            }
            while (openCosmeticsKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette) SendMessage.sendMessage("@cosmetics")
            }
            while (openEventsKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette) SendMessage.sendMessage("@events")
            }
            while (openSummerKeyBinding.consumeClick()) {
                if(PlotManager.onMaceRoulette) SendMessage.sendMessage("@summer")
            }
            while (openModOptionsKeyBinding.consumeClick()) {
                if (client.screen == null) {
                    val configScreen = ConfigMenu.generateScreen(null)
                    client.setScreen(configScreen)
                }
            }
            while (toggleHudKeyBinding.consumeClick()) {
                val newValue = !Config.displayHud.value
                Config.displayHud.set(newValue)
                Config.saveToFile()
            }
            while (toggleAutoGLKeyBinding.consumeClick()) {
                val newValue = !Config.useAutoGL.value
                Config.useAutoGL.set(newValue)
                Config.saveToFile()
                if(newValue) client.player?.displayClientMessage(Component.literal("Auto GL enabled!").withColor(CommonColors.GREEN), false)
                if(!newValue) client.player?.displayClientMessage(Component.literal("Auto GL disabled!").withColor(CommonColors.RED), false)
            }
            while (toggleAutoGGKeyBinding.consumeClick()) {
                val newValue = !Config.useAutoGG.value
                Config.useAutoGG.set(newValue)
                Config.saveToFile()
                if(newValue) client.player?.displayClientMessage(Component.literal("Auto GG enabled!").withColor(CommonColors.GREEN), false)
                if(!newValue) client.player?.displayClientMessage(Component.literal("Auto GG disabled!").withColor(CommonColors.RED), false)
            }
        }
    }
}