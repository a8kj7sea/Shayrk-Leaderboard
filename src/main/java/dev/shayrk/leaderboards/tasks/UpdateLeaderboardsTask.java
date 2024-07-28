package dev.shayrk.leaderboards.tasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.scheduler.BukkitRunnable;

import dev.shayrk.leaderboards.config.Configuration;
import dev.shayrk.leaderboards.entity.general.Leaderboard;
import dev.shayrk.leaderboards.entity.player.TopUser;
import dev.shayrk.leaderboards.enums.PlayerDataType;
import dev.shayrk.leaderboards.manager.LeaderboardsManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;

@RequiredArgsConstructor
@Getter
public class UpdateLeaderboardsTask extends BukkitRunnable {

    private final LeaderboardsPlugin plugin;

    public Configuration getConfiguration() {
        return this.plugin.getConfiguration();
    }

    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @Override
    public void run() {
        LeaderboardsManager leaderboardsManager = plugin.getLeaderboardsManager();
        for (Leaderboard leaderboard : leaderboardsManager.getLeaderboardsManager().getCollection()) {

            String id = leaderboard.getId();
            String type = leaderboard.getType();

            Hologram hologram = DHAPI.getHologram(id);

            if (hologram == null) {
                hologram = DHAPI.createHologram(id, leaderboard.getLocation());
            } else {
                hologram.destroy();
            }

            Configuration configuration = getConfiguration();
            int limit = configuration.getConfigurationFile().getInt("leaderboards-settings.limit");
            //
            String header = format(
                    getConfiguration().getConfigurationFile().getString("leaderboards-data." + id + ".header"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String formattedDate = sdf.format(new Date());
            //
            String footer = format(
                    getConfiguration().getConfigurationFile().getString("leaderboards-data." + id + ".footer"));

            DHAPI.addHologramLine(hologram,
                    header
                            .replace("%type%", type)
                            .replace("%limit%", "" + limit)
                            .replace("time", formattedDate));

            String bodyFormat = "";
            AtomicInteger userPos = new AtomicInteger(1);

            for (TopUser topUser : plugin.getDatabaseManager().getTopUsers(limit,
                    PlayerDataType.valueOf(type.toUpperCase()))) {
                String name = topUser.getName();
                int amount = topUser.getDataAmount();

                bodyFormat = configuration.getConfigurationFile().getString("leaderboards-settings.body-format")
                        .replace("%user%", name)
                        .replace("%count%", "" + userPos.getAndIncrement())
                        .replace("%data-amount%", "" + amount);

                DHAPI.addHologramLine(hologram, format(bodyFormat));
            }

            DHAPI.addHologramLine(hologram, footer
                    .replace("%type%", type)
                    .replace("%limit%", "" + limit)
                    .replace("time", formattedDate));

            DHAPI.updateHologram(id);
            hologram.updateAll();
        }
    }

}
