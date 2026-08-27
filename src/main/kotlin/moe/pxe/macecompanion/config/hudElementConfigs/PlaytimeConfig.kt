package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.playtimeIconColor
import moe.pxe.macecompanion.config.Config.playtimeNumberColor
import moe.pxe.macecompanion.config.Config.playtimeOverrideColors
import moe.pxe.macecompanion.config.Config.playtimeTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object PlaytimeConfig {
    const val NAME = "playtime"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.playtime"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.playtime.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.playtime.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.playtime.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, playtimeOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, playtimeTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, playtimeNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            val iconColor = overrideColorOption(NAME, playtimeIconColor.asBinding(), "icon_color")
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