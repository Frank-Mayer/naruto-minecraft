package io.frankmayer.naruto.Jutsu

import com.destroystokyo.paper.block.TargetBlockInfo
import io.frankmayer.naruto.Naruto
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.EntityType
import org.bukkit.entity.LightningStrike
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Kirin : IJutsu {
    override val displayName = "Kirin"

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val world = player.world
        val targetEntity = player.getTargetEntity(32, false)
        val targetLocation = targetEntity?.location ?: player.getTargetBlock(
            32, TargetBlockInfo.FluidMode.NEVER
        )?.location

        if (targetLocation != null) {
            val particleOptions = Particle.DustOptions(Color.AQUA, 5.0f)
            world.spawnParticle(Particle.REDSTONE, player.location, 10, particleOptions)
            world.spawnParticle(Particle.REDSTONE, targetLocation, 10, particleOptions)

            if (targetEntity is LivingEntity && !targetEntity.isDead) {
                targetEntity.addPotionEffect(
                    PotionEffect(
                        PotionEffectType.SLOW, 10, 10, false, false, false
                    )
                )
                targetEntity.damage(10.0, player)
            }

            for (i in 0L..16L) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Naruto.instance!!, {
                    val entity = world.spawnEntity(targetLocation, EntityType.LIGHTNING) as LightningStrike
                    entity.setCausingPlayer(player)
                    entity.flashCount = 3
                }, i + Math.random().toLong())
            }
        }
    }

    override val onDefend: Nothing? = null
}
