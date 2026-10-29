package moe.pxe.macecompanion.resourceLoaders

import com.google.gson.JsonParser
import moe.pxe.macecompanion.MaceCompanion
import moe.pxe.macecompanion.dataClasses.ItemStackMaker
import moe.pxe.macecompanion.dataClasses.Modifier
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.SimplePreparableReloadListener
import net.minecraft.util.profiling.ProfilerFiller
import net.minecraft.world.item.component.UseCooldown
import java.nio.charset.StandardCharsets
import java.util.Optional

object ModifierResourceLoader : SimplePreparableReloadListener<Map<String, Modifier>>() {
    const val MODIFIER_DIRECTORY = "mace_roulette_companion/modifiers"

    override fun prepare(resourceManager: ResourceManager, profiler: ProfilerFiller): Map<String, Modifier> {
        val result = mutableMapOf<String, Modifier>()
        val resources = resourceManager.listResources(MODIFIER_DIRECTORY) { path -> path.path.endsWith(".json") }
        for ((id, resource) in resources) {
            try {
                resource.open().use { input ->
                    val json = input.reader(StandardCharsets.UTF_8).use { JsonParser.parseReader(it) }
                    val modifier = Modifier.fromJson(json)
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

    override fun apply(data: Map<String, Modifier>, resourceManager: ResourceManager, profiler: ProfilerFiller) {
        loadedModifiers = data
    }

    private var loadedModifiers: Map<String, Modifier> = emptyMap()
    fun get(id: String): Modifier? = loadedModifiers[id]
    fun getModifierFromId(id: String): Modifier? = loadedModifiers.values.find { it.id == id }
    fun getMystery(): Modifier = getModifierFromId("mystery") ?: Modifier("mystery", "Mystery!", 0xD2B5FF, italic = true, itemStack = ItemStackMaker.fromItemIdAndPatch("minecraft:light_gray_candle", DataComponentPatch.builder().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).build()))
    fun getUnknown(): Modifier = getModifierFromId("unknown") ?: Modifier("unknown", "\uE024", 0xFF5555, bold = true, itemStack = ItemStackMaker.fromItemIdAndPatch("minecraft:mace", DataComponentPatch.builder().set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "glitched_mace")).set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon")))).build()))
    fun getAll(): Map<String, Modifier> = loadedModifiers
    fun getAllIdStrings(): List<String> = loadedModifiers.values.map { it.id }
}
