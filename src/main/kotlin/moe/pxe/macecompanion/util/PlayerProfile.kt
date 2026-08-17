package moe.pxe.macecompanion.util

import com.google.common.collect.ImmutableMultimap
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import com.mojang.authlib.properties.PropertyMap
import com.mojang.serialization.JsonOps
import moe.pxe.macecompanion.config.Config.getAccentColor
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.ResolvableProfile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.network.chat.Style
import java.util.UUID

object PlayerProfile {
    private val headItemCache = mutableMapOf<GameProfile, ItemStack>()

    val client: Minecraft = Minecraft.getInstance()

    fun getPlayerProfile(player: String?): GameProfile? {
        if (player == null) return null
        return client.connection?.getPlayerInfo(player)?.profile ?: resolvePlayerFromRawName(player)
    }

    fun resolvePlayerFromRawName(rawName: String?): GameProfile? {
        val candidate = rawName?.trim().orEmpty()
        if (candidate.isEmpty()) return null
        if (rawName?.contains(client.user.name) ?: false) return client.gameProfile
        client.connection?.onlinePlayers?.forEach { player ->
            if (rawName?.contains(player.profile.name) ?: false) return player.profile
        }
        return null
    }

    fun headFromProperty(property: Property): ItemStack {
        val profile = GameProfile(UUID.randomUUID(), "", PropertyMap(ImmutableMultimap.of("textures", property)))
//        profile.properties.put("textures", property)
        return headFromProfile(profile)
    }

    fun headFromProfile(profile: GameProfile): ItemStack {
        headItemCache[profile]?.also { return it }
        val head = ItemStack(Items.PLAYER_HEAD)
        head.set(DataComponents.PROFILE, ResolvableProfile.createResolved(profile))
        headItemCache[profile] = head
        return head
    }

    fun player2dHeadTextComponent(profile: GameProfile): Component {
        val json = JsonObject()
        json.addProperty("player", profile.name)
        val playerComponent = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
        return playerComponent
    }
    fun player2dHeadTextComponentList(profileList:  List<GameProfile>, maxProfiles: Int, rightAligned: Boolean): Component {
        if(profileList.isEmpty()) return Component.empty()
        val visibleCount = maxProfiles.coerceAtMost(profileList.size).coerceAtLeast(0)
        val extraCount = profileList.size - visibleCount
        val headsComponent = Component.empty().apply {
            profileList.subList(0, visibleCount).forEachIndexed { index, profile ->
                if (index > 0) append(" ")
                append(player2dHeadTextComponent(profile))
            }
        }.setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))

        val extraText = if (extraCount > 0) Component.literal("+$extraCount").withColor(getAccentColor("modifiers", "text_color", 0xa63efc))
        else null
        if(visibleCount < 1) return if(rightAligned) extraText!!.append(" ") else Component.literal(" ").append(extraText!!)
        return Component.empty().apply {
            if (rightAligned) {
                extraText?.let { append(it).append(" ") }
                append(headsComponent).append("  ")
            } else {
                append("  ").append(headsComponent)
                extraText?.let { append(" ").append(it) }
            }
        }
    }
}