package moe.pxe.macecompanion.util

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.util.ActionResult

interface SubtitleCallback {
    fun onSubtitle(packet: SubtitleS2CPacket): ActionResult

    companion object {
        val EVENT: Event<SubtitleCallback> = EventFactory.createArrayBacked(SubtitleCallback::class.java) { listeners ->
            object : SubtitleCallback {
                override fun onSubtitle(packet: SubtitleS2CPacket): ActionResult {
                    for (listener in listeners) {
                        val result = listener.onSubtitle(packet)
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