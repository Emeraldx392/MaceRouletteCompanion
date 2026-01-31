package moe.pxe.macecompanion.util

import moe.pxe.macecompanion.MaceCompanion
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket
import net.minecraft.util.Identifier
import java.util.Optional

object SendMessage {
    private val delayedMessages = mutableListOf<String>()
    private val delayedTicks = mutableListOf<Int>()

    fun sendMessage(message: String) {
        if (MaceCompanion.DEBUG_MODE) {
            MaceCompanion.LOGGER.info("Debug Mode prevented the message \"${message}\" from being sent.")
            return
        }
        val player = MinecraftClient.getInstance().player ?: return
        player.networkHandler.sendChatMessage(message)
    }
    fun sendDelayedMessage(message: String, delay: Int) {
        delayedMessages.add(message)
        delayedTicks.add(delay)
    }
    fun sendCommand(message: String) {
        if (MaceCompanion.DEBUG_MODE) {
            MaceCompanion.LOGGER.info("Debug Mode prevented the command \"${message}\" from being sent.")
            return
        }
        val player = MinecraftClient.getInstance().player ?: return
        player.networkHandler.sendChatCommand(message)
    }
    fun sendPlotCommand(command: String) {
        if (MaceCompanion.DEBUG_MODE) {
            MaceCompanion.LOGGER.info("Debug Mode prevented the plot command \"${command}\" from being sent.")
            return
        }
        val player = MinecraftClient.getInstance().player ?: return
        player.networkHandler.sendPacket(CustomClickActionC2SPacket(
            Identifier.of("hypercube", "plot_command"),
            Optional.of(NbtCompound().also { it.putString("command", command) })
        ))
    }

    fun registerTickListener() {
        ClientTickEvents.START_CLIENT_TICK.register {
            for (i in delayedMessages.indices) {
                if (i >= delayedTicks.size) return@register
                delayedTicks[i]--
                if (delayedTicks[i] <= 0) {
                    MaceCompanion.LOGGER.info("$delayedMessages $delayedTicks")
                    sendMessage(delayedMessages[i])
                    delayedTicks.removeAt(i)
                    delayedMessages.removeAt(i)
                    MaceCompanion.LOGGER.info("$delayedMessages $delayedTicks")
                }
            }
        }
    }
}