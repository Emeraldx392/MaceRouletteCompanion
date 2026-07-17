package moe.pxe.macecompanion

import com.mojang.authlib.GameProfile
import com.mojang.serialization.JsonOps
import dev.isxander.yacl3.config.v3.value
import kotlinx.serialization.json.*
import moe.pxe.macecompanion.AutoBet.sendAutoBet
import moe.pxe.macecompanion.CustomToasts.sendChaosStarterToast
import moe.pxe.macecompanion.CustomToasts.sendEternalElectorToast
import moe.pxe.macecompanion.CustomToasts.sendModifierChargerToast
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.util.OnMaceRoulette
import moe.pxe.macecompanion.util.SendMessage
import moe.pxe.macecompanion.util.SubtitleCallback
import moe.pxe.macecompanion.util.TitleCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.Style
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import kotlin.Int
import kotlin.collections.mutableListOf
import kotlin.math.roundToInt
import kotlin.time.TimeMark
import kotlin.time.TimeSource


object StateManager {

    var gameOngoing = false
        private set
    var round = -1
        private set
    var roundColor: Style? = Style.EMPTY.withColor(0x9ef6fc)
        private set
    var playersAlive = -1
        private set
    var playersTotal = -1
        private set
    var eliminations = -1
        private set
    var starFragmentMultiplier = -1f
        private set
    var starFragments = -1
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
    var maceAttempts = mutableMapOf<Int, Boolean>()
        private set
    var bounties = mutableMapOf<GameProfile, Int>()
        private set
    var eternalModifier: Modifiers? = null
        private set
    var eternalElectorPlayer: String? = null
        private set
    var eternalElectorModifier: String? = null
        private set
    var hideFindPlayerText: Boolean = false
        private set
    var hasMace: Boolean = false
        private set
    var fps: Int = -1
        private set
    var tps: Float = -1f
        private set
    var lastRoundWithMace: Int = -1
        private set
    var redPlayer: GameProfile? = null
    var bluePlayer: GameProfile? = null
    var redVotesPercentage: Int = -1
    var blueVotesPercentage: Int = -1

    val client: Minecraft = Minecraft.getInstance()

    private val chatJoinRegex = """\+ (.+)""".toRegex()
    private val chatJoinDFnNormalRegex = """(.+) joined.""".toRegex()
    private val chatJoinDFnSpecialRegex = """\[.+](.+) joined!""".toRegex()
    private val chatLeaveRegex = """(.+) left\.""".toRegex()

    private val showdownVotingRegex = """\s+(.+)\s+vs\.\s+(.+)""".toRegex()
    private val showdownBarRegex = """.+ - (\d+)%""".toRegex()
    private val showdownOverRegex ="""☆ Showdown Over ☆""".toRegex()

    private val chatRoundNumberRegex = """ +Round (\d+) +""".toRegex()

    private val chatModifierHeaderRegex = """⏵ .*ᴍᴏᴅɪꜰɪᴇʀ:""".toRegex()
    private val chatModifierItemRegex = """\s+◇ (.+)""".toRegex()
    private val chatModifierBoostedRegex = """\s+◇ (.+) \(☁ Boosted by (.+)\)""".toRegex()
    private val chatModifierReallyBoostedRegex = """\s+◇ (.+) \(☁ Boosted by (.+), (.+), and (\d+) others\)""".toRegex()

    private val eternalModifierTexture = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjFjNWQ3NjZjODQwMWM5NTY2Y2E1MDhhYTNkMjU0NDQwYjg4YjIxZjU5MGI1MWVjMTVjNGE5ZDk4YjE4OWMzZiJ9fX0="
    private val chargedModifierTexture = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDc1Mzg2MDAwNWQzNGRkNTMwMmRhNWVmOTA1Y2Q3ODFhYzcxNDFkMjJhYmMxZGIzOWMzMWJhMmZlM2M2ODRiZCJ9fX0="
    private val mysteryModifierTexture = "eyJ0ZXh0dXJlcyI6IHsiU0tJTiI6IHsidXJsIjogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzlkODliMGJmNmY2NjU1YWJjMGFlY2NjY2Q2YTE4OGQwZWNjMzY2YTRiNWU2ZDFmZTJhM2ExY2U1MWYzMGU4YSJ9fX0="

