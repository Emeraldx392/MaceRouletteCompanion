package moe.pxe.macecompanion.config.hudElementConfigs

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.Config.pingNumberColor
import moe.pxe.macecompanion.config.Config.pingOverrideColors
import moe.pxe.macecompanion.config.Config.pingTextColor
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

object PingConfig {
    const val NAME = "ping"

    fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("mrc.hudelement.ping"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.ping.category.styling"))
                .group(
                    OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.ping.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.ping.category.styling.group.colors.description")))
                        .also {
                            val overrideColors = overrideColorsOption(NAME, pingOverrideColors.asBinding())

                            val textColor = overrideColorOption(NAME, pingTextColor.asBinding(), "text_color")
                            addColorOptionDependency(textColor, overrideColors)

                            val numberColor = overrideColorOption(NAME, pingNumberColor.asBinding(), "number_color")
                            addColorOptionDependency(numberColor, overrideColors)

                            it.option(overrideColors)
                            it.option(textColor)
                            it.option(numberColor)
                        }
                        .build())
                .build())
        .save(Config::saveToFile)
        .build()
        .generateScreen(parent)
}