package dev.shayrk.leaderboards.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import dev.shayrk.leaderboards.database.DatabaseManager;
import dev.shayrk.leaderboards.entity.player.User;
import dev.shayrk.leaderboards.manager.UsersManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlayerQuitListener implements Listener {

    private final LeaderboardsPlugin plugin;

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        DatabaseManager databaseManager = plugin.getDatabaseManager();

        Player player = event.getPlayer();

        UsersManager usersManager = plugin.getUsersManager();

        if (usersManager == null)
            return;

        User user = usersManager.getUserByName(player.getName());

        if (user == null)
            return;

        databaseManager.storeUser(user);
        user = null;
        plugin.getUsersManager().getUsers().remove(player.getName());

    }
}