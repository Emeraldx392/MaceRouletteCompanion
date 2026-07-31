package moe.pxe.macecompanion.config.controllers

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.utils.Dimension
import dev.isxander.yacl3.gui.AbstractWidget
import dev.isxander.yacl3.gui.YACLScreen
import dev.isxander.yacl3.gui.controllers.cycling.EnumController

class EnumWithConfigController<T>(val opt: Option<T>, cls: Class<T>) : EnumController<T>(opt, cls) where T : Enum<T>, T: ConfigurableEnum {
    override fun provideWidget(screen: YACLScreen, widgetDimension: Dimension<Int>): AbstractWidget {
        return ConfigurableOptionElement(
            screen,
            { opt.pendingValue().generateConfig(screen) },
            super.provideWidget(screen, widgetDimension),
            opt
        )
    }
}