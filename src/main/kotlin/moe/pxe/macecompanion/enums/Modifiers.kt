package moe.pxe.macecompanion.enums

import com.mojang.authlib.properties.Property
import moe.pxe.macecompanion.MaceCompanion
import moe.pxe.macecompanion.util.PlayerHead
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier

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

    QUADRUPLE {
        override val matchName = "Quadruple Mace"
        override val icon = Items.MACE.defaultStack.apply {
            count = 4
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
        override val icon = Items.CLOCK.defaultStack.apply {
            set(DataComponentTypes.DAMAGE, 850)
            set(DataComponentTypes.MAX_DAMAGE, 1000)
        }
    },

    FAST_TIME {
        override val matchName = "Fast Time"
        override val icon = Items.CLOCK.defaultStack.apply {
            set(DataComponentTypes.DAMAGE, 1)
            set(DataComponentTypes.MAX_DAMAGE, 1000)
        }
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

    WIND_STORM {
        override val matchName = "Wind Storm"
        override val icon = Items.WIND_CHARGE.defaultStack
    },

    COBWEBS {
        override val matchName = "Cobwebs"
        override val icon = Items.COBWEB.defaultStack
    },

    BLOCKS {
        override val matchName = "Blocks"
        override val icon = Items.CHERRY_PLANKS.defaultStack
    },

    CAGED {
        override val matchName = "Caged!"
        override val icon = Items.COPPER_BARS.oxidized.defaultStack
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
        override val icon = Items.MUSIC_DISC_11.defaultStack
    },

    DONUT {
        override val matchName = "Donut Map"
        override val icon = Items.MUSIC_DISC_STRAD.defaultStack
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
        override val icon = Items.STONE_PICKAXE.defaultStack
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
        override val icon = Items.HORSE_SPAWN_EGG.defaultStack
    },

    EXPLOSIVE {
        override val matchName = "Explosive Burst"
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

    LUNGE {
        override val matchName = "Lunge"
        override val icon = Items.COPPER_SPEAR.defaultStack
    },

    RODS {
        override val matchName = "Fishing Rods"
        override val icon = Items.FISHING_ROD.defaultStack
    },

    MAGNETIC {
        override val matchName = "Magnetic Burst"
        override val icon: ItemStack = Items.IRON_NAUTILUS_ARMOR.defaultStack
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

    LEVITATION {
        override val matchName = "Levitation Burst"
        override val icon = Items.ALLAY_SPAWN_EGG.defaultStack
    },

    TOTEM {
        override val matchName = "Steal the Totem"
        override val icon = Items.TOTEM_OF_UNDYING.defaultStack
    },

    SPIKES {
        override val matchName = "Spikes"
        override val icon = Items.POINTED_DRIPSTONE.defaultStack
    },

    MIRROR {
        override val matchName = "Mirror"
        override val icon = Items.WHITE_BANNER.defaultStack
    },

    MINEFIELD {
        override val matchName = "Minefield"
        override val icon = Items.MANGROVE_PRESSURE_PLATE.defaultStack
    },

    BOUNCY_CHARGES {
        override val matchName = "Bouncy Charges"
        override val icon = Items.SLIME_BALL.defaultStack
    },

    UNKNOWN {
        override val matchName = "\uE024"
        override val icon = Items.MACE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "glitched_mace"))
        }
        override val translatable = Text.translatable("mrc.modifier.${name.lowercase()}").formatted(Formatting.RED, Formatting.BOLD)
    },

    MYSTERY {
        override val matchName = "???"
        override val icon = Items.LIGHT_GRAY_CANDLE.defaultStack
        override val translatable = Text.translatable("mrc.modifier.${name.lowercase()}").withColor(0xcfb3fc).formatted(Formatting.ITALIC)
    };


    abstract val matchName: String
    open val translatable = Text.translatable("mrc.modifier.${name.lowercase()}").withColor(0xfcfc54)
    abstract val icon: ItemStack
}
