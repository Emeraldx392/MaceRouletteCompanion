package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.toasts.SystemToast
import net.minecraft.network.chat.Component

object CustomToasts {
    //Generic toast
    fun sendCustomToast(title: Component, description: Component) {
        SystemToast.add(
            Minecraft.getInstance().toastManager,
            SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
            title,
            description
        )
    }
    //Event toast
    fun sendNewEventToast(newEventType: String, newEventDuration : Int, newEventStarter : String) {
        if (!Config.showNewEventToasts.value) return
        val description = Component.literal("${newEventType}: ${newEventDuration}h (by ${newEventStarter})")
        sendCustomToast(Component.literal("New Event Started!"), description)
    }
    //Consumable toasts
    fun sendModifierChargerToast(modifier: String, queueLength: Int, player: String) {
        if (!Config.showConsumableToasts.value) return
        var playerName = player
        if(player == StateManager.client.user.name) playerName = "You"
        val description = Component.literal("$modifier will be charged for the next $queueLength appearances")
        sendCustomToast(Component.literal("$playerName used a Modifier Charger!"), description)
    }
    fun sendChaosStarterToast(player: String) {
        if (!Config.showConsumableToasts.value) return
        var playerName = player
        if(player == StateManager.client.user.name) playerName = "You"
        val description = Component.literal("The next round will have five modifiers!")
        sendCustomToast(Component.literal("$playerName used a Chaos Starter!"), description)
    }
    fun sendEternalElectorToast(modifier: String, player: String, queuePosition: Int) {
        if (!Config.showConsumableToasts.value) return
        var playerName = player
        if(player == StateManager.client.user.name) playerName = "You"
        val description = Component.literal("It has been queued at position #${queuePosition}!")
        sendCustomToast(Component.literal("$playerName used a Eternal Elector for ${modifier}!"), description)
    }
    //Bounty toasts
    fun sendPlacedBountyToast(bountyAmount: Int?, bountyPlacer: String) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("A ${bountyAmount}⛂ bounty was placed on you by $bountyPlacer")
        sendCustomToast(Component.literal("Bounty Placed!"), description)
    }
    fun sendSelfPlacedBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You placed a ${bountyAmount}⛂ bounty on yourself")
        sendCustomToast(Component.literal("Bounty Placed!"), description)
    }
    fun sendRaisedBountyToast(bountyAmount: Int?, bountyRaiser: String) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("Your bounty amount was raised to ${bountyAmount}⛂ by $bountyRaiser")
        sendCustomToast(Component.literal("Bounty Amount Raised!"), description)
    }
    fun sendSelfRaisedBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You raised your bounty amount to ${bountyAmount}⛂")
        sendCustomToast(Component.literal("Bounty Amount Raised!"), description)
    }
    fun sendRewardedBountyToast(bountyAmount: Int?, playerWithBounty: String) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You have been rewarded ${bountyAmount}⛂ for eliminating $playerWithBounty")
        sendCustomToast(Component.literal("Bounty Rewarded!"), description)
    }
    fun sendCashedInBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Component.literal("You cashed in your ${bountyAmount}⛂ bounty")
        sendCustomToast(Component.literal("Bounty Cashed In!"), description)
    }
    fun sendPlayerJoinedToast(player: String){
        if(!Config.playerStrings.value.contains(player)) return
        sendCustomToast(Component.literal("Player Joined!"), Component.literal("$player joined the game!"))
    }
}