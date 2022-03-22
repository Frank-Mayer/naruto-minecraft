package io.frankmayer.naruto

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class ItemFactory(plugin: Plugin) {
    private val ninjutsuKey = NamespacedKey(plugin, "jutsu")

    private val rasenganTitle = Component.text(Jutsu.RASENGAN.displayName)
    private val rasenganLore = listOf(
        Component.text("Classification: Ninjutsu"),
        Component.text("Rank: A-rank"),
        Component.text("Class: Offensive"),
        Component.text("Range: Short range"),
        Component.text("Creator: Namikaze Minato")
    )
    private val shinraTenseiTitle = Component.text(Jutsu.SHINRATENSEI.displayName)
    private val shinraTenseiLore = listOf(
        Component.text("Classification: Dōjutsu"),
        Component.text("Class: Offensive, Defensive"),
        Component.text("Range: All ranges"),
        Component.text("Kekkei Genkai: Rin'negan")
    )

    private val amenotejikaraTitle = Component.text(Jutsu.AMENOTEJIKARA.displayName)
    private val amenotejikaraLore = listOf(
        Component.text("Classification: Dōjutsu"),
        Component.text("Class: Supplementary"),
        Component.text("Range: Short to Mid range"),
        Component.text("Kekkei Genkai: Rin'negan")
    )

    internal fun createRasengan(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(ninjutsuKey, PersistentDataType.STRING, Jutsu.RASENGAN.displayName)
        meta.displayName(rasenganTitle)
        meta.lore(rasenganLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)

        meta.addAttributeModifier(
            Attribute.GENERIC_ATTACK_SPEED, AttributeModifier(
                "generic.attackSpeed", 0.2, AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun createShinraTensei(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(ninjutsuKey, PersistentDataType.STRING, Jutsu.SHINRATENSEI.displayName)
        meta.displayName(shinraTenseiTitle)
        meta.lore(shinraTenseiLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)

        stack.itemMeta = meta
        return stack
    }

    internal fun createAmenotejikara(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(ninjutsuKey, PersistentDataType.STRING, Jutsu.AMENOTEJIKARA.displayName)
        meta.displayName(amenotejikaraTitle)
        meta.lore(amenotejikaraLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)

        stack.itemMeta = meta
        return stack
    }

    internal fun isJutsu(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) {
            return false
        }

        return item.itemMeta.persistentDataContainer.has(ninjutsuKey, PersistentDataType.STRING)
    }

    internal fun getJutsu(item: ItemStack): Jutsu? {
        if (!item.hasItemMeta()) {
            return null
        }

        for (jutsu in Jutsu.values()) {
            if (item.itemMeta.persistentDataContainer.get(
                    ninjutsuKey,
                    PersistentDataType.STRING
                ) == jutsu.displayName
            ) {
                return jutsu
            }
        }

        return null
    }
}
