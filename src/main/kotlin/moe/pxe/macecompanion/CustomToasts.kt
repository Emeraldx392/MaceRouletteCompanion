package moe.pxe.macecompanion

import dev.isxander.yacl3.config.v3.value
import moe.pxe.macecompanion.config.Config
import net.minecraft.client.MinecraftClient
import net.minecraft.client.toast.SystemToast
import net.minecraft.text.Text

object CustomToasts {
    //Generic toast
    fun sendCustomToast(title: Text, description: Text) {
        SystemToast.add(
            MinecraftClient.getInstance().getToastManager(),
            SystemToast.Type.PERIODIC_NOTIFICATION,
            title,
            description
        )
    }
    //Event toast
    fun sendNewEventToast(newEventType: String, newEventDuration : Int, newEventStarter : String) {
        if (!Config.showNewEventToasts.value) return
        val description = Text.literal("${newEventType}: ${newEventDuration}h (by ${newEventStarter})")
        sendCustomToast(Text.literal("New Event Started!"), description)
    }
    //Consumable toasts
    fun sendModifierChargerToast(modifier: String, queueLength: Int, player: String) {
        if (!Config.showConsumableToasts.value) return
        var playerName = player
        if(player == StateManager.client.session.username.toString()) playerName = "You"
        val description = Text.literal("${modifier} will be charged for the next ${queueLength} appearances")
        sendCustomToast(Text.literal("${playerName} used a Modifier Charger!"), description)
    }
    fun sendChaosStarterToast(player: String) {
        if (!Config.showConsumableToasts.value) return
        var playerName = player
        if(player == StateManager.client.session.username.toString()) playerName = "You"
        val description = Text.literal("The next round will have five modifiers!")
        sendCustomToast(Text.literal("${playerName} used a Chaos Starter!"), description)
    }
    fun sendEternalElectorToast(modifier: String, player: String, queuePosition: Int) {
        if (!Config.showConsumableToasts.value) return
        var playerName = player
        if(player == StateManager.client.session.username.toString()) playerName = "You"
        val description = Text.literal("It has been queued at position #${queuePosition}!")
        sendCustomToast(Text.literal("${playerName} used a Eternal Elector for ${modifier}!"), description)
    }
    //Bounty toasts
    fun sendPlacedBountyToast(bountyAmount: Int?, bountyPlacer: String) {
        if (!Config.showBountyToasts.value) return
        val description = Text.literal("A ${bountyAmount}⛂ bounty was placed on you by ${bountyPlacer}")
        sendCustomToast(Text.literal("Bounty Placed!"), description)
    }
    fun sendSelfPlacedBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Text.literal("You placed a ${bountyAmount}⛂ bounty on yourself")
        sendCustomToast(Text.literal("Bounty Placed!"), description)
    }
    fun sendRaisedBountyToast(bountyAmount: Int?, bountyRaiser: String) {
        if (!Config.showBountyToasts.value) return
        val description = Text.literal("Your bounty amount was raised to ${bountyAmount}⛂ by ${bountyRaiser}")
        sendCustomToast(Text.literal("Bounty Amount Raised!"), description)
    }
    fun sendSelfRaisedBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Text.literal("You raised your bounty amount to ${bountyAmount}⛂")
        sendCustomToast(Text.literal("Bounty Amount Raised!"), description)
    }
    fun sendRewardedBountyToast(bountyAmount: Int?, playerWithBounty: String) {
        if (!Config.showBountyToasts.value) return
        val description = Text.literal("You have been rewarded ${bountyAmount}⛂ for eliminating ${playerWithBounty}")
        sendCustomToast(Text.literal("Bounty Rewarded!"), description)
    }
    fun sendCashedInBountyToast(bountyAmount: Int?) {
        if (!Config.showBountyToasts.value) return
        val description = Text.literal("You cashed in your ${bountyAmount}⛂ bounty")
        sendCustomToast(Text.literal("Bounty Cashed In!"), description)
    }
    fun sendPlayerJoinedToast(player: String){
        if(!Config.playerStrings.value.contains(player)) return
        sendCustomToast(Text.literal("Player Joined!"), Text.literal("${player} joined the game!"))
    }
}