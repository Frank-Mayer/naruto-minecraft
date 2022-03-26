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
import org.bukkit.entity.Player
import org.bukkit.event.block.Action

class GokakyuNoJutsu : IJutsu {
    override val displayName = "Gōkakyū no Jutsu"
    override val identifier = "gokakyu_no_jutsu"
    override val description = listOf("Great Fireball.")
    override val classification = JutsuClassification.NINJUTSU
    override val rank = JutsuRank.C
    override val creator: Nothing? = null
    override val range = 8.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.KATON

    val explosionRadius = 3

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val world = player.world
        val targetLocation =
            player.getTargetEntity(range.toInt(), false)?.location ?: player.getTargetBlock(range.toInt())?.location
            ?: player.location.clone().add(player.location.direction.clone().multiply(range))
        val direction = player.location.direction

        val scheduler = Bukkit.getScheduler()

        world.playSound(player.eyeLocation, Sound.ENTITY_WITHER_SHOOT, 1f, 0.4f)

        for (i in 1..400) {
            scheduler.scheduleSyncDelayedTask(Naruto.instance!!, {
                world.spawnParticle(
                    Particle.FLAME,
                    player.eyeLocation,
                    0,
                    direction.x + (Math.random() - 0.5) / 4,
                    direction.y + (Math.random() - 0.5) / 4,
                    direction.z + (Math.random() - 0.5) / 4,
                    Math.random() / 2 + 0.5
                )
            }, (i / 20).toLong())
        }

        scheduler.scheduleSyncDelayedTask(Naruto.instance!!, {
            for (x in -explosionRadius..explosionRadius) {
                for (y in -explosionRadius..explosionRadius) {
                    for (z in -explosionRadius..explosionRadius) {
                        val fireLocation = targetLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                        val block = fireLocation.block
                        if (block.type.isAir) {
                            block.type = Material.FIRE
                        }
                    }
                }
            }

            targetLocation.getNearbyEntities(
                explosionRadius.toDouble(),
                explosionRadius.toDouble(),
                explosionRadius.toDouble()
            ).forEach {
                it.isVisualFire = true
            }
        }, 5)
    }

    override val onDefend: Nothing? = null
}
