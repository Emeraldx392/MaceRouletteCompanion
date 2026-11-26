package moe.pxe.macecompanion.dataClasses

import moe.pxe.macecompanion.enums.achievements.AchievementLostTriggers
import moe.pxe.macecompanion.enums.achievements.AchievementRequirements
import moe.pxe.macecompanion.enums.achievements.AchievementResetConditions
import moe.pxe.macecompanion.resourceLoaders.IconOverridesResourceLoader.getOffAchievementFromId
import moe.pxe.macecompanion.resourceLoaders.IconOverridesResourceLoader.getOnAchievementFromId
import net.minecraft.core.component.DataComponents
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

data class Achievement(val id: String, val matchString: String, val onItemModel: Identifier, val offItemModel: Identifier, val lossTriggers: Map<AchievementLostTriggers, Int>, val resetCondition: AchievementResetConditions, val requirement: AchievementRequirements? = null) {
    fun createOnItemStack(): ItemStack {
        val iconOverride: IconOverride? = getOnAchievementFromId(id)
        if(iconOverride != null) return iconOverride.createItemStack()
        return Items.ITEM_FRAME.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, onItemModel)
        }
    }
    fun createOffItemStack(): ItemStack {
        val iconOverride: IconOverride? = getOffAchievementFromId(id)
        if(iconOverride != null) return iconOverride.createItemStack()
        return Items.ITEM_FRAME.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, offItemModel)
        }
    }
    companion object {
        fun fromJson(json: com.google.gson.JsonElement): Achievement {
            val obj = json.asJsonObject
            val lossTriggerElement = obj.get("loss_triggers")
            val parsedLossTriggers = when {
                lossTriggerElement.isJsonArray -> {
                    lossTriggerElement.asJsonArray.associate { element ->
                        when {
                            element.isJsonObject -> {
                                val elementObj = element.asJsonObject
                                val trigger = AchievementLostTriggers.valueOf(elementObj.get("trigger").asString.uppercase())
                                val amount = elementObj.get("amount").asInt
                                trigger to amount
                            }

                            else -> AchievementLostTriggers.valueOf(element.asString.uppercase()) to 1
                        }
                    }
                }

                lossTriggerElement.isJsonObject -> {
                    val jsonObject = lossTriggerElement.asJsonObject
                    mapOf(AchievementLostTriggers.valueOf(jsonObject.get("trigger").asString.uppercase()) to jsonObject.get("amount").asInt)
                }

                else -> mapOf(AchievementLostTriggers.valueOf(lossTriggerElement.asString.uppercase()) to 1)
            }
            return Achievement(id = obj.get("id").asString, matchString = obj.get("match_string").asString, onItemModel = Identifier.parse(obj.get("on_item_model").asString), offItemModel = Identifier.parse(obj.get("off_item_model").asString), lossTriggers = parsedLossTriggers, resetCondition = AchievementResetConditions.valueOf(obj.get("reset_condition").asString.uppercase()), requirement = obj.get("requirement")?.takeIf { !it.isJsonNull }?.asString?.uppercase()?.let { AchievementRequirements.valueOf(it) })
        }
    }
}