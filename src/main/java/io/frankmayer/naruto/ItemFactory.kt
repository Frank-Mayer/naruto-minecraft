package io.frankmayer.naruto

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

class ItemFactory(private val plugin: Plugin) {
    private val ninjutsuKey = NamespacedKey(plugin, "jutsu")

    private val rasenganTitle = Component.text(Jutsu.RASENGAN.displayName)
    private val rasenganLore = listOf(
        Component.text("Classification: Ninjutsu"),
        Component.text("Rank: A-rank"),
        Component.text("Class: Offensive"),
        Component.text("Range: Short range"),
        Component.text("Creator: Namikaze Minato")
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

    internal fun isJutsu(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) {
            return false
        }

        return item.itemMeta.persistentDataContainer.has(ninjutsuKey, PersistentDataType.STRING)
    }

    internal fun getJutsu(item: ItemStack): Jutsu? {
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

    internal fun getJutsu(player: Player): Jutsu? {
        return getJutsu(player.inventory.itemInMainHand) ?: getJutsu(player.inventory.itemInOffHand)
    }
}
