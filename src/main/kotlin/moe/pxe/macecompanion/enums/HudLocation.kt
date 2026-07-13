package moe.pxe.macecompanion.enums

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable

enum class HudLocation : NameableEnum, StringRepresentable {
    TOP {
        override val bottomAligned = false
    },
    BOTTOM {
        override val bottomAligned = true
    };
    abstract val bottomAligned: Boolean
    override fun getSerializedName(): String = name
    override fun getDisplayName(): Component = Component.translatable("mrc.hudlocation.${name.lowercase()}")

    companion object {
        val CODEC = StringRepresentable.fromEnum(::values)
    }
}