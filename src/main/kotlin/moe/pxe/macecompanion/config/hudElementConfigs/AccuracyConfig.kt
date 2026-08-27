package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.accuracyIconColor
import moe.pxe.macecompanion.config.Config.accuracyNumberColor
import moe.pxe.macecompanion.config.Config.accuracyOverrideColors
import moe.pxe.macecompanion.config.Config.accuracyTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object AccuracyConfig {
    const val NAME = "accuracy"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.accuracy"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.accuracy.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.accuracy.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.accuracy.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, accuracyOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, accuracyTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, accuracyNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            val iconColor = overrideColorOption(NAME, accuracyIconColor.asBinding(), "icon_color")
                            addColorOptionDependency(iconColor, overrideColors)

                            it.option(overrideColors)
                            it.option(textColor)
                            it.option(numberColor)
                            it.option(iconColor)
                        }
                        .build())
                .build())
        .save(Config::saveToFileAndRefreshRendering)
        .build()
        .generateScreen(parent)
}