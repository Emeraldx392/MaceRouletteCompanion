package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.modifiersMaxBoosters
import moe.pxe.macecompanion.config.Config.modifiersOverrideColors
import moe.pxe.macecompanion.config.Config.modifiersShadowColorEternalModifier
import moe.pxe.macecompanion.config.Config.modifiersTextColorChargedModifier
import moe.pxe.macecompanion.config.Config.modifiersTextColorEternalModifier
import moe.pxe.macecompanion.config.Config.modifiersTextColorMysteryModifier
import moe.pxe.macecompanion.config.Config.modifiersTextColorRegularModifier
import moe.pxe.macecompanion.config.Config.modifiersUse2dHeadIcons
import moe.pxe.macecompanion.config.Config.modifiersUseCustomModifierIcons
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.iconBooleanOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import moe.pxe.macecompanion.util.OptionUtils.sliderOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object ModifiersConfig {
    const val NAME = "modifiers"

    fun generateConfig(parent: Screen): Screen? =
        YetAnotherConfigLib.createBuilder().title(Component.translatable("mrc.hudelement.modifiers")).category(
            ConfigCategory.createBuilder().name(Component.translatable("mrc.config.modifiers.category.misc")).option(sliderOption(NAME, modifiersMaxBoosters.asBinding(), 0, 15, 1, "max_boosters")).build()
        ).category(
            ConfigCategory.createBuilder().name(Component.translatable("mrc.config.modifiers.category.styling")).group(
                OptionGroup.createBuilder().name(Component.translatable("mrc.config.modifiers.category.styling.group.colors")).description(OptionDescription.of(Component.translatable("mrc.config.modifiers.category.styling.group.colors.description"))).also {
                    val overrideColors = overrideColorsOption(NAME, modifiersOverrideColors.asBinding())

                    val textColor = overrideColorOption(NAME, Config.modifiersTextColor.asBinding(), "text_color")
                    addColorOptionDependency(textColor, overrideColors)

                    val textColorRegularModifier = overrideColorOption(NAME, modifiersTextColorRegularModifier.asBinding(), "text_color.regular_modifier")
                    addColorOptionDependency(textColorRegularModifier, overrideColors)

                    val textColorEternalModifier = overrideColorOption(NAME, modifiersTextColorEternalModifier.asBinding(), "text_color.eternal_modifier")
                    addColorOptionDependency(textColorEternalModifier, overrideColors)

                    val shadowColorEternalModifier = overrideColorOption(NAME, modifiersShadowColorEternalModifier.asBinding(), "shadow_color.eternal_modifier")
                    addColorOptionDependency(shadowColorEternalModifier, overrideColors)

                    val textColorChargedModifier = overrideColorOption(NAME, modifiersTextColorChargedModifier.asBinding(), "text_color.charged_modifier")
                    addColorOptionDependency(textColorChargedModifier, overrideColors)

                    val textColorMysteryModifier = overrideColorOption(NAME, modifiersTextColorMysteryModifier.asBinding(), "text_color.mystery_modifier")
                    addColorOptionDependency(textColorMysteryModifier, overrideColors)

                    it.option(overrideColors)
                    it.option(textColor)
                    it.option(textColorRegularModifier)
                    it.option(textColorEternalModifier)
                    it.option(shadowColorEternalModifier)
                    it.option(textColorChargedModifier)
                    it.option(textColorMysteryModifier)
                }.build()
            ).group(
                    OptionGroup.createBuilder().name(Component.translatable("mrc.config.modifiers.category.styling.group.icons")).description(OptionDescription.of(Component.translatable("mrc.config.modifiers.category.styling.group.icons.description"))).option(iconBooleanOption(NAME, modifiersUseCustomModifierIcons.asBinding(), "use_custom_modifier_icons")).option(iconBooleanOption(NAME, modifiersUse2dHeadIcons.asBinding(), "use_2d_head_icons")).build()
                ).build()
        ).save(Config::saveToFileAndRefreshRendering).build().generateScreen(parent)
}