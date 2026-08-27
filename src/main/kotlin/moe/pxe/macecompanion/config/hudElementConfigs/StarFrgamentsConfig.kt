package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.starFragmentsHideWhenEliminated
import moe.pxe.macecompanion.config.Config.starFragmentsIconColor
import moe.pxe.macecompanion.config.Config.starFragmentsNumberColor
import moe.pxe.macecompanion.config.Config.starFragmentsOverrideColors
import moe.pxe.macecompanion.config.Config.starFragmentsTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.hideWhenEliminatedOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object StarFrgamentsConfig {
    const val NAME = "star_framents"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.star_framents"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.star_framents.category.misc"))
                .option(hideWhenEliminatedOption(NAME, starFragmentsHideWhenEliminated.asBinding()))
                .build()
        )
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.star_framents.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.star_framents.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.star_framents.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, starFragmentsOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, starFragmentsTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, starFragmentsNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            val iconColor = overrideColorOption(NAME, starFragmentsIconColor.asBinding(), "icon_color")
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