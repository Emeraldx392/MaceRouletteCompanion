package moe.pxe.macecompanion.util

import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.serialization.JsonOps
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.enums.Modifiers
import moe.pxe.macecompanion.util.PlayerProfile.player2dHeadTextComponentList
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.util.CommonColors

object TextUtils {
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
        val textStyle = if (charged) Config.getChargedModifierTextAccentStyle(0x0786FF)
        else if (eternal) Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549)
        else modifier.translatable.copy().style

        val eternalText = Component.literal("∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549))
        val chargedText = Component.literal("⚡").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF))

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
        val boosterText = player2dHeadTextComponentList(boosters, Config.boosterListMax.value, rightAligned)
        return if (rightAligned) Component.empty().append(boosterText).append(startingText)
        else Component.empty().append(startingText).append(boosterText)
    }
}