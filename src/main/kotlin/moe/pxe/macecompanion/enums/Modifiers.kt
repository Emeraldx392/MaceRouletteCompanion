package moe.pxe.macecompanion.enums

import com.mojang.authlib.properties.Property
import moe.pxe.macecompanion.MaceCompanion
import moe.pxe.macecompanion.util.PlayerHead
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.UseCooldownComponent
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnchantmentLevelEntry
import net.minecraft.enchantment.Enchantments
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.Identifier
import java.util.Optional

enum class Modifiers {
    DOUBLE {
        override val matchName = "Double Mace"
        override val icon: ItemStack = Items.MACE.defaultStack.apply {
            count = 2
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.MACE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/double_mace"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    TRIPLE {
        override val matchName = "Triple Mace"
        override val icon: ItemStack = Items.MACE.defaultStack.apply {
            count = 3
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.MACE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/triple_mace"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    QUADRUPLE {
        override val matchName = "Quadruple Mace"
        override val icon: ItemStack = Items.MACE.defaultStack.apply {
            count = 4
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.MACE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/quadruple_mace"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    TINY {
        override val matchName = "Tiny Mace"
        override val icon: ItemStack = Items.STONE_BUTTON.defaultStack
        override val customIcon: ItemStack = Items.STONE_BUTTON.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/tiny_mace"))
        }
    },

    BIG {
        override val matchName = "Big Mace"
        override val icon: ItemStack = Items.STONE.defaultStack
        override val customIcon: ItemStack = Items.STONE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/big_mace"))
        }
    },

    SLOW_TIME {
        override val matchName = "Slow Time"
        override val icon: ItemStack = Items.CLOCK.defaultStack.apply {
            set(DataComponentTypes.DAMAGE, 850)
            set(DataComponentTypes.MAX_DAMAGE, 1000)
        }
        override val customIcon: ItemStack = Items.CLOCK.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/slow_time"))
        }
    },

