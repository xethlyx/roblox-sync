package com.xethlyx.robloxsync.bungee

import java.util.*
import java.util.concurrent.TimeUnit


object RedisHelper {
    private var redisListener: RedisListener? = null
    private var playersCache: Set<UUID>? = null

    fun registerListener() {
        RobloxSync.redisBungee!!.registerPubSubChannels("robloxsync:didVerify", "robloxsync:didUnverify")
        redisListener = RedisListener()
        RobloxSync.instance.proxy.pluginManager.registerListener(RobloxSync.instance, redisListener)
    }

    fun unregisterListener() {
        RobloxSync.redisBungee!!.unregisterPubSubChannels("robloxsync:didVerify", "robloxsync:didUnverify")
        RobloxSync.instance.proxy.pluginManager.unregisterListener(redisListener!!)
    }

    fun didVerify(uuid: UUID) {
        RobloxSync.redisBungee!!.sendChannelMessage("robloxsync:didVerify", uuid.toString())
    }

    fun didUnverify(uuid: UUID) {
        RobloxSync.redisBungee!!.sendChannelMessage("robloxsync:didUnverify", uuid.toString())
    }

    fun getAllPlayers(): ArrayList<AnyPlayer> {
        if (playersCache == null) {
            playersCache = RobloxSync.redisBungee!!.playersOnline
            RobloxSync.instance.proxy.scheduler.schedule(RobloxSync.instance,
                { playersCache = null }, 5, TimeUnit.SECONDS)
        }

        val players = ArrayList<AnyPlayer>()

        for (player in RobloxSync.redisBungee!!.playersOnline) {
            players.add(AnyPlayer(player))
        }

        return players
    }
}
