package moe.pxe.macecompanion.util

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

object SendMessage {
    private data class DelayedAction(var remainingTicks: Int, val action: () -> Unit)
    private val delayedActions = mutableListOf<DelayedAction>()

    fun sendMessage(message: String) {
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
        val player = Minecraft.getInstance().player ?: return
        player.connection.sendCommand(message)
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