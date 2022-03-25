package io.frankmayer.naruto.Jutsu

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.block.Action

class ShinraTensei : IJutsu {
    override val displayName = "Shinra Tensei"

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val attackerLocationVector = player.location.toVector()
        val forceDistance = 32.0
        val explosionRadius = (forceDistance / 4.0).toInt()

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
                it.damage(4.0, player)
            }
        }

        for (x in -explosionRadius..explosionRadius) {
            for (y in 0..explosionRadius) {
                for (z in -explosionRadius..explosionRadius) {
                    val blockLocation = player.location.add(x.toDouble(), y.toDouble(), z.toDouble())
                    val block = blockLocation.block
                    if (block.type.isSolid) {
                        block.breakNaturally()
                        blockLocation.world.spawnParticle(Particle.EXPLOSION_NORMAL, blockLocation, 1)
                    } else if (block.isLiquid) {
                        block.type = Material.AIR
                    }
                }
            }
        }

        for (x in -explosionRadius..explosionRadius) {
            for (z in -explosionRadius..explosionRadius) {
                val blockLocation = player.location.add(x.toDouble(), -1.0, z.toDouble())
                val block = blockLocation.block
                when (block.type) {
                    Material.GRASS_BLOCK, Material.DIRT -> {
                        block.type = Material.COARSE_DIRT
                    }
                    Material.STONE -> {
                        block.type = Material.GRAVEL
                    }
                    Material.SANDSTONE, Material.CHISELED_RED_SANDSTONE, Material.CHISELED_SANDSTONE, Material.SMOOTH_RED_SANDSTONE, Material.SMOOTH_SANDSTONE -> {
                        block.type = Material.SAND
                    }
                    else -> {}
                }
            }
        }
    }

    override val onDefend: Nothing? = null
}
