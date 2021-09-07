package com.xethlyx.robloxsync.bungee

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command
import net.md_5.bungee.api.plugin.TabExecutor


class CommandHandler: Command("roblox"), TabExecutor {
    private val compatCommandHandler = CompatCommandHandler()
    private val prefix = TextComponent(ChatColor.DARK_GRAY.toString() + "[" + ChatColor.RED + "Sync" + ChatColor.DARK_GRAY + "] ")

    private fun invalidUsage(message: String?): Array<out BaseComponent> {
        val componentBuilder = ComponentBuilder()

        componentBuilder.append(prefix)
        componentBuilder.append(ChatColor.GRAY.toString() + (message ?: "Invalid usage.") + "\n")
        componentBuilder.append(ChatColor.DARK_GRAY.toString() + " - " + ChatColor.GRAY + "/roblox identify <player>\n")
        componentBuilder.append(ChatColor.DARK_GRAY.toString() + " - " + ChatColor.GRAY + "/roblox update <player>\n")
        componentBuilder.append(ChatColor.DARK_GRAY.toString() + " - " + ChatColor.GRAY + "/roblox verify <player> <username>\n")
        componentBuilder.append(ChatColor.DARK_GRAY.toString() + " - " + ChatColor.GRAY + "/roblox unverify <player>")

        return componentBuilder.create()
    }

    private fun internalError(error: String): Array<out BaseComponent> {
        val componentBuilder = ComponentBuilder()

        componentBuilder.append(prefix)
        componentBuilder.append(ChatColor.GRAY.toString() + "Error occurred: " + error)

        return componentBuilder.create()
    }

    private fun getPlayer(search: String): ProxiedPlayer? {
        for (player in ProxyServer.getInstance().players) {
            if (player.name == search) return player
        }

        return null
    }

    private fun formatRobloxData(data: RobloxData): BaseComponent {
        val component = TextComponent(ChatColor.AQUA.toString() + "[@" + data.username + "]")

        val componentBuilder = ComponentBuilder()
        componentBuilder.append(ChatColor.AQUA.toString() + "[@" + data.username + "]" + "\n")
        componentBuilder.append(ChatColor.GRAY.toString() + "Display Name" + ChatColor.DARK_GRAY.toString() + ": " + ChatColor.GRAY.toString() + data.displayName + "\n")
        componentBuilder.append(ChatColor.GRAY.toString() + "Join Date" + ChatColor.DARK_GRAY.toString() + ": " + ChatColor.GRAY.toString() + data.created + "\n")
        componentBuilder.append(ChatColor.GRAY.toString() + "User ID" + ChatColor.DARK_GRAY.toString() + ": " + ChatColor.GRAY.toString() + data.id + "\n")
        componentBuilder.append(ChatColor.GRAY.toString() + "Is Banned" + ChatColor.DARK_GRAY.toString() + ": " + ChatColor.GRAY.toString() + data.isBanned)

        component.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create())
        component.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.roblox.com/users/" + data.id + "/profile")

