package moe.pxe.macecompanion.stateManagers

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft

object PerformanceStatsManager {
    var fps: Int = -1
    var tps: Float = -1f

    fun registerPerformanceStatsListeners(){
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            fps = client.fps
            tps = client.level?.tickRateManager()?.tickrate() ?: -1f
        })
    }
}