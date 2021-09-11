package com.xethlyx.robloxsync.bungee

import com.imaginarycode.minecraft.redisbungee.RedisBungee
import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.md_5.bungee.api.plugin.Plugin

class RobloxSync: Plugin() {
    companion object {
        lateinit var instance: RobloxSync
            private set

        lateinit var luckPerms: LuckPerms
            private set

        var redisBungee: RedisBungeeAPI? = null
            private set
    }

    override fun onEnable() {
        super.onEnable()

        proxy.registerChannel("robloxsync:nickname")

        instance = this
        luckPerms = LuckPermsProvider.get()

        try {
            redisBungee = RedisBungee.getApi()
            if (redisBungee != null) {
                logger.info("RedisBungee integration configured!")
                RedisHelper.registerListener()
            }
        } catch (error: NoClassDefFoundError) {
            // Redis bungee is not loaded, ignore
        }

        val commandHandler = CommandHandler()
        proxy.pluginManager.registerCommand(this, commandHandler)

    }

    override fun onDisable() {
        super.onDisable()

        if (redisBungee != null) {
            RedisHelper.unregisterListener()
        }
    }
}