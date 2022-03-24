package io.frankmayer.naruto

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit.getServer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType

class ItemFactory {
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

    private val kirinTitle = Component.text(Jutsu.KIRIN.displayName)
    private val kirinLore = listOf(
        Component.text("Classification: Ninjutsu"),
        Component.text("Element: Raiton"),
        Component.text("Rank: S-rank"),
        Component.text("Class: Offensive"),
        Component.text("Range: Long range"),
        Component.text("Creator: Uchiha Sasuke")
    )

    private val HiraishinTitle = Component.text(Jutsu.HIRAISHINNOJUTSU.displayName)
    private val HiraishinLore = listOf(
        Component.text("Classification: Space–Time Ninjutsu"),
        Component.text("Rank: S-rank"),
        Component.text("Class: Supplementary"),
        Component.text("Range: Long range"),
        Component.text("Creator: Senju Tobirama")
    )

    private val HiraishinArrowTitle = Component.text("Hiraishin Arrow")
    private val HiraishinArrowLore = listOf(
        Component.text("The signature tools of Namikaze Minato,"),
        Component.text("who uses them in conjunction with his"),
        Component.text("Space–Time Ninjutsu: " + Jutsu.HIRAISHINNOJUTSU.displayName + ".")
    )

    private val OrukaTitle = Component.text(Jutsu.ORUKA.displayName)
    private val OrukaLore = listOf(
        Component.text("Classification: Ninjutsu"),
        Component.text("Element: Suiton"),
        Component.text("Class: Offensive"),
        Component.text("Range: Short range"),
        Component.text("Creator: Uzumaki Boruto")
    )

    internal fun createRasengan(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(Naruto.ninjutsuKey!!, PersistentDataType.STRING, Jutsu.RASENGAN.displayName)
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
        meta.persistentDataContainer.set(
            Naruto.ninjutsuKey!!, PersistentDataType.STRING, Jutsu.SHINRATENSEI.displayName
        )
        meta.displayName(shinraTenseiTitle)
        meta.lore(shinraTenseiLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)
        meta.itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun createAmenotejikara(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(
            Naruto.ninjutsuKey!!, PersistentDataType.STRING, Jutsu.AMENOTEJIKARA.displayName
        )
        meta.displayName(amenotejikaraTitle)
        meta.lore(amenotejikaraLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)
        meta.itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun createKirin(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(Naruto.ninjutsuKey!!, PersistentDataType.STRING, Jutsu.KIRIN.displayName)
        meta.displayName(kirinTitle)
        meta.lore(kirinLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)
        meta.itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun createHiraishinArrow(): ItemStack {
        val stack = ItemStack(Material.ARROW)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(Naruto.hiraishinKey!!, PersistentDataType.STRING, "")
        meta.displayName(HiraishinArrowTitle)
        meta.lore(HiraishinArrowLore)

        stack.itemMeta = meta
        return stack
    }

    internal fun createHiraishin(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(
            Naruto.ninjutsuKey!!, PersistentDataType.STRING, Jutsu.HIRAISHINNOJUTSU.displayName
        )
        meta.displayName(HiraishinTitle)
        meta.lore(HiraishinLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)
        meta.itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun createOruka(): ItemStack {
        val stack = ItemStack(Material.PLAYER_HEAD)
        val meta = stack.itemMeta
        meta.persistentDataContainer.set(Naruto.ninjutsuKey!!, PersistentDataType.STRING, Jutsu.ORUKA.displayName)
        meta.displayName(OrukaTitle)
        meta.lore(OrukaLore)
        meta.addEnchant(org.bukkit.enchantments.Enchantment.VANISHING_CURSE, 1, true)
        meta.itemFlags.add(ItemFlag.HIDE_ATTRIBUTES)

        stack.itemMeta = meta
        return stack
    }

    internal fun isJutsu(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) {
            return false
        }

        return item.itemMeta.persistentDataContainer.has(Naruto.ninjutsuKey!!, PersistentDataType.STRING)
    }

    internal fun getJutsu(item: ItemStack): Jutsu? {
        if (!item.hasItemMeta()) {
            return null
        }

        for (jutsu in Jutsu.values()) {
            if (item.itemMeta.persistentDataContainer.get(
                    Naruto.ninjutsuKey!!, PersistentDataType.STRING
                ) == jutsu.displayName
            ) {
                return jutsu
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
