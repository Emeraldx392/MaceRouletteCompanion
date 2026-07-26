package moe.pxe.macecompanion.enums

import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.controllers.ConfigurableEnum
import moe.pxe.macecompanion.stateManagers.AccuracyManager.maceAttempts
import moe.pxe.macecompanion.stateManagers.BountyManager
import moe.pxe.macecompanion.stateManagers.EliminationManager
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersAlive
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.ModifierManager.eternalModifier
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifierBoosters
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifiers
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.fps
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.tps
import moe.pxe.macecompanion.stateManagers.PlotManager
import moe.pxe.macecompanion.stateManagers.RoundManager.gameOngoing
import moe.pxe.macecompanion.stateManagers.RoundManager.maceChance
import moe.pxe.macecompanion.stateManagers.RoundManager.playtime
import moe.pxe.macecompanion.stateManagers.RoundManager.round
import moe.pxe.macecompanion.stateManagers.RoundManager.roundColor
import moe.pxe.macecompanion.stateManagers.StarFragmentManager.starFragments
import moe.pxe.macecompanion.stateManagers.SummerPointsManager.summerColor
import moe.pxe.macecompanion.stateManagers.SummerPointsManager.summerPoints
import moe.pxe.macecompanion.util.PlayerProfile
import moe.pxe.macecompanion.util.PlayerProfile.headFromProfile
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.util.CommonColors
import net.minecraft.util.StringRepresentable
import java.awt.Color
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration

