package moe.pxe.macecompanion.config

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.enums.HudElements
import moe.pxe.macecompanion.enums.HudLocation
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Style
import java.awt.Color

object Config : JsonFileCodecConfig<Config>(FabricLoader.getInstance().configDir.resolve("mrc.json")) {

    val RGB_COLOR_CODEC = object : PrimitiveCodec<Color>{
        override fun <T : Any> read(ops: DynamicOps<T>, input: T): DataResult<Color> {
            return ops.getStringValue(input).map(Color::decode)
        }

        override fun <T : Any> write(ops: DynamicOps<T>, value: Color): T {
            return ops.createString("#"+Integer.toHexString(value.rgb).substring(2))
        }
    }

    val RGBA_COLOR_CODEC = object : PrimitiveCodec<Color>{
        override fun <T : Any> read(ops: DynamicOps<T>, input: T): DataResult<Color> {
            return ops.getStringValue(input).map(Color::decode)
        }

        override fun <T : Any> write(ops: DynamicOps<T>, value: Color): T {
            return ops.createString("#"+Integer.toHexString(value.rgb))
        }
    }

    // AutoGG + AutoGL
    val useAutoGG by register<Boolean>(true, BOOL)
    val autoGGStrings by register<List<String>>(listOf("gg", "good game"), STRING.listOf())
    val ggDelayTicks by register<Int>(10, INT)

    val useAutoGL by register<Boolean>(true, BOOL)
    val autoGLStrings by register<List<String>>(listOf("gl", "hf", "glhf", "good luck", "have fun", "good luck have fun"), STRING.listOf())
    val glDelayTicks by register<Int>(60, INT)

    val hideGGMessages by register<Boolean>(false, BOOL)
    val hideGLMessages by register<Boolean>(false, BOOL)

    // Round Info HUD
    val displayHud by register<Boolean>(true, BOOL)

    val hudLocation by register<HudLocation>(HudLocation.TOP_LEFT, HudLocation.CODEC)
    val hudXMargin by register<Int>(10, INT)
    val hudYMargin by register<Int>(10, INT)
    val hudScale by register<Float>(1f, FLOAT)

    val useAccentColor by register<Boolean>(false, BOOL)
    val accentColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)

    val hudElements by register<List<HudElements>>(listOf(
        HudElements.ROUND_NUMBER, HudElements.PLAYERS_ALIVE, HudElements.MACE_CHANCE,
        HudElements.ELIMINATIONS, HudElements.PLAYTIME, HudElements.MODIFIERS), HudElements.CODEC.listOf())

    val showNewEventToasts by register<Boolean>(true, BOOL)
    val showBountyToasts by register<Boolean>(true, BOOL)

    // Misc. Config
    val useFlint by register<Boolean>(true, BOOL)
    val plotIds by register<List<String>>(listOf("mace", "maceroulette", "statless", "14000004"), STRING.listOf())

    // NESTED CONFIG ===========================================================
    val boosterListMax by register<Int>(5, INT)
    val use2dHeads by register<Boolean>(false, BOOL)

    val chanceUseColor by register<Boolean>(true, BOOL)
    val hideMaceChanceWhenEliminated by register<Boolean>(false, BOOL)

    fun getAccentStyle(): Style {
        return Style.EMPTY.withColor(accentColor.value.rgb and 0x00ffffff)
    }

    fun getAccentStyle(defaultColor: Int): Style {
        return getAccentStyle().let { if (!useAccentColor.value) it.withColor(defaultColor) else it }
    }

    fun getAccentStyle(defaultColor: Style) : Style {
        return defaultColor.let { if (useAccentColor.value) it.withColor(accentColor.value.rgb and 0x00ffffff) else it }
    }
}