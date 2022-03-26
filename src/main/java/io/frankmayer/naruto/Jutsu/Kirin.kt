package io.frankmayer.naruto.Jutsu

import com.destroystokyo.paper.block.TargetBlockInfo
import io.frankmayer.naruto.Jutsu.MetaData.JutsuClassification
import io.frankmayer.naruto.Jutsu.MetaData.JutsuElement
import io.frankmayer.naruto.Jutsu.MetaData.JutsuRank
import io.frankmayer.naruto.Jutsu.MetaData.KekkeiGenkai
import io.frankmayer.naruto.Naruto
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.FireworkEffect
import org.bukkit.Particle
import org.bukkit.entity.*
import org.bukkit.event.block.Action
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class Kirin : IJutsu {
    override val displayName = "Kirin"
    override val identifier = "kirin"
    override val description = listOf("Summon a lightning dragon that", "strikes the target and its surrounding.")
    override val classification = JutsuClassification.NINJUTSU
    override val rank = JutsuRank.S
    override val creator = "Uchiha Sasuke"
    override val range = 32.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.RAITON

    override val onHit: Nothing? = null

    val fireworkEffect =
        FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.CREEPER).withFade(Color.BLUE)
            .flicker(true).trail(true)
    val particleOptions = Particle.DustOptions(Color.AQUA, 5.0f)

    override val onUse = { player: Player, _: Action ->
        val world = player.world
        val targetEntity = player.getTargetEntity(range.toInt(), false)
        val targetLocation = targetEntity?.location ?: player.getTargetBlock(
            32, TargetBlockInfo.FluidMode.NEVER
        )?.location

        if (targetLocation != null) {
            val fireworkEntity =
                world.spawn(world.getHighestBlockAt(targetLocation).location.add(0.0, 1.0, 0.0), Firework::class.java)
            val fireworkMeta = fireworkEntity.fireworkMeta
            fireworkMeta.addEffect(fireworkEffect.build())
            fireworkMeta.power = 1
            fireworkEntity.fireworkMeta = fireworkMeta

            world.spawnParticle(Particle.REDSTONE, player.location, 10, particleOptions)
            world.spawnParticle(Particle.REDSTONE, targetLocation, 10, particleOptions)

            Bukkit.getScheduler().scheduleSyncDelayedTask(Naruto.instance!!, {
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
            }, 8)
        }
    }

    override val onDefend: Nothing? = null
}
