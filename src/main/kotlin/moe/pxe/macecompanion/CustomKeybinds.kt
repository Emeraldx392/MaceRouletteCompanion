package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.ConfigMenu
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Identifier

object CustomKeybinds {
    lateinit var openProfileKeyBinding: KeyBinding
        private set
    lateinit var openCosmeticsKeyBinding: KeyBinding
        private set
    lateinit var openEventsKeyBinding: KeyBinding
        private set
    lateinit var openModOptionsKeyBinding: KeyBinding
        private set
    lateinit var toggleHudKeyBinding: KeyBinding
        private set
    lateinit var toggleAutoGLKeyBinding: KeyBinding
        private set
    lateinit var toggleAutoGGKeyBinding: KeyBinding
        private set

    fun registerKeybinds() {
        MaceCompanion.LOGGER.info("Registering mod keybinds")

        val maceRouletteCategory = KeyBinding.Category.create(Identifier.of(MaceCompanion.MOD_ID, "title"))

        openProfileKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.open_profile",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )
        openCosmeticsKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.open_cosmetics",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )
        openEventsKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.open_events",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )
        openModOptionsKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.open_mod_options",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )
        toggleHudKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.toggle_hud",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )
        toggleAutoGLKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.toggle_auto_gl",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )
        toggleAutoGGKeyBinding = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.macecompanion.toggle_auto_gg",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.code,
                maceRouletteCategory
            )
        )

        ClientTickEvents.END_CLIENT_TICK.register { client ->
            while (openProfileKeyBinding.wasPressed()) {
                SendMessage.sendMessage("@profile")
            }
            while (openCosmeticsKeyBinding.wasPressed()) {
                SendMessage.sendMessage("@cosmetics")
            }
            while (openEventsKeyBinding.wasPressed()) {
                SendMessage.sendMessage("@events")
            }
            while (openModOptionsKeyBinding.wasPressed()) {
                if (client.currentScreen == null) {
                    val configScreen = ConfigMenu.generateScreen(null)
                    client.setScreen(configScreen)
                }
            }
            while (toggleHudKeyBinding.wasPressed()) {
                val newValue = !Config.displayHud.value
                Config.displayHud.set(newValue)
                Config.saveToFile()
            }
            while (toggleAutoGLKeyBinding.wasPressed()) {
                val newValue = !Config.useAutoGL.value
                Config.useAutoGL.set(newValue)
                Config.saveToFile()
                if(newValue) client.player?.sendMessage(Text.literal("Auto GL enabled!").setStyle(Style.EMPTY.withColor(
                    Colors.GREEN)), false)
                if(!newValue) client.player?.sendMessage(Text.literal("Auto GL disabled!").setStyle(Style.EMPTY.withColor(
                    Colors.RED)), false)
            }
            while (toggleAutoGGKeyBinding.wasPressed()) {
                val newValue = !Config.useAutoGG.value
                Config.useAutoGG.set(newValue)
                Config.saveToFile()
                if(newValue) client.player?.sendMessage(Text.literal("Auto GG enabled!").setStyle(Style.EMPTY.withColor(
                    Colors.GREEN)), false)
                if(!newValue) client.player?.sendMessage(Text.literal("Auto GG disabled!").setStyle(Style.EMPTY.withColor(
                    Colors.RED)), false)
            }
        }
    }
}