        return component
    }

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.isEmpty()) {
            sender.sendMessage(*invalidUsage(null))
            return
        }

        when(args[0]) {
            "identify" -> {
                if (!sender.hasPermission("xethlyx.roblox.identify")) {
                    sender.sendMessage(*internalError("No permission"))
                    return
                }

                if (args.size != 2) {
                    sender.sendMessage(*invalidUsage(null))
                    return
                }

                val player = getPlayer(args[1])
                if (player == null) {
                    sender.sendMessage(*invalidUsage("Player not found."))
                    return
                }

                val robloxId = LuckPermsHelper.getRoblox(player)
                if (robloxId == null) {
                    sender.sendMessage(*internalError("Player is not verified"))
                    return
                }

                val robloxData = RobloxData.from(robloxId)
                if (robloxData == null) {
                    sender.sendMessage(*internalError("Player data could not be fetched"))
                    return
                }

                val componentBuilder = ComponentBuilder()
                componentBuilder.append(prefix)
                componentBuilder.append(ChatColor.GRAY.toString() + "Found user: ")
                componentBuilder.append(formatRobloxData(robloxData))

                sender.sendMessage(*componentBuilder.create())
            }
            "update" -> {

                if (args.size > 2) {
                    sender.sendMessage(*invalidUsage(null))
                    return
                }

                if (sender !is ProxiedPlayer && args.size == 1) {
                    sender.sendMessage(*invalidUsage("Player not found."))
                    return
                }

                val player: ProxiedPlayer = if (args.size == 1) {
                    if (!sender.hasPermission("xethlyx.roblox.update")) {
                        sender.sendMessage(*internalError("No permission"))
                        return
                    }

                    sender as ProxiedPlayer
                } else {
                    if (!sender.hasPermission("xethlyx.roblox.admin")) {
                        sender.sendMessage(*internalError("No permission"))
                        return
                    }

                    val player = getPlayer(args[1])
                    if (player == null) {
                        sender.sendMessage(*invalidUsage("Player not found."))
                        return
                    }

                    player
                }

                val robloxId = LuckPermsHelper.getRoblox(player)
                if (robloxId == null) {
                    sender.sendMessage(*internalError("Player is not verified"))
                    return
                }

                val robloxData = RobloxData.from(robloxId)
                if (robloxData == null) {
                    sender.sendMessage(*internalError("Player data could not be fetched"))
                    return
                }

                MessagingHelper.updateUsername(player, robloxData.username)

                val componentBuilder = ComponentBuilder()
                componentBuilder.append(prefix)
                componentBuilder.append(ChatColor.GRAY.toString() + "Updated user " + player.displayName + " ")
                componentBuilder.append(formatRobloxData(robloxData))

                sender.sendMessage(*componentBuilder.create())
            }
            "verify" -> {
                if (!sender.hasPermission("xethlyx.roblox.admin")) {
                    sender.sendMessage(*internalError("No permission"))
                    return
                }

                if (args.size != 3) {
                    sender.sendMessage(*invalidUsage(null))
                    return
                }

                val player = getPlayer(args[1])
                if (player == null) {
                    sender.sendMessage(*invalidUsage("Player not found."))
                    return
                }

                val robloxData = RobloxData.from(args[2])
                if (robloxData == null) {
                    sender.sendMessage(*internalError("Player data could not be fetched. Invalid username?"))
                    return
                }

                LuckPermsHelper.setRoblox(player, robloxData.id)
                MessagingHelper.updateUsername(player, robloxData.username)

                val componentBuilder = ComponentBuilder()
                componentBuilder.append(prefix)
                componentBuilder.append(ChatColor.GRAY.toString() + "Verified user " + player.displayName + " ")
                componentBuilder.append(formatRobloxData(robloxData))

                sender.sendMessage(*componentBuilder.create())
            }
            "unverify" -> {
                if (!sender.hasPermission("xethlyx.roblox.admin")) {
                    sender.sendMessage(*internalError("No permission"))
                    return
                }

                if (args.size != 2) {
                    sender.sendMessage(*invalidUsage(null))
                    return
                }

                val player = getPlayer(args[1])
                if (player == null) {
                    sender.sendMessage(*invalidUsage("Player not found."))
                    return
                }

                val success = LuckPermsHelper.resetRoblox(player)
                if (!success) {
                    sender.sendMessage(*internalError("Player internal data could not be fetched."))
                    return
                }

                val componentBuilder = ComponentBuilder()
                componentBuilder.append(prefix)
                componentBuilder.append(ChatColor.GRAY.toString() + "Unverified user " + player.displayName)

                sender.sendMessage(*componentBuilder.create())
            }
            else -> {
                sender.sendMessage(*invalidUsage(null))
                return
            }
        }
    }

    override fun onTabComplete(sender: CommandSender, args: Array<out String>): MutableIterable<String> {
        // for some reason bungeecord doesn't like if i do this in kotlin
        return compatCommandHandler.onTabComplete(sender, args)
    }
}