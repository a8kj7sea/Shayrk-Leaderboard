package dev.shayrk.leaderboards.command.subs;

import org.bukkit.entity.Player;

import dev.shayrk.leaderboards.entity.general.Leaderboard;
import dev.shayrk.leaderboards.entity.general.utils.SubCommand;
import dev.shayrk.leaderboards.manager.LeaderboardsManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import eu.decentsoftware.holograms.api.DHAPI;

public class Move extends SubCommand {

    public Move(LeaderboardsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getLabel() {
        return "move";
    }

    @Override
    public String getPermission() {
        return "leaderboards-plugin.admin";
    }

    @Override
    public String getUsage() {
        return "/lbs move [id]";
    }

    @Override
    public String getDescription() {
        return "to move leaderboard location by his id";
    }

    @Override
    public void handle(Player player, String[] args) {
        if (args.length != 2) {
            player.sendMessage(format("&cWrong usage please try again and type : &7/lbs move <id>"));
            player.sendMessage(format("&7For example : /lb move kills_lb"));
            return;
        }

        String id = args[1];

        LeaderboardsManager leaderboardsManager = getPlugin().getLeaderboardsManager();

        if (leaderboardsManager.getLeaderboardById(id) == null) {
            player.sendMessage(format("&cSorry you cannot move this leaderboard , not found leaderboard !"));
            return;
        }

        Leaderboard leaderboard = leaderboardsManager.getLeaderboardById(id);
        leaderboard.setLocation(player.getLocation());

        leaderboardsManager.updateLeaderboard(leaderboard, getConfiguration());
        
        DHAPI.moveHologram(id, player.getLocation());
        DHAPI.updateHologram(id);

        player.sendMessage(format("&aYou have been moved leaderboard successfully !"));
    }

}
