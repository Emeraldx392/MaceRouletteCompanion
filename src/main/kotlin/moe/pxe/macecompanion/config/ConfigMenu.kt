package moe.pxe.macecompanion.config

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.controllers.EnumWithConfigControllerBuilder
import moe.pxe.macecompanion.config.controllers.FormattedStringControllerBuilder
import moe.pxe.macecompanion.enums.HudElements
import moe.pxe.macecompanion.enums.HudLocation
import moe.pxe.macecompanion.util.OnMaceRoulette
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screen.ConfirmLinkScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.awt.Color

object ConfigMenu {
    fun generateScreen(parent: Screen?): Screen? {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.config.title"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.category.automsg"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.automsg.group.autogg"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autogg.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.category.automsg.group.autogg.option.useAutoGG"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autogg.option.useAutoGG.description")))
                        .binding(
                            Config.useAutoGG.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Text.translatable("mrc.config.category.automsg.group.autogg.option.ggDelayTicks"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autogg.option.ggDelayTicks.description")))
                        .binding(
                            Config.ggDelayTicks.asBinding()
                        )
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 100)
                                .step(1)
                                .formatValue { i -> Text.of("${String.format("%.2f", i.toFloat()/20f)} seconds") }
                        }
                        .build())
                    .build())
                .group(ListOption.createBuilder<String>()
                    .name(Text.translatable("mrc.config.category.automsg.group.autoGGStrings"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autoGGStrings.description")))
                    .binding(
                        Config.autoGGStrings.asBinding()
                    )
                    .controller { FormattedStringControllerBuilder.create(it)
                        .valueFormatter { str ->
                            val text = Text.empty()
                            val matches = """(gg)|(good game)""".toRegex(RegexOption.IGNORE_CASE).findAll(str)
                            var lastIdx = 0
                            matches.forEach { match ->
                                text.append(str.substring(lastIdx, match.range.first))
                                    .append(Text.literal(match.value).setStyle(Style.EMPTY.withColor(0xA0F9FF)))
                                lastIdx = match.range.last + 1
                            }
                            return@valueFormatter text.append(str.substring(matches.lastOrNull()?.let { it.range.last + 1 } ?: 0))
                        }
                    }
                    .initial("")
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.automsg.group.autogl"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autogl.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.category.automsg.group.autogl.option.useAutoGL"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autogl.option.useAutoGL.description")))
                        .binding(
                            Config.useAutoGL.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Text.translatable("mrc.config.category.automsg.group.autogl.option.glDelayTicks"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autogl.option.glDelayTicks.description")))
                        .binding(
                            Config.glDelayTicks.asBinding()
                        )
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 100)
                                .step(1)
                                .formatValue { i -> Text.of("${String.format("%.2f", i.toFloat()/20f)} seconds") }
                        }
                        .build())
                    .build())
                .group(ListOption.createBuilder<String>()
                    .name(Text.translatable("mrc.config.category.automsg.group.autoGLStrings"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.autoGLStrings.description")))
                    .binding(
                        Config.autoGLStrings.asBinding()
                    )
                    .controller { FormattedStringControllerBuilder.create(it)
                        .valueFormatter { str ->
                            val text = Text.empty()
                            val matches = """(gl)|(hf)|(good luck)|(have fun)""".toRegex(RegexOption.IGNORE_CASE).findAll(str)
                            var lastIdx = 0
                            matches.forEach { match ->
                                text.append(str.substring(lastIdx, match.range.first))
                                    .append(Text.literal(match.value).setStyle(Style.EMPTY.withColor(0xFFCE2D)))
                                lastIdx = match.range.last + 1
                            }
                            text.append(str.substring(matches.lastOrNull()?.let { it.range.last + 1 } ?: 0))
                        }
                    }
                    .initial("")
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.automsg.group.hidemessages"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.hidemessages.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGGMessages"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGGMessages.description")))
                        .binding(
                            Config.hideGGMessages.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGLMessages"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.automsg.group.hidemessages.option.hideGLMessages.description")))
                        .binding(
                            Config.hideGLMessages.asBinding()
                        )
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.category.roundhud"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("mrc.config.category.roundhud.option.displayHud"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.option.displayHud.description")))
                    .binding(Config.displayHud.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.roundhud.group.hudtransforms"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.description")))
                    .option(Option.createBuilder<HudLocation>()
                        .name(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudLocation"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudLocation.description")))
                        .binding(Config.hudLocation.asBinding())
                        .controller { EnumControllerBuilder.create(it).enumClass(HudLocation::class.java) }
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudXMargin"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudXMargin.description")))
                        .binding(Config.hudXMargin.asBinding())
                        .controller({ IntegerFieldControllerBuilder.create(it).formatValue { Text.of("$it pixels") } })
                        .build())
                    .option(Option.createBuilder<Int>()
                        .name(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudYMargin"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudYMargin.description")))
                        .binding(Config.hudYMargin.asBinding())
                        .controller({ IntegerFieldControllerBuilder.create(it).formatValue { Text.of("$it pixels") } })
                        .build())
                    .option(Option.createBuilder<Float>()
                        .name(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudScale"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudtransforms.option.hudScale.description")))
                        .binding(Config.hudScale.asBinding())
                        .controller { FloatSliderControllerBuilder.create(it)
                            .formatValue { Text.of("${it * 100}%") }
                            .range(0.05f, 3f)
                            .step(0.05f)
                        }
                        .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.roundhud.group.hudstyle"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudstyle.description")))
                    .also {
                        val useAccentColors = Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.useAccentColors"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.useAccentColors.description")))
                            .binding(Config.useAccentColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val accentColorNumber = Option.createBuilder<Int>()
                                .name(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.accentColorNumber"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.accentColorNumber.description")))
                                .binding(Config.accentColorNumber.asBinding())
                                .controller {
                                    IntegerSliderControllerBuilder.create(it)
                                        .range(1, 3)
                                        .step(1)
                                }
                                .build()
                        accentColorNumber.setAvailable(useAccentColors.pendingValue())
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) accentColorNumber.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) accentColorNumber.setAvailable(option.pendingValue())
                        }
                        val mainAccentColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.mainAccentColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.mainAccentColor.description")))
                            .binding(Config.mainAccentColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        mainAccentColor.setAvailable(useAccentColors.pendingValue())
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) mainAccentColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) mainAccentColor.setAvailable(option.pendingValue())
                        }
                        val secondAccentColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.secondAccentColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.secondAccentColor.description")))
                            .binding(Config.secondAccentColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        secondAccentColor.setAvailable(useAccentColors.pendingValue() && accentColorNumber.pendingValue() > 1)
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) secondAccentColor.setAvailable(option.pendingValue() && accentColorNumber.pendingValue() > 1)
                            if (event == OptionEventListener.Event.STATE_CHANGE) secondAccentColor.setAvailable(option.pendingValue( )&& accentColorNumber.pendingValue() > 1)
                        }
                        accentColorNumber.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) secondAccentColor.setAvailable(option.pendingValue() > 1 && useAccentColors.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) secondAccentColor.setAvailable(option.pendingValue() > 1 && useAccentColors.pendingValue())
                        }
                        val thirdAccentColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.thirdAccentColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudstyle.option.thirdAccentColor.description")))
                            .binding(Config.thirdAccentColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        thirdAccentColor.setAvailable(useAccentColors.pendingValue() && accentColorNumber.pendingValue() > 2)
                        useAccentColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) thirdAccentColor.setAvailable(option.pendingValue() && accentColorNumber.pendingValue() > 2)
                            if (event == OptionEventListener.Event.STATE_CHANGE) thirdAccentColor.setAvailable(option.pendingValue( )&& accentColorNumber.pendingValue() > 2)
                        }
                        accentColorNumber.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) thirdAccentColor.setAvailable(option.pendingValue() > 2 && useAccentColors.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) thirdAccentColor.setAvailable(option.pendingValue() > 2 && useAccentColors.pendingValue())
                        }
                        it.option(useAccentColors)
                        it.option(accentColorNumber)
                        it.option(mainAccentColor)
                        it.option(secondAccentColor)
                        it.option(thirdAccentColor)
                    }
                    .build())
                .group(ListOption.createBuilder<HudElements>()
                    .name(Text.translatable("mrc.config.category.roundhud.group.hudElements"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.roundhud.group.hudElements.description")))
                    .binding(Config.hudElements.asBinding())
                    .controller { EnumWithConfigControllerBuilder.create(it).enumClass(HudElements::class.java) }
                    .initial(HudElements.ROUND_NUMBER)
                    .build()
                )
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.category.toasts"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.toasts.group.game"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.game.description")))
                        .option(Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.category.toasts.group.game.option.showNewEventToasts"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.game.option.showNewEventToasts.description")))
                            .binding(Config.showNewEventToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                        .option(Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.category.toasts.group.game.option.showModifierChargerToasts"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.game.option.showModifierChargerToasts.description")))
                            .binding(Config.showModifierChargerToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                        .option(Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.category.toasts.group.game.option.showBountyToasts"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.game.option.showBountyToasts.description")))
                            .binding(Config.showBountyToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .build())
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.category.toasts.group.player"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.player.description")))
                        .option(Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.category.toasts.group.player.option.showPlayerToasts"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.player.option.showPlayerToasts.description")))
                            .binding(Config.showPlayerJoinToasts.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build())
                    .build())
                .group(ListOption.createBuilder<String>()
                    .name(Text.translatable("mrc.config.category.toasts.group.playerStrings"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.toasts.group.playerStrings.description")))
                    .binding(Config.playerStrings.asBinding())
                    .controller { FormattedStringControllerBuilder.create(it) }
                    .initial("")
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.category.miscellaneous"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("mrc.config.category.miscellaneous.option.useFlint"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.miscellaneous.option.useFlint.description")))
                    .binding(Config.useFlint.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .flag(OptionFlag.GAME_RESTART)
                    .available(FabricLoader.getInstance().isModLoaded("flint"))
                    .build())
                .also {
                    if (FabricLoader.getInstance().isModLoaded("flint")) return@also
                    it.option(ButtonOption.createBuilder()
                        .name(Text.translatable("mrc.config.category.miscellaneous.option.downloadFlint"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.category.miscellaneous.option.useFlint.description")))
                        .action { screen, _ ->
                            ConfirmLinkScreen.open(screen, "https://modrinth.com/mod/flint")
                        }
                        .build())
                }
                .group(ListOption.createBuilder<String>()
                    .name(Text.translatable("mrc.config.category.miscellaneous.group.plotIds"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.category.miscellaneous.group.plotIds.description")))
                    .binding(Config.plotIds.asBinding())
                    .controller { FormattedStringControllerBuilder.create(it)
                        .valueFormatter { str -> Text.literal(str).also { text ->
                            str.toIntOrNull()?.let { text.formatted(Formatting.AQUA) } ?: text.formatted(Formatting.YELLOW)
                        } }
                    }
                    .initial("")
                    .flag({
                        OnMaceRoulette.fillPlotIds(Config.plotIds.value.toSet())
                    })
                    .available(FabricLoader.getInstance().isModLoaded("flint"))
                    .build())
                .build())
            .save(Config::saveToFile)
            .build()
            .generateScreen(parent)
    }
}