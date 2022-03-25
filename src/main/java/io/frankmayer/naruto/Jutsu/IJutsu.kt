package io.frankmayer.naruto.Jutsu

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent

interface IJutsu {
    val displayName: String
    val onHit: ((attacker: Player, target: LivingEntity) -> Unit)?
    val onUse: ((player: Player, action: Action) -> Unit)?
    val onDefend: ((defender: Player, event: EntityDamageByEntityEvent) -> Unit)?
}
