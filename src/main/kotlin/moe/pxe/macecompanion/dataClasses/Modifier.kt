package moe.pxe.macecompanion.dataClasses

import com.google.gson.JsonElement
import moe.pxe.macecompanion.config.Config.getAccentColor
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack

data class Modifier(
    val id: String,
    val matchString: String,
    val color: Int? = null,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val itemStack: ItemStackMaker
) {
    fun createItemStack(): ItemStack {
        return itemStack.createItemStack()
    }

    fun getTranslatable(): Component {
        val colorId = if (id == "mystery") "mystery" else if (id == "unknown") "unknown" else "regular"
        val style = Style.EMPTY.withBold(bold).withItalic(italic)
        return Component.translatable("mrc.modifier.$id").withStyle(style).withColor(getAccentColor("modifiers", "text_color.${colorId}_modifier", color ?: CommonColors.YELLOW))
    }

    companion object {
        fun fromJson(json: JsonElement): Modifier {
            val obj = json.asJsonObject
            val itemStackMaker = when {
                obj.get("item").isJsonObject -> ItemStackMaker.fromJson(obj.get("item").asJsonObject)
                else -> ItemStackMaker.fromString(obj.get("item").asString)
            }
            val color: Int? = if (obj.has("color")) {
                val colorStr = obj.get("color").asString
                if (colorStr.startsWith("#")) colorStr.substring(1).toInt(16)
                else colorStr.toInt()
            } else null

            val bold = obj.has("bold") && obj.get("bold").asBoolean
            val italic = obj.has("italic") && obj.get("italic").asBoolean

            return Modifier(
                id = obj.get("id").asString,
                matchString = obj.get("match_string").asString,
                bold = bold,
                italic = italic,
                color = color,
                itemStack = itemStackMaker
            )
        }
    }
}
