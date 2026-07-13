package moe.pxe.macecompanion.util

import com.google.common.collect.ImmutableMultimap
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import com.mojang.serialization.JsonOps
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import java.util.UUID

object PlayerHead {
    private val headItemCache = mutableMapOf<GameProfile, ItemStack>()

    fun fromProperty(property: Property): ItemStack {
        val profile = GameProfile(UUID.randomUUID(), "", PropertyMap(ImmutableMultimap.of("textures", property)))
//        profile.properties.put("textures", property)
        return fromProfile(profile)
    }

    fun fromProfile(profile: GameProfile): ItemStack {
        headItemCache[profile]?.also { return it }
        val head = ItemStack(Items.PLAYER_HEAD)
        head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile))
        headItemCache[profile] = head
        return head
    }

    fun player2dHeadTextComponent(profile: String): Component {
        val json = JsonObject()
        json.addProperty("player", profile)
        val playerComponent = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
        return playerComponent
    }
}