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
import moe.pxe.macecompanion.StateManager
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.controllers.ConfigurableEnum
import moe.pxe.macecompanion.util.PlayerHead
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.text.TextColor
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import net.minecraft.util.StringIdentifiable
import java.awt.Color
import kotlin.math.absoluteValue
import kotlin.time.DurationUnit
import kotlin.time.toDuration

enum class HudElements : NameableEnum, StringIdentifiable, ConfigurableEnum {
    ROUND_NUMBER {
        override fun render(
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (StateManager.round == -1) return 0

            val textRenderer = MinecraftClient.getInstance().textRenderer
            val numberText = Text.literal("${StateManager.round}").setStyle(Config.getRoundNumberAccentStyle(StateManager.roundColor.color!!.rgb).withBold(true))
            val text = Text.translatable("mrc.roundhud.round", numberText)
                .setStyle(Config.getRoundTextAccentStyle(StateManager.roundColor.color!!.rgb).withBold(true))
            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset/2f
            if (bottomAligned) yPos = (-yOffset/2f) - 12
            context.matrices.pushMatrix()
            context.matrices.scale(2f)
            context.matrices.translate(xPos.toFloat(), yPos)
            context.drawTextWithShadow(textRenderer, text, 0, 0, -1)
            context.matrices.popMatrix()
            return 24
        }
        override fun generateConfig(parent: Screen) = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.roundNumberConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.description"))).also {
                    val overrideRoundColors = Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.overrideRoundColors"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.overrideRoundColors.description")))
                        .binding(Config.overrideRoundColors.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build()
                    val roundTextColor = Option.createBuilder<Color>()
                        .name(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundTextColor"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundTextColor.description")))
                        .binding(Config.roundTextColor.asBinding())
                        .controller(ColorControllerBuilder::create)
                        .build()
                    roundTextColor.setAvailable(overrideRoundColors.pendingValue())
                        overrideRoundColors.addEventListener { option, event ->
                        if (event == OptionEventListener.Event.INITIAL) roundTextColor.setAvailable(option.pendingValue())
                        if (event == OptionEventListener.Event.STATE_CHANGE) roundTextColor.setAvailable(option.pendingValue( ))
                    }
                    val roundNumberColor = Option.createBuilder<Color>()
                        .name(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundNumberColor"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.roundNumberConfig.category.styling.group.colors.option.roundNumberColor.description")))
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
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (StateManager.playersAlive == -1) return 0

            val textRenderer = MinecraftClient.getInstance().textRenderer
            val countText = Text.literal("${StateManager.playersAlive}").setStyle(Config.getAlivePLayersAccentStyle(0xd5fcf5))
            if (StateManager.playersTotal >= 0) countText.append(Text.literal("/${StateManager.playersTotal}").setStyle(Config.getTotalPLayersAccentStyle(0xd0d0d0)))
            val text = Text.translatable("mrc.roundhud.alive", countText).setStyle(Config.getPlayerCountTextAccentStyle(Colors.WHITE))
            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
            return 12
        }
        override fun generateConfig(parent: Screen) = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.playersAliveConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.description"))).also {
                        val overridePlayerCountColors = Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.overridePlayerCountColors"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.overridePlayerCountColors.description")))
                            .binding(Config.overridePlayerCountColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val playerCountTextColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.playerCountTextColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.playerCountTextColor.description")))
                            .binding(Config.playerCountTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playerCountTextColor.setAvailable(overridePlayerCountColors.pendingValue())
                        overridePlayerCountColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playerCountTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playerCountTextColor.setAvailable(option.pendingValue( ))
                        }
                        val alivePlayersColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.alivePlayersColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.alivePlayersColor.description")))
                            .binding(Config.alivePlayersColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        alivePlayersColor.setAvailable(overridePlayerCountColors.pendingValue())
                        overridePlayerCountColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) alivePlayersColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) alivePlayersColor.setAvailable(option.pendingValue( ))
                        }
                        val totalPlayersColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.totalPlayersColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playersAliveConfig.category.styling.group.colors.option.totalPlayersColor.description")))
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

    ELIMINATIONS {
        override fun render(
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (StateManager.eliminations == -1) return 0
            if(Config.hideEliminationsWhenEliminated.value && StateManager.eliminated) return 0

            val textRenderer = MinecraftClient.getInstance().textRenderer
            val textIcon = Text.literal("\uD83E\uDE93 ").setStyle(Config.getEliminationsIconAccentStyle(0xa63efc))
            val text = textIcon.append(Text.translatable("mrc.roundhud.eliminations", Text.literal("${StateManager.eliminations}")
                .setStyle(Config.getEliminationsNumberAccentStyle(0xa63efc)))
                .setStyle(Config.getEliminationsTextAccentStyle(0xa63efc)))
            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen) = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.eliminationsConfig.category.misc"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("mrc.config.eliminationsConfig.category.misc.option.hideEliminationsWhenEliminated"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.eliminationsConfig.category.misc.option.hideEliminationsWhenEliminated.description")))
                    .binding(Config.hideEliminationsWhenEliminated.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.eliminationsConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.description"))).also {
                        val overrideEliminationsColors = Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.overrideEliminationsColors"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.overrideEliminationsColors.description")))
                            .binding(Config.overrideEliminationsColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val eliminationsTextColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsTextColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsTextColor.description")))
                            .binding(Config.eliminationsTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        eliminationsTextColor.setAvailable(overrideEliminationsColors.pendingValue())
                        overrideEliminationsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) eliminationsTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) eliminationsTextColor.setAvailable(option.pendingValue( ))
                        }
                        val eliminationsNumberColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsNumberColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsNumberColor.description")))
                            .binding(Config.eliminationsNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        eliminationsNumberColor.setAvailable(overrideEliminationsColors.pendingValue())
                        overrideEliminationsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) eliminationsNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) eliminationsNumberColor.setAvailable(option.pendingValue( ))
                        }
                        val eliminationsIconColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsIconColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.eliminationsConfig.category.styling.group.colors.option.eliminationsIconColor.description")))
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

    STAR_FRAGMENTS {
        override fun render(
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (StateManager.starFragments == -1) return 0
            if(Config.hideStarFragmentsWhenEliminated.value && StateManager.eliminated) return 0

            val textRenderer = MinecraftClient.getInstance().textRenderer
            val json = JsonObject()
            json.addProperty("atlas", "minecraft:particles")
            json.addProperty("sprite", "spark_2")
            val starFragment = TextCodecs.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
            val starFragmentIconText = Text.empty().append(starFragment).setStyle(Config.getStarFragmentsIconAccentStyle(0xa0f9ff))
            var text = starFragmentIconText.append(Text.translatable("mrc.roundhud.starFragments", Text.literal(" ${StateManager.starFragments}")
                .setStyle(Config.getStarFragmentsNumberAccentStyle(0xa0f9ff)))
                .setStyle(Config.getStarFragmentsTextAccentStyle(0xa0f9ff)))
            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen) = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.starFragmentsConfig.category.misc"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("mrc.config.starFragmentsConfig.category.misc.option.hideStarFragmentsWhenEliminated"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.starFragmentsConfig.category.misc.option.hideStarFragmentsWhenEliminated.description")))
                    .binding(Config.hideStarFragmentsWhenEliminated.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.starFragmentsConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.description"))).also {
                        val overrideStarFragmentsColors = Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.overrideStarFragmentsColors"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.overrideStarFragmentsColors.description")))
                            .binding(Config.overrideStarFragmentsColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val starFragmentsTextColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsTextColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsTextColor.description")))
                            .binding(Config.starFragmentsTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        starFragmentsTextColor.setAvailable(overrideStarFragmentsColors.pendingValue())
                        overrideStarFragmentsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) starFragmentsTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) starFragmentsTextColor.setAvailable(option.pendingValue( ))
                        }
                        val starFragmentsNumberColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsNumberColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsNumberColor.description")))
                            .binding(Config.starFragmentsNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        starFragmentsNumberColor.setAvailable(overrideStarFragmentsColors.pendingValue())
                        overrideStarFragmentsColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) starFragmentsNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) starFragmentsNumberColor.setAvailable(option.pendingValue( ))
                        }
                        val starFragmentsIconColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsIconColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.starFragmentsConfig.category.styling.group.colors.option.starFragmentsIconColor.description")))
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
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            StateManager.playtime?.let {
                val textRenderer = MinecraftClient.getInstance().textRenderer
                val text = Text.literal("⌚ ").setStyle(Config.getPlaytimeIconAccentStyle(0x3efca1)).append(Text.translatable("mrc.roundhud.playtime", Text.literal(it.elapsedNow().toLong(DurationUnit.SECONDS).toDuration(DurationUnit.SECONDS).toString()).setStyle(Config.getPlaytimeNumberAccentStyle(0x3efca1)))
                    .setStyle(Config.getPlaytimeTextAccentStyle(0x3efca1)))
                val width = textRenderer.getWidth(text)
                var xPos = 0
                if (rightAligned) xPos = -width
                var yPos = yOffset
                if (bottomAligned) yPos = -yOffset - 12
                context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
                return 12
            }
            return 0
        }
        override fun generateConfig(parent: Screen) = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.playtimeConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.description"))).also {
                        val overridePlaytimeColors = Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.overridePlaytimeColors"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.overridePlaytimeColors.description")))
                            .binding(Config.overridePlaytimeColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val playtimeTextColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeTextColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeTextColor.description")))
                            .binding(Config.playtimeTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playtimeTextColor.setAvailable(overridePlaytimeColors.pendingValue())
                        overridePlaytimeColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playtimeTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playtimeTextColor.setAvailable(option.pendingValue( ))
                        }
                        val playtimeColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeColor.description")))
                            .binding(Config.playtimeColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        playtimeColor.setAvailable(overridePlaytimeColors.pendingValue())
                        overridePlaytimeColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) playtimeColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) playtimeColor.setAvailable(option.pendingValue( ))
                        }
                        val playtimeIconColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeIconColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.playtimeConfig.category.styling.group.colors.option.playtimeIconColor.description")))
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
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (StateManager.modifiers.isEmpty()) return 0
            val textRenderer = MinecraftClient.getInstance().textRenderer
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12 - (StateManager.modifiers.size*20)

            val headerText = Text.translatable("mrc.roundhud.modifiers")
                .setStyle(Config.getModifiersTextAccentStyle(0xa63efc))
            val headerWidth = textRenderer.getWidth(headerText)
            var xPos = 0
            if (rightAligned) xPos = -headerWidth
            context.drawTextWithShadow(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12

            StateManager.modifiers.forEach {
                var xPos = 0
                if (rightAligned) xPos = -16
                if(!Config.customModifierIcons.value) {
                    context.drawItem(it.icon, xPos, yPos)
                    context.drawStackOverlay(textRenderer, it.icon, xPos, yPos)
                }else {
                    context.drawItem(it.customIcon, xPos, yPos)
                    context.drawStackOverlay(textRenderer, it.customIcon, xPos, yPos)
                }
                var modifierText = it.translatable.copy().setStyle(Config.getNormalModifierTextAccentStyle(Colors.YELLOW))
                if (Config.showMysteryModifiers.value && StateManager.mysteryModifiers.contains(it)){
                    modifierText.setStyle(Config.getMysteryModifierTextAccentStyle(0xD2B5FF))
                    if(!Config.hudLocation.value.rightAligned) modifierText.append(Text.literal(" ???").setStyle(Config.getMysteryModifierTextAccentStyle(0xD2B5FF)))
                    if(Config.hudLocation.value.rightAligned) modifierText = Text.literal("??? ").setStyle(Config.getMysteryModifierTextAccentStyle(0xD2B5FF)).append(modifierText)
                }
                if (StateManager.eternalModifier == it && StateManager.chargedModifiers.contains(it)) {
                    modifierText.setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF))
                    if(!Config.hudLocation.value.rightAligned) modifierText.append(Text.literal(" ⚡").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF))).append(Text.literal("∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(Colors.WHITE, -10071549)))
                    if(Config.hudLocation.value.rightAligned) modifierText = Text.literal("∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(Colors.WHITE, -10071549)).append(Text.literal("⚡ ").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF).withShadowColor(-16777216)).append(modifierText))
                }else if (StateManager.eternalModifier == it) {
                    modifierText.setStyle(Config.getEternalModifierTextWithShadowAccentStyle(Colors.WHITE, -10071549))
                    if(!Config.hudLocation.value.rightAligned) modifierText.append(Text.literal(" ∞").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(Colors.WHITE, -10071549)))
                    if(Config.hudLocation.value.rightAligned) modifierText = Text.literal("∞ ").setStyle(Config.getEternalModifierTextWithShadowAccentStyle(Colors.WHITE, -10071549)).append(modifierText)
                }else if (StateManager.chargedModifiers.contains(it)) {
                    modifierText.setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF))
                    if(!Config.hudLocation.value.rightAligned) modifierText.append(Text.literal(" ⚡").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF)))
                    if(Config.hudLocation.value.rightAligned) modifierText = Text.literal("⚡ ").setStyle(Config.getChargedModifierTextAccentStyle(0x0786FF)).append(modifierText)
                }
                var headText2d = Text.empty()

                if (Config.use2dHeads.value) StateManager.modifierBoosters[it]?.let { playerList ->
                    val bonusBoostersAmount = playerList.size - Config.boosterListMax.value
                    playerList.forEachIndexed { index, profile ->
                        if(index < Config.boosterListMax.value){
                            if(Config.hudLocation.value.rightAligned) headText2d.append(PlayerHead.player2dHeadTextComponent(profile.name)).setStyle(Style.EMPTY.withColor(Formatting.WHITE)).append(Text.literal(" "))
                            else headText2d.append(Text.literal(" ")).append(PlayerHead.player2dHeadTextComponent(profile.name))
                        }
                    }
                    if(bonusBoostersAmount > 0){
                        if(Config.hudLocation.value.rightAligned) headText2d = Text.literal("+${bonusBoostersAmount} ").setStyle(Config.getModifiersTextAccentStyle(0xa63efc)).append(headText2d)
                        else headText2d.append(Text.literal(" +${bonusBoostersAmount}").setStyle(Config.getModifiersTextAccentStyle(0xa63efc)))
                    }
                }
                val finalText = if (Config.hudLocation.value.rightAligned) Text.empty().append(headText2d).append(modifierText)
                else Text.empty().append(modifierText).append(headText2d)
                val modifierWidth = textRenderer.getWidth(finalText)
                xPos = 22
                if (rightAligned) xPos = -22 - modifierWidth
                context.drawTextWithShadow(textRenderer, finalText, xPos, yPos+4, -1)

                if (!Config.use2dHeads.value) StateManager.modifierBoosters[it]?.let { playerList ->
                    val bonusBoostersAmount = playerList.size - Config.boosterListMax.value
                    playerList.forEachIndexed { index, profile ->
                        if(index < Config.boosterListMax.value) {
                            xPos = 28 + modifierWidth + (index * 15)
                            if (rightAligned) xPos = -44 - modifierWidth - (index * 15)
                            context.drawItem(PlayerHead.fromProfile(profile), xPos, yPos)
                        }
                    }
                    if (rightAligned) xPos -= 15
                    else xPos += 15
                    if(bonusBoostersAmount > 0) context.drawTextWithShadow(textRenderer, Text.literal("+${bonusBoostersAmount}").setStyle(Config.getModifiersTextAccentStyle(0xa63efc)), xPos, yPos + 4, -1)
                }

                yPos += 20
            }

            return 12+(StateManager.modifiers.size*20)
        }

        override fun generateConfig(parent: Screen): Screen? {
            return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
                .category(ConfigCategory.createBuilder()
                    .name(Text.translatable("mrc.config.modifiersConfig.category.misc"))
                    .option(Option.createBuilder<Int>()
                        .name(Text.translatable("mrc.config.modifiersConfig.category.misc.option.boosterListMax"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.misc.option.boosterListMax.description")))
                        .binding(Config.boosterListMax.asBinding())
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 15)
                                .step(1)
                        }
                        .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.modifiersConfig.category.misc.option.showMysteryModifiers"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.misc.option.showMysteryModifiers.description")))
                        .binding(Config.showMysteryModifiers.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .build())
                .category(ConfigCategory.createBuilder()
                    .name(Text.translatable("mrc.config.modifiersConfig.category.styling"))
                    .group(OptionGroup.createBuilder()
                        .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.description"))).also {
                            val overrideModifiersColors = Option.createBuilder<Boolean>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.overrideModifiersColors"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.overrideModifiersColors.description")))
                                .binding(Config.overrideModifiersColors.asBinding())
                                .controller(TickBoxControllerBuilder::create)
                                .build()
                            val modifiersTextColor = Option.createBuilder<Color>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.modifiersTextColor"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.modifiersTextColor.description")))
                                .binding(Config.modifiersTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            modifiersTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) modifiersTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) modifiersTextColor.setAvailable(option.pendingValue( ))
                            }
                            val normalModifierTextColor = Option.createBuilder<Color>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.normalModifierTextColor"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.normalModifierTextColor.description")))
                                .binding(Config.normalModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            normalModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) normalModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) normalModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            val eternalModifierTextColor = Option.createBuilder<Color>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextColor"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextColor.description")))
                                .binding(Config.eternalModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            eternalModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) eternalModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) eternalModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            val eternalModifierTextShadowColor = Option.createBuilder<Color>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextShadowColor"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.eternalModifierTextShadowColor.description")))
                                .binding(Config.eternalModifierTextShadowColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            eternalModifierTextShadowColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) eternalModifierTextShadowColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) eternalModifierTextShadowColor.setAvailable(option.pendingValue( ))
                            }
                            val chargedModifierTextColor = Option.createBuilder<Color>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.chargedModifierTextColor"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.chargedModifierTextColor.description")))
                                .binding(Config.chargedModifierTextColor.asBinding())
                                .controller(ColorControllerBuilder::create)
                                .build()
                            chargedModifierTextColor.setAvailable(overrideModifiersColors.pendingValue())
                            overrideModifiersColors.addEventListener { option, event ->
                                if (event == OptionEventListener.Event.INITIAL) chargedModifierTextColor.setAvailable(option.pendingValue())
                                if (event == OptionEventListener.Event.STATE_CHANGE) chargedModifierTextColor.setAvailable(option.pendingValue( ))
                            }
                            val mysteryModifierTextColor = Option.createBuilder<Color>()
                                .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.mysteryModifierTextColor"))
                                .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.colors.option.mysteryModifierTextColor.description")))
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
                        .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.icons"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.icons.description")))
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.customModifierIcons"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.customModifierIcons.description")))
                        .binding(Config.customModifierIcons.asBinding())
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                    .option(Option.createBuilder<Boolean>()
                        .name(Text.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.use2dHeads"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.category.styling.group.icons.option.use2dHeads.description")))
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
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {

            if (StateManager.maceChance == -1f) return 0
            if (StateManager.maceChance == -100f) return 0
            if(Config.hideMaceChanceWhenEliminated.value && StateManager.eliminated) return 0

            val textRenderer = MinecraftClient.getInstance().textRenderer
            var maceChanceColorIdx = (StateManager.maceChance / 7.7).toInt().absoluteValue
            if (maceChanceColorIdx > 12) maceChanceColorIdx = 12
            val text = Text.literal("⚄ ").setStyle(Config.getMaceChanceIconAccentStyle(0x79fc00))
                .append(Text.translatable("mrc.roundhud.mace_chance_text",
                Text.literal("%.2f%%".format(StateManager.maceChance))
                    .setStyle(Config.getMaceChanceNumberAccentStyle(textColors[maceChanceColorIdx])))
                .setStyle(Config.getMaceChanceTextAccentStyle(0x79fc00)))

            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen) = YetAnotherConfigLib.createBuilder()
            .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.maceChanceConfig.category.misc"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("mrc.config.maceChanceConfig.category.misc.option.hideMaceChanceWhenEliminated"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.maceChanceConfig.category.misc.option.hideMaceChanceWhenEliminated.description")))
                    .binding(Config.hideMaceChanceWhenEliminated.asBinding())
                    .controller(TickBoxControllerBuilder::create)
                    .build())
                .build())
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable("mrc.config.maceChanceConfig.category.styling"))
                .group(OptionGroup.createBuilder()
                    .name(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.description"))).also {
                        val overrideMaceChanceColors = Option.createBuilder<Boolean>()
                            .name(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.overrideMaceChanceColors"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.overrideMaceChanceColors.description")))
                            .binding(Config.overrideMaceChanceColors.asBinding())
                            .controller(TickBoxControllerBuilder::create)
                            .build()
                        val maceChanceTextColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceTextColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceTextColor.description")))
                            .binding(Config.maceChanceTextColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        maceChanceTextColor.setAvailable(overrideMaceChanceColors.pendingValue())
                        overrideMaceChanceColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) maceChanceTextColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) maceChanceTextColor.setAvailable(option.pendingValue( ))
                        }
                        val maceChanceNumberColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceNumberColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceNumberColor.description")))
                            .binding(Config.maceChanceNumberColor.asBinding())
                            .controller(ColorControllerBuilder::create)
                            .build()
                        maceChanceNumberColor.setAvailable(overrideMaceChanceColors.pendingValue())
                        overrideMaceChanceColors.addEventListener { option, event ->
                            if (event == OptionEventListener.Event.INITIAL) maceChanceNumberColor.setAvailable(option.pendingValue())
                            if (event == OptionEventListener.Event.STATE_CHANGE) maceChanceNumberColor.setAvailable(option.pendingValue( ))
                        }
                        val maceChanceIconColor = Option.createBuilder<Color>()
                            .name(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceIconColor"))
                            .description(OptionDescription.of(Text.translatable("mrc.config.maceChanceConfig.category.styling.group.colors.option.maceChanceIconColor.description")))
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
    };

    abstract fun render(context: DrawContext, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int
    override fun generateConfig(parent: Screen): Screen? = null
    override fun asString(): String = name
    override fun getDisplayName(): Text = Text.translatable("mrc.hudelement.${name.lowercase()}")

    companion object {
        val CODEC = StringIdentifiable.createCodec(::values)
    }
}
