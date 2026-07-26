package moe.pxe.macecompanion.enums

import com.mojang.authlib.properties.Property
import moe.pxe.macecompanion.MaceCompanion
import moe.pxe.macecompanion.config.Config.getMysteryModifierTextAccentStyle
import moe.pxe.macecompanion.util.PlayerProfile.headFromProperty
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.UseCooldown
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.EnchantmentInstance
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.ChatFormatting
import net.minecraft.resources.Identifier
import java.util.Optional

enum class Modifiers {
    DOUBLE {
        override val matchName = "Double Mace"
        override val icon: ItemStack = Items.MACE.defaultInstance.apply {
            count = 2
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.MACE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/double_mace"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    TRIPLE {
        override val matchName = "Triple Mace"
        override val icon: ItemStack = Items.MACE.defaultInstance.apply {
            count = 3
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.MACE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/triple_mace"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    QUADRUPLE {
        override val matchName = "Quadruple Mace"
        override val icon: ItemStack = Items.MACE.defaultInstance.apply {
            count = 4
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.MACE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/quadruple_mace"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    TINY {
        override val matchName = "Tiny Mace"
        override val icon: ItemStack = Items.STONE_BUTTON.defaultInstance
        override val customIcon: ItemStack = Items.STONE_BUTTON.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/tiny_mace"))
        }
    },

    BIG {
        override val matchName = "Big Mace"
        override val icon: ItemStack = Items.STONE.defaultInstance
        override val customIcon: ItemStack = Items.STONE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/big_mace"))
        }
    },

    SLOW_TIME {
        override val matchName = "Slow Time"
        override val icon: ItemStack = Items.CLOCK.defaultInstance.apply {
            set(DataComponents.DAMAGE, 850)
            set(DataComponents.MAX_DAMAGE, 1000)
        }
        override val customIcon: ItemStack = Items.CLOCK.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/slow_time"))
        }
    },

