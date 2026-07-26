package moe.pxe.macecompanion.util

import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack

object PlayerInventory {
    fun getPlayerSlotItemStack(slot: Int): ItemStack {
        val client = Minecraft.getInstance()
        val playerInventory = client.player?.inventory
        val itemStack: ItemStack = playerInventory?.getSlot(slot)?.get() ?: ItemStack.EMPTY
        return itemStack
    }
}