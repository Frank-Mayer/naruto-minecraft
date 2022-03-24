package io.frankmayer.naruto

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin


class Naruto : JavaPlugin(), Listener {
    companion object {
        var instance: Naruto? = null
        var ninjutsuKey: NamespacedKey? = null
        var hiraishinKey: NamespacedKey? = null
        var itemFactory: ItemFactory? = null
    }

    init {
        instance = this
        ninjutsuKey = NamespacedKey(this, "jutsu")
        hiraishinKey = NamespacedKey(this, "hiraishin")
        itemFactory = ItemFactory()
    }

    override fun onEnable() {
        // Plugin startup logic
        val cmdExec = NarutoCommand(itemFactory!!)
        val cmd = getCommand("naruto")!!
        cmd.setExecutor(cmdExec)
        cmd.tabCompleter = cmdExec

        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager is Player) {
            val attacker = event.damager as? Player ?: return
            val weapon = attacker.inventory.itemInMainHand
            val target = event.entity as? LivingEntity ?: return
            val jutsu = itemFactory!!.getJutsu(weapon)
            if (jutsu != null) {
                jutsu.onHit?.invoke(attacker, target)
                event.isCancelled = true
                return
            }
        }

        val defender = event.entity as? Player ?: return
        val defenderJutsu = itemFactory!!.getJutsu(defender.inventory.itemInMainHand) ?: return
        defenderJutsu.onDefend?.invoke(defender, event)
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        val jutsu = itemFactory!!.getJutsu(item)

        if (jutsu != null) {
            jutsu.onUse?.invoke(player, event.action)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (itemFactory!!.isJutsu(player.inventory.itemInMainHand) || itemFactory!!.isJutsu(player.inventory.itemInOffHand)) {
            player.sendMessage("Jutsu can't be placed!")
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (itemFactory!!.isJutsu(event.itemDrop.itemStack)) {
            event.player.sendMessage("Jutsu can't be dropped!")
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onEntityShootBow(event: EntityShootBowEvent) {
        val user = event.entity as? Player ?: return
        val arrowItem = event.consumable ?: return
        val arrowEntity = event.projectile

        if (itemFactory!!.isHiraishin(arrowItem)) {
            arrowEntity.setMetadata(hiraishinKey!!.toString(), FixedMetadataValue(this, user.uniqueId))
        }
    }
}
