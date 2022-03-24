package io.frankmayer.naruto

import com.destroystokyo.paper.block.TargetBlockInfo
import org.bukkit.Bukkit.getScheduler
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor

enum class Jutsu(
    val displayName: String,
    val onHit: ((attacker: Player, target: LivingEntity) -> Unit)?,
    val onUse: ((player: Player) -> Unit)?,
    val onDefend: ((defender: Player, event: EntityDamageByEntityEvent) -> Unit)?,
) {
    RASENGAN("Rasengan", { attacker, target ->
        target.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 8, 10, true, false))
        target.world.spawnParticle(Particle.EXPLOSION_LARGE, target.eyeLocation, 5)
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
    }, null, null),
    SHINRATENSEI("Shinra Tensei", null, { attacker ->
        val attackerLocationVector = attacker.location.toVector()
        val forceDistance = 32.0
        val explosionRadius = (forceDistance / 4.0).toInt()

        attacker.world.spawnParticle(
            Particle.REDSTONE, attacker.location, 10, Particle.DustOptions(Color.PURPLE, 5.0f)
        )

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
                        blockLocation.world.spawnParticle(Particle.EXPLOSION_NORMAL, blockLocation, 1)
                    } else if (block.isLiquid) {
                        block.type = Material.AIR
                    }
                }
            }
        }

        for (x in -explosionRadius..explosionRadius) {
            for (z in -explosionRadius..explosionRadius) {
                val blockLocation = attacker.location.add(x.toDouble(), -1.0, z.toDouble())
                val block = blockLocation.block
                if (block.type == Material.GRASS_BLOCK || block.type == Material.DIRT) {
                    block.type = Material.COARSE_DIRT
                } else if (block.type == Material.STONE) {
                    block.type = Material.GRAVEL
                } else if (block.type == Material.SANDSTONE || block.type == Material.CHISELED_RED_SANDSTONE || block.type == Material.CHISELED_SANDSTONE || block.type == Material.SMOOTH_RED_SANDSTONE || block.type == Material.SMOOTH_SANDSTONE) {
                    block.type = Material.SAND
                }
            }
        }
    }, null),
    AMENOTEJIKARA("Amenotejikara", null, { user ->
        val range = Math.random() * 16.0 + 16.0

        val possibleTargets: List<Entity> =
            user.world.getNearbyEntities(user.location, range, range, range).filter { it.uniqueId != user.uniqueId }
                .sortedByDescending { it.location.distance(user.location) }

        if (possibleTargets.isNotEmpty()) {
            val target = possibleTargets[floor(Math.random() * possibleTargets.size).toInt()]

            val particleOptions = Particle.DustOptions(Color.PURPLE, 5.0f)
            user.world.spawnParticle(Particle.REDSTONE, user.location, 10, particleOptions)
            user.world.spawnParticle(Particle.REDSTONE, target.location, 10, particleOptions)
            if (target is LivingEntity) {
                val livingTarget = target
                livingTarget.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.CONFUSION, 8, 10, true, false
                    )
                )
            }
            user.addPotionEffect(
                PotionEffect(
                    PotionEffectType.CONFUSION, 8, 10, true, false
                )
            )

            val targetLocation = target.location.clone()
            val userLocation = user.location.clone()

            val targetVelocity = target.velocity.clone()
            val userVelocity = user.velocity.clone()

            target.teleport(userLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            target.velocity = userVelocity

            user.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            user.velocity = targetVelocity
        }
    }, { user, event ->
        val range = Math.random() * 16.0 + 16.0

        val possibleTargets: List<Entity> = user.world.getNearbyEntities(user.location, range, range, range)
            .filter { it.uniqueId != user.uniqueId && it.uniqueId != event.damager.uniqueId }
            .sortedByDescending { it.location.distance(user.location) }

        if (possibleTargets.isNotEmpty()) {
            val target = possibleTargets[floor(Math.random() * possibleTargets.size).toInt()]

            val particleOptions = Particle.DustOptions(Color.PURPLE, 5.0f)
            user.world.spawnParticle(Particle.REDSTONE, user.location, 10, particleOptions)
            user.world.spawnParticle(Particle.REDSTONE, target.location, 10, particleOptions)
            if (target is LivingEntity) {
                val livingTarget = target
                livingTarget.damage(event.damage, event.damager)
                livingTarget.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.CONFUSION, 10, 10, true, false
                    )
                )
            }
            user.addPotionEffect(
                PotionEffect(
                    PotionEffectType.CONFUSION, 10, 10, true, false
                )
            )

            val targetLocation = target.location.clone()
            val userLocation = user.location.clone()

            val targetVelocity = target.velocity.clone()
            val userVelocity = user.velocity.clone()

            target.teleport(userLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            target.velocity = userVelocity

            user.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            user.velocity = targetVelocity
        }
    }),
    KIRIN("Kirin", null, { user ->
        val world = user.world
        val targetLocation = user.getTargetEntity(16, false)?.location ?: user.getTargetBlock(
            16, TargetBlockInfo.FluidMode.NEVER
        )?.location

        if (targetLocation != null) {
            val particleOptions = Particle.DustOptions(Color.AQUA, 5.0f)
            world.spawnParticle(Particle.REDSTONE, user.location, 10, particleOptions)
            world.spawnParticle(Particle.REDSTONE, targetLocation, 10, particleOptions)
            for (i in 0L..16L) {
                getScheduler().scheduleSyncDelayedTask(Naruto.instance!!, {
                    val entity = world.spawnEntity(targetLocation, EntityType.LIGHTNING) as LightningStrike
                    entity.setCausingPlayer(user)
                    entity.flashCount = 3
                }, i + Math.random().toLong())
            }
        }
    }, null)
}
