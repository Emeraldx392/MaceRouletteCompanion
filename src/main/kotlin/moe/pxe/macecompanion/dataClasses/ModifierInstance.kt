package moe.pxe.macecompanion.dataClasses

import com.mojang.authlib.GameProfile
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config.getAccentColor
import moe.pxe.macecompanion.config.Config.modifiersMaxBoosters
import moe.pxe.macecompanion.stateManagers.ModifierManager.CHARGED_MODIFIER_TEXTURE
import moe.pxe.macecompanion.stateManagers.ModifierManager.ETERNAL_MODIFIER_TEXTURE
import moe.pxe.macecompanion.stateManagers.ModifierManager.MYSTERY_MODIFIER_TEXTURE
import moe.pxe.macecompanion.util.PlayerProfile.custom2dHeadTextComponent
import moe.pxe.macecompanion.util.PlayerProfile.player2dHeadTextComponentList
import moe.pxe.macecompanion.util.TextUtils.createGradientText
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors

data class ModifierInstance(
    val modifier: Modifier,
    val isCharged: Boolean = false,
    val isEternal: Boolean = false,
    val isMidas: Boolean = false,
    val boosters: List<GameProfile> = listOf()
){
    fun buildModifierText(rightAligned: Boolean, use2dHeads: Boolean): Component {
        val startingText = buildRawModifierText()
        if (!use2dHeads || boosters.isEmpty()) return startingText
        val boosterText = player2dHeadTextComponentList(boosters, modifiersMaxBoosters.value, rightAligned)
        return if (rightAligned) Component.empty().append(boosterText).append(startingText)
        else Component.empty().append(startingText).append(boosterText)
    }
    fun buildRawModifierText(): Component {
        if(!isCharged && !isEternal && isMidas) return createGradientText(modifier.getTranslatable().copy(), listOf(0xFFC42D, 0xFFE684, 0xFFC42D)).withStyle { it.withShadowColor(-10076138) }

        val chargedStyle = Style.EMPTY.withColor(getAccentColor("modifiers", "text_color.charged_modifier", 0x0786FF))
        val eternalStyle = Style.EMPTY.withColor(getAccentColor("modifiers", "text_color.eternal_modifier", CommonColors.WHITE)).withShadowColor(getAccentColor("modifiers", "shadow_color.eternal_modifier", -10071549))
        val textStyle = if (isCharged) chargedStyle else if (isEternal) eternalStyle else modifier.getTranslatable().copy().style

        val eternalText = custom2dHeadTextComponent(ETERNAL_MODIFIER_TEXTURE).copy().withStyle(eternalStyle)
        val chargedText = custom2dHeadTextComponent(CHARGED_MODIFIER_TEXTURE).copy().withColor(CommonColors.WHITE)
        val mysteryText = custom2dHeadTextComponent(MYSTERY_MODIFIER_TEXTURE).copy().withColor(CommonColors.WHITE)

        val extraText = when {
            modifier.id == "mystery" -> Component.empty().append(mysteryText).append(" ")
            isEternal && isCharged -> Component.empty().append(eternalText).append(chargedText).append(" ")
            isEternal && !isCharged -> Component.empty().append(eternalText).append(" ")
            !isEternal && isCharged -> Component.empty().append(chargedText).append(" ")
            else -> Component.empty()
        }

        val baseText = modifier.getTranslatable().copy().setStyle(textStyle)

        val modifierText = Component.empty().append(extraText).append(baseText)
        return modifierText
    }
}