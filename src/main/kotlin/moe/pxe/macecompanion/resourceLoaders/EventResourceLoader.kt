package moe.pxe.macecompanion.resourceLoaders

import com.google.gson.JsonParser
import moe.pxe.macecompanion.dataClasses.Event
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import java.nio.charset.StandardCharsets
import kotlin.collections.forEach

object EventResourceLoader : SimplePreparableReloadListener<Map<String, Event>>() {
    const val EVENT_DIRECTORY = "mace_roulette_companion/events"

    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller): Map<String, Event> {
        val result = mutableMapOf<String, Event>()
        resourceManager.listResources(EVENT_DIRECTORY) { path ->
            path.path.endsWith(".json")
        }.forEach { (id, resource) ->
            resource.open().use { input ->
                val json = input.reader(StandardCharsets.UTF_8).use { JsonParser.parseReader(it) }
                val event = Event.fromJson(json)
                result[id.path] = event
            }
        }
        return result
    }

    override fun apply(data: Map<String, Event>, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        loadedEvents = data
    }

    private var loadedEvents: Map<String, Event> = emptyMap()
    fun get(id: String): Event? = loadedEvents[id]
    fun getEventFromId(id: String): Event? = loadedEvents.values.find { it.id == id }
    fun getEventFromMatchString(matchString: String): Event? = loadedEvents.values.find { it.matchString == matchString }
    fun getAll(): Map<String, Event> = loadedEvents
    fun getAllIdStrings(): List<String> = loadedEvents.values.map { it.id }
}
