package moe.pxe.macecompanion.util

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.util.ActionResult

interface TitleCallback {
    fun onTitle(packet: TitleS2CPacket): ActionResult

    companion object {
        val EVENT: Event<TitleCallback> = EventFactory.createArrayBacked(TitleCallback::class.java) { listeners ->
            object : TitleCallback {
                override fun onTitle(
                    packet: TitleS2CPacket
                ): ActionResult {
                    for (listener in listeners) {
                        val result = listener.onTitle(packet)
                        if (result != ActionResult.PASS) {
                            return result
                        }
                    }
                    return ActionResult.PASS
                }
            }
        }
    }
}