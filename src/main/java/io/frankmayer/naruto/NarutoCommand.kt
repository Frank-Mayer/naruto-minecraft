package io.frankmayer.naruto

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

class NarutoCommand(private val itemFactory: ItemFactory) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(command.name+ " "+args.joinToString(" "))
        if (!sender.isOp || !command.name.equals("naruto", true)) {
            sender.sendMessage("You're not a Ninja!")
            return false
        }

        val player = sender as Player
        val inventory: PlayerInventory = player.inventory

        if (args.size == 2) {
            when (args[0]) {
                "give" -> {
                    when (args[1]) {
                        "rasengan" -> {
                            inventory.addItem(itemFactory.createRasengan())
                            return true
                        }
                        "shinratensei" -> {
                            inventory.addItem(itemFactory.createShinraTensei())
                            return true
                        }
                        "amenotejikara" -> {
                            inventory.addItem(itemFactory.createAmenotejikara())
                            return true
                        }
                        "kirin" -> {
                            inventory.addItem(itemFactory.createKirin())
                            return true
                        }
                    }
                }
            }
        }

        return false
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<out String>
    ): MutableList<String>? {
        if (!sender.isOp || !command.name.equals("naruto", true)) {
            return null
        }

        when (args.count()) {
            1 -> return mutableListOf("give")
            2 ->  when (args[0]) {
                "give" -> {
                    return mutableListOf("rasengan", "shinratensei", "amenotejikara", "kirin")
                }
            }
        }

        return null
    }
}
