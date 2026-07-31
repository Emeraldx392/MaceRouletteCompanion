package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.tps
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.Minecraft
object PlotManager {

    val patchPlotRegex = Regex("""⏵ Current Patch""")
    val plotRegex = Regex("""You are currently playing on:\n\n→ .+ \[(\d+)] \[(.+)]""")
    val findPlayerCommandRegex = Regex("""→ In Lobby - .+/(\d+) Remain""")

    var hidePlotRegex = false

    var onDiamondfire = false
    var onMaceRoulette = false
    var isStatless = false
    var plotHandle: String? = null
    var plotId: Int? = null

    val plotIds = mutableSetOf<Int>()
    val plotHandles = mutableSetOf<String>()

    val client: Minecraft = Minecraft.getInstance()

    fun isOnDiamondfire(): Boolean {
        val serverEntry = client.currentServer ?: return false
        val address = serverEntry.ip.lowercase()
        return address.endsWith("diamondfire.games") || address == "mcdiamondfire.com" || address.contains("148.113.223.138")
    }

    fun fillPlotIds(ids: Set<String>) {
        plotIds.clear()
        plotHandles.clear()
        ids.forEach {
            it.toIntOrNull()?.let { i -> plotIds.add(i) } ?: plotHandles.add(it)
        }
    }
    fun requestPlotId() {
        if (onDiamondfire) {
            hidePlotRegex = true
            SendMessage.sendCommand("find ${client.user.name}")
        }
    }
    fun registerServerAndPlotListeners() {
        ClientPlayConnectionEvents.JOIN.register { _, _, _ ->
            onDiamondfire = isOnDiamondfire()
            tps = -1f
            BountyManager.resetBountyData()
            EliminationManager.resetEliminationData()
            EventManager.resetEventData()
            StarFragmentManager.resetStarFragmentData()
            ConsumableManager.resetConsumableData()
            ShowdownManager.resetShowdownData()
            AccuracyManager.resetAccuracyData()
            RoundManager.resetRoundData()
            ModifierManager.resetModifierData()
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            onDiamondfire = false
            onMaceRoulette = false
            isStatless = false
        }
        ClientSendMessageEvents.ALLOW_CHAT.register { message ->
            if (onDiamondfire && message.contains("/play") || message.startsWith("/join $plotHandle") || message.startsWith("/join $plotId")) {
                BountyManager.resetBountyData()
                EliminationManager.resetEliminationData()
                EventManager.resetEventData()
                StarFragmentManager.resetStarFragmentData()
                ConsumableManager.resetConsumableData()
                ShowdownManager.resetShowdownData()
                AccuracyManager.resetAccuracyData()
            }
            true
        }
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register true
            if (!text.contains('⏵') && !text.contains('→')) return@register true

            if (patchPlotRegex.containsMatchIn(text)) requestPlotId()

            plotRegex.find(text)?.groups?.let {
                plotId = it[1]?.value?.toIntOrNull()
                plotHandle = it[2]?.value

                onMaceRoulette = plotHandles.contains(plotHandle) || plotIds.contains(plotId)
                isStatless = (plotId == 25000002 && plotHandle == "statless")
                findPlayerCommandRegex.find(text)?.groups?.let {
                    val totalPlayersFound = it[1]?.value?.toIntOrNull() ?: -1
                    playersTotal = totalPlayersFound
                }
                if (hidePlotRegex) {
                    hidePlotRegex = false
                    return@register false
                }
            }

            return@register true
        }
    }
}