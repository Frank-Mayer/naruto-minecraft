package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Jutsu.MetaData.JutsuClassification
import io.frankmayer.naruto.Jutsu.MetaData.JutsuElement
import io.frankmayer.naruto.Jutsu.MetaData.JutsuRank
import io.frankmayer.naruto.Jutsu.MetaData.KekkeiGenkai
import io.frankmayer.naruto.Naruto
import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SuirouNoJutsu : IJutsu {
    override val displayName = "Suirou no Jutsu"
    override val identifier = "suirou_no_jutsu"
    override val description = listOf(
        "This technique traps a target in a sphere of water.",
        "The water that the sphere is made from is noticeably heavy,",
        "which restricts the target's movements and can make breathing difficult.",
        "As such, those trapped within the water prison cannot ordinarily escape on their own."
    )
    override val classification = JutsuClassification.NINJUTSU
    override val rank = JutsuRank.C
    override val creator = null
    override val range = 5.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.SUITON

    override val onHit = { attacker: Player, target: LivingEntity ->
        if (target.height <= 2.0) {
            val sphereLocation = target.location.clone()
            val world = target.world

            val targetTeleportLocation = target.location.clone()
            targetTeleportLocation.x = targetTeleportLocation.blockX + 0.5
            targetTeleportLocation.y = targetTeleportLocation.blockY + 1.0
            targetTeleportLocation.z = targetTeleportLocation.blockZ + 0.5

            target.teleport(targetTeleportLocation, PlayerTeleportEvent.TeleportCause.PLUGIN)

            for (x in -1..1) {
                for (z in -1..1) {
                    for (y in 0..3) {
                        val location = sphereLocation.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
                        val block = world.getBlockAt(location)
                        if ((x == 0) && (z == 0) && (y == 1 || y == 2)) {
                            block.type = Material.WATER
                        } else {
                            block.type = Material.BARRIER
                        }
                    }
                }
            }

            target.setMetadata(
                Naruto.prisonKey!!.toString(), FixedMetadataValue(Naruto.instance!!, attacker.uniqueId.toString())
            )

            target.addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 40, 1, false, false, false))
        } else {
            attacker.sendMessage("Target ${target.name} is too big")
        }
    }

    override val onUse: Nothing? = null

    override val onDefend: Nothing? = null
}
