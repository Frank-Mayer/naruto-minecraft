package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Jutsu.MetaData.JutsuClassification
import io.frankmayer.naruto.Jutsu.MetaData.JutsuElement
import io.frankmayer.naruto.Jutsu.MetaData.JutsuRank
import io.frankmayer.naruto.Jutsu.MetaData.KekkeiGenkai
import io.frankmayer.naruto.Naruto
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.roundToLong

class Suijinheki : IJutsu {
    override val displayName = "Suijinheki"
    override val identifier = "suijinheki"
    override val description = listOf("Great water wave.")
    override val classification = JutsuClassification.NINJUTSU
    override val rank = JutsuRank.B
    override val creator: Nothing? = null
    override val range = 5.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.SUITON

    val explosionRadius = 5

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val world = player.world
        val targetLocation =
            player.getTargetEntity(range.toInt(), false)?.location ?: player.getTargetBlock(range.toInt())?.location
            ?: player.location.clone().add(player.location.direction.clone().multiply(range))
        val direction = player.location.direction

        val scheduler = Bukkit.getScheduler()

        world.playSound(player.eyeLocation, Sound.ENTITY_PLAYER_SWIM, 1f, 1.5f)

        for (i in 1..500) {
            scheduler.scheduleSyncDelayedTask(Naruto.instance!!, {
                world.spawnParticle(
                    Particle.WATER_WAKE,
                    player.eyeLocation.clone().subtract(0.0, 0.2, 0.0),
                    0,
                    direction.x + (Math.random() - 0.5) / 3,
                    direction.y + (Math.random() - 0.5) / 3,
                    direction.z + (Math.random() - 0.5) / 3,
                    Math.random() / 2 + 0.5
                )
            }, (i.toFloat() / 40.0f).roundToLong())
        }

        val entityVelocity = direction.clone()
        targetLocation.getNearbyEntities(
            explosionRadius.toDouble(),
            explosionRadius.toDouble(),
            explosionRadius.toDouble()
        ).forEach {
            if (it.uniqueId != player.uniqueId) {
                it.velocity = entityVelocity
                if (it is LivingEntity) {
                    it.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 40, 10, true, true))
                    it.damage(2.0)
                }
            }
        }

        scheduler.scheduleSyncDelayedTask(Naruto.instance!!, {
            for (x in -explosionRadius..explosionRadius) {
                for (y in -explosionRadius..explosionRadius) {
                    for (z in -explosionRadius..explosionRadius) {
                        val fireLocation = targetLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                        val block = fireLocation.block
                        when (block.type) {
                            Material.FIRE -> {
                                block.type = Material.AIR
                            }
                            Material.LAVA -> {
                                block.type = Material.OBSIDIAN
                            }
                            Material.MAGMA_BLOCK -> {
                                block.type = Material.COBBLESTONE
                            }
                            Material.SPONGE -> {
                                block.type = Material.WET_SPONGE
                            }
                            else -> {}
                        }
                    }
                }
            }
        }, 5)
    }

    override val onDefend: Nothing? = null
}