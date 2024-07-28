package dev.shayrk.leaderboards.command.subs;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.entity.Player;

import dev.shayrk.leaderboards.config.Configuration;
import dev.shayrk.leaderboards.database.DatabaseManager;
import dev.shayrk.leaderboards.entity.general.Leaderboard;
import dev.shayrk.leaderboards.entity.general.utils.SubCommand;
import dev.shayrk.leaderboards.entity.player.TopUser;
import dev.shayrk.leaderboards.enums.PlayerDataType;
import dev.shayrk.leaderboards.manager.LeaderboardsManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;

public class Create extends SubCommand {

    public Create(LeaderboardsPlugin leaderboardsPlugin) {
        super(leaderboardsPlugin);
    }

    @Override
    public String getLabel() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "leaderboards-plugin.admin";
    }

    @Override
    public String getUsage() {
        return "/lbs create [id] [type]";
    }

    @Override
    public String getDescription() {
        return "to create leaderboard hologram in custom location with type";
    }

    @Override
    public void handle(Player player, String[] args) {

        if (args.length != 3) {
            player.sendMessage(format("&cWrong usage please try again and type : &7/lbs create <id> <type>"));
            player.sendMessage(format("&7For example : /lb create kills_lb kills"));
            return;
        }

        String id = args[1];
        String type = args[2];

        if (!isValidPlayerDataType(type.toUpperCase())) {
            player.sendMessage(format(
                    "&cSorry you cannot make a leaderboard with this type, please choose one of these types &7(kills , deaths)"));
            return;

        }

        LeaderboardsManager leaderboardsManager = getPlugin().getLeaderboardsManager();

        if (leaderboardsManager.getLeaderboardById(id) != null
                || leaderboardsManager.getLeaderboardByType(type) != null) {
            player.sendMessage(format("&cSorry you cannot create another leaderboard with same id or type !"));
            return;
        }

        DatabaseManager databaseManager = getPlugin().getDatabaseManager();

        Configuration configuration = getConfiguration();

        if (configuration.getConfigurationFile().getStringList("leaderboards").contains(id)) {
            player.sendMessage(format("&cSorry you cannot create another leaderboard with same id or type !"));
            return;
        }

        int limit = configuration.getConfigurationFile().getInt("leaderboards-settings.limit");
        String header = format("&aTop " + limit + " leaderboard of " + type + " !");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = sdf.format(new Date());

        String footer = format("Thx for playing last update was at " + formattedDate);

        Leaderboard leaderboard = Leaderboard.of(id, PlayerDataType.valueOf(type.toUpperCase()), header, footer,
                player.getLocation());

        leaderboardsManager.getLeaderboardsManager().add(leaderboard);
        leaderboardsManager.saveLeaderboard(leaderboard, configuration);

        Hologram hologram = DHAPI.createHologram(id, player.getLocation());

        DHAPI.addHologramLine(hologram,
                header.replace("%type%", type).replace("%limit%", "" + limit));

        for (TopUser topUser : databaseManager.getTopUsers(limit, PlayerDataType.valueOf(type.toUpperCase()))) {
            String name = topUser.getName();
            int userpos = topUser.getPos();
            int amount = topUser.getDataAmount();

            String bodyFormat = configuration.getConfigurationFile().getString("leaderboards-settings.body-format")
                    .replace("%user%", name)
                    .replace("%count%", "" + userpos)
                    .replace("%data-amount%", "" + amount);

            DHAPI.addHologramLine(hologram, format(bodyFormat));
        }

        DHAPI.addHologramLine(hologram, footer.replace("time", formattedDate));
        hologram.save();
        player.sendMessage(format("&aYou have been created leaderboard successfully !"));
    }

    private boolean isValidPlayerDataType(String type) {
        try {
            PlayerDataType.valueOf(type.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
