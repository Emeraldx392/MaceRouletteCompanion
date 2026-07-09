package moe.pxe.macecompanion.enums

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable

enum class HudLocation : NameableEnum, StringIdentifiable {
    TOP {
        override val bottomAligned = false
    },
    BOTTOM {
        override val bottomAligned = true
    };
    abstract val bottomAligned: Boolean
    override fun asString(): String = name
    override fun getDisplayName(): Text = Text.translatable("mrc.hudlocation.${name.lowercase()}")

    companion object {
        val CODEC = StringIdentifiable.createCodec(::values)
    }
}