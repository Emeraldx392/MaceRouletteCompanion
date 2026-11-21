package moe.pxe.macecompanion.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import moe.pxe.macecompanion.stateManagers.ModifierManager.client
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import java.util.Optional

object TextUtils {

    var hideNewRoundOrGameTextMessage: Boolean = false

    fun boldString(text: Component, subtext: String): Boolean {
        if (text.string.contains(subtext) && text.style.isBold) return true
        for (child in text.siblings) {
            if (boldString(child, subtext)) return true
        }
        return false
    }

    fun getStarFragmentIcon(): Component {
        val json = JsonObject()
        json.addProperty("atlas", "minecraft:particles")
        json.addProperty("sprite", "spark_2")
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
    }

    fun messageToJsonString(message: Component): String {
        return ComponentSerialization.CODEC
            .encodeStart(client.level!!.registryAccess().createSerializationContext(JsonOps.INSTANCE), message)
            .getOrThrow()
            .toString()
    }

    fun messageToJson(message: Component): JsonElement {
        return ComponentSerialization.CODEC
            .encodeStart(client.level!!.registryAccess().createSerializationContext(JsonOps.INSTANCE), message)
            .getOrThrow()
    }

    fun findTextColorInJson(json: JsonElement, targetText: String, parentColorStr: String? = null): Int? {
        if (json.isJsonPrimitive) {
            val text = json.asString
            if (text.contains(targetText)) {
                val colorToParse = parentColorStr ?: "#FFFFFF"
                return TextColor.parseColor(colorToParse).result().orElse(null)?.value
            }
            return null
        } else if (json.isJsonObject) {
            val obj = json.asJsonObject

            val text = if (obj.has("text")) obj.get("text").asString else ""
            val colorStr = if (obj.has("color")) obj.get("color").asString else parentColorStr

            if (text.contains(targetText)) {
                val colorToParse = colorStr ?: "#FFFFFF"
                return TextColor.parseColor(colorToParse).result().orElse(null)?.value
            }

            if (obj.has("extra") && obj.get("extra").isJsonArray) {
                val extraArray = obj.getAsJsonArray("extra")
                for (child in extraArray) {
                    val foundColorInt = findTextColorInJson(child, targetText, colorStr)
                    if (foundColorInt != null) return foundColorInt
                }
            }
        }
        return null
    }

    fun createGradientText(text: Component, colors: List<Int>): MutableComponent {
        val result = Component.empty()

        if (colors.isEmpty()) return text.copy()
        if (colors.size == 1) {
            val targetColor = TextColor.fromRgb(colors[0])
            return text.copy().withStyle { it.withColor(targetColor) }
        }

        var totalLength = 0
        text.visit({ _, part ->
            totalLength += part.length
            Optional.empty<Any>()
        }, Style.EMPTY)

        if (totalLength == 0) return result

        var globalIndex = 0
        text.visit({ currentStyle, part ->
            for (i in part.indices) {
                val globalRatio = if (totalLength > 1) globalIndex.toFloat() / (totalLength - 1) else 0.0f

                val totalSegments = colors.size - 1
                val exactSegmentPosition = globalRatio * totalSegments

                val segmentIndex = exactSegmentPosition.toInt().coerceAtMost(totalSegments - 1)
                val nextSegmentIndex = segmentIndex + 1

                val localRatio = exactSegmentPosition - segmentIndex

                val startColor = colors[segmentIndex]
                val endColor = colors[nextSegmentIndex]

                val startR = (startColor shr 16) and 0xFF
                val startG = (startColor shr 8) and 0xFF
                val startB = startColor and 0xFF

                val endR = (endColor shr 16) and 0xFF
                val endG = (endColor shr 8) and 0xFF
                val endB = endColor and 0xFF

                val finalR = (startR + localRatio * (endR - startR)).toInt()
                val finalG = (startG + localRatio * (endG - startG)).toInt()
                val finalB = (startB + localRatio * (endB - startB)).toInt()

                val interpolatedColor = (finalR shl 16) or (finalG shl 8) or finalB

                val newStyle = currentStyle.withColor(TextColor.fromRgb(interpolatedColor))
                result.append(Component.literal(part[i].toString()).withStyle(newStyle))
                globalIndex++
            }
            Optional.empty<Any>()
        }, Style.EMPTY)
        return result
    }


    fun getNewRoundOrGameText(type: String, time: Int, mainColor: Int, timeColor: Int): Component {
        if (hideNewRoundOrGameTextMessage) return Component.empty()
        val firstText = when (type) {
            "Round" -> Component.literal("Next round").withColor(mainColor)
            "Game" -> Component.literal("Game starting").withColor(mainColor)
            else -> return Component.empty()
        }
        val inText = Component.literal(" in ").withStyle(ChatFormatting.GRAY)
        val timeLeft = Component.literal(time.toString()).withColor(timeColor)
        val secondText = firstText.append(inText)
        return secondText.append(timeLeft)
    }
}