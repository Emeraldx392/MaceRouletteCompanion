package moe.pxe.macecompanion.config

import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import dev.isxander.yacl3.config.v3.JsonFileCodecConfig
import dev.isxander.yacl3.config.v3.register
import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.enums.BetConditions
import moe.pxe.macecompanion.enums.HudElements
import moe.pxe.macecompanion.enums.HudLocation
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
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

    // AutoGG + AutoGL
    val useAutoGG by register<Boolean>(true, BOOL)
    val autoGGStrings by register<List<String>>(listOf("gg", "good game"), STRING.listOf())
    val ggDelayTicks by register<Int>(10, INT)

    val useAutoGL by register<Boolean>(true, BOOL)
    val autoGLStrings by register<List<String>>(listOf("gl", "hf", "glhf", "good luck", "have fun", "good luck have fun"), STRING.listOf())
    val glDelayTicks by register<Int>(60, INT)

    val hideGGMessages by register<Boolean>(false, BOOL)
    val hideGLMessages by register<Boolean>(false, BOOL)

    // AutoBet
    val useAutoBet by register<Boolean>(true, BOOL)
    val autoBetDelayTicks by register<Int>(40, INT)
    val autoBetConditions by register<List<BetConditions>>(listOf(BetConditions.IS_MOST_VOTED, BetConditions.CHOOSE_RANDOMLY), BetConditions.CODEC.listOf())

    // Round Info HUD
    val displayHud by register<Boolean>(true, BOOL)

    val rightHudLocation by register<HudLocation>(HudLocation.TOP, HudLocation.CODEC)
    val leftHudLocation by register<HudLocation>(HudLocation.TOP, HudLocation.CODEC)
    val hudXMargin by register<Int>(10, INT)
    val hudYMargin by register<Int>(10, INT)
    val hudScale by register<Float>(1f, FLOAT)

    val useAccentColors by register<Boolean>(false, BOOL)
    val accentColorNumber by register<Int>(1, INT)
    val mainAccentColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)
    val secondAccentColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)
    val thirdAccentColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)

    val leftHudElements by register<List<HudElements>>(listOf(
        HudElements.ROUND_NUMBER, HudElements.PLAYERS_ALIVE, HudElements.MACE_CHANCE,HudElements.ACCURACY,
        HudElements.ELIMINATIONS, HudElements.STAR_FRAGMENTS, HudElements.PLAYTIME, HudElements.MODIFIERS), HudElements.CODEC.listOf())
    val rightHudElements by register<List<HudElements>>(listOf(HudElements.PING, HudElements.FPS, HudElements.SUMMER_POINTS, HudElements.BOUNTY_BOARD), HudElements.CODEC.listOf())
    // Toasts
    val showNewEventToasts by register<Boolean>(true, BOOL)
    val showConsumableToasts by register<Boolean>(true, BOOL)
    val showBountyToasts by register<Boolean>(true, BOOL)
    val showPlayerToasts by register<Boolean>(true, BOOL)
    val hidePlayerJoinedLeftMessages by register<Boolean>(false, BOOL)
    val playerStrings by register<List<String>>(listOf("flopsuh"), STRING.listOf())

    // Misc. Config
    val plotIds by register<List<String>>(listOf("mace", "statless"), STRING.listOf())

    // NESTED CONFIG ===========================================================
    val overrideRoundColors by register<Boolean>(false, BOOL)
    val roundNumberColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)
    val roundTextColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)

    val overridePlayerCountColors by register<Boolean>(false, BOOL)
    val alivePlayersColor by register<Color>(Color(0xd5fcf5), RGB_COLOR_CODEC)
    val totalPlayersColor by register<Color>(Color(0xd0d0d0), RGB_COLOR_CODEC)
    val playerCountTextColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)

    val overrideAccuracyColors by register<Boolean>(false, BOOL)
    val accuracyColor by register<Color>(Color(0x79fc00), RGB_COLOR_CODEC)
    val accuracyIconColor by register<Color>(Color(0x79fc00), RGB_COLOR_CODEC)
    val accuracyTextColor by register<Color>(Color(0x79fc00), RGB_COLOR_CODEC)

    val hideEliminationsWhenEliminated by register<Boolean>(false, BOOL)
    val overrideEliminationsColors by register<Boolean>(false, BOOL)
    val eliminationsNumberColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)
    val eliminationsIconColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)
    val eliminationsTextColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)

    val hideStarFragmentsWhenEliminated by register<Boolean>(true, BOOL)
    val overrideStarFragmentsColors by register<Boolean>(false, BOOL)
    val starFragmentsNumberColor by register<Color>(Color(0xa0f9ff), RGB_COLOR_CODEC)
    val starFragmentsIconColor by register<Color>(Color(0xa0f9ff), RGB_COLOR_CODEC)
    val starFragmentsTextColor by register<Color>(Color(0xa0f9ff), RGB_COLOR_CODEC)

    val overridePlaytimeColors by register<Boolean>(false, BOOL)
    val playtimeColor by register<Color>(Color(0x3efca1), RGB_COLOR_CODEC)
    val playtimeIconColor by register<Color>(Color(0x3efca1), RGB_COLOR_CODEC)
    val playtimeTextColor by register<Color>(Color(0x3efca1), RGB_COLOR_CODEC)


    val overrideModifiersColors by register<Boolean>(false, BOOL)
    val modifiersTextColor by register<Color>(Color(0xa63efc), RGB_COLOR_CODEC)
    val normalModifierTextColor by register<Color>(Color.YELLOW, RGB_COLOR_CODEC)
    val eternalModifierTextColor by register<Color>(Color.WHITE, RGB_COLOR_CODEC)
    val eternalModifierTextShadowColor by register<Color>(Color(-10071549), RGB_COLOR_CODEC)
    val chargedModifierTextColor by register<Color>(Color(0x0786FF), RGB_COLOR_CODEC)
    val mysteryModifierTextColor by register<Color>(Color(0xD2B5FF), RGB_COLOR_CODEC)
    val boosterListMax by register<Int>(5, INT)
    val customModifierIcons by register<Boolean>(false, BOOL)
    val use2dHeads by register<Boolean>(false, BOOL)

    val overrideMaceChanceColors by register<Boolean>(false, BOOL)
    val maceChanceNumberColor by register<Color>(Color(0x42C1FF), RGB_COLOR_CODEC)
    val maceChanceIconColor by register<Color>(Color(0x42C1FF), RGB_COLOR_CODEC)
    val maceChanceTextColor by register<Color>(Color(0x42C1FF), RGB_COLOR_CODEC)
    val hideMaceChanceWhenEliminated by register<Boolean>(false, BOOL)

    val overrideBountyBoardColors by register<Boolean>(false, BOOL)
    val bountyBoardNumberColor by register<Color>(Color(0xff7cf4), RGB_COLOR_CODEC)
    val bountyBoardPlayerColor by register<Color>(Color(CommonColors.YELLOW), RGB_COLOR_CODEC)
    val bountyBoardTextColor by register<Color>(Color(0xff7cf4), RGB_COLOR_CODEC)
    val bountyBoardMaxPlayers by register<Int>(3, INT)
    val bountyBoardMinBounty by register<Int>(1, INT)

    val overrideFpsColors by register<Boolean>(false, BOOL)
    val fpsNumberColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val fpsTextColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    val overridePingColors by register<Boolean>(false, BOOL)
    val pingNumberColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)
    val pingTextColor by register<Color>(Color(CommonColors.WHITE), RGB_COLOR_CODEC)

    val overrideTpsColors by register<Boolean>(false, BOOL)
    val tpsNumberColor by register<Color>(Color(0xbfff00), RGB_COLOR_CODEC)
    val tpsTextColor by register<Color>(Color(0xbfff00), RGB_COLOR_CODEC)

    fun getRoundTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideRoundColors.value) it.withColor(roundTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getRoundNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideRoundColors.value) it.withColor(roundNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value){ if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getPlayerCountTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePlayerCountColors.value) it.withColor(playerCountTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getAlivePLayersAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePlayerCountColors.value) it.withColor(alivePlayersColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getTotalPLayersAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePlayerCountColors.value) it.withColor(totalPlayersColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getAccuracyTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideAccuracyColors.value) it.withColor(accuracyTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getAccuracyAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideAccuracyColors.value) it.withColor(accuracyColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getAccuracyIconAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideAccuracyColors.value) it.withColor(accuracyIconColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getEliminationsTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideEliminationsColors.value) it.withColor(eliminationsTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getEliminationsNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideEliminationsColors.value) it.withColor(eliminationsNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getEliminationsIconAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideEliminationsColors.value) it.withColor(eliminationsIconColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else  it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getSummerPointsTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let {  if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getSummerPointsNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getSummerPointsIconAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else  it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getStarFragmentsTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideStarFragmentsColors.value) it.withColor(starFragmentsTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getStarFragmentsNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideStarFragmentsColors.value) it.withColor(starFragmentsNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getStarFragmentsIconAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideStarFragmentsColors.value) it.withColor(starFragmentsIconColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else  it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getPlaytimeTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePlaytimeColors.value) it.withColor(playtimeTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getPlaytimeNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePlaytimeColors.value) it.withColor(playtimeColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getPlaytimeIconAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePlaytimeColors.value) it.withColor(playtimeIconColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else  it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getModifiersTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideModifiersColors.value) it.withColor(modifiersTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getNormalModifierTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideModifiersColors.value) it.withColor(normalModifierTextColor.value.rgb and 0x00ffffff) else it}
    }
    fun getEternalModifierTextWithShadowAccentStyle(defaultColor: Int, defaultShadowColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor).withShadowColor(defaultShadowColor)
        return defaultStyle.let { if (overrideModifiersColors.value) it.withColor(eternalModifierTextColor.value.rgb and 0x00ffffff).withShadowColor(eternalModifierTextShadowColor.value.rgb and 0x00ffffff) else it}
    }
    fun getChargedModifierTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideModifiersColors.value) it.withColor(chargedModifierTextColor.value.rgb and 0x00ffffff) else it}
    }
    fun getMysteryModifierTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideModifiersColors.value) it.withColor(mysteryModifierTextColor.value.rgb and 0x00ffffff) else it}
    }

    fun getMaceChanceTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideMaceChanceColors.value) it.withColor(maceChanceTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getMaceChanceNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideMaceChanceColors.value) it.withColor(maceChanceNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getMaceChanceIconAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideMaceChanceColors.value) it.withColor(maceChanceIconColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else  it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getBountyBoardTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideBountyBoardColors.value) it.withColor(bountyBoardTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getBountyBoardAmountAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideBountyBoardColors.value) it.withColor(bountyBoardNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 2) it.withColor(thirdAccentColor.value.rgb and 0x00ffffff) else  it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
    fun getBountyBoardPlayerAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideBountyBoardColors.value) it.withColor(bountyBoardPlayerColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getFpsTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideFpsColors.value) it.withColor(fpsTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getFpsNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideFpsColors.value) it.withColor(fpsNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getPingTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePingColors.value) it.withColor(pingTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getPingNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overridePingColors.value) it.withColor(pingNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }

    fun getTpsTextAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideTpsColors.value) it.withColor(tpsTextColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) it.withColor(mainAccentColor.value.rgb and 0x00ffffff) else it}
    }
    fun getTpsNumberAccentStyle(defaultColor: Int) : Style {
        val defaultStyle = Style.EMPTY.withColor(defaultColor)
        return defaultStyle.let { if (overrideTpsColors.value) it.withColor(tpsNumberColor.value.rgb and 0x00ffffff) else if (useAccentColors.value) { if(accentColorNumber.value > 1) it.withColor(secondAccentColor.value.rgb and 0x00ffffff) else it.withColor(mainAccentColor.value.rgb and 0x00ffffff)} else it}
    }
}