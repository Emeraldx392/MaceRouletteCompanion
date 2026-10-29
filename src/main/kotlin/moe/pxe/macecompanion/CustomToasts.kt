package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import moe.pxe.macecompanion.dataClasses.Event
import moe.pxe.macecompanion.util.PlayerProfile.getOnlinePlayerProfile
import moe.pxe.macecompanion.util.PlayerProfile.player2dHeadTextComponent
import moe.pxe.macecompanion.util.TextUtils.createGradientText
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.FriendToast
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.CommonColors
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ResolvableProfile
import java.util.*

object CustomToasts {
    val client: Minecraft = Minecraft.getInstance()
    val toastManager = client.gui.toastManager()

    //Generic toast
    fun sendCustomToast(title: Component, description: Component) {
        SystemToast.add(toastManager, SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, description)
    }

    //Generic player toast
    fun sendCustomPlayerToast(uuid: UUID, message: Component) {
        val skinProfile: ResolvableProfile = ResolvableProfile.createUnresolved(uuid)
        FriendToast.add(toastManager, client.font, skinProfile, message)
    }

    fun sendCustomItemStackToast(itemStack: ItemStack, title: Component, description: Component?) {
        toastManager.addToast(CustomItemStackToast(itemStack, title, description))
    }

    //Event toast
    fun sendNewEventToast(newEvent: Event, newEventDuration: Int, newEventStarter: String) {
        if (!Config.showNewEventToasts.value) return
        val eventId = createGradientText(Component.translatable("mrc.event.${newEvent.id}"), newEvent.colors).run { newEvent.shadowColor?.let { withStyle(Style.EMPTY.withShadowColor(it)) } ?: this }
        val description = Component.translatable("mrc.toast.new_event.description", eventId, Component.literal(newEventDuration.toString()).withColor(CommonColors.WHITE))
        val eventStarter = Component.translatable("mrc.toast.new_event.starter", getOnlinePlayerProfile(newEventStarter)?.let{player2dHeadTextComponent(it).copy().withColor(CommonColors.WHITE).append(" ")} ?: "", Component.literal(newEventStarter).withColor(CommonColors.WHITE)).withStyle(ChatFormatting.GRAY)
        val title = Component.translatable("mrc.toast.new_event.title", eventStarter).withColor(0x0F9FF)
        sendCustomItemStackToast(newEvent.createItemStack(), title, description)
    }

    //Consumable toasts
    fun sendModifierChargerToast(modifier: String, queueLength: Int, player: String) {
        if (!Config.showConsumableToasts.value) return
        val client: Minecraft = Minecraft.getInstance()
        var playerName = player
        if (player == client.user.name) playerName = "You"
        val description = Component.literal("$modifier will be charged for the next $queueLength appearances")
        sendCustomToast(Component.literal("$playerName used a Modifier Charger!"), description)
    }

    fun sendChaosDustToast(player: String, amount: Int) {
        if (!Config.showConsumableToasts.value) return
        val client: Minecraft = Minecraft.getInstance()
        var playerName = player
        if (player == client.user.name) playerName = "You"
        val numberString = if(amount == 5) "five" else if(amount == 7) "seven" else "ten"
        val modString = if(amount == 5) "Chaos" else if(amount == 7) "Mayhem" else "Doomsday"
        val description = Component.literal("The next round will have $numberString modifiers!")
        sendCustomToast(Component.literal("$playerName activated $modString!"), description)
    }

    fun sendEternalElectorToast(modifier: String?, player: String?, queuePosition: Int) {
        if (!Config.showConsumableToasts.value) return
        val client: Minecraft = Minecraft.getInstance()
        var playerName = player
        if (player == client.user.name) playerName = "You"
        val description = Component.literal("It has been queued at position #${queuePosition}!")
        sendCustomToast(Component.literal("$playerName used a Eternal Elector for ${modifier}!"), description)
    }

    //Bounty toasts
    fun sendPlacedBountyToast(bountyAmount: Int?, bountyPlacer: String?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("A ${bountyAmount}⛂ bounty was placed on you by $bountyPlacer")
        sendCustomToast(Component.literal("Bounty Placed!"), description)
    }

    fun sendSelfPlacedBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You placed a ${bountyAmount}⛂ bounty on yourself")
        sendCustomToast(Component.literal("Bounty Placed!"), description)
    }

    fun sendRaisedBountyToast(bountyAmount: Int?, bountyRaiser: String?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("Your bounty amount was raised to ${bountyAmount}⛂ by $bountyRaiser")
        sendCustomToast(Component.literal("Bounty Amount Raised!"), description)
    }

    fun sendSelfRaisedBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You raised your bounty amount to ${bountyAmount}⛂")
        sendCustomToast(Component.literal("Bounty Amount Raised!"), description)
    }

    fun sendRewardedBountyToast(bountyAmount: Int?, playerWithBounty: String?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You have been rewarded ${bountyAmount}⛂ for eliminating $playerWithBounty")
        sendCustomToast(Component.literal("Bounty Rewarded!"), description)
    }

    fun sendCashedInBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You cashed in your ${bountyAmount}⛂ bounty")
        sendCustomToast(Component.literal("Bounty Cashed In!"), description)
    }

    //Player Toasts
    fun sendPlayerJoinedToast(player: String, color: Int?) {
        val playerProfile = getOnlinePlayerProfile(player) ?: return
        val configValues = Config.playerStrings.get()

        val hasProfileName = configValues.contains(playerProfile.name)
        val hasProfileUuid = configValues.mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }.contains(playerProfile.id)

        if (!hasProfileName && !hasProfileUuid) return

        sendCustomPlayerToast(playerProfile.id, Component.translatable("mrc.toast.player_joined", Component.literal(player).withColor(color?: CommonColors.YELLOW)))
    }

    fun sendPlayerLeftToast(player: String) {
        val playerProfile = getOnlinePlayerProfile(player) ?: return
        val configValues = Config.playerStrings.get()

        val hasProfileName = configValues.contains(playerProfile.name)
        val hasProfileUuid = configValues.mapNotNull { runCatching { UUID.fromString(it) }.getOrNull() }.contains(playerProfile.id)

        if (!hasProfileName && !hasProfileUuid) return

        sendCustomPlayerToast(playerProfile.id, Component.translatable("mrc.toast.player_left", Component.literal(playerProfile.name).withStyle(ChatFormatting.GRAY)))
    }
}