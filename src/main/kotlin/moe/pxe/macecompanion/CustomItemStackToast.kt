package moe.pxe.macecompanion

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.toasts.Toast
import net.minecraft.client.gui.components.toasts.ToastManager
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import kotlin.math.max

class CustomItemStackToast(
    private val itemStack: ItemStack,
    private val title: Component,
    private val description: Component? = null,
) : Toast {
    private val backgroundSprite: Identifier = Identifier.withDefaultNamespace("toast/advancement")
    private lateinit var wantedVisibility: Toast.Visibility
    val displayTime = 5000L
    private var width = 160

    override fun extractRenderState(graphics: GuiGraphicsExtractor, font: Font, fullyVisibleForMs: Long){
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, backgroundSprite, 0, 0, this.width(), this.height())

        if (description == null) graphics.text(font, title, 30, 12, -1)
        else {
            graphics.text(font, title, 30, 7, -1)
            graphics.text(font, description, 30, 18, -1)
        }
        graphics.item(itemStack, 8, 8)
    }

    override fun getWantedVisibility(): Toast.Visibility {
        return this.wantedVisibility
    }

    override fun update(manager: ToastManager, fullyVisibleForMs: Long) {
        this.wantedVisibility = if (fullyVisibleForMs.toDouble() >= displayTime * manager.notificationDisplayTimeMultiplier) Toast.Visibility.HIDE else Toast.Visibility.SHOW
        this.recalculateWidth()
    }

    override fun width(): Int {
        return this.width
    }

    fun recalculateWidth() {
        val font = Minecraft.getInstance().font
        val width = if (description == null) font.width(title) else max(font.width(title), font.width(description))
        this.width = width + 40
        //Minecraft.getInstance().player?.sendSystemMessage(Component.literal("[${title.string}] Width: $width"))
    }
}
