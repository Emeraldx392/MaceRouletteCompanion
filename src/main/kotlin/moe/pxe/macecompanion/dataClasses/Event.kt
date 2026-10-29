package moe.pxe.macecompanion.dataClasses

import net.minecraft.network.chat.TextColor
import net.minecraft.world.item.ItemStack

data class Event(
    val id: String,
    val matchString: String,
    val colors: List<Int>,
    val shadowColor: Int? = null,
    val itemStack: ItemStackMaker
) {
    fun createItemStack(): ItemStack {
        return itemStack.createItemStack()
    }

    companion object {
        fun fromJson(json: com.google.gson.JsonElement): Event {
            val obj = json.asJsonObject
            val itemStackMaker = when {
                obj.get("item").isJsonObject -> ItemStackMaker.fromJson(obj.get("item").asJsonObject)
                else -> ItemStackMaker.fromString(obj.get("item").asString)
            }
            val colorList: List<Int> = when {
                obj.has("color") && obj.get("color").isJsonArray -> {
                    obj.get("color").asJsonArray.map {
                        TextColor.parseColor(it.asString).result().orElse(null)?.value ?: 0x00000
                    }
                }

                obj.has("color") -> {
                    val colorStr = obj.get("color").asString
                    if (colorStr.startsWith("#")) listOf(colorStr.substring(1).toInt(16))
                    else listOf(colorStr.toInt())
                }

                else -> emptyList()
            }
            val shadowString = if (!obj.has("shadow_color")) null
            else obj.get("shadow_color").asString

            val shadowColor = if (shadowString != null) {
                if (shadowString.startsWith("#")) shadowString.substring(1).toInt(16)
                else shadowString.toInt()
            } else null

            return Event(
                id = obj.get("id").asString,
                matchString = obj.get("match_string").asString,
                itemStack = itemStackMaker,
                shadowColor = shadowColor,
                colors = colorList
            )
        }
    }
}
