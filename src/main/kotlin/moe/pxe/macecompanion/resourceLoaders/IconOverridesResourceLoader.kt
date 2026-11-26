package moe.pxe.macecompanion.resourceLoaders

import com.google.gson.JsonParser
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.MaceCompanion
import moe.pxe.macecompanion.config.Config.leftHudElements
import moe.pxe.macecompanion.config.Config.rightHudElements
import moe.pxe.macecompanion.dataClasses.IconOverride
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import java.nio.charset.StandardCharsets

object IconOverridesResourceLoader : SimplePreparableReloadListener<Map<String, IconOverride>>() {
    const val ICON_OVERRIDES_DIRECTORY = "mace_roulette_companion/icon_overrides"

    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller): Map<String, IconOverride> {
        val result = mutableMapOf<String, IconOverride>()
        val resources = resourceManager.listResources(ICON_OVERRIDES_DIRECTORY) { path -> path.path.endsWith(".json") }
        for ((id, resource) in resources) {
            try {
                resource.open().use { input ->
                    val json = input.reader(StandardCharsets.UTF_8).use { JsonParser.parseReader(it) }
                    val modifier = IconOverride.fromJson(json)
                    result[id.path] = modifier
                }
            } catch (e: Exception) {
                MaceCompanion.LOGGER.error("Error loading Modifier file: $id")
                MaceCompanion.LOGGER.error("Reason: ${e.message}")
                throw e
            }
        }
        return result
    }

    override fun apply(data: Map<String, IconOverride>, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        loadedIconOverrides = data
        rightHudElements.value.forEach { it.refreshRendering() }
        leftHudElements.value.forEach { it.refreshRendering() }
    }

    private var loadedIconOverrides: Map<String, IconOverride> = emptyMap()
    fun get(id: String): IconOverride? = loadedIconOverrides[id]
    fun getModifierFromId(id: String): IconOverride? = loadedIconOverrides.values.find { it.type == "modifier" && it.id == id }
    fun getEventFromId(id: String): IconOverride? = loadedIconOverrides.values.find { it.type == "event" && it.id == id }
    fun getOnAchievementFromId(id: String): IconOverride? = loadedIconOverrides.values.find { it.type == "achievement_on" && it.id == id }
    fun getOffAchievementFromId(id: String): IconOverride? = loadedIconOverrides.values.find { it.type == "achievement_off" && it.id == id }
}
