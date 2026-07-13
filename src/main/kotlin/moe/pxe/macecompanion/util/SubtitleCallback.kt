package moe.pxe.macecompanion.util

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.world.InteractionResult

interface SubtitleCallback {
    fun setSubtitleText(packet: ClientboundSetSubtitleTextPacket): InteractionResult

    companion object {
        val EVENT: Event<SubtitleCallback> = EventFactory.createArrayBacked(SubtitleCallback::class.java) { listeners ->
            object : SubtitleCallback {
                override fun setSubtitleText(packet: ClientboundSetSubtitleTextPacket): InteractionResult {
                    for (listener in listeners) {
                        val result = listener.setSubtitleText(packet)
                        if (result != InteractionResult.PASS) {
                            return result
                        }
                    }
                    return InteractionResult.PASS
                }

            }
        }
    }
}