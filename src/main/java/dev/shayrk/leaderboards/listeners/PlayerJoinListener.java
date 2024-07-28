package dev.shayrk.leaderboards.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.shayrk.leaderboards.database.DatabaseManager;
import dev.shayrk.leaderboards.entity.player.User;
import dev.shayrk.leaderboards.enums.PlayerDataType;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlayerJoinListener implements Listener {

    private final LeaderboardsPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        DatabaseManager databaseManager = plugin.getDatabaseManager();

        Player player = event.getPlayer();

        User user = new User(player.getName());
        if (databaseManager.exists(user)) {

            user = databaseManager.loadUser(player);
            plugin.getUsersManager().addUser(player.getName(), user);
            return;
        } else {
            databaseManager.insertPlayerAsNew(player);
            user.setPlayerDataAmount(PlayerDataType.DEATHS, 0);
            user.setPlayerDataAmount(PlayerDataType.KILLS, 0);
            plugin.getUsersManager().addUser(player.getName(), user);
            return;
        }
    }
}
