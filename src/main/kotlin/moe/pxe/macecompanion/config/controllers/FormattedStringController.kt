package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.controllers.string.IStringController
import net.minecraft.text.Text

class FormattedStringController(val opt: Option<String>, val formatter: (String) -> Text) : IStringController<String> {

    constructor(opt: Option<String>) : this(opt, DEFAULT_FORMATTER)

    override fun getString(): String {
        return opt.pendingValue()
    }

    override fun setFromString(value: String) {
        opt.requestSet(value)
    }

    override fun option(): Option<String> {
        return opt
    }

    override fun formatValue(): Text = formatter(string)

    companion object {
        val DEFAULT_FORMATTER: (String) -> Text = { it: String -> Text.literal(it) }
    }
}