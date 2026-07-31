package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.AutoGG
import moe.pxe.macecompanion.AutoGL
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.stateManagers.AccuracyManager.resetAccuracyData
import moe.pxe.macecompanion.stateManagers.EliminationManager.checkIfEliminated
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminations
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersAlive
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.ModifierManager.eternalModifier
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifierBoosters
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifiers
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifiersToCheck
import moe.pxe.macecompanion.stateManagers.StarFragmentManager.starFragments
import moe.pxe.macecompanion.util.TitleCallback
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.network.chat.Style
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.world.InteractionResult
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object RoundManager {
    var gameOngoing = false
    var round = -1
    var roundColor: Style? = Style.EMPTY.withColor(0x9ef6fc)
    var playtime: TimeMark? = null
    var maceChance = -1f

    val chatRoundNumberRegex = Regex(""" +Round (\d+) +""")
    val titleRoundNumberRegex = Regex("""ʀᴏᴜɴᴅ (\d+)""")
    val chatLeaderboardHeaderRegex = Regex("""\s+‌‌ɢᴀᴍᴇ ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅ:""")

    fun resetRoundData(){
        gameOngoing = false
        round = -1
        roundColor = Style.EMPTY.withColor(0x9ef6fc)
        playtime = null
        maceChance = -1f
    }

    fun updateMaceChance() {
        maceChance = when {
            Modifiers.VICTIM in modifiers ->  (100f * (playersAlive - 1)) / playersAlive
            Modifiers.DOUBLE in modifiers ->  200f / playersAlive
            Modifiers.TRIPLE in modifiers ->  300f / playersAlive
            Modifiers.QUADRUPLE in modifiers ->  400f / playersAlive
            else -> 100f / playersAlive
        }
    }

    fun setRoundNumber(number: Int) {
        if (round != 1 && number == 1) {
            checkIfEliminated()
            playersTotal = playersAlive
            playtime = TimeSource.Monotonic.markNow()
            resetAccuracyData()
            eliminations = if (eliminated) -1 else 0
            starFragments = if (eliminated) -1 else 0
            AutoGL.sendGlMessage()
            BountyManager.getBountyData()
        }
        round = number
        gameOngoing = true
        modifiers.clear()
        modifierBoosters.clear()
        eternalModifier = null
        modifiersToCheck = -1
        maceChance = 100f / playersAlive
    }

    fun registerRoundListeners() {
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!PlotManager.onMaceRoulette) return@register true

            chatRoundNumberRegex.matchEntire(text)?.groups[1]?.let {
                setRoundNumber(it.value.toInt())
            }
            chatLeaderboardHeaderRegex.matchEntire(message.string)?.let {
                gameOngoing = false
                AutoGG.sendGGMessage()
            }

            return@register true
        }
        TitleCallback.EVENT.register(
            object : TitleCallback {
                override fun setTitleText(packet: ClientboundSetTitleTextPacket): InteractionResult {
                    if (!PlotManager.onMaceRoulette) return InteractionResult.PASS
                    titleRoundNumberRegex.matchEntire(packet.text.string)?.let { roundNumberMatch ->
                        roundNumberMatch.groups[1]?.let { setRoundNumber(it.value.toIntOrNull() ?: -1) }
                        roundColor = packet.text.siblings[0].style
                    }
                    return InteractionResult.PASS
                }
            }
        )
    }
}