package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.util.OnMaceRoulette
import moe.pxe.macecompanion.util.SendMessage
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory

class MaceCompanion : ModInitializer {
    companion object {
        const val MOD_ID = "macecompanion"
        val LOGGER = LoggerFactory.getLogger(MOD_ID)
        const val DEBUG_MODE = false
    }
    override fun onInitialize() {
        LOGGER.info("Initializing Mace Roulette Companion...")
        if (FabricLoader.getInstance().environmentType == EnvType.SERVER) {
            LOGGER.error("Mace Roulette Companion is not a server-side mod!")
            return
        }
        if (!Config.loadFromFile()) Config.saveToFile()
        CustomKeybinds.registerKeybinds()
        OnMaceRoulette.fillPlotIds(Config.plotIds.value.toSet())
        OnMaceRoulette.registerFlintFeature()
        StateManager.registerListeners()
        SendMessage.registerTickListener()
        HideGLGG.registerListener()
        RoundInfoHud.registerListener()
    }

}