package dev.shayrk.leaderboards.command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import dev.shayrk.leaderboards.command.subs.Create;
import dev.shayrk.leaderboards.command.subs.Delete;
import dev.shayrk.leaderboards.command.subs.Move;
import dev.shayrk.leaderboards.command.subs.Reload;
import dev.shayrk.leaderboards.entity.general.utils.SubCommand;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;

@Getter
public class LeaderboardCommand implements CommandExecutor {

    private List<SubCommand> subs = Lists.newArrayList();

    private final LeaderboardsPlugin leaderboardsPlugin;

    public LeaderboardCommand(LeaderboardsPlugin leaderboardsPlugin) {
        this.leaderboardsPlugin = leaderboardsPlugin;
        this.subs.add(new Create(leaderboardsPlugin));
        this.subs.add(new Delete(leaderboardsPlugin));
        this.subs.add(new dev.shayrk.leaderboards.command.subs.List(leaderboardsPlugin));
        this.subs.add(new Move(leaderboardsPlugin));
        this.subs.add(new Reload(leaderboardsPlugin));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(format("&cThis command only for player usage !"));
            return false;
        }
        Player player = (Player) commandSender;

        if (!player.hasPermission("leaderboards-plugin.admin")) {
            player.sendMessage(format("&cYou don't have enough permission to execute this command !"));
            return false;
        }

        if (args.length == 0) {
            sendHelpMessage(args, player);
            return true;
        }

        for (SubCommand subCommand : subs) {
            if (args[0].equalsIgnoreCase(subCommand.getLabel())) {
                if (subCommand.getPermission() != null) {
                    if (!player.hasPermission(subCommand.getPermission())) {
                        return true;
                    }
                }
                subCommand.handle(player, args);
                return true;
            }

        }
        return true;
    }

    public String format(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private void sendHelpMessage(String[] args, Player player) {
        if (args.length == 0) {
            player.sendMessage(
                    format("&8&m                                                                         "));
            player.sendMessage(format("          "));
            for (SubCommand subCommand : subs) {
                player.sendMessage(format("&6" + subCommand.getUsage() + format(" &8&l\u00bb &7")
                        + subCommand.getDescription()));
            }
            player.sendMessage(format("          "));
            player.sendMessage(
                    format("&8&m                                                                         "));
            return;
        }
    }

}