enum class HudElements : NameableEnum, StringRepresentable, ConfigurableEnum {
    ROUND_NUMBER {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (round == -1) return 0
            if (!gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val numberText = Component.literal("$round").setStyle(Config.getRoundNumberAccentStyle(roundColor?.color?.value ?: 0x9ef6fc).withBold(true))
            val text = Component.translatable("mrc.roundhud.round", numberText)
                .setStyle(Config.getRoundTextAccentStyle(roundColor?.color?.value ?: 0x9ef6fc).withBold(true))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset/2f
            if (bottomAligned) yPos = (-yOffset/2f) - 12
            context.pose().pushMatrix()
            context.pose().scale(2f)
            context.pose().translate(xPos.toFloat(), yPos)
            context.text(textRenderer, text, 0, 0, -1)
            context.pose().popMatrix()
            return 24
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.roundNumberConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.description"))).also {
                    val overrideRoundColors = Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.overrideRoundColors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.overrideRoundColors.description")))
                        .binding(Config.overrideRoundColors.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    val roundTextColor = Option.createBuilder<Color>()
                        .name(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundTextColor"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundTextColor.description")))
                        .binding(Config.roundTextColor.asBinding())
                        .controller(ColorControllerBuilder::create)
                        .build()
                    roundTextColor.setAvailable(overrideRoundColors.pendingValue())
                        overrideRoundColors.addEventListener { option, event ->
                        if (event == OptionEventListener.Event.INITIAL) roundTextColor.setAvailable(option.pendingValue())
                        if (event == OptionEventListener.Event.STATE_CHANGE) roundTextColor.setAvailable(option.pendingValue( ))
                    }
                    val roundNumberColor = Option.createBuilder<Color>()
                        .name(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundNumberColor"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundNumberColor.description")))
                        .binding(Config.roundNumberColor.asBinding())
                        .controller(ColorControllerBuilder::create)
                        .build()
                    roundNumberColor.setAvailable(overrideRoundColors.pendingValue())
                        overrideRoundColors.addEventListener { option, event ->
                        if (event == OptionEventListener.Event.INITIAL) roundNumberColor.setAvailable(option.pendingValue())
                        if (event == OptionEventListener.Event.STATE_CHANGE) roundNumberColor.setAvailable(option.pendingValue( ))
                    }
                    it.option(overrideRoundColors)
                    it.option(roundTextColor)
                    it.option(roundNumberColor)
                }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },

    PLAYERS_ALIVE {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (playersAlive == -1) return 0
            if (!gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val countText = Component.literal("$playersAlive").setStyle(Config.getAlivePLayersAccentStyle(0xd5fcf5))
            if (playersTotal >= 0) countText.append(Component.literal("/$playersTotal").setStyle(Config.getTotalPLayersAccentStyle(0xd0d0d0)))
            val text = Component.translatable("mrc.roundhud.alive", countText).setStyle(Config.getPlayerCountTextAccentStyle(CommonColors.WHITE))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.playersAliveConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.description"))).also {
                        val overridePlayerCountColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.overridePlayerCountColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.overridePlayerCountColors.description")))
                            .binding(Config.overridePlayerCountColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val playerCountTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.playerCountTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.playerCountTextColor.description")))
                            .binding(Config.playerCountTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playerCountTextColor.setAvailable(overridePlayerCountColors.pendingValue())
                        overridePlayerCountColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playerCountTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playerCountTextColor.setAvailable(option.pendingValue( ))
                        }
                        val alivePlayersColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.alivePlayersColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.alivePlayersColor.description")))
                            .binding(Config.alivePlayersColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        alivePlayersColor.setAvailable(overridePlayerCountColors.pendingValue())
                        overridePlayerCountColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) alivePlayersColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) alivePlayersColor.setAvailable(option.pendingValue( ))
                        }
                        val totalPlayersColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.totalPlayersColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.totalPlayersColor.description")))
                            .binding(Config.totalPlayersColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        totalPlayersColor.setAvailable(overridePlayerCountColors.pendingValue())
                        overridePlayerCountColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) totalPlayersColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) totalPlayersColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overridePlayerCountColors)
                        it.option(playerCountTextColor)
                        it.option(alivePlayersColor)
                        it.option(totalPlayersColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },

    ACCURACY {
        val textColors = arrayOf(0xff2c01, 0xff5500, 0xff8400, 0xffa503, 0xffd202, 0xfff400, 0xe6ff01, 0xc0ff03, 0x92ff00, 0x74ff02, 0x3cff01, 0x13ff00, 0x01ff00)

        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (maceAttempts.isEmpty()) return 0
            if (!gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val totalMaceAttempts = maceAttempts.size
            val successfulMaceAttempts = maceAttempts.count { it.value }
            val accuracy = (successfulMaceAttempts.toFloat() / totalMaceAttempts.toFloat() * 100).roundToInt()
            var accuracyColorIdx = (accuracy / 7.7).toInt().absoluteValue

            val iconText = Component.literal("\uD83C\uDFF9 ").setStyle(Config.getAccuracyIconAccentStyle(0x79fc00))
            val countText = Component.literal("$successfulMaceAttempts/$totalMaceAttempts").setStyle(Config.getAccuracyTextAccentStyle(0x79fc00))
            val percentageText = Component.literal("$accuracy%").setStyle(Config.getAccuracyAccentStyle(textColors[accuracyColorIdx]))
            val text = Component.translatable("mrc.roundhud.accuracy", percentageText, countText).setStyle(Config.getAccuracyTextAccentStyle(0x79fc00))
            val finalText = iconText.append(text)
            val width = textRenderer.width(finalText)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, finalText, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.accuracyConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.description"))).also {
                        val overrideAccuracyColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.overrideAccuracyColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.overrideAccuracyColors.description")))
                            .binding(Config.overrideAccuracyColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val accuracyTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.accuracyTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.accuracyTextColor.description")))
                            .binding(Config.accuracyTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        accuracyTextColor.setAvailable(overrideAccuracyColors.pendingValue())
                        overrideAccuracyColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) accuracyTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) accuracyTextColor.setAvailable(option.pendingValue( ))
                        }
                        val accuracyColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.accuracyColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.accuracyColor.description")))
                            .binding(Config.accuracyColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        accuracyColor.setAvailable(overrideAccuracyColors.pendingValue())
                        overrideAccuracyColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) accuracyColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) accuracyColor.setAvailable(option.pendingValue( ))
                        }
                        val accuracyIconColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.accuracyIconColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.accuracyConfig.category.styling.group.colors.option.accuracyIconColor.description")))
                            .binding(Config.accuracyIconColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        accuracyIconColor.setAvailable(overrideAccuracyColors.pendingValue())
                        overrideAccuracyColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) accuracyIconColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) accuracyIconColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overrideAccuracyColors)
                        it.option(accuracyTextColor)
                        it.option(accuracyColor)
                        it.option(accuracyIconColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },


    ELIMINATIONS {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (EliminationManager.eliminations == -1) return 0
            if (!gameOngoing) return 0
            if(Config.hideEliminationsWhenEliminated.value && EliminationManager.eliminated) return 0

            val textRenderer = Minecraft.getInstance().font
            val textIcon = Component.literal("\uD83E\uDE93 ").setStyle(Config.getEliminationsIconAccentStyle(0xa63efc))
            val text = textIcon.append(Component.translatable("mrc.roundhud.eliminations", Component.literal("${EliminationManager.eliminations}")
                .setStyle(Config.getEliminationsNumberAccentStyle(0xa63efc)))
                .setStyle(Config.getEliminationsTextAccentStyle(0xa63efc)))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.eliminationsConfig.category.misc"))
                .option(Option.createBuilder<Boolean>()
                    .name(Component.translatable("mrc.config.eliminationsConfig.category.misc.option.hideEliminationsWhenEliminated"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.eliminationsConfig.category.misc.option.hideEliminationsWhenEliminated.description")))
                    .binding(Config.hideEliminationsWhenEliminated.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.eliminationsConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.description"))).also {
                        val overrideEliminationsColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.overrideEliminationsColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.overrideEliminationsColors.description")))
                            .binding(Config.overrideEliminationsColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val eliminationsTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsTextColor.description")))
                            .binding(Config.eliminationsTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        eliminationsTextColor.setAvailable(overrideEliminationsColors.pendingValue())
                        overrideEliminationsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) eliminationsTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) eliminationsTextColor.setAvailable(option.pendingValue( ))
                        }
                        val eliminationsNumberColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsNumberColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsNumberColor.description")))
                            .binding(Config.eliminationsNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        eliminationsNumberColor.setAvailable(overrideEliminationsColors.pendingValue())
                        overrideEliminationsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) eliminationsNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) eliminationsNumberColor.setAvailable(option.pendingValue( ))
                        }
                        val eliminationsIconColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsIconColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsIconColor.description")))
                            .binding(Config.eliminationsIconColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        eliminationsIconColor.setAvailable(overrideEliminationsColors.pendingValue())
                        overrideEliminationsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) eliminationsIconColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) eliminationsIconColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overrideEliminationsColors)
                        it.option(eliminationsTextColor)
                        it.option(eliminationsNumberColor)
                        it.option(eliminationsIconColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },
    SUMMER_POINTS {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (summerPoints == -1) return 0
            if (!gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val textIcon = Component.literal("⚑ ").setStyle(Config.getSummerPointsIconAccentStyle(summerColor))
            val text = textIcon.append(Component.translatable("mrc.roundhud.summer_points", Component.literal("$summerPoints")
                .setStyle(Config.getSummerPointsNumberAccentStyle(summerColor)))
                .setStyle(Config.getSummerPointsTextAccentStyle(summerColor)))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }
    },
    STAR_FRAGMENTS {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (starFragments == -1) return 0
            if (!gameOngoing) return 0
            if(PlotManager.isStatless) return 0
            if(Config.hideStarFragmentsWhenEliminated.value && EliminationManager.eliminated) return 0

            val textRenderer = Minecraft.getInstance().font
            val json = JsonObject()
            json.addProperty("atlas", "minecraft:particles")
            json.addProperty("sprite", "spark_2")
            val starFragment = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
            val starFragmentIconText = Component.empty().append(starFragment).setStyle(Config.getStarFragmentsIconAccentStyle(0xa0f9ff))
            var text = starFragmentIconText.append(Component.translatable("mrc.roundhud.starFragments", Component.literal(" $starFragments")
                .setStyle(Config.getStarFragmentsNumberAccentStyle(0xa0f9ff)))
                .setStyle(Config.getStarFragmentsTextAccentStyle(0xa0f9ff)))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.starFragmentsConfig.category.misc"))
                .option(Option.createBuilder<Boolean>()
                    .name(Component.translatable("mrc.config.starFragmentsConfig.category.misc.option.hideStarFragmentsWhenEliminated"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.starFragmentsConfig.category.misc.option.hideStarFragmentsWhenEliminated.description")))
                    .binding(Config.hideStarFragmentsWhenEliminated.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.starFragmentsConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.description"))).also {
                        val overrideStarFragmentsColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.overrideStarFragmentsColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.overrideStarFragmentsColors.description")))
                            .binding(Config.overrideStarFragmentsColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val starFragmentsTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsTextColor.description")))
                            .binding(Config.starFragmentsTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        starFragmentsTextColor.setAvailable(overrideStarFragmentsColors.pendingValue())
                        overrideStarFragmentsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) starFragmentsTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) starFragmentsTextColor.setAvailable(option.pendingValue( ))
                        }
                        val starFragmentsNumberColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsNumberColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsNumberColor.description")))
                            .binding(Config.starFragmentsNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        starFragmentsNumberColor.setAvailable(overrideStarFragmentsColors.pendingValue())
                        overrideStarFragmentsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) starFragmentsNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) starFragmentsNumberColor.setAvailable(option.pendingValue( ))
                        }
                        val starFragmentsIconColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsIconColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsIconColor.description")))
                            .binding(Config.starFragmentsIconColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        starFragmentsIconColor.setAvailable(overrideStarFragmentsColors.pendingValue())
                        overrideStarFragmentsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) starFragmentsIconColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) starFragmentsIconColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overrideStarFragmentsColors)
                        it.option(starFragmentsTextColor)
                        it.option(starFragmentsNumberColor)
                        it.option(starFragmentsIconColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },

    PLAYTIME {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (!gameOngoing) return 0

            playtime?.let {
                val textRenderer = Minecraft.getInstance().font
                val text = Component.literal("⌚ ").setStyle(Config.getPlaytimeIconAccentStyle(0x3efca1)).append(Component.translatable("mrc.roundhud.playtime", Component.literal(it.elapsedNow().toLong(DurationUnit.SECONDS).toDuration(DurationUnit.SECONDS).toString()).setStyle(Config.getPlaytimeNumberAccentStyle(0x3efca1)))
                    .setStyle(Config.getPlaytimeTextAccentStyle(0x3efca1)))
                val width = textRenderer.width(text)
                var xPos = 0
                if (rightAligned) xPos = -width
                var yPos = yOffset
                if (bottomAligned) yPos = -yOffset - 12
                context.text(textRenderer, text, xPos, yPos, -1)
                return 12
            }
            return 0
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.playtimeConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.description"))).also {
                        val overridePlaytimeColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.overridePlaytimeColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.overridePlaytimeColors.description")))
                            .binding(Config.overridePlaytimeColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val playtimeTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeTextColor.description")))
                            .binding(Config.playtimeTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playtimeTextColor.setAvailable(overridePlaytimeColors.pendingValue())
                        overridePlaytimeColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playtimeTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playtimeTextColor.setAvailable(option.pendingValue( ))
                        }
                        val playtimeColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeColor.description")))
                            .binding(Config.playtimeColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playtimeColor.setAvailable(overridePlaytimeColors.pendingValue())
                        overridePlaytimeColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playtimeColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playtimeColor.setAvailable(option.pendingValue( ))
                        }
                        val playtimeIconColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeIconColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeIconColor.description")))
                            .binding(Config.playtimeIconColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playtimeIconColor.setAvailable(overridePlaytimeColors.pendingValue())
                        overridePlaytimeColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playtimeIconColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playtimeIconColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overridePlaytimeColors)
                        it.option(playtimeTextColor)
                        it.option(playtimeColor)
                        it.option(playtimeIconColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },

    MODIFIERS {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (!gameOngoing) return 0
            if (modifiers.isEmpty()) return 0

            val textRenderer = Minecraft.getInstance().font
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12 - (modifiers.size*20)

            val headerText = Component.translatable("mrc.roundhud.modifiers")
                .setStyle(Config.getModifiersTextAccentStyle(0xa63efc))
            val headerWidth = textRenderer.width(headerText)
            var xPos = 0
            if (rightAligned) xPos = -headerWidth
            context.text(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12

            modifiers.forEach { (modifier, isCharged) ->
                var xPos = 0
                if (rightAligned) xPos = -16
                if(!Config.customModifierIcons.value) {
                    context.item(modifier.icon, xPos, yPos)
                    context.itemDecorations(textRenderer, modifier.icon, xPos, yPos)
                }else {
                    context.item(modifier.customIcon, xPos, yPos)
                    context.itemDecorations(textRenderer, modifier.customIcon, xPos, yPos)
                }
                var modifierText = modifier.translatable.copy().setStyle(Config.getNormalModifierTextAccentStyle(CommonColors.YELLOW))
                if (eternalModifier == modifier && isCharged) {
                    modifierText.style = Config.getChargedModifierTextAccentStyle(0x0786FF)
                    if(!rightAligned) modifierText.append(Component.literal(" ⚡").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF))).append(Component.literal("∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549)))
                    if(rightAligned) modifierText = Component.literal("∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549)).append(Component.literal("⚡ ").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF).withShadowColor(-16777216)).append(modifierText))
                }else if (eternalModifier == modifier) {
                    modifierText.style = Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549)
                    if(!rightAligned) modifierText.append(Component.literal(" ∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549)))
                    if(rightAligned) modifierText = Component.literal("∞ ").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(CommonColors.WHITE, -10071549)).append(modifierText)
                }else if (isCharged) {
                    modifierText.style = Config.getChargedModifierTextAccentStyle(0x0786FF)
                    if(!rightAligned) modifierText.append(Component.literal(" ⚡").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF)))
                    if(rightAligned) modifierText = Component.literal("⚡ ").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF)).append(modifierText)
                }
                var headText2d = if (Config.use2dHeads.value) modifierBoosters[modifier]?.let { playerList ->
                    PlayerProfile.player2dHeadTextComponentList(playerList, Config.boosterListMax.value, rightAligned)
                } ?: Component.literal("") else Component.literal("")
                val finalText = if (rightAligned) Component.empty().append(headText2d).append(modifierText)
                else Component.empty().append(modifierText).append(headText2d)
                val modifierWidth = textRenderer.width(finalText)
                xPos = 22
                if (rightAligned) xPos = -22 - modifierWidth
                context.text(textRenderer, finalText, xPos, yPos+4, -1)

                if (!Config.use2dHeads.value) modifierBoosters[modifier]?.let { playerList ->
                    val bonusBoostersAmount = playerList.size - Config.boosterListMax.value
                    playerList.forEachIndexed { index, profile ->
                        if(index < Config.boosterListMax.value) {
                            xPos = 28 + modifierWidth + (index * 15)
                            if (rightAligned) xPos = -44 - modifierWidth - (index * 15)
                            context.item(headFromProfile(profile), xPos, yPos)
                        }
                    }
                    if (rightAligned) xPos -= 15
                    else xPos += 15
                    if(bonusBoostersAmount > 0) context.text(textRenderer, Component.literal("+${bonusBoostersAmount}").setStyle(Config.getModifiersTextAccentStyle(0xa63efc)), xPos, yPos + 4, -1)
                }

                yPos += 20
            }

            return 12+(modifiers.size*20)
        }

        override fun generateConfig(parent: Screen): Screen? {
            return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
                .category(ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.modifiersConfig.category.misc"))
                    .option(Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.modifiersConfig.category.misc.option.boosterListMax"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.misc.option.boosterListMax.description")))
                        .binding(Config.boosterListMax.asBinding())
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 15)
                                .step(1)
                        }
                        .build())
                    .build())
                .category(ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.modifiersConfig.category.styling"))
                    .group(OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.description"))).also {
                            val overrideModifiersColors = Option.createBuilder<Boolean>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.overrideModifiersColors"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.overrideModifiersColors.description")))
                                .binding(Config.overrideModifiersColors.asBinding())
                                .controller(TickBoxControllerBuilder::create)
                                .build()
                            val modifiersTextColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.modifiersTextColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.modifiersTextColor.description")))
                                .binding(Config.modifiersTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            modifiersTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) modifiersTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) modifiersTextColor.setAvailable(option.pendingValue( ))
                            }
                            val normalModifierTextColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.normalModifierTextColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.normalModifierTextColor.description")))
                                .binding(Config.normalModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            normalModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) normalModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) normalModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            val eternalModifierTextColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextColor.description")))
                                .binding(Config.eternalModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            eternalModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) eternalModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) eternalModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            val eternalModifierTextShadowColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextShadowColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextShadowColor.description")))
                                .binding(Config.eternalModifierTextShadowColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            eternalModifierTextShadowColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) eternalModifierTextShadowColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) eternalModifierTextShadowColor.setAvailable(option.pendingValue( ))
                            }
                            val chargedModifierTextColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.chargedModifierTextColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.chargedModifierTextColor.description")))
                                .binding(Config.chargedModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            chargedModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) chargedModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) chargedModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            val mysteryModifierTextColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.mysteryModifierTextColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.mysteryModifierTextColor.description")))
                                .binding(Config.mysteryModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            mysteryModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) mysteryModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) mysteryModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            it.option(overrideModifiersColors)
                            it.option(modifiersTextColor)
                            it.option(normalModifierTextColor)
                            it.option(eternalModifierTextColor)
                            it.option(eternalModifierTextShadowColor)
                            it.option(chargedModifierTextColor)
                            it.option(mysteryModifierTextColor)
                        }
                        .build())
                    .group(OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.icons"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.icons.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.customModifierIcons"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.customModifierIcons.description")))
                        .binding(Config.customModifierIcons.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Component.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.use2dHeads"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.use2dHeads.description")))
                        .binding(Config.use2dHeads.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .build())
                .save(Config::saveToFile)
                .build()
                .generateScreen(parent)
        }
    },
    MACE_CHANCE {
        val textColors = arrayOf(0xff2c01, 0xff5500, 0xff8400, 0xffa503, 0xffd202, 0xfff400, 0xe6ff01, 0xc0ff03, 0x92ff00, 0x74ff02, 0x3cff01, 0x13ff00, 0x01ff00)

        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (!gameOngoing) return 0
            if (maceChance < 0f) return 0
            if(Config.hideMaceChanceWhenEliminated.value && EliminationManager.eliminated) return 0

            val textRenderer = Minecraft.getInstance().font
            var maceChanceColorIdx = (maceChance / 7.7).toInt().absoluteValue
            if (maceChanceColorIdx > 12) maceChanceColorIdx = 12
            val text = Component.literal("⚄ ").setStyle(Config.getMaceChanceIconAccentStyle(0x42C1FF))
                .append(Component.translatable("mrc.roundhud.mace_chance",
                Component.literal("%.2f%%".format(maceChance))
                    .setStyle(Config.getMaceChanceNumberAccentStyle(textColors[maceChanceColorIdx])))
                .setStyle(Config.getMaceChanceTextAccentStyle(0x42C1FF)))

            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.maceChanceConfig.category.misc"))
                .option(Option.createBuilder<Boolean>()
                    .name(Component.translatable("mrc.config.maceChanceConfig.category.misc.option.hideMaceChanceWhenEliminated"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.maceChanceConfig.category.misc.option.hideMaceChanceWhenEliminated.description")))
                    .binding(Config.hideMaceChanceWhenEliminated.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.maceChanceConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.description"))).also {
                        val overrideMaceChanceColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.overrideMaceChanceColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.overrideMaceChanceColors.description")))
                            .binding(Config.overrideMaceChanceColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val maceChanceTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceTextColor.description")))
                            .binding(Config.maceChanceTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        maceChanceTextColor.setAvailable(overrideMaceChanceColors.pendingValue())
                        overrideMaceChanceColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) maceChanceTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) maceChanceTextColor.setAvailable(option.pendingValue( ))
                        }
                        val maceChanceNumberColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceNumberColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceNumberColor.description")))
                            .binding(Config.maceChanceNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        maceChanceNumberColor.setAvailable(overrideMaceChanceColors.pendingValue())
                        overrideMaceChanceColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) maceChanceNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) maceChanceNumberColor.setAvailable(option.pendingValue( ))
                        }
                        val maceChanceIconColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceIconColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceIconColor.description")))
                            .binding(Config.maceChanceIconColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        maceChanceIconColor.setAvailable(overrideMaceChanceColors.pendingValue())
                        overrideMaceChanceColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) maceChanceIconColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) maceChanceIconColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overrideMaceChanceColors)
                        it.option(maceChanceTextColor)
                        it.option(maceChanceNumberColor)
                        it.option(maceChanceIconColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },
    BOUNTY_BOARD {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (!gameOngoing) return 0
            if (BountyManager.bounties.isEmpty()) return 0
            if (EliminationManager.eliminated) return 0

            val textRenderer = Minecraft.getInstance().font
            var yPos = yOffset
            val sortedBounties = BountyManager.bounties.entries
                .sortedByDescending { it.value }
                .associate { it.key to it.value }
            if (bottomAligned) yPos = -yOffset - 12 - (sortedBounties.size * 20)

            val headerText = Component.translatable("mrc.roundhud.bounty_board")
                .setStyle(Config.getBountyBoardTextAccentStyle(0xff7cf4))
            val headerWidth = textRenderer.width(headerText)
            var xPos = 0
            if (rightAligned) xPos = -headerWidth
            context.text(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12
            var index = 1
            var bountyCount = 0
            sortedBounties.forEach { (profile, bountyAmount) ->
                if(bountyAmount >= Config.bountyBoardMinBounty.value && index <=  Config.bountyBoardMaxPlayers.value) {
                    val playerUsername = profile.name
                    var xPos = 0
                    if (rightAligned) xPos = -16
                    context.item(headFromProfile(profile), xPos, yPos)
                    val playerText =
                        Component.literal("$playerUsername").setStyle(Config.getBountyBoardPlayerAccentStyle(CommonColors.YELLOW))
                    val bountyText =
                        Component.literal("$bountyAmount⛂").setStyle(Config.getBountyBoardAmountAccentStyle(0xff7cf4))
                    val finalText = if (rightAligned) {
                        Component.empty()
                            .append(bountyText)
                            .append(" ")
                            .append(playerText)
                    } else {
                        Component.empty()
                            .append(playerText)
                            .append(" ")
                            .append(bountyText)
                    }
                    val modifierWidth = textRenderer.width(finalText)
                    xPos = 22
                    if (rightAligned) xPos = -22 - modifierWidth
                    context.text(textRenderer, finalText, xPos, yPos + 4, -1)
                    yPos += 20
                    bountyCount++
                    index++
                }
            }
            return 12 + (bountyCount * 20)
        }
        override fun generateConfig(parent: Screen): Screen? {
            return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
                .category(
                    ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.bountyBoardConfig.category.misc"))
                    .option(
                        Option.createBuilder<Int>()
                        .name(Component.translatable("mrc.config.bountyBoardConfig.category.misc.option.bountyBoardMaxPlayers"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.misc.option.bountyBoardMaxPlayers.description")))
                        .binding(Config.bountyBoardMaxPlayers.asBinding())
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(1, 15)
                                .step(1)
                        }
                        .build())
                        .option(
                            Option.createBuilder<Int>()
                                .name(Component.translatable("mrc.config.bountyBoardConfig.category.misc.option.bountyBoardMinBounty"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.misc.option.bountyBoardMinBounty.description")))
                                .binding(Config.bountyBoardMinBounty.asBinding())
                                .controller {
                                    IntegerSliderControllerBuilder.create(it)
                                        .range(1, 10)
                                        .step(1)
                                }
                                .build())
                    .build())
                .category(
                    ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.bountyBoardConfig.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                        .name(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors"))
                        .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.description")))
                        .also {
                            val overrideBountyBoardColors = Option.createBuilder<Boolean>()
                                .name(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.overrideBountyBoardColors"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.overrideBountyBoardColors.description")))
                                .binding(Config.overrideBountyBoardColors.asBinding())
                                .controller(TickBoxControllerBuilder::create)
                                .build()
                            val bountyBoardTextColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.bountyBoardTextColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.bountyBoardTextColor.description")))
                                .binding(Config.bountyBoardTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            bountyBoardTextColor.setAvailable(overrideBountyBoardColors.pendingValue())
                            overrideBountyBoardColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) bountyBoardTextColor.setAvailable(
                                    option.pendingValue()
                                )
                                if (event == OptionEventListener.Event.STATE_CHANGE) bountyBoardTextColor.setAvailable(
                                    option.pendingValue()
                                )
                            }
                            val bountyBoardPlayerColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.bountyBoardPlayerColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.bountyBoardPlayerColor.description")))
                                .binding(Config.bountyBoardPlayerColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            bountyBoardPlayerColor.setAvailable(overrideBountyBoardColors.pendingValue())
                            overrideBountyBoardColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) bountyBoardPlayerColor.setAvailable(
                                    option.pendingValue()
                                )
                                if (event == OptionEventListener.Event.STATE_CHANGE) bountyBoardPlayerColor.setAvailable(
                                    option.pendingValue()
                                )
                            }
                            val bountyBoardNumberColor = Option.createBuilder<Color>()
                                .name(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.bountyBoardNumberColor"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.bountyBoardConfig.category.styling.group.colors.option.bountyBoardNumberColor.description")))
                                .binding(Config.bountyBoardNumberColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            bountyBoardNumberColor.setAvailable(overrideBountyBoardColors.pendingValue())
                            overrideBountyBoardColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) bountyBoardNumberColor.setAvailable(
                                    option.pendingValue()
                                )
                                if (event == OptionEventListener.Event.STATE_CHANGE) bountyBoardNumberColor.setAvailable(
                                    option.pendingValue()
                                )
                            }
                            it.option(overrideBountyBoardColors)
                            it.option(bountyBoardTextColor)
                            it.option(bountyBoardPlayerColor)
                            it.option(bountyBoardNumberColor)
                        }
                        .build())
                    .build())
                .save(Config::saveToFile)
                .build()
                .generateScreen(parent)
        }
    },
    FPS {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (fps == -1) return 0

            val textRenderer = Minecraft.getInstance().font
            val text = Component.translatable("mrc.roundhud.fps", Component.literal("$fps").setStyle(Config.getFpsNumberAccentStyle(CommonColors.WHITE))).setStyle(Config.getFpsTextAccentStyle(CommonColors.WHITE))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.fpsConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.description"))).also {
                        val overrideFpsColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.option.overrideFpsColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.option.overrideFpsColors.description")))
                            .binding(Config.overrideFpsColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val fpsTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.option.fpsTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.option.fpsTextColor.description")))
                            .binding(Config.fpsTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        fpsTextColor.setAvailable(overrideFpsColors.pendingValue())
                        overrideFpsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) fpsTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) fpsTextColor.setAvailable(option.pendingValue( ))
                        }
                        val fpsNumberColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.option.fpsNumberColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.fpsConfig.category.styling.group.colors.option.fpsNumberColor.description")))
                            .binding(Config.fpsNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        fpsNumberColor.setAvailable(overrideFpsColors.pendingValue())
                        overrideFpsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) fpsNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) fpsNumberColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overrideFpsColors)
                        it.option(fpsTextColor)
                        it.option(fpsNumberColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },
    PING {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            val networkHandler = Minecraft.getInstance().connection
            val client = Minecraft.getInstance()
            val ping = networkHandler?.getPlayerInfo(client.user.name)?.latency ?: return 0
            if (ping <= 0) return 0
            val pingColor = if (ping < 50) 0x1eff00 else if (ping < 100) 0xfff100 else if (ping < 200) 0xff9500 else 0xff3b3b
            val textRenderer = Minecraft.getInstance().font
            val text = Component.translatable("mrc.roundhud.ping", Component.literal("$ping").setStyle(Config.getPingNumberAccentStyle(pingColor))).setStyle(Config.getPingTextAccentStyle(pingColor))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.pingConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.pingConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.description"))).also {
                        val overridePingColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.option.overridePingColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.option.overridePingColors.description")))
                            .binding(Config.overridePingColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val pingTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.option.pingTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.option.pingTextColor.description")))
                            .binding(Config.pingTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        pingTextColor.setAvailable(overridePingColors.pendingValue())
                        overridePingColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) pingTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) pingTextColor.setAvailable(option.pendingValue( ))
                        }
                        val pingNumberColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.option.pingNumberColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.pingConfig.category.styling.group.colors.option.pingNumberColor.description")))
                            .binding(Config.pingNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        pingNumberColor.setAvailable(overridePingColors.pendingValue())
                        overridePingColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) pingNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) pingNumberColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overridePingColors)
                        it.option(pingTextColor)
                        it.option(pingNumberColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },
    TPS {
        override fun render(
            context: GuiGraphicsExtractor,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (tps == -1f) return 0

            val textRenderer = Minecraft.getInstance().font
            val text = Component.translatable("mrc.roundhud.tps", Component.literal("$tps").setStyle(Config.getTpsNumberAccentStyle(0xbfff00))).setStyle(Config.getTpsTextAccentStyle(0xbfff00))
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.text(textRenderer, text, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Component.translatable("mrc.config.tpsConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.description"))).also {
                        val overrideTpsColors = Option.createBuilder<Boolean>()
                            .name(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.option.overrideTpsColors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.option.overrideTpsColors.description")))
                            .binding(Config.overrideTpsColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val tpsTextColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.option.tpsTextColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.option.tpsTextColor.description")))
                            .binding(Config.tpsTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        tpsTextColor.setAvailable(overrideTpsColors.pendingValue())
                        overrideTpsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) tpsTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) tpsTextColor.setAvailable(option.pendingValue( ))
                        }
                        val tpsNumberColor = Option.createBuilder<Color>()
                            .name(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.option.tpsNumberColor"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.option.tpsNumberColor.description")))
                            .binding(Config.tpsNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        tpsNumberColor.setAvailable(overrideTpsColors.pendingValue())
                        overrideTpsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) tpsNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) tpsNumberColor.setAvailable(option.pendingValue( ))
                        }
                        it.option(overrideTpsColors)
                        it.option(tpsTextColor)
                        it.option(tpsNumberColor)
                    }
                    .build())
                .build())
            .build()
            .generateScreen(parent)
    },;

    abstract fun render(context: GuiGraphicsExtractor, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int
    override fun generateConfig(parent: Screen): Screen? = null
    override fun getSerializedName(): String = name
    override fun getDisplayName(): Component = Component.translatable("mrc.hudelement.${name.lowercase()}")

    companion object {
            val CODEC = StringRepresentable.fromEnum(::values)
    }
}
