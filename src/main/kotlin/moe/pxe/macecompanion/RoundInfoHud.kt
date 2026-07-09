package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier

object RoundInfoHud {
    fun registerListener() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of("macecompanion","round_info")
        ) { context, tickCounter ->
            if (!Config.displayHud.value) return@attachElementBefore
//            if (MinecraftClient.getInstance().debugHud.shouldShowDebugHud()) return@attachElementBefore
            if (MinecraftClient.getInstance().debugHudEntryList.isF3Enabled) return@attachElementBefore
            if (!StateManager.gameOngoing) return@attachElementBefore

            val window = MinecraftClient.getInstance().window
            val scale = Config.hudScale.value

            //RIGHT HUD
            context.matrices.pushMatrix()
            context.matrices.scale(scale)

            val rightX = window.scaledWidth.toFloat() / scale
            val rightY = if (Config.rightHudLocation.value.bottomAligned) window.scaledHeight.toFloat() / scale else 0f
            context.matrices.translate(rightX, rightY)

            val padXRight = -Config.hudXMargin.value.toFloat() / scale
            val padYRight = Config.hudYMargin.value.toFloat() * (if (Config.rightHudLocation.value.bottomAligned) -1f else 1f) / scale
            context.matrices.translate(padXRight, padYRight)

            var yOffset = 0
            var rightElements = Config.rightHudElements.value
            if (Config.rightHudLocation.value.bottomAligned) rightElements = rightElements.reversed()
            rightElements.forEach {
                yOffset += it.render(context, yOffset, true, Config.rightHudLocation.value.bottomAligned)
            }

            context.matrices.popMatrix()

            //LEFT HUD
            context.matrices.pushMatrix()
            context.matrices.scale(scale)

            // Translate to the left edge (0) and top/bottom
            val leftY = if (Config.leftHudLocation.value.bottomAligned) window.scaledHeight.toFloat() / scale else 0f
            context.matrices.translate(0f, leftY)

            // Apply padding offset
            val padXLeft = Config.hudXMargin.value.toFloat() / scale
            val padYLeft = Config.hudYMargin.value.toFloat() * (if (Config.leftHudLocation.value.bottomAligned) -1f else 1f) / scale
            context.matrices.translate(padXLeft, padYLeft)

            yOffset = 0
            var leftElements = Config.leftHudElements.value
            if (Config.leftHudLocation.value.bottomAligned) leftElements = leftElements.reversed()

            leftElements.forEach {
                yOffset += it.render(context, yOffset, false, Config.leftHudLocation.value.bottomAligned)
            }
            context.matrices.popMatrix()
        }
    }
}