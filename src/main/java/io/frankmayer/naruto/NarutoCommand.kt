package io.frankmayer.naruto

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

class NarutoCommand(private val itemFactory: ItemFactory) : CommandExecutor, TabCompleter {
    private val jutsuList = EJutsu.values().map { it.jutsu }
    private val jutsuIdList = jutsuList.map { it.identifier }.toMutableList()
    private val defaultIdemCounts = mutableListOf("1", "2", "4", "8", "16", "32", "64")

    init {
        jutsuIdList.add("hiraishin_arrow")
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        sender.sendMessage(command.name + " " + args.joinToString(" "))
        if (!sender.isOp || !command.name.equals("naruto", true)) {
            sender.sendMessage("You're not a Ninja!")
            return false
        }

        val player = sender as Player
        val inventory: PlayerInventory = player.inventory

        when (args.size) {
            2 -> {
                when (args[0]) {
                    "give" -> {
                        val jutsu = jutsuList.firstOrNull { it.identifier.equals(args[1], true) }
                        if (jutsu != null) {
                            val item = itemFactory.createJutsu(jutsu)
                            inventory.addItem(item)
                            return true
                        } else {
                            when (args[1]) {
                                "hiraishin_arrow" -> {
                                    inventory.addItem(itemFactory.createHiraishinArrow())
                                    return true
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                when (args[0]) {
                    "give" -> {
                        when (args[1]) {
                            "hiraishin_arrow" -> {
                                return try {
                                    val count = args[2].toInt()
                                    val stack = itemFactory.createHiraishinArrow()
                                    stack.amount = count
                                    inventory.addItem(stack)
                                    true
                                } catch (e: NumberFormatException) {
                                    sender.sendMessage("Invalid count: ${args[2]}")
                                    false
                                }
                            }
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
            2 -> when (args[0]) {
                "give" -> {
                    return jutsuIdList
                }
            }
            3 -> {
                when (args[0]) {
                    "give" -> {
                        when (args[1]) {
                            "hiraishin_arrow" -> {
                                return defaultIdemCounts
                            }
                        }
                    }
                }
            }
        }

        return null
    }
}
