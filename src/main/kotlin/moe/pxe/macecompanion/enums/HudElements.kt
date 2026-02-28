package moe.pxe.macecompanion.enums

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.YetAnotherConfigLib
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
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import net.minecraft.util.StringIdentifiable
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
            val text = Text.translatable("mrc.roundhud.round", "${StateManager.round}")
                .setStyle(Config.getAccentStyle(StateManager.roundColor).withBold(true))
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
            val countText = Text.literal("${StateManager.playersAlive}").setStyle(Config.getAccentStyle(0xd5fcf5))
            if (StateManager.playersTotal >= 0) countText.append(Text.literal("/${StateManager.playersTotal}").withColor(0xd0d0d0))
            val text = Text.translatable("mrc.roundhud.alive", countText).setStyle(Style.EMPTY.withColor(Colors.WHITE))
            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
            return 12
        }
    },

    ELIMINATIONS {
        override fun render(
            context: DrawContext,
            yOffset: Int,
            rightAligned: Boolean,
            bottomAligned: Boolean
        ): Int {
            if (StateManager.eliminations == -1) return 0

            val textRenderer = MinecraftClient.getInstance().textRenderer
            val text = Text.translatable("mrc.roundhud.eliminations", "${StateManager.eliminations}")
                .setStyle(Config.getAccentStyle(0xa63efc))
            val width = textRenderer.getWidth(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawTextWithShadow(textRenderer, text, xPos, yPos, -1)
            return 12
        }
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
                val text = Text.translatable("mrc.roundhud.playtime", it.elapsedNow().toLong(DurationUnit.SECONDS).toDuration(DurationUnit.SECONDS).toString())
                    .setStyle(Config.getAccentStyle(0x3efca1))
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
                .setStyle(Config.getAccentStyle(0xa63efc))
            val headerWidth = textRenderer.getWidth(headerText)
            var xPos = 0
            if (rightAligned) xPos = -headerWidth
            context.drawTextWithShadow(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12

            StateManager.modifiers.forEach {
                var xPos = 0
                if (rightAligned) xPos = -16
                context.drawItem(it.icon, xPos, yPos)
                context.drawStackOverlay(textRenderer, it.icon, xPos, yPos)

                val modifierText = it.translatable.copy().setStyle(Config.getAccentStyle(it.translatable.style)).also { text ->
                    if (StateManager.eternalModifier == it) text.append(Text.literal(" ∞").setStyle(Style.EMPTY.withColor(Formatting.WHITE).withShadowColor(-10068202)))
                }
                val modifierWidth = textRenderer.getWidth(modifierText)
                xPos = 22
                if (rightAligned) xPos = -22 - modifierWidth
                context.drawTextWithShadow(textRenderer, modifierText, xPos, yPos+4, -1)

                StateManager.modifierBoosters[it]?.let { playerList ->
                    playerList.forEachIndexed { index, profile ->
                        xPos = 28 + modifierWidth + (index*20)
                        if (rightAligned) xPos = -44 - modifierWidth - (index*20)
                        if (index >= Config.boosterListMax.value) {
                            context.drawTextWithShadow(textRenderer, Text.literal("+${playerList.size - Config.boosterListMax.value}").withColor(0xa63efc), xPos, yPos+4, -1)
                            val boosterText = Text.literal("+${playerList.size - Config.boosterListMax.value}")
                                .setStyle(Config.getAccentStyle(0xb0b2fc))
                            context.drawTextWithShadow(textRenderer, boosterText,
                                xPos, yPos+4, -1)
                            return@let
                        }
                        context.drawItem(PlayerHead.fromProfile(profile), xPos, yPos)
                    }
                }

                yPos += 20
            }

            return 12+(StateManager.modifiers.size*20)
        }

        override fun generateConfig(parent: Screen): Screen? {
            return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("mrc.hudelement.${name.lowercase()}"))
                .category(ConfigCategory.createBuilder()
                    .name(Text.translatable("mrc.hudelement.${name.lowercase()}"))
                    .option(Option.createBuilder<Int>()
                        .name(Text.translatable("mrc.config.modifiersConfig.option.boosterListMax"))
                        .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.option.boosterListMax.description")))
                        .binding(Config.boosterListMax.asBinding())
                        .controller {
                            IntegerSliderControllerBuilder.create(it)
                                .range(0, 15)
                                .step(1)
                        }
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

            val textRenderer = MinecraftClient.getInstance().textRenderer
            val text = Text.translatable("mrc.roundhud.mace_chance_text",
                Text.literal("%.2f%%".format(StateManager.maceChance)).also {
                    if (Config.chanceUseColor.value) it.setStyle(Config.getAccentStyle(textColors[(StateManager.maceChance / 7.7).toInt().absoluteValue]))
                }).setStyle(Config.getAccentStyle(0x79fc00))

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
                .name(Text.translatable("mrc.hudelement.${name.lowercase()}"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("mrc.config.modifiersConfig.option.chanceUseColor"))
                    .description(OptionDescription.of(Text.translatable("mrc.config.modifiersConfig.option.chanceUseColor.description")))
                    .binding(Config.chanceUseColor.asBinding())
                    .controller(TickBoxControllerBuilder::create)
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
