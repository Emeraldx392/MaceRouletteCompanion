package moe.pxe.macecompanion.util

import dev.dfonline.flint.Flint
import dev.dfonline.flint.FlintAPI
import dev.dfonline.flint.feature.trait.ModeSwitchListeningFeature
import dev.dfonline.flint.hypercube.Mode
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.StateManager
import moe.pxe.macecompanion.config.Config
import net.fabricmc.loader.api.FabricLoader

object OnMaceRoulette {
    val useFlint = FabricLoader.getInstance().isModLoaded("flint") && Config.useFlint.value
    var onMace = !useFlint

    val plotIds = mutableSetOf<Int>()
    val plotHandles = mutableSetOf<String>()

    fun fillPlotIds(ids: Set<String>) {
        plotIds.clear()
        plotHandles.clear()
        ids.forEach {
            it.toIntOrNull()?.let { i -> plotIds.add(i) } ?: plotHandles.add(it)
        }
    }

    fun registerFlintFeature() {
        if (!FabricLoader.getInstance().isModLoaded("flint")) return
        if (!useFlint) return
        FlintAPI.confirmLocationWithLocate()
        FlintAPI.registerFeature(
            ModeSwitchListeningFeature { oldMode, newMode ->
                StateManager.resetState()
                onMace = newMode == Mode.PLAY && (plotIds.contains(Flint.getUser().plot?.id) || plotHandles.contains(Flint.getUser().plot?.handle))
            }
        )
    }
}