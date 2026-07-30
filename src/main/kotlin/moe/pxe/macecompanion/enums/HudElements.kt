package moe.pxe.macecompanion.enums

import com.mojang.authlib.GameProfile
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.config.controllers.ConfigurableEnum
import moe.pxe.macecompanion.stateManagers.AccuracyManager.maceAttempts
import moe.pxe.macecompanion.stateManagers.BountyManager.bounties
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminated
import moe.pxe.macecompanion.stateManagers.EliminationManager.eliminations
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersAlive
import moe.pxe.macecompanion.stateManagers.EliminationManager.playersTotal
import moe.pxe.macecompanion.stateManagers.ModifierManager.eternalModifier
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifierBoosters
import moe.pxe.macecompanion.stateManagers.ModifierManager.modifiers
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.fps
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.ping
import moe.pxe.macecompanion.stateManagers.PerformanceStatsManager.tps
import moe.pxe.macecompanion.stateManagers.PlotManager.isStatless
import moe.pxe.macecompanion.stateManagers.RoundManager.gameOngoing
import moe.pxe.macecompanion.stateManagers.RoundManager.maceChance
import moe.pxe.macecompanion.stateManagers.RoundManager.playtime
import moe.pxe.macecompanion.stateManagers.RoundManager.round
import moe.pxe.macecompanion.stateManagers.RoundManager.roundColor
import moe.pxe.macecompanion.stateManagers.StarFragmentManager.starFragments
import moe.pxe.macecompanion.stateManagers.SummerPointsManager.summerColor
import moe.pxe.macecompanion.stateManagers.SummerPointsManager.summerPoints
import moe.pxe.macecompanion.util.OptionUtils.overrideColorOption
import moe.pxe.macecompanion.util.OptionUtils.overrideColorsOption
import moe.pxe.macecompanion.util.OptionUtils.addColorOptionDependency
import moe.pxe.macecompanion.util.OptionUtils.hideWhenEliminatedOption
import moe.pxe.macecompanion.util.OptionUtils.iconBooleanOption
import moe.pxe.macecompanion.util.OptionUtils.sliderOption
import moe.pxe.macecompanion.util.PlayerProfile.headFromProfile
import moe.pxe.macecompanion.util.TextUtils.buildModifierTextWith2dBoosters
import moe.pxe.macecompanion.util.TextUtils.getStarFragmentIcon
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.ItemStack
import kotlin.math.roundToInt
import kotlin.time.DurationUnit

