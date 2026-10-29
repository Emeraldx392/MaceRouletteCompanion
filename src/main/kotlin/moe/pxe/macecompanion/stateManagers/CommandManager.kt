package moe.pxe.macecompanion.stateManagers

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import moe.pxe.macecompanion.CustomToasts
import moe.pxe.macecompanion.MaceCompanion.Companion.DEBUG_MODE
import moe.pxe.macecompanion.dataClasses.ModifierInstance
import moe.pxe.macecompanion.resourceLoaders.EventResourceLoader
import moe.pxe.macecompanion.resourceLoaders.ModifierResourceLoader
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import net.minecraft.util.CommonColors

object CommandManager {
    val client = Minecraft.getInstance()

    fun registerCommands() {
        if (DEBUG_MODE) ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommands.literal("event").then(
                    ClientCommands.argument("id", StringArgumentType.string()).suggests { _, builder ->
                        val eventList = EventResourceLoader.getAllIdStrings()
                        SharedSuggestionProvider.suggest(eventList, builder)
                    }.then(
                        ClientCommands.argument("hours", IntegerArgumentType.integer(1)).then(ClientCommands.argument("starter", StringArgumentType.string()).suggests { _, builder ->
                                val playerList: List<String> = client.connection?.onlinePlayers?.map { it.profile.name }?.filter { name -> !name.contains("§") }?.filter { name -> name.isNotBlank() }
                                    ?: emptyList()
                                SharedSuggestionProvider.suggest(playerList, builder)
                            }.executes { context ->
                                val id = StringArgumentType.getString(context, "id")
                                val hours = IntegerArgumentType.getInteger(context, "hours")
                                val starter = StringArgumentType.getString(context, "starter")

                                val event = EventResourceLoader.getEventFromId(id)
                                if (event != null) {
                                    CustomToasts.sendNewEventToast(event, hours, starter)
                                    context.source.sendFeedback(Component.literal("Event toast created!").withStyle(ChatFormatting.GRAY))
                                    1
                                } else {
                                    context.source.sendFeedback(Component.literal("Event $id doesn't exist").withColor(CommonColors.RED))
                                    0
                                }
                            })
                    )
                )
            )
        }
        if (DEBUG_MODE) ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            dispatcher.register(
                ClientCommands.literal("modifier").then(
                    ClientCommands.argument("id", StringArgumentType.string()).suggests { _, builder ->
                        val eventList = ModifierResourceLoader.getAllIdStrings()
                        SharedSuggestionProvider.suggest(eventList, builder)
                    }.then(
                        ClientCommands.argument("charged", BoolArgumentType.bool()).then(
                                ClientCommands.argument("eternal", BoolArgumentType.bool()).then(
                                        ClientCommands.argument("midas", BoolArgumentType.bool()).executes { context ->
                                                val id = StringArgumentType.getString(context, "id")
                                                val charged = BoolArgumentType.getBool(context, "charged")
                                                val eternal = BoolArgumentType.getBool(context, "eternal")
                                                val midas = BoolArgumentType.getBool(context, "midas")

                                                val modifier = ModifierResourceLoader.getModifierFromId(id)
                                                if (modifier != null) {
                                                    ModifierManager.modifiers.addLast(ModifierInstance(modifier, charged, eternal, midas))
                                                    context.source.sendFeedback(Component.literal("Modifier added!").withStyle(ChatFormatting.GRAY))
                                                    1
                                                } else {
                                                    context.source.sendFeedback(Component.literal("Modifier $id doesn't exist").withColor(CommonColors.RED))
                                                    0
                                                }
                                })
                       )
                    )
                )
            )
        }
    }
}