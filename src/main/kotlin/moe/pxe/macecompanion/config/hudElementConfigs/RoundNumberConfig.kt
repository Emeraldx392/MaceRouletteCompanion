package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.roundNumberOverrideColors
import moe.pxe.macecompanion.config.Config.roundNumberNumberColor
import moe.pxe.macecompanion.config.Config.roundNumberTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object RoundNumberConfig {
    const val NAME = "round_number"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.round_number"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.round_number.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.round_number.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.round_number.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, roundNumberOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, roundNumberTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, roundNumberNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            it.option(overrideColors)
                            it.option(textColor)
                            it.option(numberColor)
                        }
                        .build())
                .build())
        .save(Config::saveToFileAndRefreshRendering)
        .build()
        .generateScreen(parent)
}