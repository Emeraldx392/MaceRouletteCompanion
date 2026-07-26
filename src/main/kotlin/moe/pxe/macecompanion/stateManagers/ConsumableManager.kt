package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.CustomToasts.sendChaosStarterToast
import moe.pxe.macecompanion.CustomToasts.sendEternalElectorToast
import moe.pxe.macecompanion.CustomToasts.sendModifierChargerToast
import moe.pxe.macecompanion.stateManagers.ModifierManager.getModifierFromMessage
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object ConsumableManager {
    var eternalElectorPlayer: String? = null
    var eternalElectorModifier: String? = null

    val modifierChargerRegex = Regex("""⏵ (.+) used a Modifier Charger on (.+)!\n\s+◇ It will be charged for its next (\d+) appearances!""")
    val chaosStarterRegex = Regex("""⏵ (.+) used a Chaos Starter!\n\s+◇ The next round will have five modifiers!""")
    val eternalElectorRegex = Regex("""⏵ (.+) used a Eternal Elector for (.+)!""")
    val eternalElectorPositionRegex = Regex("""\s+◇ It has been queued at position #(\d+)!""")

    fun resetConsumableData(){
        eternalElectorModifier = null
        eternalElectorPlayer = null
    }

    fun registerConsumableListeners() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!text.startsWith("⏵") && !text.contains("#")) return@register true

            modifierChargerRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value.toString()
                val modifier = getModifierFromMessage(message).toString()
                val queueLength = it[3]?.value?.toIntOrNull() ?: 0
                sendModifierChargerToast(modifier, queueLength, player)
            }
            chaosStarterRegex.matchEntire(text)?.groups?.let {
                val player = it[1]?.value.toString()
                sendChaosStarterToast(player)
            }
            eternalElectorRegex.matchEntire(text)?.groups?.let {
                eternalElectorPlayer = it[1]?.value.toString()
                eternalElectorModifier = getModifierFromMessage(message).toString()
            }
            eternalElectorPositionRegex.matchEntire(text)?.groups?.let {
                val queuePosition = it[1]?.value!!.toInt()
                if (eternalElectorPlayer != null && eternalElectorModifier != null) sendEternalElectorToast(eternalElectorModifier, eternalElectorPlayer, queuePosition)
                eternalElectorPlayer = null
                eternalElectorModifier = null
            }
            return@register true
        }
    }
}