package moe.pxe.macecompanion

import com.mojang.authlib.GameProfile
import moe.pxe.macecompanion.MaceCompanion.Companion.LOGGER
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.util.SubtitleCallback
import moe.pxe.macecompanion.util.TitleCallback
import moe.pxe.macecompanion.util.OnMaceRoulette
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.text.Style
import net.minecraft.util.ActionResult
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object StateManager {

    var gameOngoing = false
        private set
    var round = -1
        private set
    var roundColor = Style.EMPTY.withColor(0x9ef6fc)
        private set
    var playersAlive = -1
        private set
    var playersTotal = -1
        private set
    var eliminations = -1
        private set
    var maceChance = -1f
        private set
    var eliminated = true
        private set
    var newEvent = false
        private set
    var newEventStarter = ""
        private set
    var newEventType = ""
        private set
    var newEventDuration = -1
        private set
    var playtime: TimeMark? = null
        private set
    var modifiers = mutableListOf<Modifiers>()
        private set
    var modifierBoosters = mutableMapOf<Modifiers, MutableList<GameProfile>>()
        private set
    var eternalModifier: Modifiers? = null
        private set

    private val chatRoundNumberRegex = """ +Round (\d+) +""".toRegex()

    private val chatModifierHeaderRegex = """⏵ .*ᴍᴏᴅɪꜰɪᴇʀ:""".toRegex()
    private val chatModifierItemRegex = """  ◇ (.+)""".toRegex()
    private val chatModifierBoostedRegex = """  ◇ (.+) \(☁ Boosted by (.+)\)""".toRegex()
    private val chatModifierIsEternalRegex = """(.+) \(Eternal\)""".toRegex()

    private val chatEliminationRegex = """⏵ .+ was eliminated by .+! \((\d+) remain\)""".toRegex()
    private val chatEarlyLeaveRegex = """⏵ .+ left while alive! \((\d+) remain\)""".toRegex()
    private val chatBlowUpRegex = """⏵ .+ blew up! \((\d+) remain\)""".toRegex()
    private val chatVoidDeathRegex = """⏵ .+ fell into the void! \((\d+) remain\)""".toRegex()
    private val chatVoidEliminationRegex = """⏵ .+ was thrown into the void by .+! \((\d+) remain\)""".toRegex()
    private val chatElimCounterRegex = """  ◇ \+\d+🪓, total (\d+)🪓""".toRegex()

    private val chatLeaderboardHeaderRegex = """ +‌*ɢᴀᴍᴇ ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅ:""".toRegex()

    private val titleRoundNumberRegex = """ʀᴏᴜɴᴅ (\d+)""".toRegex()
    private val titlePlayersAliveRegex = """(\d+) ᴀʟɪᴠᴇ""".toRegex()
    private val titleEliminatedRegex = """☠☠☠""".toRegex()

    private val newEventRegex = """⏵ New Event Started! \(by (.+)\)""".toRegex()
    private val newEventTypeRegex = """  ⏵ Type: (.+)""".toRegex()
    private val newEventDurationRegex = """  ⏵ Length: (\d+)h""".toRegex()
    
    private val placedBountyRegex = """⏵ (.+) placed a (\d+)⛂ bounty on (.+)!""".toRegex()
    private val selfPlacedBountyRegex = """⏵ (.+) placed a (\d+)⛂ bounty on themself!""".toRegex()
    private val raisedBountyRegex = """⏵ (.+) raised the bounty amount to (\d+)⛂ on (.+)!""".toRegex()
    private val selfRaisedBountyRegex = """⏵ (.+) raised the bounty amount on themself to (\d+)⛂!""".toRegex()
    private val rewardedBountyRegex = """⏵ (.+) was rewarded (\d+)⛂ for eliminating (.+)!""".toRegex()
    private val cashedInBountyRegex = """⏵ (.+) cashed in their bounty of (\d+)⛂!""".toRegex()

    private var checkForModifiers = false

    private fun setRoundNumber(number: Int) {
        if (round != 1 && number == 1) {
            playersTotal = playersAlive
            playtime = TimeSource.Monotonic.markNow()
            eliminations = 0
            eliminated = false
            AutoGL.sendGlMessage()
        }
        round = number
        gameOngoing = true
        modifiers = mutableListOf()
        modifierBoosters = mutableMapOf()
        maceChance = 100f/playersAlive
//        MaceCompanion.LOGGER.info("Round: $round - Alive:$playersAlive/$playersTotal")
    }
    fun resetState() {
        gameOngoing = false
        round = -1
        roundColor = Style.EMPTY.withColor(0x9ef6fc)
        playersAlive = -1
        playersTotal = -1
        eliminations = -1
        maceChance = -1f
        eliminated = true
        playtime = null
        modifiers = mutableListOf()
        modifierBoosters = mutableMapOf()
    }
    fun registerListeners() {
        // Chat Listener
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            if (overlay) return@register true
            if (!OnMaceRoulette.onMace) return@register true

            // Round Number Header
            chatRoundNumberRegex.matchEntire(message.string)?.groups[1]?.let { setRoundNumber(it.value.toIntOrNull() ?: -1) }
            // Elimination Messages (slain by, left the game, blew up, fell off the map)
            val eliminationMatch = chatEliminationRegex.matchEntire(message.string) ?: chatEarlyLeaveRegex.matchEntire(message.string) ?: chatBlowUpRegex.matchEntire(message.string) ?: chatVoidDeathRegex.matchEntire(message.string) ?: chatVoidEliminationRegex.matchEntire(message.string)
            eliminationMatch?.groups[1]?.let { playersAlive = it.value.toIntOrNull() ?: -1 }
            // Elimination Counter
            chatElimCounterRegex.matchEntire(message.string)?.groups[1]?.let { eliminations = it.value.toIntOrNull() ?: 0 }
            // Game Leaderboard Header
            chatLeaderboardHeaderRegex.matchEntire(message.string)?.let {
                gameOngoing = false
                AutoGG.sendGGMessage()
            }
            newEventRegex.matchEntire(message.string)?.groups[1]?.let {
                newEvent = true
                newEventStarter = it.value
            }
            newEventTypeRegex.matchEntire(message.string)?.groups[1]?.let {
                newEventType = it.value
            }
            newEventDurationRegex.matchEntire(message.string)?.groups[1]?.let {
                newEventDuration = it.value.toInt()
                if(newEvent){
                    CustomToasts.sendNewEventToast(newEventType, newEventDuration, newEventStarter)
                    newEvent = false
                }
            }
            placedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]?.value.toString()
                val bountyAmount = it[2]?.value?.toInt()
                val bountyReceiver = it[3]?.value.toString()
                val username = MinecraftClient.getInstance().getSession().getUsername().toString()
                if(bountyReceiver == username) CustomToasts.sendPlacedBountyToast(bountyAmount, bountyPlacer)
            }
            selfPlacedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]?.value.toString()
                val bountyAmount = it[2]?.value?.toInt()
                val username = MinecraftClient.getInstance().getSession().getUsername().toString()
                if(bountyPlacer == username) CustomToasts.sendSelfPlacedBountyToast(bountyAmount)
            }
            raisedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]?.value.toString()
                val bountyAmount = it[2]?.value?.toInt()
                val bountyReceiver = it[3]?.value.toString()
                val username = MinecraftClient.getInstance().getSession().getUsername().toString()
                if(bountyReceiver == username) CustomToasts.sendRaisedBountyToast(bountyAmount, bountyPlacer)
            }
            selfRaisedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]?.value.toString()
                val bountyAmount = it[2]?.value?.toInt()
                val username = MinecraftClient.getInstance().getSession().getUsername().toString()
                if(bountyPlacer == username) CustomToasts.sendSelfRaisedBountyToast(bountyAmount)
            }
            rewardedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyReceiver = it[1]?.value.toString()
                val bountyAmount = it[2]?.value?.toInt()
                val playerWithBounty = it[3]?.value.toString()
                val username = MinecraftClient.getInstance().getSession().getUsername().toString()
                if(bountyReceiver == username) CustomToasts.sendRewardedBountyToast(bountyAmount, playerWithBounty)
            }
            cashedInBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyReceiver = it[1]?.value.toString()
                val bountyAmount = it[2]?.value?.toInt()
                val username = MinecraftClient.getInstance().getSession().getUsername().toString()
                if(bountyReceiver == username) CustomToasts.sendCashedInBountyToast(bountyAmount)
            }

            // Modifier Entry
            if (checkForModifiers) {
                val modBoostedMatch = chatModifierBoostedRegex.matchEntire(message.string)
                val modMatch = chatModifierItemRegex.matchEntire(message.string)
                var modifier = Modifiers.UNKNOWN

                (modBoostedMatch ?: modMatch)?.also {
                    it.groupValues[1].let {
                        var modName = it
                        var isEternal = false
                        chatModifierIsEternalRegex.matchEntire(it)?.let {
                            modName = it.groupValues[1]
                            isEternal = true
                        }
                        modifier = Modifiers.entries.find { enum -> enum.matchName == modName } ?: Modifiers.UNKNOWN
                        if (isEternal) {
                            eternalModifier = modifier
                            modifiers.add(0, modifier)
                        } else modifiers.add(modifier)
                        modifierBoosters[modifier] = mutableListOf()
                    }
                    when (modifier) {
                        Modifiers.VICTIM -> maceChance = (100f * (playersAlive - 1)) / playersAlive
                        Modifiers.DOUBLE -> maceChance = 200f / playersAlive
                        Modifiers.TRIPLE -> maceChance = 300f / playersAlive
                        else -> {}
                    }
                } ?: run {
                    checkForModifiers = false
                }

                if (modifier != Modifiers.UNKNOWN) modBoostedMatch?.let {
                    it.groupValues[2].let { playerList ->
                        val playerNames = playerList.split(", ")
                        val networkHandler = MinecraftClient.getInstance().networkHandler ?: return@let
                        for (player in playerNames) {
                            val profile = networkHandler.getPlayerListEntry(player)?.profile ?: continue
                            modifierBoosters[modifier]?.add(profile)
                        }
                    }
                }
            }

            // Modifier Header
            chatModifierHeaderRegex.matchEntire(message.string)?.let { checkForModifiers = true }

            return@register true
        }

        // Title Listener
        TitleCallback.EVENT.register(
            object : TitleCallback {
                override fun onTitle(packet: TitleS2CPacket): ActionResult {
                    if (!OnMaceRoulette.onMace) return ActionResult.PASS
                    titleRoundNumberRegex.matchEntire(packet.text.string)?.let { roundNumberMatch ->
                        roundNumberMatch.groups[1]?.let { setRoundNumber(it.value.toIntOrNull() ?: -1) }
                        roundColor = packet.text.siblings[0].style ?: roundColor
                    }
                    titleEliminatedRegex.matchEntire(packet.text.string)?.let { eliminated = true }
                    return ActionResult.PASS
                }
            }
        )

        // Subtitle Listener
        SubtitleCallback.EVENT.register(
            object : SubtitleCallback {
                override fun onSubtitle(packet: SubtitleS2CPacket): ActionResult {
                    if (!OnMaceRoulette.onMace) return ActionResult.PASS
                    titlePlayersAliveRegex.matchEntire(packet.text.string)?.let { playersAliveMatch -> playersAliveMatch.groups[1]?.let { playersAlive = it.value.toIntOrNull() ?: -1 } }
                    return ActionResult.PASS
                }
            }
        )

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> resetState() }
    }
}
