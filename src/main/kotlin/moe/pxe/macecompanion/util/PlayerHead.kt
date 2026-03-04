package moe.pxe.macecompanion.util

import com.google.common.collect.ImmutableMultimap
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import com.mojang.serialization.JsonOps
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
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
        MinecraftClient.getInstance().skinProvider.fetchSkinTextures(profile)
        head.set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(profile))
        headItemCache[profile] = head
        return head
    }

    fun player2dHeadTextComponent(profile: String): Text {
        val json = JsonObject();
        json.addProperty("player", profile);
        val playerComponent = TextCodecs.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
        return playerComponent
    }
}