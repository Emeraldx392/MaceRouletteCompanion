package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.controllers.string.IStringController
import net.minecraft.network.chat.Component

class FormattedStringController(val opt: Option<String>, val formatter: (String) -> Component) : IStringController<String> {

    override fun getString(): String {
        return opt.pendingValue()
    }

    override fun setFromString(value: String) {
        opt.requestSet(value)
    }

    override fun option(): Option<String> {
        return opt
    }

    override fun formatValue(): Component = formatter(string)

    companion object {
        val DEFAULT_FORMATTER: (String) -> Component = { it: String -> Component.literal(it) }
    }
}