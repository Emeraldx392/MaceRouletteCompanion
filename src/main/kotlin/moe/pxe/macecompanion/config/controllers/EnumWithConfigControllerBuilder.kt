package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.impl.controller.AbstractControllerBuilderImpl

class EnumWithConfigControllerBuilder<T>(val opt: Option<T>) : AbstractControllerBuilderImpl<T>(opt), EnumControllerBuilder<T> where T : Enum<T>, T: ConfigurableEnum {
    lateinit var enumClass: Class<T>
    var formatter: ValueFormatter<T>? = null

    override fun enumClass(enumClass: Class<T>): EnumControllerBuilder<T> {
        this.enumClass = enumClass
        return this
    }

    override fun formatValue(formatter: ValueFormatter<T>): EnumControllerBuilder<T> {
        this.formatter = formatter
        return this
    }

    override fun build(): Controller<T> {
        return EnumWithConfigController(opt, enumClass)
    }

    companion object {
        fun <T> create(opt: Option<T>): EnumWithConfigControllerBuilder<T> where T : Enum<T>, T : ConfigurableEnum {
            return EnumWithConfigControllerBuilder(opt)
        }
    }
}