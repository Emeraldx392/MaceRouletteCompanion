package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl
import net.minecraft.text.Text

class FormattedStringControllerBuilder(val opt: Option<String>) : AbstractControllerBuilderImpl<String>(opt), StringControllerBuilder {
    private var formatter = FormattedStringController.DEFAULT_FORMATTER

    fun valueFormatter(func: (String) -> Text): FormattedStringControllerBuilder {
        formatter = func
        return this
    }

    override fun build(): Controller<String> {
        return FormattedStringController(opt, formatter)
    }

    companion object {
        fun create(opt: Option<String>): FormattedStringControllerBuilder {
            return FormattedStringControllerBuilder(opt)
        }
    }
}