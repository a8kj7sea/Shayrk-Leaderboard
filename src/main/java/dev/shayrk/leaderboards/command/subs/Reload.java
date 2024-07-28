package dev.shayrk.leaderboards.command.subs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import dev.shayrk.leaderboards.entity.general.utils.SubCommand;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;

public class Reload extends SubCommand {

    public Reload(LeaderboardsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public String getPermission() {
        return "leaderboards-plugin.admin";
    }

    @Override
    public String getUsage() {
        return "/lbs reload";
    }

    @Override
    public String getDescription() {
        return "to reload configuration file and plugin";
    }

    @Override
    public void handle(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(format("&cWrong usage please try again and type : &7/lbs reload"));
            return;
        }

        this.getConfiguration().reload();

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().disablePlugin(getPlugin().getPlugin());
                Bukkit.getPluginManager().enablePlugin(getPlugin().getPlugin());
            }
        }.runTaskAsynchronously(this.getPlugin().getPlugin());

        player.sendMessage(format("&aYou have been reloaded leaderboards plugin successfully !"));
    }

}
