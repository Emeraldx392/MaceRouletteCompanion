package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.tpsNumberColor
import moe.pxe.macecompanion.config.Config.tpsOverrideColors
import moe.pxe.macecompanion.config.Config.tpsTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object TpsConfig {
    const val NAME = "tps"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.tps"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.tps.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.tps.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.tps.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, tpsOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, tpsTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, tpsNumberColor.asBinding(), "number_color")
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