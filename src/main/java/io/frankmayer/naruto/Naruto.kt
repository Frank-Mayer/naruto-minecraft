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


class Naruto() : JavaPlugin(), Listener {
    companion object {
        var instance: Naruto? = null
    }

    init {
        instance = this
    }

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
        val attacker = event.damager as? Player ?: return
        val weapon = attacker.inventory.itemInMainHand
        val target = event.entity as? LivingEntity ?: return

        val jutsu = itemFactory.getJutsu(weapon)

        if (jutsu != null) {
            jutsu.onHit?.invoke(attacker, target)
            event.isCancelled = true
        } else {
            val defenceJutsu = itemFactory.getJutsu(weapon)?: return
            val targetPlayer = target as? Player ?: return
            defenceJutsu.onDefend?.invoke(targetPlayer, attacker)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        val jutsu = itemFactory.getJutsu(item)

        if (jutsu != null) {
            jutsu.onUse?.invoke(player)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (itemFactory.isJutsu(player.inventory.itemInMainHand) || itemFactory.isJutsu(player.inventory.itemInOffHand)) {
            player.sendMessage("Jutsu can't be placed!")
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (itemFactory.isJutsu(event.itemDrop.itemStack)) {
            event.player.sendMessage("Jutsu can't be dropped!")
            event.isCancelled = true
        }
    }
}
