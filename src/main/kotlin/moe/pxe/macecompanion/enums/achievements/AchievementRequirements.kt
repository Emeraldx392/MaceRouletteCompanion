package moe.pxe.macecompanion.enums.achievements

import moe.pxe.macecompanion.stateManagers.AccuracyManager.hasMace
import moe.pxe.macecompanion.stateManagers.AccuracyManager.hasWindCharge

enum class AchievementRequirements {
    HAS_MACE {
        override fun get(): Boolean {
            return hasMace
        }
    },
    HAS_WIND_CHARGE {
        override fun get(): Boolean {
            return hasWindCharge
        }
    };

    abstract fun get(): Boolean
}