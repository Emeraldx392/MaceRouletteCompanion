package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.bountyBoardMaxPlayers
import moe.pxe.macecompanion.config.Config.bountyBoardMinBounty
import moe.pxe.macecompanion.config.Config.bountyBoardNumberColor
import moe.pxe.macecompanion.config.Config.bountyBoardOverrideColors
import moe.pxe.macecompanion.config.Config.bountyBoardTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import moe.pxe.macecompanion.util.OptionUtils.sliderOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object BountyBoardConfig {
    const val NAME = "bounty_board"

    fun generateConfig(parent: Screen): Screen? =
        YetAnotherConfigLib.createBuilder().title(Component.translatable("mrc.hudelement.bounty_board")).category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.bounty_board.category.misc"))
                .option(sliderOption(NAME, bountyBoardMaxPlayers.asBinding(), 1, 15, 1, "max_players"))
                .option(sliderOption(NAME, bountyBoardMinBounty.asBinding(), 1, 10, 1, "min_bounty"))
                .build()
        ).category(
            ConfigCategory.createBuilder().name(Component.translatable("mrc.config.bounty_board.category.styling")).group(OptionGroup.createBuilder().name(Component.translatable("mrc.config.bounty_board.category.styling.group.colors")).description(OptionDescription.of(Component.translatable("mrc.config.bounty_board.category.styling.group.colors.description"))).also {
                val overrideColors = overrideColorsOption(NAME, bountyBoardOverrideColors.asBinding())

                val textColor = overrideColorOption(NAME, bountyBoardTextColor.asBinding(), "text_color")
                addColorOptionDependency(textColor, overrideColors)

                val textColorPlayer = overrideColorOption(NAME, bountyBoardTextColor.asBinding(), "text_color.player")
                addColorOptionDependency(textColorPlayer, overrideColors)

                val numberColor = overrideColorOption(NAME, bountyBoardNumberColor.asBinding(), "number_color")
                addColorOptionDependency(numberColor, overrideColors)

                it.option(overrideColors)
                it.option(textColor)
                it.option(textColorPlayer)
                it.option(numberColor)
            }.build()).build()
        ).save(Config::saveToFileAndRefreshRendering).build().generateScreen(parent)
}