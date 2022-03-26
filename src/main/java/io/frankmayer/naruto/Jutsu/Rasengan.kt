package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Naruto
import org.bukkit.Bukkit.getScheduler
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


internal class Rasengan : IJutsu {
    override val displayName = "Rasengan"
    override val description = listOf("The Rasengan is a spinning ball of chakra formed and held in the palm","of the user's hand, which is only known to a handful of shinobi.")
    override val classification = JutsuClassification.NINJUTSU
    override val rank = JutsuRank.A
    override val creator = "Namikaze Minato"
    override val range = 5.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.NONE

    override val onHit = { attacker: Player, target: LivingEntity ->
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
    }

    override val onUse: Nothing? = null

    override val onDefend: Nothing? = null
}
