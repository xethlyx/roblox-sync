package com.xethlyx.robloxsync.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Locale;

import static java.util.Collections.emptyList;

public class CompatCommandHandler implements TabExecutor {
    private void addIf(String argument, String entry, ArrayList<String> list) {
        if (!(entry.toLowerCase(Locale.ROOT).startsWith(argument.toLowerCase(Locale.ROOT)))) return;
        list.add(entry);
    }

    // why doesn't this work in kotlin??
    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length > 2 || args.length == 0) return emptyList();

        ArrayList<String> matches = new ArrayList<>();

        if (args.length == 1) {
            addIf(args[0], "identify", matches);
            addIf(args[0], "verify", matches);
            addIf(args[0], "unverify", matches);
            addIf(args[0], "update", matches);
        } else {
            if (RobloxSync.Companion.getRedisBungee() == null) {
                for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                    if (!player.getName().startsWith(args[1])) continue;
                    matches.add(player.getName());
                }
            } else {
                for (AnyPlayer player : RedisHelper.INSTANCE.getAllPlayers()) {
                    if (!player.getUsername().startsWith(args[1])) continue;
                    matches.add(player.getUsername());
                }
            }
        }

        return matches;
    }
}