    private val chatEliminationRegex = """⏵ (.+) was eliminated by .+! \((\d+) remain\)""".toRegex()
    private val chatEarlyLeaveRegex = """⏵ (.+) left while alive! \((\d+) remain\)""".toRegex()
    private val chatBlowUpRegex = """⏵ (.+) blew up! \((\d+) remain\)""".toRegex()
    private val chatVoidDeathRegex = """⏵ (.+) fell into the void! \((\d+) remain\)""".toRegex()
    private val chatVoidEliminationRegex = """⏵ (.+) was thrown into the void by .+! \((\d+) remain\)""".toRegex()
    private val chatSpikeDeathRegex = """⏵ (.+) fell on a spike! \((\d+) remain\)""".toRegex()
    private val chatElimCounterRegex = """\s+◇ \+\d+🪓, total (\d+)🪓""".toRegex()

    private val findPlayerCommandRegex = """→ In Lobby - .+/(\d+) Remain""".toRegex()

    private val totalStarFragmentGainRegex = """ᴛᴏᴛᴀʟ ɢᴀɪɴ: \+(\d+)""".toRegex()

    private val chatLeaderboardHeaderRegex = """\s+‌‌ɢᴀᴍᴇ ʟᴇᴀᴅᴇʀʙᴏᴀʀᴅ:""".toRegex()

    private val titleRoundNumberRegex = """ʀᴏᴜɴᴅ (\d+)""".toRegex()
    private val titlePlayersAliveRegex = """(\d+) ᴀʟɪᴠᴇ""".toRegex()
    private val titleEliminatedRegex = """☠☠☠""".toRegex()

    private val newEventRegex = """⏵ New Event Started! \(by (.+)\)""".toRegex()
    private val newEventTypeRegex = """\s+⏵ Type: (.+)""".toRegex()
    private val newEventDurationRegex = """\s+⏵ Length: (\d+)h""".toRegex()

    private val modifierChargerRegex = """⏵ (.+) used a Modifier Charger on (.+)!\n\s+◇ It will be charged for its next (\d+) appearances!""".toRegex()
    private val chaosStarterRegex = """⏵ (.+) used a Chaos Starter!\n\s+◇ The next round will have five modifiers!""".toRegex()
    private val eternalElectorRegex = """⏵ (.+) used a Eternal Elector for (.+)!""".toRegex()
    private val eternalElectorPositionRegex = """\s+◇ It has been queued at position #(\d+)!""".toRegex()

    private val placedBountyRegex = """⏵ (.+) placed a (\d+)⛂ bounty on (.+)!""".toRegex()
    private val selfPlacedBountyRegex = """⏵ (.+) placed a (\d+)⛂ bounty on themself!""".toRegex()
    private val raisedBountyRegex = """⏵ (.+) raised the bounty amount to (\d+)⛂ on (.+)!""".toRegex()
    private val selfRaisedBountyRegex = """⏵ (.+) raised the bounty amount on themself to (\d+)⛂!""".toRegex()
    private val rewardedBountyRegex = """⏵ (.+) was rewarded (\d+)⛂ for eliminating (.+)!""".toRegex()
    private val cashedInBountyRegex = """⏵ (.+) cashed in their bounty of (\d+)⛂!""".toRegex()
    private val playerListBountyRegex = """.+\s+◇\s+(\d+)⛂""".toRegex()

