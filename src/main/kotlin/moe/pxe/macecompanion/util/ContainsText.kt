package moe.pxe.macecompanion.util

import net.minecraft.network.chat.Component

object ContainsText {
    fun strict(text: Component, subtext: Component): Boolean {
        if (text.string.contains(subtext.string) && text.style == subtext.style) {
            return true
        }
        for (child in text.siblings) {
            if (strict(child, subtext)) return true
        }
        return false
    }

    fun boldString(text: Component, subtext: String): Boolean {
        if (text.string.contains(subtext) && text.style.isBold) {
            return true
        }
        for (child in text.siblings) {
            if (boldString(child, subtext)) return true
        }
        return false
    }
}