enum class HudElements : NameableEnum, StringRepresentable, ConfigurableEnum {
    ROUND_NUMBER {
        private var cachedRound: Int = -2
        private var cachedColor: Int = -1
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (round == -1 || !gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val currentColor = roundColor?.color?.value ?: 0x9ef6fc

            if (round != cachedRound || currentColor != cachedColor || cachedText == null) {
                cachedRound = round
                cachedColor = currentColor

                val numberText = Component.literal("$round")
                    .setStyle(Config.getRoundNumberAccentStyle(currentColor).withBold(true))

                val newText = Component.translatable("mrc.roundhud.round", numberText)
                    .setStyle(Config.getRoundTextAccentStyle(currentColor).withBold(true))

                cachedText = newText
                cachedTextWidth = textRenderer.width(newText)
            }

            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth.toFloat() else 0f
            val yPos = if (bottomAligned) (-yOffset shr 1).toFloat() - 12f else (yOffset shr 1).toFloat()

            context.pose().pushMatrix()
            context.pose().scale(2f)
            context.pose().translate(xPos, yPos)
            context.drawString(textRenderer, text, 0, 0, -1)
            context.pose().popMatrix()
            return 24
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overrideRoundColors = overrideColorsOption(name.lowercase(), Config.overrideRoundColors.asBinding())

                                val roundTextColor = overrideColorOption(name.lowercase(), Config.roundTextColor.asBinding(), "text_color")
                                addColorOptionDependency(roundTextColor, overrideRoundColors)

                                val roundNumberColor = overrideColorOption(name.lowercase(), Config.roundNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(roundNumberColor, overrideRoundColors)

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
        private var cachedAlive: Int = -2
        private var cachedTotal: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (playersAlive == -1 || !gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font

            if (playersAlive != cachedAlive || playersTotal != cachedTotal || cachedText == null) {
                cachedAlive = playersAlive
                cachedTotal = playersTotal
                val countText = Component.literal("$playersAlive")
                    .setStyle(Config.getAlivePLayersAccentStyle(0xd5fcf5))
                if (playersTotal >= 0) {
                    countText.append(
                        Component.literal("/$playersTotal")
                            .setStyle(Config.getTotalPLayersAccentStyle(0xd0d0d0))
                    )
                }
                val newText = Component.translatable("mrc.roundhud.alive", countText)
                    .setStyle(Config.getPlayerCountTextAccentStyle(CommonColors.WHITE))
                cachedText = newText
                cachedTextWidth = textRenderer.width(newText)
            }
            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth else 0
            val yPos = if (bottomAligned) -yOffset - 12 else yOffset

            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overridePlayerCountColors = overrideColorsOption(name.lowercase(), Config.overridePlayerCountColors.asBinding())

                                val playerCountTextColor = overrideColorOption(name.lowercase(), Config.playerCountTextColor.asBinding(), "text_color")
                                addColorOptionDependency(playerCountTextColor, overridePlayerCountColors)

                                val alivePlayersColor = overrideColorOption(name.lowercase(), Config.alivePlayersColor.asBinding(), "number_color.alive")
                                addColorOptionDependency(alivePlayersColor, overridePlayerCountColors)

                                val totalPlayersColor = overrideColorOption(name.lowercase(), Config.totalPlayersColor.asBinding(), "number_color.total")
                                addColorOptionDependency(totalPlayersColor, overridePlayerCountColors)

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
        private var cachedMaceAttempts = mutableMapOf<Int, Boolean>()
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        val textColors = arrayOf(0xff2c01, 0xff5500, 0xff8400, 0xffa503, 0xffd202, 0xfff400, 0xe6ff01, 0xc0ff03, 0x92ff00, 0x74ff02, 0x3cff01, 0x13ff00, 0x01ff00)

        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (maceAttempts.isEmpty() || !gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            if (cachedMaceAttempts != maceAttempts || cachedText == null) {
                cachedMaceAttempts = maceAttempts.toMutableMap()
                val totalMaceAttempts = maceAttempts.size
                var successCount = 0
                for (attempt in maceAttempts) {
                    if (attempt.value) successCount++
                }
                val accuracy = ((successCount.toFloat() / totalMaceAttempts) * 100f).roundToInt()
                val accuracyColorIdx = (accuracy / 7.7).toInt().coerceIn(0, textColors.lastIndex)
                val iconText = Component.literal("\uD83C\uDFF9 ")
                    .setStyle(Config.getAccuracyIconAccentStyle(0x79fc00))
                val countText = Component.literal("$successCount/$totalMaceAttempts")
                    .setStyle(Config.getAccuracyTextAccentStyle(0x79fc00))
                val percentageText = Component.literal("$accuracy%")
                    .setStyle(Config.getAccuracyAccentStyle(textColors[accuracyColorIdx]))
                val text = Component.translatable("mrc.roundhud.accuracy", percentageText, countText)
                    .setStyle(Config.getAccuracyTextAccentStyle(0x79fc00))
                val finalText = iconText.append(text)

                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth else 0
            val yPos = if (bottomAligned) -yOffset - 12 else yOffset

            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overrideAccuracyColors = overrideColorsOption(name.lowercase(), Config.overrideAccuracyColors.asBinding())

                                val accuracyTextColor = overrideColorOption(name.lowercase(), Config.accuracyTextColor.asBinding(), "text_color")
                                addColorOptionDependency(accuracyTextColor, overrideAccuracyColors)

                                val accuracyColor = overrideColorOption(name.lowercase(), Config.accuracyColor.asBinding(), "number_color")
                                addColorOptionDependency(accuracyColor, overrideAccuracyColors)

                                val accuracyIconColor = overrideColorOption(name.lowercase(), Config.accuracyIconColor.asBinding(), "icon_color")
                                addColorOptionDependency(accuracyIconColor, overrideAccuracyColors)

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
        private var cachedEliminations: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (eliminations == -1 || !gameOngoing || (Config.hideEliminationsWhenEliminated.value && eliminated)) return 0

            val textRenderer = Minecraft.getInstance().font

            if (eliminations != cachedEliminations || cachedText == null) {
                val iconText = Component.literal("\uD83E\uDE93 ")
                    .setStyle(Config.getEliminationsIconAccentStyle(0xa63efc))
                val numberText = Component.literal("$eliminations")
                    .setStyle(Config.getEliminationsNumberAccentStyle(0xa63efc))
                val text = Component.translatable("mrc.roundhud.eliminations", numberText)
                    .setStyle(Config.getEliminationsTextAccentStyle(0xa63efc))
                val finalText = iconText.append(text)
                cachedEliminations = eliminations
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.misc"))
                    .option(hideWhenEliminatedOption(name.lowercase(), Config.hideEliminationsWhenEliminated.asBinding()))
                    .build()
            )
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overrideEliminationsColors = overrideColorsOption(name.lowercase(), Config.overrideEliminationsColors.asBinding())

                                val eliminationsTextColor = overrideColorOption(name.lowercase(), Config.eliminationsTextColor.asBinding(), "text_color")
                                addColorOptionDependency(eliminationsTextColor, overrideEliminationsColors)

                                val eliminationsNumberColor = overrideColorOption(name.lowercase(), Config.eliminationsNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(eliminationsNumberColor, overrideEliminationsColors)

                                val eliminationsIconColor = overrideColorOption(name.lowercase(), Config.eliminationsIconColor.asBinding(), "icon_color")
                                addColorOptionDependency(eliminationsIconColor, overrideEliminationsColors)

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
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (summerPoints == -1) return 0
            if (!gameOngoing) return 0

            val textRenderer = Minecraft.getInstance().font
            val textIcon = Component.literal("⚑ ").setStyle(Config.getSummerPointsIconAccentStyle(summerColor))
            val text = textIcon.append(
                Component.translatable(
                    "mrc.roundhud.summer_points", Component.literal("$summerPoints")
                        .setStyle(Config.getSummerPointsNumberAccentStyle(summerColor))
                )
                    .setStyle(Config.getSummerPointsTextAccentStyle(summerColor))
            )
            val width = textRenderer.width(text)
            var xPos = 0
            if (rightAligned) xPos = -width
            var yPos = yOffset
            if (bottomAligned) yPos = -yOffset - 12
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }
    },
    STAR_FRAGMENTS {
        private val starFragmentIcon = getStarFragmentIcon()
        private var cachedStarFragments: Int = 0
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (starFragments == -1 || !gameOngoing || isStatless || (Config.hideStarFragmentsWhenEliminated.value && eliminated)) return 0

            val textRenderer = Minecraft.getInstance().font
            if (cachedStarFragments != starFragments || cachedText == null) {
                val starFragmentIconText = Component.empty().append(starFragmentIcon)
                    .setStyle(Config.getStarFragmentsIconAccentStyle(0xa0f9ff))
                val numberText = Component.literal(" $starFragments")
                    .setStyle(Config.getStarFragmentsNumberAccentStyle(0xa0f9ff))
                val text = Component.translatable("mrc.roundhud.starFragments", numberText)
                    .setStyle(Config.getStarFragmentsTextAccentStyle(0xa0f9ff))
                val finalText = starFragmentIconText.append(text)
                cachedStarFragments = starFragments
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.misc"))
                    .option(hideWhenEliminatedOption(name.lowercase(), Config.hideStarFragmentsWhenEliminated.asBinding()))
                    .build()
            )
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overrideStarFragmentsColors = overrideColorsOption(name.lowercase(), Config.overrideStarFragmentsColors.asBinding())

                                val starFragmentsTextColor = overrideColorOption(name.lowercase(), Config.starFragmentsTextColor.asBinding(), "text_color")
                                addColorOptionDependency(starFragmentsTextColor, overrideStarFragmentsColors)

                                val starFragmentsNumberColor = overrideColorOption(name.lowercase(), Config.starFragmentsNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(starFragmentsNumberColor, overrideStarFragmentsColors)

                                val starFragmentsIconColor = overrideColorOption(name.lowercase(), Config.starFragmentsIconColor.asBinding(), "icon_color")
                                addColorOptionDependency(starFragmentsIconColor, overrideStarFragmentsColors)

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
        private var lastCachedSecond: Long = -1L
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing) return 0
            val currentPlaytime = playtime ?: return 0

            val currentSecond = currentPlaytime.elapsedNow().toLong(DurationUnit.SECONDS)
            val textRenderer = Minecraft.getInstance().font

            if (currentSecond != lastCachedSecond || cachedText == null) {
                lastCachedSecond = currentSecond

                val minutes = currentSecond / 60
                val seconds = currentSecond % 60

                val timeString = if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"

                val iconText = Component.literal("⌚ ")
                    .setStyle(Config.getPlaytimeIconAccentStyle(0x3efca1))

                val numberText = Component.literal(timeString)
                    .setStyle(Config.getPlaytimeNumberAccentStyle(0x3efca1))

                val text = Component.translatable("mrc.roundhud.playtime", numberText)
                    .setStyle(Config.getPlaytimeTextAccentStyle(0x3efca1))

                val finalText = iconText.append(text)

                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            val xPos = if (rightAligned) -cachedTextWidth else 0
            val yPos = if (bottomAligned) -yOffset - 12 else yOffset

            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12

        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overridePlaytimeColors = overrideColorsOption(name.lowercase(), Config.overridePlaytimeColors.asBinding())

                                val playtimeTextColor = overrideColorOption(name.lowercase(), Config.playtimeTextColor.asBinding(), "text_color")
                                addColorOptionDependency(playtimeTextColor, overridePlaytimeColors)

                                val playtimeColor = overrideColorOption(name.lowercase(), Config.playtimeColor.asBinding(), "number_color")
                                addColorOptionDependency(playtimeColor, overridePlaytimeColors)

                                val playtimeIconColor = overrideColorOption(name.lowercase(), Config.playtimeIconColor.asBinding(), "icon_color")
                                addColorOptionDependency(playtimeIconColor, overridePlaytimeColors)

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
        private var cachedModifiers = mutableMapOf<Modifiers, Boolean>()
        private var cachedHeaderText: Component? = null
        private var cachedHeaderTextWidth: Int = 0
        private var cachedUse2dHeads: Boolean? = null
        private var cachedModifierIcons = mutableMapOf<Modifiers, ItemStack>()
        private var cachedRightModifierTexts = mutableMapOf<Modifiers, Component>()
        private var cachedLeftModifierTexts = mutableMapOf<Modifiers, Component>()
        private var cachedModifierTextWidths = mutableMapOf<Modifiers, Int>()
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || modifiers.isEmpty()) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedHeaderText == null) {
                val headerText = Component.translatable("mrc.roundhud.modifiers")
                    .setStyle(Config.getModifiersTextAccentStyle(0xa63efc))
                cachedHeaderText = headerText
                cachedHeaderTextWidth = textRenderer.width(headerText)
            }

            val headerText = cachedHeaderText!!
            var yPos = if (bottomAligned) -yOffset - 12 - (modifiers.size * 20) else yOffset
            var xPos = if (rightAligned) -cachedHeaderTextWidth else 0
            context.drawString(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12
            val use2dHeads = Config.use2dHeads.value
            val modifiersChanged = (cachedModifiers != modifiers)
            if (modifiersChanged) cachedModifiers = modifiers.toMutableMap()
            modifiers.forEach { (modifier, isCharged) ->
                var xPos = if (rightAligned) -16 else 0
                if (modifiersChanged || cachedModifierIcons[modifier] == null) cachedModifierIcons[modifier] =
                    if (!Config.customModifierIcons.value) modifier.icon else modifier.customIcon
                val icon = cachedModifierIcons[modifier]!!
                context.renderItem(icon, xPos, yPos)
                context.renderItemDecorations(textRenderer, icon, xPos, yPos)
                val isEternal = eternalModifier == modifier
                if (modifiersChanged || (rightAligned && cachedRightModifierTexts[modifier] == null) || (!rightAligned && cachedLeftModifierTexts[modifier] == null) || cachedUse2dHeads != use2dHeads) {
                    cachedUse2dHeads = use2dHeads
                    val finalText = buildModifierTextWith2dBoosters(modifier, isEternal, isCharged, rightAligned, use2dHeads, modifierBoosters[modifier])
                    if (rightAligned) cachedRightModifierTexts[modifier] = finalText
                    else cachedLeftModifierTexts[modifier] = finalText
                    cachedModifierTextWidths[modifier] = textRenderer.width(finalText)
                }
                val text = if (rightAligned) cachedRightModifierTexts[modifier]!! else cachedLeftModifierTexts[modifier]!!
                val width = cachedModifierTextWidths[modifier] ?: 0
                xPos = if (rightAligned) -22 - width else 22
                context.drawString(textRenderer, text, xPos, yPos + 4, -1)

                if (!use2dHeads) modifierBoosters[modifier]?.let { playerList ->
                    val maxBoosters = Config.boosterListMax.value
                    val visibleBoosters = playerList.take(maxBoosters)
                    val bonusBoostersAmount = playerList.size - maxBoosters

                    val direction = if (rightAligned) -1 else 1
                    val stepSize = 15 * direction
                    val baseStart = if (rightAligned) -44 - width else 28 + width

                    visibleBoosters.forEachIndexed { index, profile ->
                        val currentX = baseStart + (index * stepSize)
                        context.renderItem(headFromProfile(profile), currentX, yPos)
                    }

                    if (bonusBoostersAmount > 0) {
                        val textX = baseStart + (visibleBoosters.size * stepSize)
                        context.drawString(textRenderer, Component.literal("+$bonusBoostersAmount").setStyle(Config.getModifiersTextAccentStyle(0xa63efc)), textX, yPos + 4, -1)
                    }
                }
                yPos += 20
            }

            return 12 + (modifiers.size * 20)
        }

        override fun generateConfig(parent: Screen): Screen? {
            return YetAnotherConfigLib.createBuilder()
                .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
                .category(
                    ConfigCategory.createBuilder()
                        .name(Component.translatable("mrc.config.${name.lowercase()}.category.misc"))
                        .option(sliderOption(name.lowercase(), Config.boosterListMax.asBinding(), 0, 15, 1, "max_boosters"))
                        .build()
                )
                .category(
                    ConfigCategory.createBuilder()
                        .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                        .group(
                            OptionGroup.createBuilder()
                                .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                                .also {
                                    val overrideModifiersColors = overrideColorsOption(name.lowercase(), Config.overrideModifiersColors.asBinding())

                                    val modifiersTextColor = overrideColorOption(name.lowercase(), Config.modifiersTextColor.asBinding(), "text_color")
                                    addColorOptionDependency(modifiersTextColor, overrideModifiersColors)

                                    val normalModifierTextColor = overrideColorOption(name.lowercase(), Config.normalModifierTextColor.asBinding(), "text_color.regular_modifier")
                                    addColorOptionDependency(normalModifierTextColor, overrideModifiersColors)

                                    val eternalModifierTextColor = overrideColorOption(name.lowercase(), Config.eternalModifierTextColor.asBinding(), "text_color.eternal_modifier")
                                    addColorOptionDependency(eternalModifierTextColor, overrideModifiersColors)

                                    val eternalModifierTextShadowColor = overrideColorOption(name.lowercase(), Config.eternalModifierTextShadowColor.asBinding(), "shadow_color.eternal_modifier")
                                    addColorOptionDependency(eternalModifierTextShadowColor, overrideModifiersColors)

                                    val chargedModifierTextColor = overrideColorOption(name.lowercase(), Config.chargedModifierTextColor.asBinding(), "text_color.charged_modifier")
                                    addColorOptionDependency(chargedModifierTextColor, overrideModifiersColors)

                                    val mysteryModifierTextColor = overrideColorOption(name.lowercase(), Config.mysteryModifierTextColor.asBinding(), "text_color.mystery_modifier")
                                    addColorOptionDependency(mysteryModifierTextColor, overrideModifiersColors)

                                    it.option(overrideModifiersColors)
                                    it.option(modifiersTextColor)
                                    it.option(normalModifierTextColor)
                                    it.option(eternalModifierTextColor)
                                    it.option(eternalModifierTextShadowColor)
                                    it.option(chargedModifierTextColor)
                                    it.option(mysteryModifierTextColor)
                                }
                                .build())
                        .group(
                            OptionGroup.createBuilder()
                                .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.icons"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.icons.description")))
                                .option(iconBooleanOption(name.lowercase(), Config.customModifierIcons.asBinding(), "use_custom"))
                                .option(iconBooleanOption(name.lowercase(), Config.use2dHeads.asBinding(), "use_2d_heads"))
                                .build()
                        )
                        .build())
                .save(Config::saveToFile)
                .build()
                .generateScreen(parent)
        }
    },
    MACE_CHANCE {
        private var cachedMaceChance: Float = -2f
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0

        val textColors = arrayOf(0xff2c01, 0xff5500, 0xff8400, 0xffa503, 0xffd202, 0xfff400, 0xe6ff01, 0xc0ff03, 0x92ff00, 0x74ff02, 0x3cff01, 0x13ff00, 0x01ff00)

        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || maceChance < 0f || (Config.hideMaceChanceWhenEliminated.value && eliminated)) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedMaceChance != maceChance || cachedText == null) {
                val maceChanceColorIdx = (maceChance / 7.7).toInt().coerceIn(0, textColors.lastIndex)
                val iconText = Component.literal("⚄ ")
                    .setStyle(Config.getMaceChanceIconAccentStyle(0x42C1FF))
                val numberText = Component.literal("%.2f%%".format(maceChance))
                    .setStyle(Config.getMaceChanceNumberAccentStyle(textColors[maceChanceColorIdx]))
                val text = Component.translatable("mrc.roundhud.mace_chance", numberText)
                    .setStyle(Config.getMaceChanceTextAccentStyle(0x42C1FF))
                val finalText = iconText.append(text)

                cachedMaceChance = maceChance
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.misc"))
                    .option(hideWhenEliminatedOption(name.lowercase(), Config.hideMaceChanceWhenEliminated.asBinding()))
                    .build()
            )
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overrideMaceChanceColors = overrideColorsOption(name.lowercase(), Config.overrideMaceChanceColors.asBinding())

