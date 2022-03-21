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

        val jutsu = itemFactory.getJutsu(weapon)

        if (jutsu != null) {
            onJutsu(attacker, jutsu)
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

    private fun onJutsu(attacker: Player, jutsu: Jutsu): Boolean {
        attacker.sendMessage("${jutsu.displayName}!")
        val target = attacker.getTargetEntity(jutsu.range) as? LivingEntity ?: return false
        target.sendMessage("${jutsu.displayName}!")
        jutsu.task(attacker, target)

        return true
    }
}
