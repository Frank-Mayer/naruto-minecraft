package io.frankmayer.naruto.Jutsu

import org.bukkit.Color
import org.bukkit.Particle
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
    override val description = listOf("Switch places with any entity in range.")
    override val classification = JutsuClassification.DOJUTSU
    override val rank: Nothing? = null
    override val creator = "Uchiha Sasuke"
    override val range = 10.0
    override val kekkeiGenkai = KekkeiGenkai.RINNEGAN
    override val element = JutsuElement.NONE

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val target = player.getTargetEntity(range.toInt(), false) ?: player.world.getNearbyEntities(player.location, range, range, range)
            .filter { it.uniqueId != player.uniqueId }.sortedByDescending { it.location.distance(player.location) }
            .randomOrNull()

        if (target != null) {
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
            target.fallDistance = 0.0f
            target.velocity = userVelocity

            player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            player.fallDistance = 0.0f
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
            target.fallDistance = 0.0f
            target.velocity = userVelocity

            defender.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)
            defender.fallDistance = 0.0f
            defender.velocity = targetVelocity
        }
    }
}