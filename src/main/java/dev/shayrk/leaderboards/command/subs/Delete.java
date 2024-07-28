package dev.shayrk.leaderboards.command.subs;

import org.bukkit.entity.Player;

import dev.shayrk.leaderboards.entity.general.Leaderboard;
import dev.shayrk.leaderboards.entity.general.utils.SubCommand;
import dev.shayrk.leaderboards.manager.LeaderboardsManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import eu.decentsoftware.holograms.api.DHAPI;

public class Delete extends SubCommand {

    public Delete(LeaderboardsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "delete";
    }

    @Override
    public String getPermission() {
        return "leaderboards-plugin.admin";
    }

    @Override
    public String getUsage() {
        return "/lbs delete [id]";
    }

    @Override
    public String getDescription() {
        return "to delete leaderboard hologram by his id";
    }

    @Override
    public void handle(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(format("&cWrong usage please try again and type : &7/lbs delete [id]"));
            player.sendMessage(format("&7For example : /lb delete kills_lb"));
            return;
        }

        String id = args[1];

        LeaderboardsManager leaderboardsManager = getPlugin().getLeaderboardsManager();
        Leaderboard leaderboard = leaderboardsManager.getLeaderboardById(id);

        if (leaderboard == null) {
            player.sendMessage(format("&cSorry you cannot move this leaderboard , not found leaderboard !"));
            return;
        }

        leaderboardsManager.deleteLeaderboard(leaderboard, getConfiguration());
        DHAPI.getHologram(id).delete();
        player.sendMessage(format("&aYou have been deleted leaderboard successfully !"));
    }

}
