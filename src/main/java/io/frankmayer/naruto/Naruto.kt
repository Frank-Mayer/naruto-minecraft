package io.frankmayer.naruto

import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin


class Naruto : JavaPlugin(), Listener {
    private val itemFactory = ItemFactory(this)

    override fun onEnable() {
        // Plugin startup logic
        val cmdExec = NarutoCommand(itemFactory)
        val cmd = getCommand("naruto")!!
        cmd.setExecutor(cmdExec)
        cmd.tabCompleter = cmdExec

        Bukkit.getPluginManager().registerEvents(this, this)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val target = event.entity as? LivingEntity ?: return
        if (target.isDead) {
            return
        }

        val attacker = event.damager as? Player ?: return

        val weapon = attacker.inventory.itemInMainHand

        if (itemFactory.isRasengan(weapon)) {
            attacker.sendMessage("Rasengan!")
            target.sendMessage("Rasengan!")
            target.velocity = target.velocity.add(attacker.location.direction.multiply(5))
        } else {
            attacker.sendMessage("Not Rasengan!")
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (itemFactory.isNinjutsu(player.inventory.itemInMainHand) || itemFactory.isNinjutsu(player.inventory.itemInOffHand)) {
            player.sendMessage("Ninjutsu can't be placed!")
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (itemFactory.isNinjutsu(event.itemDrop.itemStack)) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (itemFactory.isNinjutsu(event.item ?: return)) {
            event.isCancelled = true
        }
    }
}
