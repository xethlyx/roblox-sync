package com.xethlyx.robloxsync.bungee

import net.luckperms.api.model.user.User
import net.luckperms.api.node.NodeType
import net.luckperms.api.node.types.InheritanceNode
import net.luckperms.api.node.types.MetaNode
import java.util.*
import java.util.concurrent.CompletableFuture

object LuckPermsHelper {
    fun getRoblox(uuid: UUID): CompletableFuture<Int?> {
        return RobloxSync.luckPerms.userManager.loadUser(uuid)
            .thenApplyAsync() { user ->
                val userId = user.cachedData.metaData.getMetaValue("roblox") ?: return@thenApplyAsync null
                return@thenApplyAsync userId.toInt()
            }
    }

    fun resetRoblox(uuid: UUID, save: Boolean = false) {
        RobloxSync.luckPerms.userManager.loadUser(uuid)
            .thenAcceptAsync() { user ->
                resetRoblox(user, save)
            }
    }

    fun resetRoblox(user: User, save: Boolean = false) {
        user.data().clear(NodeType.META.predicate { mn -> mn.metaKey == "roblox" })

        if (save) {
            RobloxSync.luckPerms.userManager.saveUser(user)
        }
    }

    fun setRoblox(uuid: UUID, userId: Number) {
        RobloxSync.luckPerms.userManager.loadUser(uuid)
            .thenAcceptAsync() { user ->
                val metaNode = MetaNode.builder("roblox", userId.toString()).build()

                resetRoblox(user)
                user.data().add(metaNode)

                RobloxSync.luckPerms.userManager.saveUser(user)
            }
    }

    fun addGroup(uuid: UUID, save: Boolean = false) {
        RobloxSync.luckPerms.userManager.loadUser(uuid)
            .thenAcceptAsync() { user ->
                val groups = user.getNodes(NodeType.INHERITANCE)
                    .map(InheritanceNode::getGroupName)

                if (groups.contains("verified")) {
                    // Already done, we can skip everything
                    return@thenAcceptAsync
                }

                val inheritanceNode = InheritanceNode.builder("verified").build()
                user.data().add(inheritanceNode)

                if (save) {
                    RobloxSync.luckPerms.userManager.saveUser(user)
                }
            }
    }

    fun removeGroup(uuid: UUID, save: Boolean = false) {
        RobloxSync.luckPerms.userManager.loadUser(uuid)
            .thenAcceptAsync() { user ->
                user.data().clear(NodeType.INHERITANCE.predicate { mn -> mn.groupName == "verified" })

                if (save) {
                    RobloxSync.luckPerms.userManager.saveUser(user)
                }
            }
    }
}