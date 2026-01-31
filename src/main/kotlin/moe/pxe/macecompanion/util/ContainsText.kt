package moe.pxe.macecompanion.util

import net.minecraft.text.Text

object ContainsText {
    fun strict(text: Text, subtext: Text): Boolean {
        if (text.string.contains(subtext.string) && text.style.equals(subtext.style)) {
            return true
        }
        for (child in text.siblings) {
            if (strict(child, subtext)) return true
        }
        return false
    }

    fun boldString(text: Text, subtext: String): Boolean {
        if (text.string.contains(subtext) && text.style.isBold) {
            return true
        }
        for (child in text.siblings) {
            if (boldString(child, subtext)) return true
        }
        return false
    }
}