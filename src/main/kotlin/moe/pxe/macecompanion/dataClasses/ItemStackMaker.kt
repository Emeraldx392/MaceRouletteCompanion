package moe.pxe.macecompanion.dataClasses

import com.google.gson.JsonElement
import com.mojang.serialization.JsonOps
import net.minecraft.client.Minecraft
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.ItemEnchantments

data class ItemStackMaker(
    val itemHolder: Holder<Item>,
    val dataComponents: DataComponentPatch? = null,
    val count: Int = 1,
    val rawStoredEnchantmentsJson: JsonElement? = null,
    val rawEnchantmentsJson: JsonElement? = null
) {
    fun createItemStack(): ItemStack {
        val itemStack = ItemStack(itemHolder)

        if (dataComponents != null) itemStack.applyComponents(dataComponents)

        val registryAccess = Minecraft.getInstance().level?.registryAccess() ?: Minecraft.getInstance().connection?.registryAccess()

        if (registryAccess != null) {
            val registryOps = RegistryOps.create(JsonOps.INSTANCE, registryAccess)

            if (rawStoredEnchantmentsJson != null) {
                ItemEnchantments.CODEC.parse(registryOps, rawStoredEnchantmentsJson)
                    .result()
                    .ifPresent { parsedEnchants ->
                        itemStack.set(DataComponents.STORED_ENCHANTMENTS, parsedEnchants)
                    }
            }

            if (rawEnchantmentsJson != null) {
                ItemEnchantments.CODEC.parse(registryOps, rawEnchantmentsJson)
                    .result()
                    .ifPresent { parsedEnchants ->
                        itemStack.set(DataComponents.ENCHANTMENTS, parsedEnchants)
                    }
            }
        }
        itemStack.count = count
        return itemStack
    }

    companion object {
        fun fromJson(json: JsonElement): ItemStackMaker {
            val originalObj = json.asJsonObject
            val newObj = originalObj.deepCopy()

            val storedEnchantsJson = originalObj.get("stored_enchantments") ?: originalObj.get("minecraft:stored_enchantments")
            val enchantsJson = originalObj.get("enchantments") ?: originalObj.get("minecraft:enchantments")

            newObj.remove("id")
            newObj.remove("count")
            newObj.remove("stored_enchantments")
            newObj.remove("minecraft:stored_enchantments")
            newObj.remove("enchantments")
            newObj.remove("minecraft:enchantments")

            val identifier = Identifier.parse(originalObj.get("id").asString)
            val itemHolder = BuiltInRegistries.ITEM.get(identifier).orElseThrow {
                IllegalArgumentException("'$identifier' is not a valid item!")
            }
            val itemCount = if (originalObj.has("count")) originalObj.get("count").asInt else 1

            val dataResult = DataComponentPatch.CODEC.parse(JsonOps.INSTANCE, newObj).getOrThrow { errorMessage -> IllegalArgumentException("Failed to parse data components from JSON: $errorMessage") }

            return ItemStackMaker(
                itemHolder = itemHolder,
                dataComponents = dataResult,
                count = itemCount,
                rawStoredEnchantmentsJson = storedEnchantsJson,
                rawEnchantmentsJson = enchantsJson
            )
        }

        fun fromString(string: String): ItemStackMaker {
            val itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(string)).get()
            return ItemStackMaker(itemHolder = itemHolder)
        }

        fun fromItemIdAndPatch(string: String, dataComponentPatch: DataComponentPatch): ItemStackMaker {
            val itemHolder = BuiltInRegistries.ITEM.get(Identifier.parse(string)).get()
            return ItemStackMaker(
                itemHolder = itemHolder,
                dataComponents = dataComponentPatch
            )
        }
    }
}
