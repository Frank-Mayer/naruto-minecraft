package io.frankmayer.naruto

import com.destroystokyo.paper.block.TargetBlockInfo
import org.bukkit.Bukkit.getScheduler
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.*
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor

enum class Jutsu(
    val displayName: String,
    val onHit: ((attacker: Player, target: LivingEntity) -> Unit)?,
    val onUse: ((player: Player, action: Action) -> Unit)?,
    val onDefend: ((defender: Player, event: EntityDamageByEntityEvent) -> Unit)?,
) {
    RASENGAN("Rasengan", { attacker, target ->
        target.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 8, 10, true, false))
        target.world.playSound(target.location, Sound.ENTITY_SHULKER_SHOOT, 1f, 1.5f)
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
    SHINRATENSEI("Shinra Tensei", null, { attacker, _ ->
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
    }, null),
    AMENOTEJIKARA("Amenotejikara", null, { user, _ ->
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
                target.addPotionEffect(
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
        val range = 32.0

        val possibleTargets: List<Entity> = user.world.getNearbyEntities(user.location, range, range, range)
            .filter { it.uniqueId != user.uniqueId && it.uniqueId != event.damager.uniqueId }
            .sortedByDescending { it.location.distance(user.location) }

        if (possibleTargets.isNotEmpty()) {
            val target = possibleTargets[floor(Math.random() * possibleTargets.size).toInt()]

            val particleOptions = Particle.DustOptions(Color.PURPLE, 5.0f)
            user.world.spawnParticle(Particle.REDSTONE, user.location, 10, particleOptions)
            user.world.spawnParticle(Particle.REDSTONE, target.location, 10, particleOptions)
            if (target is LivingEntity) {
                target.damage(event.damage, event.damager)
                target.addPotionEffect(
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
    KIRIN("Kirin", null, { user, _ ->
        val world = user.world
        val targetEntity = user.getTargetEntity(32, false)
        val targetLocation = targetEntity?.location ?: user.getTargetBlock(
            32, TargetBlockInfo.FluidMode.NEVER
        )?.location

        if (targetLocation != null) {
            val particleOptions = Particle.DustOptions(Color.AQUA, 5.0f)
            world.spawnParticle(Particle.REDSTONE, user.location, 10, particleOptions)
            world.spawnParticle(Particle.REDSTONE, targetLocation, 10, particleOptions)

            if (targetEntity is LivingEntity && !targetEntity.isDead) {
                targetEntity.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.SLOW, 10, 10, false, false, false
                    )
                )
                targetEntity.damage(10.0, user)
            }

            for (i in 0L..16L) {
                getScheduler().scheduleSyncDelayedTask(Naruto.instance!!, {
                    val entity = world.spawnEntity(targetLocation, EntityType.LIGHTNING) as LightningStrike
                    entity.setCausingPlayer(user)
                    entity.flashCount = 3
                }, i + Math.random().toLong())
            }
        }
    }, null),
    HIRAISHINNOJUTSU("Hiraishin no Jutsu", { attacker, target ->
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
            if (hiraishinArrow.isOnGround || hiraishinArrow.isInBlock) {
                target.velocity = velocity
            } else {
                target.velocity = velocity.multiply(3)
            }
            hiraishinArrow.remove()
        }
    }, { user, action ->
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            val hiraishinKeyString = Naruto.hiraishinKey!!.toString()
            val range = 256.0
            val userIdString = user.uniqueId.toString()
            val world = user.world
            val hiraishinArrow = world.getNearbyEntitiesByType(Arrow::class.java, user.location, range, range, range)
                .firstOrNull { entity ->
                    entity.getMetadata(hiraishinKeyString).map { meta ->
                        meta.asString()
                    }.contains(userIdString)
                }

            if (hiraishinArrow != null) {
                val velocity = hiraishinArrow.velocity.clone()
                val particleOptions = Particle.DustOptions(Color.YELLOW, 5.0f)
                world.spawnParticle(Particle.REDSTONE, user.location, 10, particleOptions)
                world.spawnParticle(Particle.REDSTONE, hiraishinArrow.location, 10, particleOptions)
                user.teleport(hiraishinArrow, PlayerTeleportEvent.TeleportCause.PLUGIN)
                if (hiraishinArrow.isOnGround || hiraishinArrow.isInBlock) {
                    user.velocity = velocity
                } else {
                    user.velocity = velocity.multiply(3)
                }
                user.inventory.addItem(Naruto.itemFactory!!.createHiraishinArrow())
                hiraishinArrow.remove()
            }
        }
    }, null),
    ORUKA("Oruka", null, { player, _ ->
        val block1 = player.location.block
        val block2 = player.location.add(0.0, 1.0, 0.0).block

        if (block1.type == Material.AIR || block1.type == Material.CAVE_AIR || block1.type == Material.VOID_AIR) {
            block1.type = Material.WATER

            getScheduler().scheduleSyncDelayedTask(Naruto.instance!!, {
                block1.type = Material.AIR
            }, 40L)
        }

        if (block2.type == Material.AIR || block2.type == Material.CAVE_AIR || block2.type == Material.VOID_AIR) {
            block2.type = Material.WATER

            getScheduler().scheduleSyncDelayedTask(Naruto.instance!!, {
                block2.type = Material.AIR
            }, 40L)
        }

    }, null)
}
