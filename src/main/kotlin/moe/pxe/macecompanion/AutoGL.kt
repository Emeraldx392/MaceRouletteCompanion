package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.util.SendMessage

object AutoGL {
    fun sendGlMessage() {
        if (!Config.useAutoGL.value) return
        SendMessage.sendDelayedMessage(Config.autoGLStrings.value.random(), Config.glDelayTicks.value)
    }
}