    private fun getBountyData() {
        bounties = mutableMapOf()
        val tabHUD = client.gui.hud.tabList
        client.connection?.onlinePlayers?.forEach { player ->
            val playerDisplayName = tabHUD.getNameForDisplay(player).string
            playerListBountyRegex.matchEntire(playerDisplayName)?.groups[1]?.let { match ->
                match.value.toIntOrNull()?.let { bountyValue ->
                    if (bountyValue > 0) {
                        bounties[player.profile] = bountyValue
                    }
                }
            }
        }
        val personalProfile = getPlayerProfile(client.user.name)
        val personalEntry = client.connection
            ?.getPlayerInfo(client.player!!.uuid)
            ?: return
        val personalPlayerDisplayName = tabHUD.getNameForDisplay(   personalEntry).string
        playerListBountyRegex.matchEntire(personalPlayerDisplayName)?.groups[1]?.let { match ->
            match.value.toIntOrNull()?.let { bountyValue ->
                if (bountyValue > 0) personalProfile?.let { profile -> bounties[profile] = bountyValue }
            }
        }
    }

    fun getPlayerProfile(player: String): GameProfile? {
        if (player == client.user.name) return client.gameProfile
        return client.connection
            ?.getPlayerInfo(player)
            ?.profile
    }

    private fun getPlayerSlotItemStack(slot: Int): ItemStack {
        val playerInventory = client.player?.inventory
        val itemStack: ItemStack = playerInventory?.getSlot(slot)?.get() ?: ItemStack.EMPTY
        return itemStack
    }

    private fun messageToJsonString(message: Component): String {
        return ComponentSerialization.CODEC
            .encodeStart(client.level!!.registryAccess().createSerializationContext(JsonOps.INSTANCE), message)
            .getOrThrow()
            .toString()
    }

    private fun messageToJson(message: Component): JsonObject {
        val jsonString = messageToJsonString(message)
        return Json.parseToJsonElement(jsonString).jsonObject
    }

    private fun messageContainsTexture(message: Component, texture: String): Boolean {
        val json = messageToJsonString(message)
        return json.contains(texture)
    }

