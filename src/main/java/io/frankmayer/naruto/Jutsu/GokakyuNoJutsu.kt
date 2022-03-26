package io.frankmayer.naruto.Jutsu

import io.frankmayer.naruto.Jutsu.MetaData.JutsuClassification
import io.frankmayer.naruto.Jutsu.MetaData.JutsuElement
import io.frankmayer.naruto.Jutsu.MetaData.JutsuRank
import io.frankmayer.naruto.Jutsu.MetaData.KekkeiGenkai
import org.bukkit.entity.Player
import org.bukkit.event.block.Action

class GokakyuNoJutsu : IJutsu {
    override val displayName = "Gōkakyū no Jutsu"
    override val identifier = "gokakyu_no_jutsu"
    override val description = listOf("Great Fireball.")
    override val classification = JutsuClassification.NINJUTSU
    override val rank = JutsuRank.C
    override val creator: Nothing? = null
    override val range = 8.0
    override val kekkeiGenkai = KekkeiGenkai.NONE
    override val element = JutsuElement.KATON

    override val onHit: Nothing? = null

    override val onUse = { player: Player, _: Action ->
        val world = player.world
        val targetLocation =
            player.getTargetEntity(range.toInt(), false)?.location ?: player.getTargetBlock(range.toInt())?.location
            ?: player.location.clone().add(player.location.direction.clone().multiply(range))
        val fireball = world.spawn(targetLocation, org.bukkit.entity.Fireball::class.java)
        fireball.shooter = player
        fireball.velocity = player.location.direction.clone().multiply(2.0)
    }

    override val onDefend: Nothing? = null
}
