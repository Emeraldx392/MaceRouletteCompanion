package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.eliminationsHideWhenEliminated
import moe.pxe.macecompanion.config.Config.eliminationsIconColor
import moe.pxe.macecompanion.config.Config.eliminationsNumberColor
import moe.pxe.macecompanion.config.Config.eliminationsOverrideColors
import moe.pxe.macecompanion.config.Config.eliminationsTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.hideWhenEliminatedOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object EliminationsConfig {
    const val NAME = "eliminations"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.eliminations"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.eliminations.category.misc"))
                .option(hideWhenEliminatedOption(NAME, eliminationsHideWhenEliminated.asBinding()))
                .build()
        )
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.eliminations.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.eliminations.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.eliminations.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, eliminationsOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, eliminationsTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, eliminationsNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            val iconColor = overrideColorOption(NAME, eliminationsIconColor.asBinding(), "icon_color")
                            addColorOptionDependency(iconColor, overrideColors)

                            it.option(overrideColors)
                            it.option(textColor)
                            it.option(numberColor)
                            it.option(iconColor)
                        }
                        .build())
                .build())
        .save(Config::saveToFile)
        .build()
        .generateScreen(parent)
}