package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Jutsu.MetaData.JutsuClassification
import io.frankmayer.naruto.Jutsu.MetaData.JutsuElement
import io.frankmayer.naruto.Jutsu.MetaData.JutsuRank
import io.frankmayer.naruto.Jutsu.MetaData.KekkeiGenkai
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.block.Action
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class ShinraTensei : IJutsu {
    override val displayName = "Shinra Tensei"
    override val identifier = "shinra_tensei"
    override val description = listOf("Push away everything in the vicinity.")
    override val classification = JutsuClassification.DOJUTSU
    override val rank = JutsuRank.NONE
    override val creator: Nothing? = null
    override val range = 32.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.NONE

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val attackerLocationVector = player.location.toVector()
        val forceDistance = range
        val explosionRadius = (forceDistance / 2.0).toInt()

        player.world.spawnParticle(
            Particle.REDSTONE, player.location, 10, Particle.DustOptions(Color.PURPLE, 5.0f)
        )

        player.location.getNearbyEntities(forceDistance, forceDistance, forceDistance).forEach {
            if (it.uniqueId == player.uniqueId) {
                return@forEach
            }

            if (it is Projectile || it is LivingEntity || it is FallingBlock) {
                it.velocity = it.velocity.add(
                    it.location.toVector().subtract(attackerLocationVector).normalize().multiply(explosionRadius)
                )
            }

            if (it is LivingEntity) {
                it.damage(8.0, player)
            }
        }

        player.world.playSound(player.location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 1.5f)

        val min = 4 - explosionRadius
        val max = explosionRadius - 4

        for (x in -explosionRadius..explosionRadius) {
            for (y in -explosionRadius..explosionRadius) {
                if ((x < min || x > max) && (y < min || y > max)) {
                    continue
                }

                for (z in -explosionRadius..explosionRadius) {
                    if (((x < min || x > max) && (z < min || z > max)) || ((y < min || y > max) && (z < min || z > max))) {
                        continue
                    }

                    val blockLocation = player.location.add(x.toDouble(), y.toDouble(), z.toDouble())
                    val block = blockLocation.block
                    if (block.type.isSolid) {
                        if (x+y+z % 4 == 0) {
                            block.breakNaturally()
                        } else {
                            block.type = Material.AIR
                        }

                        if (x+y+z % 5 == 0) {
                            blockLocation.world.spawnParticle(Particle.EXPLOSION_NORMAL, blockLocation, 1)
                        }
                    } else if (block.isLiquid) {
                        block.type = Material.AIR
                    }
                }
            }
        }

        player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_FALLING, 80, 1, false, false, false))
    }

    override val onDefend: Nothing? = null
}
