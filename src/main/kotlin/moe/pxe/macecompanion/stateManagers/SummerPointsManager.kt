package moe.pxe.macecompanion.stateManagers

import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.Minecraft

object SummerPointsManager {
    var summerPoints: Int = -1
    var summerColor: Int = 0xffffff

    val summerPointRegex = Regex("""⚑ (.+) scored 1 point for team (.+)! \(.+\)""")
    val summerPointsRegex = Regex("""⚑ (.+) scored (\d+) points for team (.+)! \(.+\)""")

    val client: Minecraft = Minecraft.getInstance()

    fun resetSummerPointsData(){
        summerPoints = -1
        summerColor= 0xffffff
    }

    fun registerSummerPointsListeners(){
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!PlotManager.onMaceRoulette) return@register true

            val hasFlag = (text.startsWith("⚑"))
            if(!hasFlag) return@register true

            summerPointRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value
                val team = it[2]?.value
                if (player == client.user.name) {
                    if (summerPoints == -1) summerPoints = 1
                    else summerPoints++
                    if (summerColor == 0xffffff) when (team) {
                        "Melon" -> summerColor = 0xFF7CAE
                        "Apricot" -> summerColor = 0xFF7E47
                        "Lemon" -> summerColor = 0xFFC447
                        "Pear" -> summerColor = 0x78D647
                        "Berry" -> summerColor = 0x47B6FF
                    }
                }
            }
            summerPointsRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value
                val points = it[2]?.value?.toInt() ?: 0
                val team = it[3]?.value
                if (player == client.user.name) {
                    if (summerPoints == -1) summerPoints = points
                    else summerPoints += points
                    if (summerColor == 0xffffff) when (team) {
                        "Melon" -> summerColor = 0xFF7CAE
                        "Apricot" -> summerColor = 0xFF7E47
                        "Lemon" -> summerColor = 0xFFC447
                        "Pear" -> summerColor = 0x78D647
                        "Berry" -> summerColor = 0x47B6FF
                    }
                }
            }

            return@register true
        }
    }
}