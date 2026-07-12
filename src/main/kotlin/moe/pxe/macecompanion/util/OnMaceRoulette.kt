package moe.pxe.macecompanion.util

import moe.pxe.macecompanion.StateManager
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.client.MinecraftClient

object OnMaceRoulette {
    val client: MinecraftClient = MinecraftClient.getInstance()

    val patchPlotRegex = """⏵ Current Patch""".toRegex()
    val plotRegex = """\s+\nYou are currently playing on:\n\n→ .+ \[(\d+)] \[(.+)]""".toRegex()
    var hidePlotRegex = false

    var onDiamondfire = false
    var onMaceRoulette = false
    var isStatless = false

    val plotIds = mutableSetOf<Int>()
    val plotHandles = mutableSetOf<String>()

    fun isOnDiamondfire(): Boolean{
        val client = MinecraftClient.getInstance()
        val serverEntry = client.currentServerEntry ?: return false
        val address = serverEntry.address.lowercase()
        return address.endsWith("diamondfire.games") ||
                address == "mcdiamondfire.com"
                || address.contains("148.113.223.138")
    }

    fun fillPlotIds(ids: Set<String>) {
        plotIds.clear()
        plotHandles.clear()
        ids.forEach {
            it.toIntOrNull()?.let { i -> plotIds.add(i) } ?: plotHandles.add(it)
        }
    }
    fun requestPlotId() {
        if(onDiamondfire) {
            hidePlotRegex = true
            SendMessage.sendCommand("find ${client.session.username}")
        }
    }
    fun registerServerAndPlotListeners(){
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            client.execute {
                onDiamondfire = isOnDiamondfire()
                StateManager.resetState()
            }
        }
        ClientPlayConnectionEvents.DISCONNECT.register { handler, client ->
            onDiamondfire = false
            onMaceRoulette = false
            isStatless = false
        }
        ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
            if (overlay) return@register true

            patchPlotRegex.find(message.string)?.groups?.let {
                requestPlotId()
            }

            plotRegex.find(message.string)?.groups?.let {
                onMaceRoulette = plotHandles.contains(it[2]?.value) || plotIds.contains(it[1]?.value!!.toInt())
                isStatless = (it[1]?.value!!.toInt() == 25000002 && it[2]?.value == "statless")
                if(hidePlotRegex) {
                    hidePlotRegex = false
                    return@register false
                }
            }

            return@register true
        }
    }
}