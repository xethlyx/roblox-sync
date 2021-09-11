package com.xethlyx.robloxsync.bungee

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import java.util.*

class RedisListener: Listener {
    @EventHandler
    fun pubSubMessageEvent(event: PubSubMessageEvent) {
        when (event.channel) {
            "robloxsync:didVerify" -> {
                val uuid = UUID.fromString(event.message)
                val anyPlayer = AnyPlayer(uuid)
                val proxiedPlayer = anyPlayer.getProxiedPlayer()

                if (proxiedPlayer != null) {
                    AnyPlayer.didSelfVerify(proxiedPlayer)
                }
            }
            "robloxsync:didUnverify" -> {
                val uuid = UUID.fromString(event.message)
                val anyPlayer = AnyPlayer(uuid)
                val proxiedPlayer = anyPlayer.getProxiedPlayer()

                if (proxiedPlayer != null) {
                    AnyPlayer.didSelfUnverify(proxiedPlayer)
                }
            }
        }
    }
}