package moe.pxe.macecompanion.util

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.serialization.JsonOps
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config.getAccentColor
import moe.pxe.macecompanion.config.Config.modifiersMaxBoosters
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.stateManagers.ModifierManager.client
import moe.pxe.macecompanion.util.PlayerProfile.player2dHeadTextComponentList
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.Style
import net.minecraft.network.chat.TextColor
import net.minecraft.util.CommonColors

object TextUtils {

    var hideNewRoundOrGameTextMessage: Boolean = false

    fun boldString(text: Component, subtext: String): Boolean {
        if (text.string.contains(subtext) && text.style.isBold) {
            return true
        }
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

    fun buildModifierText(modifier: Modifiers, eternal: Boolean, charged: Boolean, rightAligned: Boolean): Component {
        val chargedStyle = Style.EMPTY.withColor(getAccentColor("modifiers", "text_color.charged_modifier", 0x0786FF))
        val eternalStyle = Style.EMPTY.withColor(getAccentColor("modifiers", "text_color.eternal_modifier", CommonColors.WHITE)).withShadowColor(getAccentColor("modifiers", "shadow_color.eternal_modifier", -10071549))
        val textStyle = if (charged) chargedStyle else if (eternal) eternalStyle else modifier.translatable.copy().style

        val eternalText = Component.literal("∞").setStyle(eternalStyle)
        val chargedText = Component.literal("⚡").setStyle(chargedStyle)

        val extraText = when {
            eternal && charged -> if (rightAligned) Component.empty().append(eternalText).append(chargedText).append(" ") else Component.literal(" ").append(chargedText).append(eternalText)
            eternal && !charged -> if (rightAligned) Component.empty().append(eternalText).append(" ") else Component.literal(" ").append(eternalText)
            !eternal && charged -> if (rightAligned) Component.empty().append(chargedText).append(" ") else Component.literal(" ").append(chargedText)
            else -> Component.empty()
        }

        val baseText = modifier.translatable.copy().setStyle(textStyle)

        val modifierText =
            if (!rightAligned) Component.empty().append(baseText).append(extraText)
            else Component.empty().append(extraText).append(baseText)

        return modifierText
    }

    fun buildModifierTextWith2dBoosters(modifier: Modifiers, eternal: Boolean, charged: Boolean, rightAligned: Boolean, use2dHeads: Boolean, boosters: MutableList<GameProfile>?): Component {
        val startingText = buildModifierText(modifier, eternal, charged, rightAligned)
        if (!use2dHeads || boosters == null) return startingText
        val boosterText = player2dHeadTextComponentList(boosters, modifiersMaxBoosters.value, rightAligned)
        return if (rightAligned) Component.empty().append(boosterText).append(startingText)
        else Component.empty().append(startingText).append(boosterText)
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

    fun findTextColorInJson(json: JsonElement, targetText: String): Int? {
        if (!json.isJsonObject) return null
        val obj = json.asJsonObject
        val text = obj.get("text")?.asString ?: ""
        if (text.contains(targetText)) {
            val colorStr = obj.get("color")?.asString
            if (colorStr != null) {
                val textColor = TextColor.parseColor(colorStr).result().orElse(null)
                return textColor?.value
            }
        }
        if (obj.has("extra") && obj.get("extra").isJsonArray) {
            val extraArray = obj.getAsJsonArray("extra")
            for (child in extraArray) {
                val foundColorInt = findTextColorInJson(child, targetText)
                if (foundColorInt != null) return foundColorInt
            }
        }
        return null
    }

    fun getNewRoundOrGameText(type: String, time: Int, mainColor: Int, timeColor: Int): Component {
        if(hideNewRoundOrGameTextMessage) return Component.empty()
        val firstText = when (type) {
                "Round" -> Component.literal("Next round").withColor(mainColor)
                "Game" -> Component.literal("Game starting").withColor(mainColor)
                else -> return Component.empty()
            }
        val inText = Component.literal(" in ").withColor(CommonColors.GRAY)
        val timeLeft = Component.literal(time.toString()).withColor(timeColor)
        val secondText = firstText.append(inText)
        return secondText.append(timeLeft)
    }
}