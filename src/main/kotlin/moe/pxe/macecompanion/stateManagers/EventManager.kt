package moe.pxe.macecompanion.stateManagers

import moe.pxe.macecompanion.CustomToasts
import moe.pxe.macecompanion.dataClasses.Event
import moe.pxe.macecompanion.resourceLoaders.EventResourceLoader
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.concurrent.CompletableFuture

object EventManager {
    var doubleXp = false
    var newEvent = false
    var newEventStarter = ""
    var newEventType: Event? = null

    val newEventRegex = Regex("""⏵ New Event Started! \(by (.+)\)""")
    val newEventTypeRegex = Regex("""\s+⏵ Type: (.+)""")
    val newEventDurationRegex = Regex("""\s+⏵ Length: (\d+)h""")

    fun resetEventData() {
        doubleXp = false
        newEvent = false
        newEventStarter = ""
        newEventType = null
    }

    fun registerEventListeners() {
        ClientReceiveMessageEvents.GAME.register { message, overlay ->
            val text = message.string

            if (overlay) return@register
            if (!text.contains("⏵ ")) return@register
            if (!PlotManager.onMaceRoulette) return@register

            CompletableFuture.runAsync {
                newEventRegex.matchEntire(text)?.groups[1]?.let {
                    newEvent = true
                    newEventStarter = it.value
                }
                newEventTypeRegex.matchEntire(text)?.groups[1]?.let {
                    newEventType = EventResourceLoader.getEventFromMatchString(it.value)
                    if (newEventType?.id == "double_xp") doubleXp = true
                }
                newEventDurationRegex.matchEntire(text)?.groups[1]?.let {
                    val eventDuration = it.value.toInt()
                    if (newEvent) {
                        newEventType?.let { CustomToasts.sendNewEventToast(it, eventDuration, newEventStarter) }
                        newEvent = false
                        newEventStarter = ""
                        newEventType = null
                    }
                }
            }
            return@register
        }
    }
}


