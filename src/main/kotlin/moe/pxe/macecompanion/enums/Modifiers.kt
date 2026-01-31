package moe.pxe.macecompanion.enums

import com.mojang.authlib.properties.Property
import moe.pxe.macecompanion.util.PlayerHead
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text

enum class Modifiers {
    DOUBLE {
        override val matchName = "Double Mace"
        override val icon = Items.MACE.defaultStack.apply {
            count = 2
        }
    },

    TRIPLE {
        override val matchName = "Triple Mace"
        override val icon = Items.MACE.defaultStack.apply {
            count = 3
        }
    },

    TINY {
        override val matchName = "Tiny Mace"
        override val icon = Items.STONE_BUTTON.defaultStack
    },

    BIG {
        override val matchName = "Big Mace"
        override val icon = Items.STONE.defaultStack
    },

    SLOW_TIME {
        override val matchName = "Slow Time"
        override val icon = Items.CLOCK.defaultStack
    },

    MISS_EQUALS_DIE {
        override val matchName = "Miss = Die"
        override val icon = PlayerHead.fromProperty(Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4NTJiYTE1ODRkYTllNTcxNDg1OTk5NTQ1MWU0Yjk0NzQ4YzRkZDYzYWU0NTQzYzE1ZjlmOGFlYzY1YzgifX19"))
    },

    SHOCKWAVE {
        override val matchName = "Shockwave Mace"
        override val icon = Items.HEART_OF_THE_SEA.defaultStack
    },

    ELYTRA {
        override val matchName = "Elytra Launch"
        override val icon = Items.ELYTRA.defaultStack
    },

    WIND_CHARGE {
        override val matchName = "Wind Charge Storm"
        override val icon = Items.WIND_CHARGE.defaultStack
    },

    COBWEBS {
        override val matchName = "Player Cobwebs"
        override val icon = Items.COBWEB.defaultStack
    },

    BLOCKS {
        override val matchName = "Player Blocks"
        override val icon = Items.CHERRY_PLANKS.defaultStack
    },

    CAGED {
        override val matchName = "Caged!"
        override val icon = Items.IRON_BARS.defaultStack
    },

    PILLARS {
        override val matchName = "Pillars"
        override val icon = Items.POLISHED_BASALT.defaultStack
    },

    SWEEPER {
        override val matchName = "Sweeper"
        override val icon = Items.COMPASS.defaultStack
    },

    DROP {
        override val matchName = "Mace Drop"
        override val icon = Items.BARREL.defaultStack
    },

    HOLES {
        override val matchName = "Holes"
        override val icon = Items.FROGSPAWN.defaultStack
    },

    SHRINKING {
        override val matchName = "Shrinking Map"
        override val icon = Items.DISC_FRAGMENT_5.defaultStack
    },

    DONUT {
        override val matchName = "Donut Map"
        override val icon = Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE.defaultStack
    },

    BOUNCY {
        override val matchName = "Bouncy Floor"
        override val icon = Items.SLIME_BLOCK.defaultStack
    },

    ICE {
        override val matchName = "Icy Floor"
        override val icon = Items.ICE.defaultStack
    },

    FRAGILE {
        override val matchName = "Fragile Floor"
        override val icon = Items.GLASS.defaultStack
    },

    HIDDEN {
        override val matchName = "Hidden Floor"
        override val icon = Items.BARRIER.defaultStack
    },

    PICKAXE {
        override val matchName = "Pickaxe"
        override val icon = Items.NETHERITE_PICKAXE.defaultStack
    },

    WIND_BURST {
        override val matchName = "Wind Burst Mace"
        override val icon = Items.ENCHANTED_BOOK.defaultStack
    },

    VICTIM {
        override val matchName = "Victim Mace"
        override val icon = Items.SPECTRAL_ARROW.defaultStack
    },

    CEILING {
        override val matchName = "Ceiling"
        override val icon = Items.WAXED_OXIDIZED_COPPER_TRAPDOOR.defaultStack
    },

    RANDOM_SIZE {
        override val matchName = "Random Size"
        override val icon = PlayerHead.fromProperty(Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkNGEwMWRiNjEyNjYwMWRlZDE0MDZjZjYyMzhjZTJiNzAyNGVhY2U1ZWE2MDRmYmMyMDhhMmFmMjljOTdhZCJ9fX0="))
    },

    GLOWING {
        override val matchName = "Glow in the Dark"
        override val icon = Items.GLOW_INK_SAC.defaultStack
    },

    MOVE_EQUALS_GROW {
        override val matchName = "Move = Grow"
        override val icon = Items.CHAINMAIL_BOOTS.defaultStack
    },

    PINATA {
        override val matchName = "Piñata"
        override val icon = Items.ZOMBIE_HORSE_SPAWN_EGG.defaultStack
    },

    EXPLOSIVE {
        override val matchName = "Explosive Charges"
        override val icon = Items.TNT_MINECART.defaultStack
    },

    NO_JUMPING {
        override val matchName = "No Jumping"
        override val icon = Items.RABBIT_FOOT.defaultStack
    },

    STACKS {
        override val matchName = "Player Stacks"
        override val icon = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.defaultStack
    },

    SNOWBALL {
        override val matchName = "Snowball Fight"
        override val icon = Items.POWDER_SNOW_BUCKET.defaultStack
    },

    SOUL_FLOOR {
        override val matchName = "Soul Floor"
        override val icon = Items.SOUL_SAND.defaultStack
    },

    PUMPKIN {
        override val matchName = "Pumpkin Curse"
        override val icon = Items.PUMPKIN.defaultStack
    },

    MIRAGE {
        override val matchName = "Mirage"
        override val icon = Items.BREEZE_SPAWN_EGG.defaultStack
    },

    ALL {
        override val matchName = "All Mace"
        override val icon = Items.MACE.defaultStack.apply {
            count = 99
        }
    },

    PHARAOHS_CURSE {
        override val matchName = "Pharaoh's Curse"
        override val icon = Items.SUSPICIOUS_SAND.defaultStack
    },

    STICKY {
        override val matchName = "Sticky Floor"
        override val icon = Items.HONEY_BLOCK.defaultStack
    },

    UNKNOWN {
        override val matchName = "???"
        override val icon = Items.AIR.defaultStack
    };


    abstract val matchName: String
    val translatable = Text.translatable("mrc.modifier.${name.lowercase()}")
    abstract val icon: ItemStack
}