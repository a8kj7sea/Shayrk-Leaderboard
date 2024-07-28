package dev.shayrk.leaderboards.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import dev.shayrk.leaderboards.entity.player.User;
import dev.shayrk.leaderboards.enums.PlayerDataType;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PlayerDeathListener implements Listener {

    private final LeaderboardsPlugin plugin;

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = event.getEntity().getKiller();
        if (killer == null) {
            return;
        }
        if (player == null) {
            return;
        }
        if (player == killer) {
            return;
        }

        User killerProfile = plugin.getUsersManager().getUserByName(killer.getName());
        User playerProfile = plugin.getUsersManager().getUserByName(player.getName());

        killerProfile.setPlayerDataAmount(PlayerDataType.KILLS, killerProfile.getKills() + 1);
        playerProfile.setPlayerDataAmount(PlayerDataType.DEATHS, playerProfile.getDeaths() + 1);
    }
}
