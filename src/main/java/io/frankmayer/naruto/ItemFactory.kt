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

class ItemFactory(private val plugin: Plugin) {
    private val ninjutsuKey = NamespacedKey(plugin, "ninjutsu")

    private val rasenganTitle = Component.text("Rasengan")
    private val rasenganLore = listOf(
        Component.text("Art: Nin-Jutsu"),
        Component.text("Rang: A"),
        Component.text("Typ: Angriff"),
        Component.text("Erfinder: Namikaze Minato")
    )

    internal fun createRasengan(): ItemStack {
        val stack = ItemStack(Material.ENDER_PEARL)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(ninjutsuKey, PersistentDataType.STRING, "rasengan")
        meta.displayName(rasenganTitle)
        meta.lore(rasenganLore)

        meta.addAttributeModifier(
            Attribute.GENERIC_ATTACK_SPEED, AttributeModifier(
                "generic.attackSpeed", 0.2, AttributeModifier.Operation.MULTIPLY_SCALAR_1
            )
        )

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun isRasengan(item: ItemStack): Boolean {
        val meta = item.itemMeta
        return meta.persistentDataContainer.get(ninjutsuKey, PersistentDataType.STRING) == "rasengan"
    }

    internal fun isNinjutsu(item: ItemStack): Boolean {
        return item.itemMeta.persistentDataContainer.has(ninjutsuKey, PersistentDataType.STRING)
    }
}
