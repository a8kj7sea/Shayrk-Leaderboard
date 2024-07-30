package dev.shayrk.leaderboards.command.subs;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import dev.shayrk.leaderboards.entity.general.Leaderboard;
import dev.shayrk.leaderboards.entity.general.utils.SubCommand;
import dev.shayrk.leaderboards.manager.LeaderboardsManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;

public class List extends SubCommand {

    public List(LeaderboardsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "leaderboards-plugin.admin";
    }

    @Override
    public String getUsage() {
        return "/lbs list";
    }

    @Override
    public String getDescription() {
        return "to sort list of leaderboards";
    }

    @Override
    public void handle(Player player, String[] args) {
        if (args.length != 1) {
            player.sendMessage(format("&cWrong usage please try again and type : &7/lbs list"));
            return;
        }

        LeaderboardsManager leaderboardsManager = getPlugin().getLeaderboardsManager();

        if (leaderboardsManager.getLeaderboardsManager().getCollection().isEmpty()) {
            player.sendMessage(format("&cSorry there are not any leaderboard !"));
            return;
        }

        player.sendMessage(format("&8&L                       &6Leaderboards List                       "));
        for (Leaderboard leaderboard : leaderboardsManager.getLeaderboardsManager().getCollection()) {

            sendMessage(player, leaderboard);
            player.sendMessage(format("&8        "));

        }
        player.sendMessage(format("&8&L                                                                 "));

    }

    private void sendMessage(Player player, Leaderboard leaderboard) {
        final Location location = leaderboard.getLocation();
        final String type = leaderboard.getType();
        final String id = leaderboard.getId();
        String locationString = location != null
                ? String.format("X: %.2f, Y: %.2f, Z: %.2f", location.getX(), location.getY(), location.getZ())
                : "Unknown";

        String message = String.format(
                "\n\n&6Leaderboard Information\n\n" +
                        "&eType: &f%s\n" +
                        "&eID: &f%s\n" +
                        "&eLocation: &f%s",
                type,
                id,
                locationString);

        player.sendMessage(format(message));
    }

}