    private fun extractModifierNameFromMessage(message: Component, isConsumable: Boolean): String? {
        Modifiers.entries.forEach { modifier ->
            if (message.string.contains(modifier.matchName) && (chatModifierItemRegex.matchEntire(message.string) != null || modifierChargerRegex.matchEntire(message.string) != null) || chaosStarterRegex.matchEntire(message.string) != null || eternalElectorRegex.matchEntire(message.string) != null || eternalElectorPositionRegex.matchEntire(message.string) != null) {
                if(!isConsumable && (message.string.contains("Modifier Charger") || message.string.contains("Eternal Elector"))) return null
                val revealMysteryModifier = Config.showMysteryModifiers.value
                if (messageContainsTexture(message, mysteryModifierTexture)) {
                    if(!revealMysteryModifier) return "???"
                    else {
                        mysteryModifiers.add(modifier)
                        return modifier.matchName
                    }
                }
                else return modifier.matchName
            }
        }
        return null
    }
    private fun getShowdownVotes(playerString: String): Int {
        val bossOverlay = client.gui.hud.bossOverlay
        val bossBars = bossOverlay.events
        val bossBarLerpingEvents = bossBars.values
        if (bossBarLerpingEvents.isEmpty()) return -1
        var value: Int = -1
        bossBarLerpingEvents.forEach { bar ->
            val name = bar.name.string
            val profile = resolvePlayerFromRawName(name)?.name
            if(profile == playerString) {
                val match = showdownBarRegex.find(name)?.groups?.let {
                    value = it[1]?.value?.toInt() ?: -1
                }
            }
            val match = showdownBarRegex.find(name)
            match?.groups?.get(1)?.value?.toIntOrNull()
        }
        return value
    }
    private fun resolvePlayerFromRawName(rawName: String?): GameProfile? {
        val candidate = rawName?.trim().orEmpty()
        if (candidate.isEmpty()) return null
        if(rawName?.contains(client.user.name) ?: false) return client.gameProfile
        client.connection?.onlinePlayers?.forEach { player ->
            if(rawName?.contains(player.profile.name) ?: false) return player.profile
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
    private fun getHover(text: Component): String? {
        val hover = text.style.hoverEvent
        if (hover != null && hover.action() == HoverEvent.Action.SHOW_TEXT) {
            val showText = hover as? HoverEvent.ShowText
            val content: Component? = showText?.value
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
            lastRoundWithMace = -1
            maceAttempts = mutableMapOf()
            eliminations = 0
            starFragmentMultiplier = 1f
            starFragments = 0
            eliminated = false
            AutoGL.sendGlMessage()
            getBountyData()
        }
        val lastSlotItem = getPlayerSlotItemStack(8).item
        eliminated = (lastSlotItem == Items.STICK || lastSlotItem == Items.BREEZE_ROD)
        if(eliminated && number == 1){
            eliminations = -1
            maceAttempts = mutableMapOf()
            starFragments = -1
        }
        if(playersTotal == -1){
            hideFindPlayerText = true
            SendMessage.sendCommand("find ${client.user.name}")
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
        starFragmentMultiplier = -1f
        starFragments = -1
        maceChance = -1f
        eliminated = true
        playtime = null
        modifiers = mutableListOf()
        modifierBoosters = mutableMapOf()
        chargedModifiers = mutableSetOf()
        mysteryModifiers = mutableSetOf()
        bounties = mutableMapOf()
        eternalModifier = null
        eternalElectorModifier = null
        eternalElectorPlayer = null
        newEvent = false
        newEventStarter = ""
        newEventType = ""
        newEventDuration = -1
        hideFindPlayerText = false
        checkForModifiers = false
        redPlayer = null
        bluePlayer = null
        redVotesPercentage = -1
        blueVotesPercentage = -1
        lastRoundWithMace = -1
        maceAttempts = mutableMapOf()
    }
    fun registerListeners() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: Minecraft ->
                redPlayer?.let { redVotesPercentage = getShowdownVotes(it.name) }
                bluePlayer?.let { blueVotesPercentage = getShowdownVotes(it.name) }
                fps = client.fps
                tps = client.level?.tickRateManager()?.tickrate() ?: -1f
                hasMace = (!eliminated && (client.player?.inventory?.hasAnyOf(setOf(Items.MACE))?: false))
                if(hasMace) {
                    lastRoundWithMace = round
                    if(!(maceAttempts[round] ?: false)) maceAttempts[round] = false
                }
        })
        // Chat Listener
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            if (overlay) return@register true
            if (!OnMaceRoulette.onMaceRoulette) return@register true
            // Round Number Header
            chatRoundNumberRegex.matchEntire(message.string)?.groups[1]?.let { setRoundNumber(it.value.toIntOrNull() ?: -1) }
            // Elimination Messages (slain by, left the game, blew up, fell off the map)
            val eliminationMatch = chatEliminationRegex.matchEntire(message.string) ?: chatEarlyLeaveRegex.matchEntire(message.string) ?: chatBlowUpRegex.matchEntire(message.string) ?: chatVoidDeathRegex.matchEntire(message.string) ?: chatVoidEliminationRegex.matchEntire(message.string) ?: chatSpikeDeathRegex.matchEntire(message.string)
            eliminationMatch?.groups?.let {
                playersAlive = it[2]?.value?.toIntOrNull() ?: -1
                if(!eliminated) {
                    if (playersAlive == 1) starFragmentMultiplier = 3.125f
                    else if (playersAlive <= (playersTotal / 4)) starFragmentMultiplier = 1.5625f
                    else if (playersAlive <= (playersTotal / 2)) starFragmentMultiplier = 1.25f
                    starFragments = (((eliminations * 3) + (playersTotal - playersAlive)) * starFragmentMultiplier).roundToInt()
                }
            }
            // Elimination Counter
            chatElimCounterRegex.matchEntire(message.string)?.groups[1]?.let {
                if(!eliminated){
                    if(lastRoundWithMace == round){ maceAttempts[round] = true }
                    eliminations = it.value.toIntOrNull() ?: 0
                    starFragments = (((eliminations * 3) + (playersTotal - playersAlive)) * starFragmentMultiplier).roundToInt()
                }
            }
            chatEarlyLeaveRegex.matchEntire(message.string)?.groups[1]?.let {
                val playerThatLeft = getPlayerProfile(it.value)
                if(bounties.contains(playerThatLeft))bounties.remove(playerThatLeft)
            }
            // Game Leaderboard Header
            chatLeaderboardHeaderRegex.matchEntire(message.string)?.let {
                gameOngoing = false
                AutoGG.sendGGMessage()
            }
            chatJoinRegex.matchEntire(message.string)?.groups?.let {
                if(Config.showPlayerToasts.value) CustomToasts.sendPlayerJoinedToast(it[1]?.value.toString())
                if(Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            chatJoinDFnNormalRegex.matchEntire(message.string)?.let {
                if(Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            chatJoinDFnSpecialRegex.matchEntire(message.string)?.groups?.let {
                if(Config.showPlayerToasts.value) CustomToasts.sendPlayerJoinedToast(it[1]?.value.toString())
                if(Config.hidePlayerJoinedLeftMessages.value) return@register false
            }
            chatLeaveRegex.matchEntire(message.string)?.groups?.let {
                if(Config.showPlayerToasts.value) CustomToasts.sendPlayerLeftToast(it[1]?.value.toString())
                if(Config.hidePlayerJoinedLeftMessages.value) return@register false
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

            modifierChargerRegex.matchEntire(message.string)?.groups?.let {
                val player = it[1]?.value.toString()
                val modifier = extractModifierNameFromMessage(message, true).toString()
                val queueLength = it[3]?.value!!.toInt()
                sendModifierChargerToast(modifier, queueLength, player)
            }
            chaosStarterRegex.matchEntire(message.string)?.groups?.let {
                val player = it[1]?.value.toString()
                sendChaosStarterToast(player)
            }
            eternalElectorRegex.matchEntire(message.string)?.groups?.let {
                eternalElectorPlayer = it[1]?.value.toString()
                eternalElectorModifier = extractModifierNameFromMessage(message,true).toString()
            }
            eternalElectorPositionRegex.matchEntire(message.string)?.groups?.let { it ->
                val queuePosition = it[1]?.value!!.toInt()
                if(eternalElectorPlayer != null && eternalElectorModifier != null) sendEternalElectorToast(eternalElectorModifier!!, eternalElectorPlayer!!, queuePosition)
                eternalElectorPlayer = null
                eternalElectorModifier = null
            }

            placedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]!!.value
                val bountyAmount = it[2]!!.value.toInt()
                val bountyReceiver = it[3]!!.value
                getPlayerProfile(bountyReceiver)?.let { profile ->
                    bounties[profile] = bountyAmount
                }
                if(bountyReceiver == client.user.name) CustomToasts.sendPlacedBountyToast(bountyAmount, bountyPlacer)
            }
            selfPlacedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]!!.value
                val bountyAmount = it[2]!!.value.toInt()
                getPlayerProfile(bountyPlacer)?.let { profile ->
                    bounties[profile] = bountyAmount
                }
                if(bountyPlacer == client.user.name) CustomToasts.sendSelfPlacedBountyToast(bountyAmount)
            }
            raisedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]!!.value
                val bountyAmount = it[2]!!.value.toInt()
                val bountyReceiver = it[3]!!.value
                getPlayerProfile(bountyReceiver)?.let { profile ->
                    bounties[profile] = bountyAmount
                }
                if(bountyReceiver == client.user.name) CustomToasts.sendRaisedBountyToast(bountyAmount, bountyPlacer)
            }
            selfRaisedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyPlacer = it[1]!!.value
                val bountyAmount = it[2]!!.value.toInt()
                getPlayerProfile(bountyPlacer)?.let { profile ->
                    bounties[profile] = bountyAmount
                }
                if(bountyPlacer == client.user.name) CustomToasts.sendSelfRaisedBountyToast(bountyAmount)
            }
            rewardedBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyReceiver = it[1]!!.value
                val bountyAmount = it[2]!!.value.toInt()
                val playerWithBounty = it[3]!!.value
                val playerWithBountyProfile = getPlayerProfile(playerWithBounty)
                bounties.remove(playerWithBountyProfile)
                if(bountyReceiver == client.user.name) CustomToasts.sendRewardedBountyToast(bountyAmount, playerWithBounty)
            }
            cashedInBountyRegex.matchEntire(message.string)?.groups?.let {
                val bountyReceiver = it[1]!!.value
                val bountyAmount = it[2]!!.value.toInt()
                val receiverProfile = getPlayerProfile(bountyReceiver)
                bounties.remove(receiverProfile)
                if(bountyReceiver == client.user.name) CustomToasts.sendCashedInBountyToast(bountyAmount)
            }
            findPlayerCommandRegex.find(message.string)?.groups?.let {
                val totalPlayersFound = it[1]!!.value.toInt()
                playersTotal = totalPlayersFound
                if(hideFindPlayerText) return@register false
                hideFindPlayerText = false
            }

            totalStarFragmentGainRegex.find(message.string)?.groups?.let {
                eliminated = true
                starFragments = it[1]?.value!!.toInt()
            }

            showdownVotingRegex.find(message.string)?.groups?.let {
                redPlayer = resolvePlayerFromRawName(it[1]?.value)
                bluePlayer = resolvePlayerFromRawName(it[2]?.value)
                sendAutoBet()
            }
            showdownOverRegex.matchEntire(message.string)?.groups?.let {
                redPlayer = null
                bluePlayer = null
                redVotesPercentage = -1
                blueVotesPercentage = -1
            }

            // Modifier Entry
            if (checkForModifiers) {
                val modReallyBoostedMatch = chatModifierReallyBoostedRegex.matchEntire(message.string)
                val modBoostedMatch = chatModifierBoostedRegex.matchEntire(message.string)
                val modMatch = chatModifierItemRegex.matchEntire(message.string)
                var modifier = Modifiers.UNKNOWN

                val capturedRawName = (modReallyBoostedMatch ?: modBoostedMatch ?: modMatch)?.groupValues?.getOrNull(1)
                val fallbackRawName = extractModifierNameFromMessage(message, false)
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
                    for (player in playerNames) {
                        getPlayerProfile(player)?.let { profile ->
                            modifierBoosters[modifier]?.add(profile)
                        }
                    }
                }else if (modifier != Modifiers.UNKNOWN && modBoostedMatch != null) modBoostedMatch.let {
                    it.groupValues[2].let { playerList ->
                        val playerNames = playerList.split(", ")
                        for (player in playerNames) {
                            getPlayerProfile(player)?.let { profile ->
                                modifierBoosters[modifier]?.add(profile)
                            }
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
                override fun setTitleText(packet: ClientboundSetTitleTextPacket): InteractionResult {
                    if (!OnMaceRoulette.onMaceRoulette) return InteractionResult.PASS
                    titleRoundNumberRegex.matchEntire(packet.text.string)?.let { roundNumberMatch ->
                        roundNumberMatch.groups[1]?.let { setRoundNumber(it.value.toIntOrNull() ?: -1) }
                        roundColor = packet.text.siblings[0].style
                    }
                    titleEliminatedRegex.matchEntire(packet.text.string)?.let { eliminated = true }
                    return InteractionResult.PASS
                }
            }
        )

        // Subtitle Listener
        SubtitleCallback.EVENT.register(
            object : SubtitleCallback {
                override fun setSubtitleText(packet: ClientboundSetSubtitleTextPacket): InteractionResult {
                    if (!OnMaceRoulette.onMaceRoulette) return InteractionResult.PASS
                    titlePlayersAliveRegex.matchEntire(packet.text.string)?.let { playersAliveMatch -> playersAliveMatch.groups[1]?.let { playersAlive = it.value.toIntOrNull() ?: -1 } }
                    return InteractionResult.PASS
                }
            }
        )

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> resetState() }
    }
}
