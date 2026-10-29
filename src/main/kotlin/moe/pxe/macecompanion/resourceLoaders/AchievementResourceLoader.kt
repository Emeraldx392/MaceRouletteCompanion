package moe.pxe.macecompanion.resourceLoaders

import com.google.gson.JsonParser
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config.areAchievementsShown
import moe.pxe.macecompanion.dataClasses.Achievement
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import java.nio.charset.StandardCharsets
import kotlin.collections.forEach

object AchievementResourceLoader : SimplePreparableReloadListener<Map<String, Achievement>>() {
    const val ACHIEVEMENT_DIRECTORY = "mace_roulette_companion/achievements"

    override fun prepare(resourceManager: ResourceManager, profiler: net.minecraft.util.profiling.ProfilerFiller): Map<String, Achievement> {
        val result = mutableMapOf<String, Achievement>()
        resourceManager.listResources(ACHIEVEMENT_DIRECTORY) { path ->
            path.path.endsWith(".json")
        }.forEach { (id, resource) ->
            resource.open().use { input ->
                val json = input.reader(StandardCharsets.UTF_8).use { JsonParser.parseReader(it) }
                val achievement = Achievement.fromJson(json)
                result[id.path] = achievement
            }
        }
        return result
    }

    fun refreshConfig() {
        val updated = areAchievementsShown.value.toMutableMap()
        loadedAchievements.forEach { (_, achievement) -> updated.putIfAbsent(achievement.id, false) }
        areAchievementsShown.value = updated
    }

    var forceStopRendering = false

    override fun apply(data: Map<String, Achievement>, resourceManager: ResourceManager, profiler: net.minecraft.util.profiling.ProfilerFiller) {
        loadedAchievements = data
        refreshConfig()
        forceStopRendering = true
    }

    private var loadedAchievements: Map<String, Achievement> = emptyMap()
    fun get(id: String): Achievement? = loadedAchievements[id]
    fun getAchievementFromMatchString(matchString: String): Achievement? = loadedAchievements.values.find { it.matchString == matchString }
    fun getAll(): Map<String, Achievement> = loadedAchievements
    fun getAllIdStrings(): List<String> = loadedAchievements.values.map { it.id }
}
