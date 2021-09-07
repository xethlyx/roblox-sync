package com.xethlyx.robloxsync.bungee

import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.MetaNode
import net.md_5.bungee.api.connection.ProxiedPlayer

object LuckPermsHelper {
    fun getRoblox(player: ProxiedPlayer): Number? {
        val metaData = RobloxSync.luckPerms!!.getPlayerAdapter(ProxiedPlayer::class.java).getMetaData(player)
        val userId = metaData.getMetaValue("roblox") ?: return null
        return userId.toInt()
    }

    fun resetRoblox(player: ProxiedPlayer): Boolean {
        resetRoblox(RobloxSync.luckPerms!!.userManager.getUser(player.uniqueId) ?: return false)
        return true
    }

    fun resetRoblox(user: User) {
        user.data().clear(NodeType.META.predicate { mn -> mn.metaKey == "roblox" })
    }

    fun setRoblox(player: ProxiedPlayer, userId: Number): Boolean {
        val user = RobloxSync.luckPerms!!.userManager.getUser(player.uniqueId) ?: return false
        val metaNode = MetaNode.builder("roblox", userId.toString()).build()

        resetRoblox(user)
        user.data().add(metaNode)

        RobloxSync.luckPerms!!.userManager.saveUser(user)

        return true
    }
}