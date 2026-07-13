package moe.pxe.macecompanion.config.controllers

import net.minecraft.client.gui.screens.Screen

interface ConfigurableEnum {
    fun generateConfig(parent: Screen): Screen?
}