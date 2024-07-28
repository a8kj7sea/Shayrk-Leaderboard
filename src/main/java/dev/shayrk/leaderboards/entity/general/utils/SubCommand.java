package dev.shayrk.leaderboards.entity.general.utils;

import org.bukkit.entity.Player;

import dev.shayrk.leaderboards.config.Configuration;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@Getter
public abstract class SubCommand {

    public abstract String getLabel();

    public abstract String getPermission();

    public abstract String getUsage();

    public abstract String getDescription();

    public abstract void handle(Player player, String[] args);

    private final LeaderboardsPlugin plugin;

    public Configuration getConfiguration() {
        return this.plugin.getConfiguration();
    }

    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}