package moe.pxe.macecompanion.dataClasses

import com.google.gson.JsonElement
import net.minecraft.world.item.ItemStack

data class IconOverride(
    val type: String,
    val id: String,
    val itemStack: ItemStackMaker
) {
    fun createItemStack(): ItemStack {
        return itemStack.createItemStack()
    }

    companion object {
        fun fromJson(json: JsonElement): IconOverride {
            val obj = json.asJsonObject
            val itemStackMaker = when {
                obj.get("item").isJsonObject -> ItemStackMaker.fromJson(obj.get("item").asJsonObject)
                else -> ItemStackMaker.fromString(obj.get("item").asString)
            }

            return IconOverride(
                type = obj.get("type").asString,
                id = obj.get("id").asString,
                itemStack = itemStackMaker
            )
        }
    }
}
