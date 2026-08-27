package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.maceChanceHideWhenEliminated
import moe.pxe.macecompanion.config.Config.maceChanceIconColor
import moe.pxe.macecompanion.config.Config.maceChanceNumberColor
import moe.pxe.macecompanion.config.Config.maceChanceOverrideColors
import moe.pxe.macecompanion.config.Config.maceChanceTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.hideWhenEliminatedOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object MaceChanceConfig {
    const val NAME = "mace_chance"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.mace_chance"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.mace_chance.category.misc"))
                .option(hideWhenEliminatedOption(NAME, maceChanceHideWhenEliminated.asBinding()))
                .build()
        )
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.mace_chance.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.mace_chance.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.mace_chance.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, maceChanceOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, maceChanceTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, maceChanceNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            val iconColor = overrideColorOption(NAME, maceChanceIconColor.asBinding(), "icon_color")
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