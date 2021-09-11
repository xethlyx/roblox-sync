package com.xethlyx.robloxsync.bungee

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import java.util.*

class AnyPlayer {
    var uuid: UUID
    var username: String

    companion object {
        fun didSelfVerify(player: ProxiedPlayer) {
            LuckPermsHelper.getRoblox(player.uniqueId).thenAccept { userId ->
                if (userId == null) return@thenAccept
                val robloxData = RobloxData.from(userId) ?: return@thenAccept

                MessagingHelper.updateUsername(player, robloxData.username)

                val updatedComponentBuilder = ComponentBuilder()
                updatedComponentBuilder.append(CommandHandler.prefix)
                updatedComponentBuilder.append(ChatColor.GRAY.toString() + "You are now verified as ")
                updatedComponentBuilder.append(CommandHandler.formatRobloxData(robloxData))
                player.sendMessage(*updatedComponentBuilder.create())
            }
        }

        fun didSelfUnverify(player: ProxiedPlayer) {
            val updatedComponentBuilder = ComponentBuilder()
            updatedComponentBuilder.append(CommandHandler.prefix)
            updatedComponentBuilder.append(ChatColor.GRAY.toString() + "You are now unverified.")
            player.sendMessage(*updatedComponentBuilder.create())
        }

        fun didVerifyPlayer(sender: CommandSender, updatedPlayer: AnyPlayer, robloxData: RobloxData) {
            val foundPlayer = updatedPlayer.getProxiedPlayer()

            if (foundPlayer != null) {
                didSelfVerify(foundPlayer)
            } else {
                assert(RobloxSync.redisBungee != null) { "RedisBungee not initialized" }
                RedisHelper.didVerify(updatedPlayer.uuid)
            }

            if (sender is ProxiedPlayer && sender == foundPlayer) return

            val componentBuilder = ComponentBuilder()
            componentBuilder.append(CommandHandler.prefix)
            componentBuilder.append(ChatColor.GRAY.toString() + "Updated user " + updatedPlayer.username + " ")
            componentBuilder.append(CommandHandler.formatRobloxData(robloxData))
            sender.sendMessage(*componentBuilder.create())
        }

        fun didUnverifyPlayer(sender: CommandSender, updatedPlayer: AnyPlayer) {
            val foundPlayer = updatedPlayer.getProxiedPlayer()

            if (foundPlayer != null) {
                didSelfVerify(foundPlayer)
            } else {
                assert(RobloxSync.redisBungee != null) { "RedisBungee not initialized" }
                RedisHelper.didUnverify(updatedPlayer.uuid)
            }

            if (sender is ProxiedPlayer && sender == foundPlayer) return

            val componentBuilder = ComponentBuilder()
            componentBuilder.append(CommandHandler.prefix)
            componentBuilder.append(ChatColor.GRAY.toString() + "Unverified user " + updatedPlayer.username)
            sender.sendMessage(*componentBuilder.create())
        }
    }

    constructor(player: ProxiedPlayer) {
        uuid = player.uniqueId
        username = player.name
    }

    constructor(uniqueId: UUID) {
        assert(RobloxSync.redisBungee != null) { "RedisBungee not initialized" }

        uuid = uniqueId
        username = RobloxSync.redisBungee!!.getNameFromUuid(uniqueId)
    }

    fun getProxiedPlayer(): ProxiedPlayer? {
        return ProxyServer.getInstance().players.find() { element ->
            return@find element.uniqueId == uuid
        }
    }
}