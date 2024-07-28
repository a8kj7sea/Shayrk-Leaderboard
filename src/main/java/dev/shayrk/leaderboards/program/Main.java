package dev.shayrk.leaderboards.program;

import org.bukkit.plugin.java.JavaPlugin;

import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;

@Getter
public class Main extends JavaPlugin {

    private LeaderboardsPlugin plugin;

    @Override
    public void onEnable() {
        plugin = new LeaderboardsPlugin(getLogger(), this);
        plugin.onStartup();
    }

    @Override
    public void onDisable() {
        plugin.onShutdown();
        plugin = null;
    }

}
