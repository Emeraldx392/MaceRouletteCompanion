package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.TooltipButtonWidget
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.input.MouseButtonEvent
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.components.events.ContainerEventHandler
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.input.CharacterEvent
import net.minecraft.client.input.KeyEvent
import net.minecraft.network.chat.Component
import kotlin.math.min

class ConfigurableOptionElement : AbstractWidget, ContainerEventHandler {
    val configureScreen: () -> Screen?
    val entryWidget: AbstractWidget
    val opt: Option<*>

    private var focused: GuiEventListener? = null
    private var dragging = false
    private val optionNameString: String

    constructor(
        screen: YACLScreen,
        configureScreen: () -> Screen?,
        entryWidget: AbstractWidget,
        opt: Option<*>
    ) : super(
        entryWidget.dimension.withHeight(min(entryWidget.dimension.height(), 20))
    ) {
        this.configureScreen = configureScreen
        this.entryWidget = entryWidget
        this.opt = opt
        this.optionNameString = opt.name().string.lowercase()

        val dim = entryWidget.dimension
        dimension = dim
        entryWidget.dimension = dim.expanded(-20, 0)

        this.configureButton = TooltipButtonWidget(
            screen,
            dim.xLimit() - 20,
            dim.y(),
            20,
            20,
            Component.literal("\u2630"),
            Component.translatable("yacl.configurable.configure")
        ) { _ ->
            configureScreen()?.let { Minecraft.getInstance().setScreen(it) }
            updateButtonStates()
        }

        updateButtonStates()

    }

    val configureButton: TooltipButtonWidget

    override fun children(): List<GuiEventListener> {
        return listOf(entryWidget, configureButton)
    }

    override fun mouseClicked(click: MouseButtonEvent, doubled: Boolean): Boolean {
        val returnValue = super<ContainerEventHandler>.mouseClicked(click, doubled)
        updateButtonStates()
        return returnValue
    }

    override fun mouseReleased(click: MouseButtonEvent): Boolean {
        return super<ContainerEventHandler>.mouseReleased(click)
    }

    override fun mouseDragged(
        click: MouseButtonEvent,
        offsetX: Double,
        offsetY: Double
    ): Boolean {
        return super<ContainerEventHandler>.mouseDragged(click, offsetX, offsetY)
    }

    override fun isDragging(): Boolean {
        return dragging
    }

    override fun setDragging(dragging: Boolean) {
        this.dragging = dragging
    }

    override fun keyPressed(input: KeyEvent): Boolean {
        val returnValue = super<ContainerEventHandler>.keyPressed(input)
        updateButtonStates()
        return returnValue
    }

    override fun keyReleased(input: KeyEvent): Boolean {
        return super<ContainerEventHandler>.keyReleased(input)
    }

    override fun charTyped(input: CharacterEvent): Boolean {
        return super<ContainerEventHandler>.charTyped(input)
    }

    override fun getFocused(): GuiEventListener? {
        return focused
    }

    override fun setFocused(focused: GuiEventListener?) {
        this.focused = focused
    }

    override fun extractRenderState(
        context: GuiGraphicsExtractor,
        mouseX: Int,
        mouseY: Int,
        deltaTicks: Float
    ) {
        configureButton.x = dimension.xLimit() - 20
        configureButton.y = dimension.y()
//        entryWidget.dimension = entryWidget.dimension.withY(dimension.y())
        entryWidget.dimension = dimension.expanded(-20, 0)

        configureButton.extractRenderState(context, mouseX, mouseY, deltaTicks)
        entryWidget.extractRenderState(context, mouseX, mouseY, deltaTicks)
    }

    private fun updateButtonStates() {
        configureButton.active = opt.available() && configureScreen() != null
    }

    override fun matchesSearch(query: String): Boolean {
        return optionNameString.contains(query.lowercase())
    }

}