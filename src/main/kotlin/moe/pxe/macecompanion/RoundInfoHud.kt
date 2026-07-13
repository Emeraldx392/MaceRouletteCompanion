package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier

object RoundInfoHud {
    fun registerListener() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("macecompanion","round_info")
        ) { context, tickCounter ->
            if (!Config.displayHud.value) return@attachElementBefore
            if (Minecraft.getInstance().debugEntries.isOverlayVisible) return@attachElementBefore

            val window = Minecraft.getInstance().window
            val scale = Config.hudScale.value

            //RIGHT HUD
            context.pose().pushMatrix()
            context.pose().scale(scale)

            val rightX = window.guiScaledWidth.toFloat() / scale
            val rightY = if (Config.rightHudLocation.value.bottomAligned) window.guiScaledHeight.toFloat() / scale else 0f
            context.pose().translate(rightX, rightY)

            val padXRight = -Config.hudXMargin.value.toFloat() / scale
            val padYRight = Config.hudYMargin.value.toFloat() * (if (Config.rightHudLocation.value.bottomAligned) -1f else 1f) / scale
            context.pose().translate(padXRight, padYRight)

            var yOffset = 0
            var rightElements = Config.rightHudElements.value
            if (Config.rightHudLocation.value.bottomAligned) rightElements = rightElements.reversed()
            rightElements.forEach {
                yOffset += it.render(context, yOffset, true, Config.rightHudLocation.value.bottomAligned)
            }

            context.pose().popMatrix()

            //LEFT HUD
            context.pose().pushMatrix()
            context.pose().scale(scale)

            // Translate to the left edge (0) and top/bottom
            val leftY = if (Config.leftHudLocation.value.bottomAligned) window.guiScaledHeight.toFloat() / scale else 0f
            context.pose().translate(0f, leftY)

            // Apply padding offset
            val padXLeft = Config.hudXMargin.value.toFloat() / scale
            val padYLeft = Config.hudYMargin.value.toFloat() * (if (Config.leftHudLocation.value.bottomAligned) -1f else 1f) / scale
            context.pose().translate(padXLeft, padYLeft)

            yOffset = 0
            var leftElements = Config.leftHudElements.value
            if (Config.leftHudLocation.value.bottomAligned) leftElements = leftElements.reversed()

            leftElements.forEach {
                yOffset += it.render(context, yOffset, false, Config.leftHudLocation.value.bottomAligned)
            }
            context.pose().popMatrix()
        }
    }
}