package moe.pxe.macecompanion.enums

import dev.isxander.yacl3.api.NameableEnum
import moe.pxe.macecompanion.stateManagers.ShowdownManager.blueVotesPercentage
import moe.pxe.macecompanion.stateManagers.ShowdownManager.redVotesPercentage
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable

enum class BetConditions : NameableEnum, StringRepresentable  {

    IS_RED{
        override fun calculate(usernameRed: String, usernameBlue: String): String {
            return "red"
        }
    },
    IS_BLUE{
        override fun calculate(usernameRed: String, usernameBlue: String): String {
            return "blue"
        }
    },
    IS_YOU {
        override fun calculate(usernameRed: String, usernameBlue: String): String? {
            val username = Minecraft.getInstance().user.name
            return if (usernameRed == username) "red"
            else if (usernameBlue == username) "blue"
            else null
        }
    },
    IS_NOT_YOU {
        override fun calculate(usernameRed: String, usernameBlue: String): String? {
            val username = Minecraft.getInstance().user.name
            return if (usernameRed == username) "blue"
            else if (usernameBlue == username) "red"
            else null
        }
    },
    IS_MOST_VOTED {
        override fun calculate(usernameRed: String, usernameBlue: String): String? {
            return if (redVotesPercentage > blueVotesPercentage) "red"
            else if (redVotesPercentage < blueVotesPercentage) "blue"
            else null
        }

    },
    IS_LEAST_VOTED {
        override fun calculate(usernameRed: String, usernameBlue: String): String? {
            return if (redVotesPercentage > blueVotesPercentage) "blue"
            else if (redVotesPercentage < blueVotesPercentage) "red"
            else null
        }
    },
    CHOOSE_RANDOMLY {
        override fun calculate(usernameRed: String, usernameBlue: String): String {
            val voteOptions = listOf("red", "blue")
            return voteOptions.random()
        }
    };

    abstract fun calculate(usernameRed: String, usernameBlue: String): String?
    override fun getSerializedName(): String = name
    override fun getDisplayName(): Component = Component.translatable("mrc.betConditions.${name.lowercase()}")


    companion object {
        val CODEC = StringRepresentable.fromEnum(::values)
    }
}
