package moe.pxe.macecompanion.stateManagers

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.CustomToasts
import moe.pxe.macecompanion.config.Config
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object PlayersOnlineManager {
    val chatJoinRegex = Regex("""\+ (.+)""")
    val chatJoinDFnNormalRegex = Regex("""(.+) joined\.""")
    val chatJoinDFnSpecialRegex = Regex("""\[.+](.+) joined!""")
    val chatLeaveRegex = Regex("""(.+) left\.""")

    fun registerPlayersOnlineListeners(){
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true

            val containsPlus = text.startsWith("+")
            val containsDot = text.endsWith(".")
            val containsExclamation = text.endsWith("!")
            if(!containsPlus && !containsDot && !containsExclamation) return@register true

            if(containsPlus) chatJoinRegex.matchEntire(text)?.groups?.let {
                if (Config.showPlayerToasts.value) CustomToasts.sendPlayerJoinedToast(it[1]?.value.toString())
                if (Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            chatJoinDFnNormalRegex.matchEntire(text)?.let {
                if (Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            if(containsExclamation) chatJoinDFnSpecialRegex.matchEntire(text)?.groups?.let {
                if (Config.showPlayerToasts.value) CustomToasts.sendPlayerJoinedToast(it[1]?.value.toString())
                if (Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            chatLeaveRegex.matchEntire(text)?.groups?.let {
                if (Config.showPlayerToasts.value) CustomToasts.sendPlayerLeftToast(it[1]?.value.toString())
                if (Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            return@register true
        }
    }
}