                                val maceChanceTextColor = overrideColorOption(name.lowercase(), Config.maceChanceTextColor.asBinding(), "text_color")
                                addColorOptionDependency(maceChanceTextColor, overrideMaceChanceColors)

                                val maceChanceNumberColor = overrideColorOption(name.lowercase(), Config.maceChanceNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(maceChanceNumberColor, overrideMaceChanceColors)

                                val maceChanceIconColor = overrideColorOption(name.lowercase(), Config.maceChanceIconColor.asBinding(), "icon_color")
                                addColorOptionDependency(maceChanceIconColor, overrideMaceChanceColors)

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
        private var cachedBounties: HashMap<GameProfile, Int> = HashMap()
        private var cachedHeaderText: Component? = null
        private var cachedHeaderTextWidth: Int = 0
        private var cachedBountyTexts: HashMap<GameProfile, Component?> = HashMap()
        private var cachedBountyTextWidths: HashMap<GameProfile, Int> = HashMap()
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (!gameOngoing || bounties.isEmpty() || eliminated) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedHeaderText == null) {
                val headerText = Component.translatable("mrc.roundhud.bounty_board")
                    .setStyle(Config.getBountyBoardTextAccentStyle(0xff7cf4))
                cachedHeaderText = headerText
                cachedHeaderTextWidth = textRenderer.width(headerText)
            }
            val headerText = cachedHeaderText!!
            val sortedBounties = bounties.entries.sortedByDescending { it.value }.associate { it.key to it.value }
            var yPos = if (bottomAligned) -yOffset - 12 - (sortedBounties.size * 20) else yOffset
            var xPos = if (rightAligned) -cachedHeaderTextWidth else 0
            context.drawString(textRenderer, headerText, xPos, yPos, -1)
            yPos += 12
            var index = 1
            var bountyCount = 0
            val bountiesChanged = (cachedBounties != bounties)
            sortedBounties.forEach { (profile, bountyAmount) ->
                if (bountyAmount >= Config.bountyBoardMinBounty.value && index <= Config.bountyBoardMaxPlayers.value) {
                    val playerUsername = profile.name
                    var xPos = if (rightAligned) -16 else 0
                    context.renderItem(headFromProfile(profile), xPos, yPos)
                    if (bountiesChanged || cachedBountyTexts[profile] == null) {
                        val playerText = Component.literal("$playerUsername")
                            .setStyle(Config.getBountyBoardPlayerAccentStyle(CommonColors.YELLOW))
                        val bountyText = Component.literal("$bountyAmount⛂")
                            .setStyle(Config.getBountyBoardAmountAccentStyle(0xff7cf4))
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
                        cachedBountyTextWidths[profile] = textRenderer.width(finalText)
                        cachedBountyTexts[profile] = finalText
                    }
                    val text = cachedBountyTexts[profile]!!
                    xPos = if (rightAligned) -22 - cachedBountyTextWidths[profile]!! else 22
                    context.drawString(textRenderer, text, xPos, yPos + 4, -1)
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
                        .name(Component.translatable("mrc.config.${name.lowercase()}.category.misc"))
                        .option(sliderOption(name.lowercase(), Config.bountyBoardMaxPlayers.asBinding(), 1, 15, 1, "max_players"))
                        .option(sliderOption(name.lowercase(), Config.bountyBoardMinBounty.asBinding(), 1, 10, 1, "min_bounty"))
                        .build()
                )
                .category(
                    ConfigCategory.createBuilder()
                        .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                        .group(
                            OptionGroup.createBuilder()
                                .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                                .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                                .also {
                                    val overrideBountyBoardColors = overrideColorsOption(name.lowercase(), Config.overrideBountyBoardColors.asBinding())

                                    val bountyBoardTextColor = overrideColorOption(name.lowercase(), Config.bountyBoardTextColor.asBinding(), "text_color")
                                    addColorOptionDependency(bountyBoardTextColor, overrideBountyBoardColors)

                                    val bountyBoardPlayerColor = overrideColorOption(name.lowercase(), Config.bountyBoardPlayerColor.asBinding(), "text_color.player")
                                    addColorOptionDependency(bountyBoardPlayerColor, overrideBountyBoardColors)

                                    val bountyBoardNumberColor = overrideColorOption(name.lowercase(), Config.bountyBoardNumberColor.asBinding(), "number_color")
                                    addColorOptionDependency(bountyBoardNumberColor, overrideBountyBoardColors)

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
        private var cachedFps: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (fps == -1) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedFps != fps || cachedText == null) {
                cachedFps = fps
                val numberText = Component.literal("$fps")
                    .setStyle(Config.getFpsNumberAccentStyle(CommonColors.WHITE))
                val finalText = Component.translatable("mrc.roundhud.fps", numberText)
                    .setStyle(Config.getFpsTextAccentStyle(CommonColors.WHITE))
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overrideFpsColors = overrideColorsOption(name.lowercase(), Config.overrideFpsColors.asBinding())

                                val fpsTextColor = overrideColorOption(name.lowercase(), Config.fpsTextColor.asBinding(), "text_color")
                                addColorOptionDependency(fpsTextColor, overrideFpsColors)

                                val fpsNumberColor = overrideColorOption(name.lowercase(), Config.fpsNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(fpsNumberColor, overrideFpsColors)

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
        private var cachedPing: Int = -2
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (ping <= 0) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedPing != ping || cachedText == null) {
                cachedPing = ping
                val pingColor =
                    if (ping < 50) 0x1eff00 else if (ping < 100) 0xfff100 else if (ping < 200) 0xff9500 else 0xff3b3b
                val numberText = Component.literal("$ping")
                    .setStyle(Config.getPingNumberAccentStyle(pingColor))
                val finalText = Component.translatable("mrc.roundhud.ping", numberText)
                    .setStyle(Config.getPingTextAccentStyle(pingColor))
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors.description")))
                            .also {
                                val overridePingColors = overrideColorsOption(name.lowercase(), Config.overridePingColors.asBinding())

                                val pingTextColor = overrideColorOption(name.lowercase(), Config.pingTextColor.asBinding(), "text_color")
                                addColorOptionDependency(pingTextColor, overridePingColors)

                                val pingNumberColor = overrideColorOption(name.lowercase(), Config.pingNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(pingNumberColor, overridePingColors)

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
        private var cachedTps: Float = -2f
        private var cachedText: Component? = null
        private var cachedTextWidth: Int = 0
        override fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int {
            if (tps == -1f) return 0

            val textRenderer = Minecraft.getInstance().font

            if (cachedTps != tps || cachedText == null) {
                cachedTps = tps
                val numberText = Component.literal("$tps")
                    .setStyle(Config.getTpsNumberAccentStyle(0xbfff00))
                val finalText = Component.translatable("mrc.roundhud.tps", numberText)
                    .setStyle(Config.getTpsTextAccentStyle(0xbfff00))
                cachedText = finalText
                cachedTextWidth = textRenderer.width(finalText)
            }
            val text = cachedText!!
            var xPos = if (rightAligned) -cachedTextWidth else 0
            var yPos = if (bottomAligned) -yOffset - 12 else yOffset
            context.drawString(textRenderer, text, xPos, yPos, -1)
            return 12
        }

        override fun generateConfig(parent: Screen): Screen? = YetAnotherConfigLib.createBuilder()
            .title(Component.translatable("mrc.hudelement.${name.lowercase()}"))
            .category(
                ConfigCategory.createBuilder()
                    .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling"))
                    .group(
                        OptionGroup.createBuilder()
                            .name(Component.translatable("mrc.config.${name.lowercase()}.category.styling.group.colors"))
                            .description(OptionDescription.of(Component.translatable("mrc.config.tpsConfig.category.styling.group.colors.description")))
                            .also {
                                val overrideTpsColors = overrideColorsOption(name.lowercase(), Config.overrideTpsColors.asBinding())

                                val tpsTextColor = overrideColorOption(name.lowercase(), Config.tpsTextColor.asBinding(), "text_color")
                                addColorOptionDependency(tpsTextColor, overrideTpsColors)

                                val tpsNumberColor = overrideColorOption(name.lowercase(), Config.tpsNumberColor.asBinding(), "number_color")
                                addColorOptionDependency(tpsNumberColor, overrideTpsColors)

                                it.option(overrideTpsColors)
                                it.option(tpsTextColor)
                                it.option(tpsNumberColor)
                            }
                            .build())
                    .build())
            .build()
            .generateScreen(parent)
    }, ;

    abstract fun render(context: GuiGraphics, yOffset: Int, rightAligned: Boolean, bottomAligned: Boolean): Int
    override fun generateConfig(parent: Screen): Screen? = null
    override fun getSerializedName(): String = name
    override fun getDisplayName(): Component = Component.translatable("mrc.hudelement.${name.lowercase()}")

    companion object {
        val CODEC = StringRepresentable.fromEnum(::values)
    }
}
