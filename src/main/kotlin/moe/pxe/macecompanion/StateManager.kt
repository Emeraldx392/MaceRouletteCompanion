package moe.pxe.macecompanion

import com.mojang.authlib.GameProfile
import com.mojang.serialization.JsonOps
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.util.SubtitleCallback
import moe.pxe.macecompanion.util.TitleCallback
import moe.pxe.macecompanion.util.OnMaceRoulette
import moe.pxe.macecompanion.MaceCompanion
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket
import net.minecraft.network.packet.s2c.play.TitleS2CPacket
import net.minecraft.text.HoverEvent
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.ActionResult
import javax.crypto.Mac
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
    var chargedModifiers = mutableSetOf<Modifiers>()
        private set
    var mysteryModifiers = mutableSetOf<Modifiers>()
        private set
    var modifierBoosters = mutableMapOf<Modifiers, MutableList<GameProfile>>()
        private set
    var eternalModifier: Modifiers? = null
        private set

    private val chatJoinRegex = """\+ (.+)""".toRegex()

    private val chatRoundNumberRegex = """ +Round (\d+) +""".toRegex()

    private val chatModifierHeaderRegex = """⏵ .*ᴍᴏᴅɪꜰɪᴇʀ:""".toRegex()
    private val chatModifierItemRegex = """\s+◇ (.+)""".toRegex()
    private val chatModifierBoostedRegex = """\s+◇ (.+) \(☁ Boosted by (.+)\)""".toRegex()
    private val chatModifierReallyBoostedRegex = """\s+◇ (.+) \(☁ Boosted by (.+), (.+), and (\d+) others\)""".toRegex()

    private val eternalModifierTexture = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFjNWQ3NjZjODQwMWM5NTY2Y2E1MDhhYTNkMjU0NDQwYjg4YjIxZjU5MGI1MWVjMTVjNGE5ZDk4YjE4OWMzZiJ9fX0="
    private val chargedModifierTexture = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc1Mzg2MDAwNWQzNGRkNTMwMmRhNWVmOTA1Y2Q3ODFhYzcxNDFkMjJhYmMxZGIzOWMzMWJhMmZlM2M2ODRiZCJ9fX0="
    private val mysteryModifierTexture = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzlkODliMGJmNmY2NjU1YWJjMGFlY2NjY2Q2YTE4OGQwZWNjMzY2YTRiNWU2ZDFmZTJhM2ExY2U1MWYzMGU4YSJ9fX0="

    private val chatEliminationRegex = """⏵ .+ was eliminated by .+! \((\d+) remain\)""".toRegex()
    private val chatEarlyLeaveRegex = """⏵ .+ left while alive! \((\d+) remain\)""".toRegex()
    private val chatBlowUpRegex = """⏵ .+ blew up! \((\d+) remain\)""".toRegex()
    private val chatVoidDeathRegex = """⏵ .+ fell into the void! \((\d+) remain\)""".toRegex()
    private val chatVoidEliminationRegex = """⏵ .+ was thrown into the void by .+! \((\d+) remain\)""".toRegex()
    private val chatSpikeDeathRegex = """⏵ .+ fell on a spike! \((\d+) remain\)""".toRegex()
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

    private fun messageToJsonString(message: Text): String {
        return TextCodecs.CODEC
            .encodeStart(MinecraftClient.getInstance().world!!.registryManager.getOps(JsonOps.INSTANCE), message)
            .getOrThrow()
            .toString()
    }

    private fun messageContainsTexture(message: Text, texture: String): Boolean {
        val json = messageToJsonString(message)
        return json.contains(texture)
    }

    private fun extractModifierNameFromMessage(message: Text): String? {
        Modifiers.entries.forEach { modifier ->
            if (message.string.contains(modifier.matchName) && !message.string.contains("Modifier Charger") && !message.string.contains("???")) {
                var revealMysteryModifier = Config.showMysteryModifiers.value
                if (messageContainsTexture(message, mysteryModifierTexture)) {
                    if(!revealMysteryModifier){
                        return "???"
                    }
                    if(revealMysteryModifier){
                        mysteryModifiers.add(modifier)
                        return modifier.matchName
                    }
                }
                else return modifier.matchName
            }
        }
        return null
    }

    private fun resolveModifierFromRawName(rawName: String?): Modifiers? {
        val candidate = rawName?.trim().orEmpty()
        if (candidate.isEmpty()) return null
        return Modifiers.entries.find { enum ->
            candidate == enum.matchName || candidate.contains(enum.matchName) || enum.matchName.contains(candidate)
        }
    }
    private fun getHover(text: Text): String? {
        val hover = text.style.hoverEvent
        if (hover != null && hover.action == HoverEvent.Action.SHOW_TEXT) {
            val showText = hover as? HoverEvent.ShowText
            val content: Text? = showText?.value
            val readableText = content?.string
            if (readableText != null) {
                return readableText
            }
        }
        for (sibling in text.siblings) {
            val found = getHover(sibling)
            if (found != "null") return found
        }

        return "null"
    }


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
        eternalModifier = null
        chargedModifiers = mutableSetOf()
        mysteryModifiers = mutableSetOf()
        maceChance = 100f/playersAlive
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
            val eliminationMatch = chatEliminationRegex.matchEntire(message.string) ?: chatEarlyLeaveRegex.matchEntire(message.string) ?: chatBlowUpRegex.matchEntire(message.string) ?: chatVoidDeathRegex.matchEntire(message.string) ?: chatVoidEliminationRegex.matchEntire(message.string) ?: chatSpikeDeathRegex.matchEntire(message.string)
            eliminationMatch?.groups[1]?.let { playersAlive = it.value.toIntOrNull() ?: -1 }
            // Elimination Counter
            chatElimCounterRegex.matchEntire(message.string)?.groups[1]?.let { eliminations = it.value.toIntOrNull() ?: 0 }
            // Game Leaderboard Header
            chatLeaderboardHeaderRegex.matchEntire(message.string)?.let {
                gameOngoing = false
                AutoGG.sendGGMessage()
            }
            chatJoinRegex.matchEntire(message.string)?.groups[1]?.let {
                if(Config.showPlayerJoinToasts.value) CustomToasts.sendPlayerJoinedToast(it.value)
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
                val modReallyBoostedMatch = chatModifierReallyBoostedRegex.matchEntire(message.string)
                val modBoostedMatch = chatModifierBoostedRegex.matchEntire(message.string)
                val modMatch = chatModifierItemRegex.matchEntire(message.string)
                var modifier = Modifiers.UNKNOWN

                val capturedRawName = (modReallyBoostedMatch ?: modBoostedMatch ?: modMatch)?.groupValues?.getOrNull(1)
                val fallbackRawName = extractModifierNameFromMessage(message)
                if(fallbackRawName == "???") modifier = resolveModifierFromRawName(fallbackRawName) ?: Modifiers.UNKNOWN
                if(fallbackRawName != "???") modifier = resolveModifierFromRawName(capturedRawName) ?: resolveModifierFromRawName(fallbackRawName) ?: Modifiers.UNKNOWN
                if (modifier != Modifiers.UNKNOWN) {
                    if (messageContainsTexture(message, eternalModifierTexture)) {
                        eternalModifier = modifier
                        // eternal modifier appears first!
                        modifiers.add(0, modifier)
                    } else {
                        modifiers.add(modifier)
                    }
                    if (messageContainsTexture(message, chargedModifierTexture)) {
                        chargedModifiers.add(modifier)
                    }
                    modifierBoosters[modifier] = mutableListOf()
                    when (modifier) {
                        Modifiers.VICTIM -> maceChance = (100f * (playersAlive - 1)) / playersAlive
                        Modifiers.DOUBLE -> maceChance = 200f / playersAlive
                        Modifiers.TRIPLE -> maceChance = 300f / playersAlive
                        Modifiers.QUADRUPLE -> maceChance = 400f / playersAlive
                        else -> {}
                    }
                } else {
                    checkForModifiers = false
                }
                if (modifier != Modifiers.UNKNOWN && modReallyBoostedMatch != null){
                    val hoverString = getHover(message).toString().replace("§r", "")
                    val playerNames = hoverString.split(", ")
                    val networkHandler = MinecraftClient.getInstance().networkHandler
                    for (player in playerNames) {
                        val profile = networkHandler?.getPlayerListEntry(player)?.profile ?: continue
                        modifierBoosters[modifier]?.add(profile)

                    }
                }else if (modifier != Modifiers.UNKNOWN && modBoostedMatch != null) modBoostedMatch?.let {
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
