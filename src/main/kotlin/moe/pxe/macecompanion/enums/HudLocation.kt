package moe.pxe.macecompanion.enums

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.text.Text
import net.minecraft.util.StringIdentifiable

enum class HudLocation : NameableEnum, StringIdentifiable {
    TOP_LEFT {
        override val bottomAligned = false
        override val rightAligned = false
    },

    TOP_RIGHT {
        override val bottomAligned = false
        override val rightAligned = true
    },

    BOTTOM_LEFT {
        override val bottomAligned = true
        override val rightAligned = false
    },

    BOTTOM_RIGHT {
        override val bottomAligned = true
        override val rightAligned = true
    };

    abstract val bottomAligned: Boolean
    abstract val rightAligned: Boolean
    override fun asString(): String = name
    override fun getDisplayName(): Text = Text.translatable("mrc.hudlocation.${name.lowercase()}")

    companion object {
        val CODEC = StringIdentifiable.createCodec(::values)
    }
}