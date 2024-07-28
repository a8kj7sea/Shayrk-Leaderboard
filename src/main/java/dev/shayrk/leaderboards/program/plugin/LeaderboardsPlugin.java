package dev.shayrk.leaderboards.program.plugin;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.shayrk.leaderboards.command.LeaderboardCommand;
import dev.shayrk.leaderboards.config.Configuration;
import dev.shayrk.leaderboards.database.DatabaseConnector;
import dev.shayrk.leaderboards.database.DatabaseCredentials;
import dev.shayrk.leaderboards.database.DatabaseManager;
import dev.shayrk.leaderboards.listeners.PlayerDeathListener;
import dev.shayrk.leaderboards.listeners.PlayerJoinListener;
import dev.shayrk.leaderboards.listeners.PlayerQuitListener;
import dev.shayrk.leaderboards.manager.LeaderboardsManager;
import dev.shayrk.leaderboards.manager.UsersManager;
import dev.shayrk.leaderboards.tasks.UpdateLeaderboardsTask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class LeaderboardsPlugin {

    private final Logger logger;
    private final JavaPlugin plugin;

    private Configuration configuration;
    private DatabaseConnector databaseConnector;
    private UsersManager usersManager;
    private LeaderboardsManager leaderboardsManager;

    private DatabaseManager databaseManager;

    private UpdateLeaderboardsTask leaderboardsTask;

    public void onStartup() {
        // load stuff like players and leaderboards

        if (!canStart()) {
            Bukkit.getPluginManager().disablePlugin(this.getPlugin());
            this.logger.severe("plugin cannot start , please make sure u have been installed DecentHolograms plugin");
            return;
        }

        logger.info("registering stuff");

        configuration = new Configuration(plugin, "config.yml", true);
        databaseConnector = new DatabaseConnector(getDatabaseCredentials(configuration));
        databaseConnector.openConnection();
        databaseManager = new DatabaseManager(this);
        databaseManager.createUsersTable();
        usersManager = new UsersManager();
        leaderboardsManager = new LeaderboardsManager();
        leaderboardsManager.loadLeaderboards(configuration);
        this.getPlugin().getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), plugin);

        int updateAfter = configuration.getConfigurationFile().getInt("leaderboards-settings.update-interval");
        leaderboardsTask = new UpdateLeaderboardsTask(this);

        if (!leaderboardsManager.getLeaderboardsManager().getCollection().isEmpty())
            leaderboardsTask.runTaskTimer(plugin, 5 * 20, updateAfter * (20 * 60));

        logger.info("plugin statred successfully !");

    }

    public void onShutdown() {
        this.leaderboardsManager.saveAll(configuration);
        // destory();
    }

    private void destory() {
        leaderboardsTask.cancel();
        leaderboardsTask = null;
        configuration = null;
        databaseConnector.closeConnection();
        databaseConnector = null;
        databaseManager = null;
        usersManager.getUsers().clear();
        usersManager = null;
        leaderboardsManager.getLeaderboardsManager().getCollection().clear();
        leaderboardsManager = null;
        System.gc();
    }

    private boolean canStart() {
        return Bukkit.getPluginManager().getPlugin("DecentHolograms") != null;
    }

    private DatabaseCredentials getDatabaseCredentials(Configuration configuration) {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) configuration.getConfigurationFile();

        ConfigurationSection mysqlSection = yamlConfiguration.getConfigurationSection("database.mysql");
        String database = mysqlSection.getString("database");
        String username = mysqlSection.getString("username");
        String host = mysqlSection.getString("host");
        String password = mysqlSection.getString("password");
        int port = mysqlSection.getInt("port");

        return DatabaseCredentials.builder()
                .database(database)
                .host(host)
                .password(password)
                .userName(username)
                .port(port)
                .build();
    }

}
