package com.xethlyx.robloxsync.bungee

import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.md_5.bungee.api.plugin.Plugin

class RobloxSync: Plugin() {
    companion object {
        var instance: RobloxSync? = null
            private set

        var luckPerms: LuckPerms? = null
            private set
    }

    override fun onEnable() {
        super.onEnable()

        proxy.registerChannel("robloxsync:nickname")

        instance = this
        luckPerms = LuckPermsProvider.get()

        val commandHandler = CommandHandler()
        proxy.pluginManager.registerCommand(this, commandHandler)

    }

    override fun onDisable() {
        super.onDisable()


    }
}