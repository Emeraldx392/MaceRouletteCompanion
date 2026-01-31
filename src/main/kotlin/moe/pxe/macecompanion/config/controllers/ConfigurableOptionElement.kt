package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.TooltipButtonWidget
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.Click
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.input.CharInput
import net.minecraft.client.input.KeyInput
import net.minecraft.text.Text
import kotlin.math.min

class ConfigurableOptionElement : AbstractWidget, ParentElement {
    val configureScreen: () -> Screen?
    val entryWidget: AbstractWidget
    val opt: Option<*>

    private var focused: Element? = null
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
            Text.literal("\u2630"),
            Text.translatable("yacl.configurable.configure")
        ) { button ->
            configureScreen()?.let { MinecraftClient.getInstance().setScreen(it) }
            updateButtonStates()
        }

        updateButtonStates()
    }

    val configureButton: TooltipButtonWidget

    override fun children(): List<Element> {
        return listOf(entryWidget, configureButton)
    }

    override fun mouseClicked(click: Click, doubled: Boolean): Boolean {
        return super<ParentElement>.mouseClicked(click, doubled)
    }

    override fun mouseReleased(click: Click): Boolean {
        return super<ParentElement>.mouseReleased(click)
    }

    override fun mouseDragged(
        click: Click,
        offsetX: Double,
        offsetY: Double
    ): Boolean {
        return super<ParentElement>.mouseDragged(click, offsetX, offsetY)
    }

    override fun isDragging(): Boolean {
        return dragging
    }

    override fun setDragging(dragging: Boolean) {
        this.dragging = dragging
    }

    override fun keyPressed(input: KeyInput?): Boolean {
        return super<ParentElement>.keyPressed(input)
    }

    override fun keyReleased(input: KeyInput?): Boolean {
        return super<ParentElement>.keyReleased(input)
    }

    override fun charTyped(input: CharInput?): Boolean {
        return super<ParentElement>.charTyped(input)
    }

    override fun getFocused(): Element? {
        return focused
    }

    override fun setFocused(focused: Element?) {
        this.focused = focused
    }

    override fun render(
        context: DrawContext?,
        mouseX: Int,
        mouseY: Int,
        deltaTicks: Float
    ) {
        updateButtonStates()

        configureButton.x = dimension.xLimit() - 20
        configureButton.y = dimension.y()
//        entryWidget.dimension = entryWidget.dimension.withY(dimension.y())
        entryWidget.dimension = dimension.expanded(-20, 0)

        configureButton.render(context, mouseX, mouseY, deltaTicks)
        entryWidget.render(context, mouseX, mouseY, deltaTicks)
    }

    private fun updateButtonStates() {
        configureButton.active = opt.available() && configureScreen() != null
    }

    override fun matchesSearch(query: String): Boolean {
        return optionNameString.contains(query.lowercase())
    }

}