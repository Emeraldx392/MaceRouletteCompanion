package moe.pxe.macecompanion.util

import moe.pxe.macecompanion.MaceCompanion
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.UseCooldown
import java.util.Optional

/**
 * Convenience helpers for building ItemStack icons with custom item models and optional extra init.
 */

fun Item.customIcon(modelPath: String, init: (ItemStack.() -> Unit)? = null): ItemStack =
    defaultInstance.apply {
        set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, modelPath))
        init?.invoke(this)
    }

fun Item.customIconWithCooldown(modelPath: String): ItemStack =
    customIcon(modelPath) {
        set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
    }

fun ItemStack.withModel(modelPath: String): ItemStack = apply {
    set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, modelPath))
}
