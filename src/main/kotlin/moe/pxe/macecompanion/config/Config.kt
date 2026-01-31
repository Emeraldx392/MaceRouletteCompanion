package moe.pxe.macecompanion.config

import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.config.v3.register
import moe.pxe.macecompanion.enums.HudElements
import moe.pxe.macecompanion.enums.HudLocation
import net.fabricmc.loader.api.FabricLoader

object Config : JsonFileCodecConfig<Config>(FabricLoader.getInstance().configDir.resolve("mrc.json")) {
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

    val hudElements by register<List<HudElements>>(listOf(
        HudElements.ROUND_NUMBER, HudElements.PLAYERS_ALIVE, HudElements.ELIMINATIONS,
        HudElements.PLAYTIME, HudElements.MODIFIERS), HudElements.CODEC.listOf())

    // Misc. Config
    val useFlint by register<Boolean>(true, BOOL)
    val plotIds by register<List<String>>(listOf("mace", "maceroulette", "statless", "14000004"), STRING.listOf())

    // NESTED CONFIG ===========================================================
    val boosterListMax by register<Int>(5, INT)
}