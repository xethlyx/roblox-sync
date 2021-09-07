package com.xethlyx.robloxsync.spigot

import com.google.common.io.ByteStreams
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.messaging.PluginMessageListener

class RobloxSync: JavaPlugin(), PluginMessageListener {
    override fun onEnable() {
        super.onEnable()

        this.server.messenger.registerIncomingPluginChannel(this, "robloxsync:nickname", this)
    }

    override fun onDisable() {
        super.onDisable()

        this.server.messenger.unregisterIncomingPluginChannel(this);
    }

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        val input = ByteStreams.newDataInput(message)
        val nickname = input.readUTF()
        val username = player.name

        logger.info("Received nickname update for $player (new value: $nickname)")
        server.dispatchCommand(server.consoleSender, "nickname $username $nickname")
    }
}