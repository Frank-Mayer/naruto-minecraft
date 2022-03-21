package io.frankmayer.naruto

import org.bukkit.Bukkit.getScheduler
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

enum class Jutsu(
    val displayName: String,
    val onHit: ((attacker: Player, target: LivingEntity) -> Unit)?,
    val onUse: ((player: Player) -> Unit)?
) {
    RASENGAN("Rasengan", { attacker, target ->
        target.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 4, 10, true, false))
        target.velocity = target.velocity.add(attacker.location.direction.multiply(5.0))
        if (Naruto.instance != null) {
            getScheduler().scheduleSyncDelayedTask(
                Naruto.instance!!, {
                    target.damage(100.0)
                    if (target.isDead) {
                        target.killer = attacker
                    }
                }, 7L
            )
        }
    }, null),
    SHINRATENSEI("Shinra Tensei", null, { attacker ->
        val attackerLocationVector = attacker.location.toVector()
        val forceDistance = 16.0
        val explosionRadius = (forceDistance / 4.0).toInt()

        attacker.location.getNearbyEntities(forceDistance, forceDistance, forceDistance).forEach {
            if (it.uniqueId == attacker.uniqueId) {
                return@forEach
            }

            if (it is Projectile || it is LivingEntity || it is FallingBlock) {
                it.velocity = it.velocity.add(
                    it.location.toVector().subtract(attackerLocationVector).normalize().multiply(explosionRadius)
                )
            }

            if (it is LivingEntity) {
                it.damage(4.0, attacker)
            }
        }

        for (x in -explosionRadius..explosionRadius) {
            for (y in 0..explosionRadius) {
                for (z in -explosionRadius..explosionRadius) {
                    val blockLocation = attacker.location.add(x.toDouble(), y.toDouble(), z.toDouble())
                    val block = blockLocation.block
                    if (block.type.isSolid) {
                        block.breakNaturally()
                        blockLocation.world.spawnParticle(org.bukkit.Particle.EXPLOSION_NORMAL, blockLocation, 1)
                    }
                }
            }
        }
    }),
}
