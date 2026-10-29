package moe.pxe.macecompanion.dataClasses

import com.mojang.authlib.GameProfile

data class ModifierInstance(
    val modifier: Modifier,
    val isCharged: Boolean = false,
    val isEternal: Boolean = false,
    val isMidas: Boolean = false,
    val boosters: List<GameProfile> = listOf()
)