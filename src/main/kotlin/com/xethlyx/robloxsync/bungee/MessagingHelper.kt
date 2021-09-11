package com.xethlyx.robloxsync.bungee

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.connection.ProxiedPlayer

object MessagingHelper {
    fun updateUsername(player: ProxiedPlayer, username: String?) {
        val out = ByteStreams.newDataOutput()
        out.writeUTF(username ?: "off")

        player.server.sendData("robloxsync:nickname", out.toByteArray())
    }
}