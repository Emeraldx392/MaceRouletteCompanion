package moe.pxe.macecompanion

import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text

object CustomToasts {
    fun sendCustomToast(title: Text, description: Text) {
        SystemToast.add(
            MinecraftClient.getInstance().getToastManager(),
            SystemToast.Type.PERIODIC_NOTIFICATION,
            title,
            description
        )
    }
    fun sendNewEventToast(description: Text) {
        sendCustomToast(Text.literal("New Event Started!"), description)
    }
}