    FAST_TIME {
        override val matchName = "Fast Time"
        override val icon: ItemStack = Items.CLOCK.defaultStack.apply {
            set(DataComponentTypes.DAMAGE, 1)
            set(DataComponentTypes.MAX_DAMAGE, 1000)
        }
        override val customIcon: ItemStack = Items.CLOCK.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/fast_time"))
        }
    },

    MISS_EQUALS_DIE {
        override val matchName = "Miss = Die"
        override val icon: ItemStack = PlayerHead.fromProperty(Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4NTJiYTE1ODRkYTllNTcxNDg1OTk5NTQ1MWU0Yjk0NzQ4YzRkZDYzYWU0NTQzYzE1ZjlmOGFlYzY1YzgifX19"))
        override val customIcon: ItemStack = Items.PLAYER_HEAD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/miss_equals_die"))
        }
    },

    SHOCKWAVE {
        override val matchName = "Shockwave Mace"
        override val icon: ItemStack = Items.HEART_OF_THE_SEA.defaultStack.apply {
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.HEART_OF_THE_SEA.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/shockwave_mace"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    ELYTRA {
        override val matchName = "Elytra Launch"
        override val icon: ItemStack = Items.ELYTRA.defaultStack
        override val customIcon: ItemStack = Items.ELYTRA.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/elytra_launch"))
        }
    },

    WIND_STORM {
        override val matchName = "Wind Storm"
        override val icon: ItemStack = Items.WIND_CHARGE.defaultStack.apply {
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.WIND_CHARGE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/wind_storm"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    COBWEBS {
        override val matchName = "Cobwebs"
        override val icon: ItemStack = Items.COBWEB.defaultStack
        override val customIcon: ItemStack = Items.COBWEB.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/cobwebs"))
        }
    },

    BLOCKS {
        override val matchName = "Blocks"
        override val icon: ItemStack = Items.CHERRY_PLANKS.defaultStack
        override val customIcon: ItemStack = Items.CHERRY_PLANKS.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/blocks"))
        }
    },

    CAGED {
        override val matchName = "Caged!"
        override val icon: ItemStack = Items.COPPER_BARS.oxidized.defaultStack
        override val customIcon: ItemStack = Items.COPPER_BARS.oxidized.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/caged"))
        }
    },

    PILLARS {
        override val matchName = "Pillars"
        override val icon: ItemStack = Items.POLISHED_BASALT.defaultStack
        override val customIcon: ItemStack = Items.POLISHED_BASALT.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/pillars"))
        }
    },

    SWEEPER {
        override val matchName = "Sweeper"
        override val icon: ItemStack = Items.COMPASS.defaultStack
        override val customIcon: ItemStack = Items.COMPASS.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/sweeper"))
        }
    },

    DROP {
        override val matchName = "Mace Drop"
        override val icon: ItemStack = Items.BARREL.defaultStack
        override val customIcon: ItemStack = Items.BARREL.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/mace_drop"))
        }
    },

    HOLES {
        override val matchName = "Holes"
        override val icon: ItemStack = Items.FROGSPAWN.defaultStack
        override val customIcon: ItemStack = Items.FROGSPAWN.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/holes"))
        }
    },

    SHRINKING {
        override val matchName = "Shrinking Map"
        override val icon: ItemStack = Items.MUSIC_DISC_11.defaultStack
        override val customIcon: ItemStack = Items.FROGSPAWN.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/shrinking_map"))
        }
    },

    DONUT {
        override val matchName = "Donut Map"
        override val icon: ItemStack = Items.MUSIC_DISC_STRAD.defaultStack
        override val customIcon: ItemStack = Items.MUSIC_DISC_STRAD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/donut_map"))
        }
    },

    BOUNCY {
        override val matchName = "Bouncy Floor"
        override val icon: ItemStack = Items.SLIME_BLOCK.defaultStack
        override val customIcon: ItemStack = Items.MUSIC_DISC_STRAD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/bouncy_floor"))
        }
    },

    ICE {
        override val matchName = "Icy Floor"
        override val icon: ItemStack = Items.ICE.defaultStack
        override val customIcon: ItemStack = Items.MUSIC_DISC_STRAD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/icy_floor"))
        }
    },

    FRAGILE {
        override val matchName = "Fragile Floor"
        override val icon: ItemStack = Items.GLASS.defaultStack
        override val customIcon: ItemStack = Items.GLASS.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/fragile_floor"))
        }
    },

    HIDDEN {
        override val matchName = "Hidden Floor"
        override val icon: ItemStack = Items.BARRIER.defaultStack
        override val customIcon: ItemStack = icon
    },

    PICKAXE {
        override val matchName = "Pickaxe"
        override val icon: ItemStack = Items.STONE_PICKAXE.defaultStack
        override val customIcon: ItemStack = Items.STONE_PICKAXE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/pickaxe"))
        }
    },

    WIND_BURST {
        override val matchName = "Wind Burst Mace"
        override val icon: ItemStack = EnchantmentHelper.getEnchantedBookWith(EnchantmentLevelEntry(MinecraftClient.getInstance().world!!.registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.WIND_BURST), 1))
        override val customIcon: ItemStack = Items.BREEZE_ROD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/wind_burst"))
        }
    },

    VICTIM {
        override val matchName = "Victim Mace"
        override val icon: ItemStack = Items.SPECTRAL_ARROW.defaultStack
        override val customIcon: ItemStack = Items.SPECTRAL_ARROW.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/victim_mace"))
        }
    },

    CEILING {
        override val matchName = "Ceiling"
        override val icon: ItemStack = Items.WAXED_OXIDIZED_COPPER_TRAPDOOR.defaultStack
        override val customIcon: ItemStack = Items.WAXED_OXIDIZED_COPPER_TRAPDOOR.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/ceiling"))
        }
    },

    RANDOM_SIZE {
        override val matchName = "Random Size"
        override val icon: ItemStack = PlayerHead.fromProperty(Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkNGEwMWRiNjEyNjYwMWRlZDE0MDZjZjYyMzhjZTJiNzAyNGVhY2U1ZWE2MDRmYmMyMDhhMmFmMjljOTdhZCJ9fX0="))
        override val customIcon: ItemStack = Items.PLAYER_HEAD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/random_size"))
        }
    },

    GLOWING {
        override val matchName = "Glow in the Dark"
        override val icon: ItemStack = Items.GLOW_INK_SAC.defaultStack
        override val customIcon: ItemStack = Items.GLOW_INK_SAC.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/glow_in_the_dark"))
        }
    },

    MOVE_EQUALS_GROW {
        override val matchName = "Move = Grow"
        override val icon: ItemStack = Items.CHAINMAIL_BOOTS.defaultStack
        override val customIcon: ItemStack = Items.CHAINMAIL_BOOTS.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/move_equals_grow"))
        }
    },

    PINATA {
        override val matchName = "Piñata"
        override val icon: ItemStack = Items.HORSE_SPAWN_EGG.defaultStack
        override val customIcon: ItemStack = Items.HORSE_SPAWN_EGG.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/pinata"))
        }
    },

    EXPLOSIVE {
        override val matchName = "Explosive Burst"
        override val icon: ItemStack = Items.TNT_MINECART.defaultStack
        override val customIcon: ItemStack = Items.TNT_MINECART.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/explosive_burst"))
        }
    },

    NO_JUMPING {
        override val matchName = "No Jumping"
        override val icon: ItemStack = Items.RABBIT_FOOT.defaultStack
        override val customIcon: ItemStack = Items.RABBIT_FOOT.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/no_jumping"))
        }
    },

    STACKS {
        override val matchName = "Stacks"
        override val icon: ItemStack = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.defaultStack
        override val customIcon: ItemStack = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/stacks"))
        }
    },

    LUNGE {
        override val matchName = "Lunge"
        override val icon: ItemStack = Items.COPPER_SPEAR.defaultStack.apply {
            addEnchantment(MinecraftClient.getInstance().world!!.registryManager.getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.LUNGE), 2)
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.COPPER_SPEAR.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/lunge"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    RODS {
        override val matchName = "Fishing Rods"
        override val icon: ItemStack = Items.FISHING_ROD.defaultStack
        override val customIcon: ItemStack = Items.FISHING_ROD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/fishing_rods"))
        }
    },

    MAGNETIC {
        override val matchName = "Magnetic Burst"
        override val icon: ItemStack = Items.IRON_NAUTILUS_ARMOR.defaultStack
        override val customIcon: ItemStack = Items.FISHING_ROD.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/magnetic_burst"))
        }
    },

    SNOWBALL {
        override val matchName = "Snowball Fight"
        override val icon: ItemStack = Items.POWDER_SNOW_BUCKET.defaultStack.apply {
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.POWDER_SNOW_BUCKET.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/snowball_fight"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    SOUL_FLOOR {
        override val matchName = "Soul Floor"
        override val icon: ItemStack = Items.SOUL_SAND.defaultStack
        override val customIcon: ItemStack = Items.SOUL_SAND.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/soul_floor"))
        }
    },

    PUMPKIN {
        override val matchName = "Pumpkin Curse"
        override val icon: ItemStack = Items.PUMPKIN.defaultStack
        override val customIcon = icon
    },

    ALL {
        override val matchName = "All Mace"
        override val icon: ItemStack = Items.MACE.defaultStack.apply {
            count = 99
        }
        override val customIcon = icon
    },

    PHARAOHS_CURSE {
        override val matchName = "Pharaoh's Curse"
        override val icon: ItemStack = Items.SUSPICIOUS_SAND.defaultStack
        override val customIcon = icon
    },

    STICKY {
        override val matchName = "Sticky Floor"
        override val icon: ItemStack = Items.HONEY_BLOCK.defaultStack
        override val customIcon: ItemStack = Items.HONEY_BLOCK.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/sticky_floor"))
        }
    },

    LEVITATION {
        override val matchName = "Levitation Burst"
        override val icon: ItemStack = Items.ALLAY_SPAWN_EGG.defaultStack
        override val customIcon: ItemStack = Items.ALLAY_SPAWN_EGG.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/levitation_burst"))
        }
    },

    TOTEM {
        override val matchName = "Steal the Totem"
        override val icon: ItemStack = Items.TOTEM_OF_UNDYING.defaultStack
        override val customIcon: ItemStack = Items.TOTEM_OF_UNDYING.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/steal_the_totem"))
        }
    },

    SPIKES {
        override val matchName = "Spikes"
        override val icon: ItemStack = Items.POINTED_DRIPSTONE.defaultStack
        override val customIcon: ItemStack = Items.POINTED_DRIPSTONE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/spikes"))
        }
    },

    MIRROR {
        override val matchName = "Mirror"
        override val icon: ItemStack = Items.WHITE_BANNER.defaultStack
        override val customIcon: ItemStack = Items.WHITE_BANNER.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/mirror"))
        }
    },

    MINEFIELD {
        override val matchName = "Minefield"
        override val icon: ItemStack = Items.MANGROVE_PRESSURE_PLATE.defaultStack
        override val customIcon: ItemStack = Items.WHITE_BANNER.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/minefield"))
        }
    },

    BOUNCY_CHARGES {
        override val matchName = "Bouncy Charges"
        override val icon: ItemStack = Items.SLIME_BALL.defaultStack
        override val customIcon: ItemStack = Items.SLIME_BALL.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/bouncy_charges"))
        }
    },

    UNKNOWN {
        override val matchName = "\uE024"
        override val icon: ItemStack = Items.MACE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "glitched_mace"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val translatable: Text = Text.translatable("mrc.modifier.${name.lowercase()}").formatted(Formatting.RED, Formatting.BOLD)
        override val customIcon: ItemStack = Items.MACE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/unknown"))
            set(DataComponentTypes.USE_COOLDOWN, UseCooldownComponent(0f, Optional.of(Identifier.of(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    MYSTERY {
        override val matchName = "???"
        override val icon: ItemStack = Items.LIGHT_GRAY_CANDLE.defaultStack
        override val translatable: Text = Text.translatable("mrc.modifier.${name.lowercase()}").withColor(0xcfb3fc).formatted(Formatting.ITALIC)
        override val customIcon: ItemStack = Items.LIGHT_GRAY_CANDLE.defaultStack.apply {
            set(DataComponentTypes.ITEM_MODEL, Identifier.of(MaceCompanion.MOD_ID, "modifiers/mystery"))
        }
    };


    abstract val matchName: String
    open val translatable: Text = Text.translatable("mrc.modifier.${name.lowercase()}").withColor(0xfcfc54)
    abstract val icon: ItemStack
    abstract val customIcon: ItemStack
}
