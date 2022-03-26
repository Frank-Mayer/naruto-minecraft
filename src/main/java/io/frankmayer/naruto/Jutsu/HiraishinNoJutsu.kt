package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Jutsu.MetaData.JutsuClassification
import io.frankmayer.naruto.Jutsu.MetaData.JutsuElement
import io.frankmayer.naruto.Jutsu.MetaData.JutsuRank
import io.frankmayer.naruto.Jutsu.MetaData.KekkeiGenkai
import io.frankmayer.naruto.Naruto
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerTeleportEvent

class HiraishinNoJutsu : IJutsu {
    override val displayName = "Hiraishin no Jutsu"
    override val identifier = "hiraishin_no_jutsu"
    override val description = listOf("Teleport to any previously marked location.")
    override val classification = JutsuClassification.SPACETIMENINJUTSU
    override val rank = JutsuRank.S
    override val creator = "Namikaze Minato"
    override val range = 8.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.RAITON

    override val onHit = { attacker: Player, target: LivingEntity ->
        val hiraishinKeyString = Naruto.hiraishinKey!!.toString()
        val world = attacker.world
        val range = 256.0
        val userIdString = attacker.uniqueId.toString()
        val hiraishinArrow = world.getNearbyEntitiesByType(Arrow::class.java, attacker.location, range, range, range)
            .firstOrNull { entity ->
                entity.getMetadata(hiraishinKeyString).map { meta ->
                    meta.asString()
                }.contains(userIdString)
            }

        if (hiraishinArrow != null) {
            val velocity = hiraishinArrow.velocity.clone()
            val particleOptions = Particle.DustOptions(Color.YELLOW, 5.0f)
            world.spawnParticle(Particle.REDSTONE, target.location, 10, particleOptions)
            world.spawnParticle(Particle.REDSTONE, hiraishinArrow.location, 10, particleOptions)
            target.teleport(hiraishinArrow, PlayerTeleportEvent.TeleportCause.PLUGIN)
            target.fallDistance = 0.0f
            if (hiraishinArrow.isOnGround || hiraishinArrow.isInBlock) {
                target.velocity = velocity
            } else {
                target.velocity = velocity.multiply(3)
            }
            hiraishinArrow.remove()
        }
    }

    override val onUse = { player: Player, action: Action ->
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            val hiraishinKeyString = Naruto.hiraishinKey!!.toString()
            val range = 256.0
            val userIdString = player.uniqueId.toString()
            val world = player.world
            val hiraishinArrow = world.getNearbyEntitiesByType(Arrow::class.java, player.location, range, range, range)
                .firstOrNull { entity ->
                    entity.getMetadata(hiraishinKeyString).map { meta ->
                        meta.asString()
                    }.contains(userIdString)
                }

            if (hiraishinArrow != null) {
                val velocity = hiraishinArrow.velocity.clone()
                val particleOptions = Particle.DustOptions(Color.YELLOW, 5.0f)
                world.spawnParticle(Particle.REDSTONE, player.location, 10, particleOptions)
                world.spawnParticle(Particle.REDSTONE, hiraishinArrow.location, 10, particleOptions)
                player.teleport(hiraishinArrow.location, PlayerTeleportEvent.TeleportCause.PLUGIN)
                player.fallDistance = 0.0f
                if (hiraishinArrow.isOnGround || hiraishinArrow.isInBlock) {
                    player.velocity = velocity
                } else {
                    player.velocity = velocity.multiply(3)
                }

                if (hiraishinArrow.getMetadata(Naruto.returnInventoryKey!!.toString()).map { meta ->
                        meta.asString()
                    }.contains("TRUE")) {
                    player.inventory.addItem(Naruto.itemFactory!!.createHiraishinArrow())
                }

                hiraishinArrow.remove()
            }
        }
    }

    override val onDefend: Nothing? = null
}