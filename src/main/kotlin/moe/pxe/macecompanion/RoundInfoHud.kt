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

            // Scale + Alignment
            context.matrices.pushMatrix()
            context.matrices.scale(Config.hudScale.value)
            context.matrices.translate(
                if (Config.hudLocation.value.rightAligned) window.scaledWidth.div(Config.hudScale.value) else 0f,
                if (Config.hudLocation.value.bottomAligned) window.scaledHeight.div(Config.hudScale.value) else 0f
            )
            // Padding
            context.matrices.pushMatrix()
            context.matrices.translate(
                Config.hudXMargin.value.times(if (Config.hudLocation.value.rightAligned) -1f else 1f).div(Config.hudScale.value),
                Config.hudYMargin.value.times(if (Config.hudLocation.value.bottomAligned) -1f else 1f).div(Config.hudScale.value),
            )

            var yOffset = 0
            var elements = Config.hudElements.value
            if (Config.hudLocation.value.bottomAligned) elements = elements.reversed()
            elements.forEach {
                yOffset += it.render(context, yOffset, Config.hudLocation.value.rightAligned, Config.hudLocation.value.bottomAligned)
            }

            context.matrices.popMatrix()
            context.matrices.popMatrix()
        }
    }
}