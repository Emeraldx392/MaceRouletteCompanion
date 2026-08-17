package moe.pxe.macecompanion.util

import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.serialization.JsonOps
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config.getAccentColor
import moe.pxe.macecompanion.config.Config.modifiersMaxBoosters
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.util.PlayerProfile.player2dHeadTextComponentList
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.Style
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

    fun getNewRoundOrGameText(type: String, time: Int): Component {
        if(hideNewRoundOrGameTextMessage) return Component.empty()
        val firstText = when (type) {
                "Round" -> Component.literal("Next round in ").withColor(CommonColors.HIGH_CONTRAST_DIAMOND)
                "Game" -> Component.literal("Game starting in ").withColor(CommonColors.YELLOW)
                else -> return Component.empty()
            }
        val timeLeft = Component.literal(time.toString()).withColor(CommonColors.YELLOW)
        return firstText.append(timeLeft)
    }
}