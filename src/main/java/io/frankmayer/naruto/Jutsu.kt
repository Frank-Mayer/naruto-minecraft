package io.frankmayer.naruto

import org.bukkit.Bukkit.getScheduler
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

enum class Jutsu(val displayName: String, val range: Int, val task: (attacker: Player, target: LivingEntity) -> Unit) {
    RASENGAN("Rasengan", 4, { attacker, target ->
        target.addPotionEffect(PotionEffect(PotionEffectType.CONFUSION, 20, 10, true, false))
        target.velocity = target.velocity.add(attacker.location.direction.multiply(5.0))
        if (Naruto.instance != null) {
            getScheduler().scheduleSyncDelayedTask(
                Naruto.instance!!, {
                    val newHealth = kotlin.math.max(0.0, target.health - 100.0)
                    if (newHealth == 0.0) {
                        target.killer = attacker
                        target.health = 0.0
                    } else {
                        target.health = newHealth
                    }
                }, 7L
            )
        }
    }),
}
