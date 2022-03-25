package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Naruto
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor

class Amenotejikara : IJutsu {
    override val displayName = "Amenotejikara"

    override val onHit = { attacker: Player, target: LivingEntity ->
        target.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 8, 10, true, false))
        target.world.playSound(target.location, Sound.ENTITY_SHULKER_SHOOT, 1f, 1.5f)
        target.world.spawnParticle(Particle.EXPLOSION_LARGE, target.eyeLocation, 5)
        target.velocity = target.velocity.add(attacker.location.direction.multiply(5.0))
        if (Naruto.instance != null) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                Naruto.instance!!, {
                    target.damage(100.0)
                    if (target.isDead) {
                        target.killer = attacker
                    }
                }, 7L
            )
        }
    }

    override val onUse = { player: Player, _: Action ->
        val range = Math.random() * 16.0 + 16.0

        val possibleTargets: List<Entity> = player.world.getNearbyEntities(player.location, range, range, range)
            .filter { it.uniqueId != player.uniqueId }.sortedByDescending { it.location.distance(player.location) }

        if (possibleTargets.isNotEmpty()) {
            val target = possibleTargets[floor(Math.random() * possibleTargets.size).toInt()]

            val particleOptions = Particle.DustOptions(Color.PURPLE, 5.0f)
            player.world.spawnParticle(Particle.REDSTONE, player.location, 10, particleOptions)
            player.world.spawnParticle(Particle.REDSTONE, target.location, 10, particleOptions)
            if (target is LivingEntity) {
                target.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.CONFUSION, 8, 10, true, false
                    )
                )
            }
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.CONFUSION, 8, 10, true, false
                )
            )

            val targetLocation = target.location.clone()
            val userLocation = player.location.clone()

            val targetVelocity = target.velocity.clone()
            val userVelocity = player.velocity.clone()

            target.teleport(userLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            target.velocity = userVelocity

            player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            player.velocity = targetVelocity
        }
    }

    override val onDefend = { defender: Player, event: EntityDamageByEntityEvent ->
        val range = 32.0

        val possibleTargets: List<Entity> = defender.world.getNearbyEntities(defender.location, range, range, range)
            .filter { it.uniqueId != defender.uniqueId && it.uniqueId != event.damager.uniqueId }
            .sortedByDescending { it.location.distance(defender.location) }

        if (possibleTargets.isNotEmpty()) {
            val target = possibleTargets[floor(Math.random() * possibleTargets.size).toInt()]

            val particleOptions = Particle.DustOptions(Color.PURPLE, 5.0f)
            defender.world.spawnParticle(Particle.REDSTONE, defender.location, 10, particleOptions)
            defender.world.spawnParticle(Particle.REDSTONE, target.location, 10, particleOptions)
            if (target is LivingEntity) {
                target.damage(event.damage, event.damager)
                target.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.CONFUSION, 10, 10, true, false
                    )
                )
            }
            defender.addPotionEffect(
                PotionEffect(
                    PotionEffectType.CONFUSION, 10, 10, true, false
                )
            )

            val targetLocation = target.location.clone()
            val userLocation = defender.location.clone()

            val targetVelocity = target.velocity.clone()
            val userVelocity = defender.velocity.clone()

            target.teleport(userLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            target.velocity = userVelocity

            defender.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            defender.velocity = targetVelocity
        }
    }
}