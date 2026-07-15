package moe.pxe.macecompanion.util

import com.google.common.collect.ImmutableMultimap
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import com.mojang.serialization.JsonOps
import moe.pxe.macecompanion.config.Config
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
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
    fun player2dHeadTextComponent(profile: GameProfile): Component {
        val json = JsonObject()
        json.addProperty("player", profile.name)
        val playerComponent = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
        return playerComponent
    }
    fun player2dHeadTextComponentList(profileList:  MutableList<GameProfile>, maxProfiles: Int, rightAligned: Boolean): Component {
        val visibleProfiles = profileList.take(maxProfiles)
        val extraProfiles = profileList.size - maxProfiles
        var extraProfilesText = if(extraProfiles > 0) Component.literal("+$extraProfiles").setStyle(Config.getModifiersTextAccentStyle(0xa63efc))
        else Component.empty()
        if(visibleProfiles.isEmpty() && extraProfiles > 0) {
            return if (rightAligned) Component.empty().append(extraProfilesText).append(" ")
            else Component.empty().append(" ").append(extraProfilesText)
        }
        val headsComponent = Component.empty()
        visibleProfiles.forEachIndexed { index, profile ->
            if (index > 0) headsComponent.append(" ")
            headsComponent.append(player2dHeadTextComponent(profile))
        }
        val final2dHeadText = headsComponent.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
        val finalText = Component.empty()
        if (rightAligned) {
            if (extraProfiles > 0) finalText.append(extraProfilesText).append(" ")
            finalText.append(final2dHeadText).append(" ")
        } else {
            finalText.append(" ").append(final2dHeadText)
            if (extraProfiles > 0) finalText.append(" ").append(extraProfilesText)
        }
        return finalText
    }
}