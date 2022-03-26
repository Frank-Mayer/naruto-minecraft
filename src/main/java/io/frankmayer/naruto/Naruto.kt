package io.frankmayer.naruto

import io.papermc.paper.event.entity.EntityMoveEvent
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
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
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Naruto : JavaPlugin(), Listener {
    companion object {
        var instance: Naruto? = null
        var ninjutsuKey: NamespacedKey? = null
        var hiraishinKey: NamespacedKey? = null
        var returnInventoryKey: NamespacedKey? = null
        var prisonKey: NamespacedKey? = null
        var itemFactory: ItemFactory? = null
    }

    init {
        instance = this
        ninjutsuKey = NamespacedKey(this, "jutsu")
        hiraishinKey = NamespacedKey(this, "hiraishin")
        returnInventoryKey = NamespacedKey(this, "return_inventory")
        prisonKey = NamespacedKey(this, "prison")
        itemFactory = ItemFactory()
    }

    override fun onEnable() {
        // Plugin startup logic
        val cmdExec = NarutoCommand(itemFactory!!)
        val cmd = getCommand("naruto")!!
        cmd.setExecutor(cmdExec)
        cmd.tabCompleter = cmdExec

        Bukkit.getPluginManager().registerEvents(this, this)

        itemFactory!!.registerItems(NamespacedKey(this, "crafting"))

        val sheduleTime = 20L
        val effectTime = (sheduleTime + 10L).toInt()

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            val prisonKeyString = prisonKey!!.toString()
            val range = EJutsu.SUIROUNOJUTSU.jutsu.range

            server.worlds.forEach { world ->
                world.entities.forEach { entity ->
                    if (entity is LivingEntity) {
                        if (entity.hasMetadata(prisonKeyString)) {
                            val prisonCreatorIDs = entity.getMetadata(prisonKeyString).map { it.asString() }
                            val prisonCreatorInRange =
                                entity.getNearbyEntities(range, range, range).any { prisonCreator ->
                                    prisonCreatorIDs.contains(prisonCreator.uniqueId.toString())
                                }

                            if (prisonCreatorInRange) {
                                entity.addPotionEffect(
                                    PotionEffect(
                                        PotionEffectType.SLOW_DIGGING, effectTime, 10, false, false
                                    )
                                )
                                entity.addPotionEffect(
                                    PotionEffect(
                                        PotionEffectType.SLOW_FALLING, effectTime, 10, false, false
                                    )
                                )
                            } else {
                                entity.removeMetadata(prisonKeyString, this)
                                val sphereLocation = entity.location.clone()
                                for (x in -1..1) {
                                    for (z in -1..1) {
                                        for (y in -1..3) {
                                            val location =
                                                sphereLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble())

                                            val block = world.getBlockAt(location)

                                            if (block.type == Material.WATER || block.type == Material.BARRIER) {
                                                block.type = Material.AIR
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }, sheduleTime, sheduleTime)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity.hasMetadata(prisonKey!!.toString())) {
            event.isCancelled = true
            return
        }

        if (event.damager is Player) {
            val attacker = event.damager as? Player ?: return
            val weapon = attacker.inventory.itemInMainHand
            val target = event.entity as? LivingEntity ?: return
            val jutsu = itemFactory!!.getJutsu(weapon)
            if (jutsu?.onHit != null) {
                attacker.sendMessage("${jutsu.displayName} hit ${target.name}")
                target.sendMessage("${jutsu.displayName} hit you")
                jutsu.onHit!!.invoke(attacker, target)
                event.isCancelled = true
                return
            }
        }

        val defender = event.entity as? Player ?: return
        val defenderJutsu = itemFactory!!.getJutsu(defender.inventory.itemInMainHand) ?: return
        if (defenderJutsu.onDefend != null) {
            defenderJutsu.onDefend!!.invoke(defender, event)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerInteract(event: PlayerInteractEvent) {
        if (event.player.hasMetadata(prisonKey!!.toString())) {
            event.isCancelled = true
            return
        }

        val player = event.player
        val item = player.inventory.itemInMainHand
        val jutsu = itemFactory!!.getJutsu(item)

        if (jutsu?.onUse != null) {
            player.sendMessage("${jutsu.displayName} used")
            jutsu.onUse!!.invoke(player, event.action)
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (event.player.hasMetadata(prisonKey!!.toString())) {
            event.isCancelled = true
            return
        }

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
        if (event.entity.hasMetadata(prisonKey!!.toString())) {
            event.isCancelled = true
            return
        }

        val user = event.entity as? Player ?: return
        val arrowItem = event.consumable ?: return
        val arrowEntity = event.projectile

        if (itemFactory!!.isHiraishin(arrowItem)) {
            user.sendMessage("Hiraishin used")
            arrowEntity.setMetadata(hiraishinKey!!.toString(), FixedMetadataValue(this, user.uniqueId))
            arrowEntity.setMetadata(
                returnInventoryKey!!.toString(), FixedMetadataValue(
                    this,
                    (if (event.shouldConsumeItem() && !arrowItem.containsEnchantment(Enchantment.ARROW_INFINITE) && user.gameMode != GameMode.CREATIVE) {
                        "TRUE"
                    } else {
                        "FALSE"
                    })
                )
            )
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntityMove(event: EntityMoveEvent) {
        if (event.hasChangedPosition() && event.entity.hasMetadata(prisonKey!!.toString())) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onEntityMove(event: PlayerMoveEvent) {
        if (event.hasChangedPosition() && event.player.hasMetadata(prisonKey!!.toString())) {
            event.isCancelled = true
        }
    }
}