    FAST_TIME {
        override val matchName = "Fast Time"
        override val icon: ItemStack = Items.CLOCK.defaultInstance.apply {
            set(DataComponents.DAMAGE, 1)
            set(DataComponents.MAX_DAMAGE, 1000)
        }
        override val customIcon: ItemStack = Items.CLOCK.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/fast_time"))
        }
    },

    MISS_EQUALS_DIE {
        override val matchName = "Miss = Die"
        override val icon: ItemStack = headFromProperty(Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2I4NTJiYTE1ODRkYTllNTcxNDg1OTk5NTQ1MWU0Yjk0NzQ4YzRkZDYzYWU0NTQzYzE1ZjlmOGFlYzY1YzgifX19"))
        override val customIcon: ItemStack = Items.PLAYER_HEAD.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/miss_equals_die"))
        }
    },

    SHOCKWAVE {
        override val matchName = "Shockwave Mace"
        override val icon: ItemStack = Items.HEART_OF_THE_SEA.defaultInstance.apply {
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.HEART_OF_THE_SEA.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/shockwave_mace"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    ELYTRA {
        override val matchName = "Elytra Launch"
        override val icon: ItemStack = Items.ELYTRA.defaultInstance
        override val customIcon: ItemStack = Items.ELYTRA.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/elytra_launch"))
        }
    },

    WIND_STORM {
        override val matchName = "Wind Storm"
        override val icon: ItemStack = Items.WIND_CHARGE.defaultInstance.apply {
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.WIND_CHARGE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/wind_storm"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    COBWEBS {
        override val matchName = "Cobwebs"
        override val icon: ItemStack = Items.COBWEB.defaultInstance
        override val customIcon: ItemStack = Items.COBWEB.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/cobwebs"))
        }
    },

    BLOCKS {
        override val matchName = "Blocks"
        override val icon: ItemStack = Items.CHERRY_PLANKS.defaultInstance
        override val customIcon: ItemStack = Items.CHERRY_PLANKS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/blocks"))
        }
    },

    CAGED {
        override val matchName = "Caged!"
        override val icon: ItemStack = Items.COPPER_BARS.oxidized.defaultInstance
        override val customIcon: ItemStack = Items.COPPER_BARS.oxidized.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/caged"))
        }
    },

    PILLARS {
        override val matchName = "Pillars"
        override val icon: ItemStack = Items.POLISHED_BASALT.defaultInstance
        override val customIcon: ItemStack = Items.POLISHED_BASALT.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/pillars"))
        }
    },

    SWEEPER {
        override val matchName = "Sweeper"
        override val icon: ItemStack = Items.COMPASS.defaultInstance
        override val customIcon: ItemStack = Items.COMPASS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/sweeper"))
        }
    },

    DROP {
        override val matchName = "Mace Drop"
        override val icon: ItemStack = Items.BARREL.defaultInstance
        override val customIcon: ItemStack = Items.BARREL.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/mace_drop"))
        }
    },

    HOLES {
        override val matchName = "Holes"
        override val icon: ItemStack = Items.FROGSPAWN.defaultInstance
        override val customIcon: ItemStack = Items.FROGSPAWN.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/holes"))
        }
    },

    SHRINKING {
        override val matchName = "Shrinking Map"
        override val icon: ItemStack = Items.MUSIC_DISC_11.defaultInstance
        override val customIcon: ItemStack = Items.MUSIC_DISC_11.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/shrinking_map"))
        }
    },

    DONUT {
        override val matchName = "Donut Map"
        override val icon: ItemStack = Items.MUSIC_DISC_STRAD.defaultInstance
        override val customIcon: ItemStack = Items.MUSIC_DISC_STRAD.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/donut_map"))
        }
    },

    BOUNCY {
        override val matchName = "Bouncy Floor"
        override val icon: ItemStack = Items.SLIME_BLOCK.defaultInstance
        override val customIcon: ItemStack = Items.SLIME_BLOCK.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/bouncy_floor"))
        }
    },

    ICE {
        override val matchName = "Icy Floor"
        override val icon: ItemStack = Items.ICE.defaultInstance
        override val customIcon: ItemStack = Items.ICE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/icy_floor"))
        }
    },

    FRAGILE {
        override val matchName = "Fragile Floor"
        override val icon: ItemStack = Items.GLASS.defaultInstance
        override val customIcon: ItemStack = Items.GLASS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/fragile_floor"))
        }
    },

    HIDDEN {
        override val matchName = "Hidden Floor"
        override val icon: ItemStack = Items.BARRIER.defaultInstance
        override val customIcon: ItemStack = icon
    },

    PICKAXE {
        override val matchName = "Pickaxe"
        override val icon: ItemStack = Items.STONE_PICKAXE.defaultInstance
        override val customIcon: ItemStack = Items.STONE_PICKAXE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/pickaxe"))
        }
    },

    WIND_BURST {
        override val matchName = "Wind Burst Mace"
        override val icon: ItemStack = EnchantmentHelper.createBook(EnchantmentInstance(Minecraft.getInstance().level!!.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.WIND_BURST), 1))
        override val customIcon: ItemStack = Items.BREEZE_ROD.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/wind_burst"))
        }
    },

    VICTIM {
        override val matchName = "Victim Mace"
        override val icon: ItemStack = Items.SPECTRAL_ARROW.defaultInstance
        override val customIcon: ItemStack = Items.SPECTRAL_ARROW.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/victim_mace"))
        }
    },

    CEILING {
        override val matchName = "Ceiling"
        override val icon: ItemStack = Items.OXIDIZED_COPPER_TRAPDOOR.defaultInstance
        override val customIcon: ItemStack = Items.OXIDIZED_COPPER_TRAPDOOR.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/ceiling"))
        }
    },

    RANDOM_SIZE {
        override val matchName = "Random Size"
        override val icon: ItemStack = headFromProperty(Property("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzZkNGEwMWRiNjEyNjYwMWRlZDE0MDZjZjYyMzhjZTJiNzAyNGVhY2U1ZWE2MDRmYmMyMDhhMmFmMjljOTdhZCJ9fX0="))
        override val customIcon: ItemStack = Items.PLAYER_HEAD.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/random_size"))
        }
    },

    GLOWING {
        override val matchName = "Glow in the Dark"
        override val icon: ItemStack = Items.GLOW_INK_SAC.defaultInstance
        override val customIcon: ItemStack = Items.GLOW_INK_SAC.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/glow_in_the_dark"))
        }
    },

    MOVE_EQUALS_GROW {
        override val matchName = "Move = Grow"
        override val icon: ItemStack = Items.CHAINMAIL_BOOTS.defaultInstance
        override val customIcon: ItemStack = Items.CHAINMAIL_BOOTS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/move_equals_grow"))
        }
    },

    PINATA {
        override val matchName = "Piñata"
        override val icon: ItemStack = Items.HORSE_SPAWN_EGG.defaultInstance
        override val customIcon: ItemStack = Items.HORSE_SPAWN_EGG.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/pinata"))
        }
    },

    EXPLOSIVE {
        override val matchName = "Explosive Burst"
        override val icon: ItemStack = Items.TNT_MINECART.defaultInstance
        override val customIcon: ItemStack = Items.TNT_MINECART.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/explosive_burst"))
        }
    },

    NO_JUMPING {
        override val matchName = "No Jumping"
        override val icon: ItemStack = Items.RABBIT_FOOT.defaultInstance
        override val customIcon: ItemStack = Items.RABBIT_FOOT.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/no_jumping"))
        }
    },

    STACKS {
        override val matchName = "Stacks"
        override val icon: ItemStack = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.defaultInstance
        override val customIcon: ItemStack = Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/stacks"))
        }
    },

    LUNGE {
        override val matchName = "Lunge"
        override val icon: ItemStack = Items.COPPER_SPEAR.defaultInstance.apply {
            enchant(Minecraft.getInstance().level!!.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LUNGE), 2)
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.COPPER_SPEAR.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/lunge"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    RODS {
        override val matchName = "Fishing Rods"
        override val icon: ItemStack = Items.FISHING_ROD.defaultInstance
        override val customIcon: ItemStack = Items.FISHING_ROD.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/fishing_rods"))
        }
    },

    MAGNETIC {
        override val matchName = "Magnetic Burst"
        override val icon: ItemStack = Items.IRON_NAUTILUS_ARMOR.defaultInstance
        override val customIcon: ItemStack = Items.IRON_NAUTILUS_ARMOR.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/magnetic_burst"))
        }
    },

    SNOWBALL {
        override val matchName = "Snowball Fight"
        override val icon: ItemStack = Items.POWDER_SNOW_BUCKET.defaultInstance.apply {
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val customIcon: ItemStack = Items.POWDER_SNOW_BUCKET.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/snowball_fight"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    SOUL_FLOOR {
        override val matchName = "Soul Floor"
        override val icon: ItemStack = Items.SOUL_SAND.defaultInstance
        override val customIcon: ItemStack = Items.SOUL_SAND.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/soul_floor"))
        }
    },

    PUMPKIN {
        override val matchName = "Pumpkin Curse"
        override val icon: ItemStack = Items.PUMPKIN.defaultInstance
        override val customIcon = icon
    },

    ALL {
        override val matchName = "All Mace"
        override val icon: ItemStack = Items.MACE.defaultInstance.apply {
            count = 99
        }
        override val customIcon = icon
    },

    PHARAOHS_CURSE {
        override val matchName = "Pharaoh's Curse"
        override val icon: ItemStack = Items.SUSPICIOUS_SAND.defaultInstance
        override val customIcon = icon
    },

    STICKY {
        override val matchName = "Sticky Floor"
        override val icon: ItemStack = Items.HONEY_BLOCK.defaultInstance
        override val customIcon: ItemStack = Items.HONEY_BLOCK.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/sticky_floor"))
        }
    },

    LEVITATION {
        override val matchName = "Levitation Burst"
        override val icon: ItemStack = Items.ALLAY_SPAWN_EGG.defaultInstance
        override val customIcon: ItemStack = Items.ALLAY_SPAWN_EGG.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/levitation_burst"))
        }
    },

    TOTEM {
        override val matchName = "Steal the Totem"
        override val icon: ItemStack = Items.TOTEM_OF_UNDYING.defaultInstance
        override val customIcon: ItemStack = Items.TOTEM_OF_UNDYING.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/steal_the_totem"))
        }
    },

    SPIKES {
        override val matchName = "Spikes"
        override val icon: ItemStack = Items.POINTED_DRIPSTONE.defaultInstance
        override val customIcon: ItemStack = Items.POINTED_DRIPSTONE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/spikes"))
        }
    },

    MIRROR {
        override val matchName = "Mirror"
        override val icon: ItemStack = Items.WHITE_BANNER.defaultInstance
        override val customIcon: ItemStack = Items.WHITE_BANNER.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/mirror"))
        }
    },

    MINEFIELD {
        override val matchName = "Minefield"
        override val icon: ItemStack = Items.MANGROVE_PRESSURE_PLATE.defaultInstance
        override val customIcon: ItemStack = Items.MANGROVE_PRESSURE_PLATE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/minefield"))
        }
    },

    BOUNCY_CHARGES {
        override val matchName = "Bouncy Charges"
        override val icon: ItemStack = Items.SLIME_BALL.defaultInstance
        override val customIcon: ItemStack = Items.SLIME_BALL.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/bouncy_charges"))
        }
    },

    PUNCH_EQUALS_FREEZE {
        override val matchName = "Punch = Freeze"
        override val icon: ItemStack = Items.SNOWBALL.defaultInstance
        override val customIcon: ItemStack = Items.SNOWBALL.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/punch_equals_freeze"))
        }
    },

    FREEZE_BURST {
        override val matchName = "Freeze Burst"
        override val icon: ItemStack = Items.POWDER_SNOW_BUCKET.defaultInstance
        override val customIcon: ItemStack = Items.POWDER_SNOW_BUCKET.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/freeze_burst"))
        }
    },

    SQUARE_MAP {
        override val matchName = "Square Map"
        override val icon: ItemStack = Items.STONE_PRESSURE_PLATE.defaultInstance
        override val customIcon: ItemStack = Items.STONE_PRESSURE_PLATE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/square_map"))
        }
    },

    LIGHTNING_MACE {
        override val matchName = "Lightning Mace"
        override val icon: ItemStack = Items.LIGHTNING_ROD.defaultInstance
        override val customIcon: ItemStack = Items.LIGHTNING_ROD.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/lightning_mace"))
        }
    },

    REWIND {
        override val matchName = "Rewind"
        override val icon: ItemStack = Items.PURPLE_HARNESS.defaultInstance
        override val customIcon: ItemStack = Items.PURPLE_HARNESS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/rewind"))
        }
    },

    TEAM_PAINT {
        override val matchName = "Team Paint"
        override val icon: ItemStack = Items.BRUSH.defaultInstance
        override val customIcon: ItemStack = Items.BRUSH.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/team_paint"))
        }
    },

    UMBRELLA {
        override val matchName = "Umbrella"
        override val icon: ItemStack = Items.CRIMSON_FUNGUS.defaultInstance
        override val customIcon: ItemStack = Items.CRIMSON_FUNGUS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/umbrella"))
        }
    },

    KEEP_SPRINTING {
        override val matchName = "Keep Sprinting"
        override val icon: ItemStack = Items.COPPER_BOOTS.defaultInstance
        override val customIcon: ItemStack = Items.COPPER_BOOTS.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/keep_sprinting"))
        }
    },

    HONEY_BURST {
        override val matchName = "Honey Burst"
        override val icon: ItemStack = Items.HONEY_BLOCK.defaultInstance
        override val customIcon: ItemStack = Items.HONEY_BLOCK.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/honey_burst"))
        }
    },

    UNKNOWN {
        override val matchName = "\uE024"
        override val icon: ItemStack = Items.MACE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "glitched_mace"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
        override val translatable: Component = Component.translatable("mrc.modifier.${name.lowercase()}").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
        override val customIcon: ItemStack = Items.MACE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/unknown"))
            set(DataComponents.USE_COOLDOWN, UseCooldown(0f, Optional.of(Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifier_icon"))))
        }
    },

    MYSTERY {
        override val matchName = "???"
        override val icon: ItemStack = Items.LIGHT_GRAY_CANDLE.defaultInstance
        override val translatable: Component = Component.translatable("mrc.modifier.${name.lowercase()}").withStyle(getMysteryModifierTextAccentStyle(0xD2B5FF)).withStyle(ChatFormatting.ITALIC)
        override val customIcon: ItemStack = Items.LIGHT_GRAY_CANDLE.defaultInstance.apply {
            set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MaceCompanion.MOD_ID, "modifiers/mystery"))
        }
    };


    abstract val matchName: String
    open val translatable: Component = Component.translatable("mrc.modifier.${name.lowercase()}").withColor(0xfcfc54)
    abstract val icon: ItemStack
    abstract val customIcon: ItemStack
}