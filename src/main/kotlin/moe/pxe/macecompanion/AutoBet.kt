package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.stateManagers.ShowdownManager.bluePlayer
import moe.pxe.macecompanion.stateManagers.ShowdownManager.redPlayer
import moe.pxe.macecompanion.util.SendMessage
import moe.pxe.macecompanion.util.SendMessage.sendMessage

object AutoBet {
    fun sendAutoBet() {
        if (!Config.useAutoBet.value) return
        SendMessage.schedule(Config.autoBetDelayTicks.value) {
            Config.autoBetConditions.value.forEach {
                val calculation = it.calculate(redPlayer!!.name, bluePlayer!!.name)
                when (calculation) {
                    null -> return@forEach
                    "red" -> sendMessage("@votered")
                    "blue" -> sendMessage("@voteblue")
                }
            }
        }
    }
}