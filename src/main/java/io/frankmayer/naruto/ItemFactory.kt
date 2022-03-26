package io.frankmayer.naruto

import io.frankmayer.naruto.Jutsu.IJutsu
import io.frankmayer.naruto.Jutsu.JutsuElement
import io.frankmayer.naruto.Jutsu.KekkeiGenkai
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType

class ItemFactory {
    private val HiraishinArrowTitle = Component.text("Hiraishin Arrow")
    private val HiraishinArrowLore = listOf(
        Component.text("The signature tools of Namikaze Minato,"),
        Component.text("who uses them in conjunction with his"),
        Component.text("Space–Time Ninjutsu: " + EJutsu.HIRAISHINNOJUTSU.jutsu.displayName + ".")
    )

    internal fun createHiraishinArrow(): ItemStack {
        val stack = ItemStack(Material.ARROW)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(Naruto.hiraishinKey!!, PersistentDataType.STRING, "")
        meta.displayName(HiraishinArrowTitle)
        meta.lore(HiraishinArrowLore)

        stack.itemMeta = meta
        return stack
    }

    internal fun createJutsu(jutsu: IJutsu): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(
            Naruto.ninjutsuKey!!, PersistentDataType.STRING, jutsu.displayName
        )
        meta.displayName(Component.text(jutsu.displayName))

        val lore = mutableListOf<String>()
        lore.addAll(jutsu.description)
        lore.add("Classification: " + jutsu.classification.displayName)
        if (jutsu.rank != null) {
            lore.add("Rank: " + jutsu.rank)
        }
        if (jutsu.creator != null) {
            lore.add("Creator: " + jutsu.creator)
        }
        lore.add("Range: " + jutsu.range)
        if (jutsu.kekkeiGenkai != KekkeiGenkai.NONE) {
            lore.add("Kekkei Genkai: " + jutsu.kekkeiGenkai.displayName)
        }
        if (jutsu.element != JutsuElement.NONE) {
            lore.add("Element: " + jutsu.element.displayName)
        }
        meta.lore(lore.map { Component.text(it) })

        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true)
        meta.itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun createJutsu(jutsu: EJutsu): ItemStack = createJutsu(jutsu.jutsu)

    internal fun isJutsu(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) {
            return false
        }

        return item.itemMeta.persistentDataContainer.has(Naruto.ninjutsuKey!!, PersistentDataType.STRING)
    }

    internal fun getJutsu(item: ItemStack): IJutsu? {
        if (!item.hasItemMeta()) {
            return null
        }

        for (jutsu in EJutsu.values()) {
            if (item.itemMeta.persistentDataContainer.get(
                    Naruto.ninjutsuKey!!, PersistentDataType.STRING
                ) == jutsu.jutsu.displayName
            ) {
                return jutsu.jutsu
            }
        }

        return null
    }

    internal fun isHiraishin(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) {
            return false
        }

        return item.itemMeta.persistentDataContainer.has(Naruto.hiraishinKey!!, PersistentDataType.STRING)
    }

    internal fun registerItems(key: NamespacedKey) {
        getServer().addRecipe(
            ShapelessRecipe(key, createHiraishinArrow()).addIngredient(Material.ARROW).addIngredient(Material.PAPER)
        )
    }
}
