package moe.pxe.macecompanion.util

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.world.InteractionResult

interface TitleCallback {
    fun setTitleText(packet: ClientboundSetTitleTextPacket): InteractionResult

    companion object {
        val EVENT: Event<TitleCallback> = EventFactory.createArrayBacked(TitleCallback::class.java) { listeners ->
            object : TitleCallback {
                override fun setTitleText(
                    packet: ClientboundSetTitleTextPacket
                ): InteractionResult {
                    for (listener in listeners) {
                        val result = listener.setTitleText(packet)
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