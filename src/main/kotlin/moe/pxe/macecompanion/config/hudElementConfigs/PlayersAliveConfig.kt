package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.playersAliveNumberColorAlive
import moe.pxe.macecompanion.config.Config.playersAliveNumberColorTotal
import moe.pxe.macecompanion.config.Config.playersAliveOverrideColors
import moe.pxe.macecompanion.config.Config.playersAliveTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object PlayersAliveConfig {
    const val NAME = "players_alive"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.players_alive"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.players_alive.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.players_alive.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.players_alive.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, playersAliveOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, playersAliveTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColorAlive = overrideColorOption(NAME, playersAliveNumberColorAlive.asBinding(), "number_color.alive")
                            addColorOptionDependency(numberColorAlive, overrideColors)

                            val numberColorTotal = overrideColorOption(NAME, playersAliveNumberColorTotal.asBinding(), "number_color.total")
                            addColorOptionDependency(numberColorTotal, overrideColors)

                            it.option(overrideColors)
                            it.option(textColor)
                            it.option(numberColorAlive)
                            it.option(numberColorTotal)
                        }
                        .build())
                .build())
        .save(Config::saveToFileAndRefreshRendering)
        .build()
        .generateScreen(parent)
}