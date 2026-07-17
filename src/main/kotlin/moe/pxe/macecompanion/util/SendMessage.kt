package moe.pxe.macecompanion.util

import moe.pxe.macecompanion.MaceCompanion
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket
import net.minecraft.resources.Identifier
import java.util.Optional

object SendMessage {
    private data class DelayedAction(var remainingTicks: Int, val action: () -> Unit)
    private val delayedActions = mutableListOf<DelayedAction>()

    fun sendMessage(message: String) {
        if (MaceCompanion.DEBUG_MODE) {
            MaceCompanion.LOGGER.info("Debug Mode prevented the message \"${message}\" from being sent.")
            return
        }
        val player = Minecraft.getInstance().player ?: return
        player.connection.sendChat(message)
    }
    fun sendDelayedMessage(message: String, delay: Int) {
        schedule(delay) {
            sendMessage(message)
        }
    }
    fun schedule(ticks: Int, action: () -> Unit) {
        if (ticks <= 0) {
            action()
            return
        }
        delayedActions.add(DelayedAction(ticks, action))
    }
    fun sendCommand(message: String) {
        if (MaceCompanion.DEBUG_MODE) {
            MaceCompanion.LOGGER.info("Debug Mode prevented the command \"${message}\" from being sent.")
            return
        }
        val player = Minecraft.getInstance().player ?: return
        player.connection.sendCommand(message)
    }

    fun sendPlotCommand(command: String) {
        if (MaceCompanion.DEBUG_MODE) {
            MaceCompanion.LOGGER.info("Debug Mode prevented the plot command \"${command}\" from being sent.")
            return
        }
        val player = Minecraft.getInstance().player ?: return
        player.connection.send(ServerboundCustomClickActionPacket(
            Identifier.fromNamespaceAndPath("hypercube", "plot_command"),
            Optional.of(CompoundTag().also { it.putString("command", command) })
        ))
    }
    fun registerTickListener() {
        ClientTickEvents.START_CLIENT_TICK.register {
            val iterator = delayedActions.iterator()
            while (iterator.hasNext()) {
                val item = iterator.next()
                item.remainingTicks--
                if (item.remainingTicks <= 0) {
                    item.action()
                    iterator.remove()
                }
            }
        }
    }
}