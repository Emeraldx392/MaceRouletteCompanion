package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.RoundManager.round
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.world.item.Items

object AccuracyManager {
    var maceAttempts = mutableMapOf<Int, Boolean>()
    var hasMace: Boolean = false
    var lastRoundWithMace: Int = -1

    fun resetAccuracyData(){
        maceAttempts = mutableMapOf()
        hasMace = false
        lastRoundWithMace = -1
    }
    fun registerAccuracyListeners(){
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
            hasMace = (!eliminated && (client.player?.inventory?.hasAnyOf(setOf(Items.MACE)) ?: false))
            if (hasMace) {
                lastRoundWithMace = round
                if (!(maceAttempts[round] ?: false)) maceAttempts[round] = false
            }
        })
    }
}