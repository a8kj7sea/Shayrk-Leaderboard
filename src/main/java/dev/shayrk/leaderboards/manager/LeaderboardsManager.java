package dev.shayrk.leaderboards.manager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.collect.Lists;

import dev.shayrk.leaderboards.config.Configuration;
import dev.shayrk.leaderboards.entity.general.Leaderboard;
import dev.shayrk.leaderboards.utils.SimpleCollectionManager;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class LeaderboardsManager {

    private static final List<Leaderboard> leaderboards = Lists.newArrayList();
    private final SimpleCollectionManager<Leaderboard> leaderboardsManager = new SimpleCollectionManager<>(
            leaderboards);

    public boolean canCreateLeaderboard(Leaderboard leaderboard) {
        return this.leaderboardsManager.getCollection().stream().filter(

                lb ->

                lb.getType().equalsIgnoreCase(leaderboard.getType())
                        || lb.getId().equalsIgnoreCase(leaderboard.getId())

        ).findAny().isPresent();
    }

    public Leaderboard getLeaderboardById(String id) {
        return this.leaderboardsManager.getCollection().stream().filter(

                lb ->

                lb.getId().equalsIgnoreCase(id)

        ).findAny().orElse(null);
    }

    public Leaderboard getLeaderboardByType(String type) {
        return this.leaderboardsManager.getCollection().stream().filter(

                lb ->

                lb.getType().equalsIgnoreCase(type)

        ).findAny().orElse(null);
    }

    public void deleteLeaderboard(Leaderboard leaderboard, Configuration configuration) {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) configuration.getConfigurationFile();
        List<String> leaderboardsIdList = yamlConfiguration.getStringList("leaderboards");

        String id = leaderboard.getId();

        if (leaderboardsIdList.contains(id)) {
            leaderboardsIdList.remove(id);
            yamlConfiguration.set("leaderboards", leaderboardsIdList);
        }

        if (yamlConfiguration.contains("leaderboards-data." + id)) {
            yamlConfiguration.set("leaderboards-data." + id, null);
        }

        leaderboardsManager.getCollection().remove(getLeaderboardById(id));
        configuration.save();
        saveAll(configuration);
        configuration.reload();
    }

    public void saveLeaderboard(Leaderboard leaderboard, @NonNull Configuration configuration) {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) configuration.getConfigurationFile();
        List<String> leaderboardsIdList = yamlConfiguration.getStringList("leaderboards");

        if (!canCreateLeaderboard(leaderboard))
            return;

        leaderboardsIdList.add(leaderboard.getId());
        yamlConfiguration.set("leaderboards", leaderboardsIdList);

        Location leaderboardLocation = leaderboard.getLocation();

        String id = leaderboard.getId();
        String type = leaderboard.getType();
        String header = leaderboard.getHeader();
        String footer = leaderboard.getFooter();

        ConfigurationSection idSection = yamlConfiguration.createSection("leaderboards-data." + id);

        idSection.set("type", type);
        idSection.set("header", header);
        idSection.set("footer", footer);

        ConfigurationSection locationSection = idSection.createSection("location");

        locationSection.set("world", leaderboardLocation.getWorld().getName());
        locationSection.set("x", leaderboardLocation.getX());
        locationSection.set("y", leaderboardLocation.getY());
        locationSection.set("z", leaderboardLocation.getZ());

        configuration.save();

    }

    public void updateLeaderboard(Leaderboard leaderboard, @NonNull Configuration configuration) {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) configuration.getConfigurationFile();

        Location leaderboardLocation = leaderboard.getLocation();

        String id = leaderboard.getId();
        String type = leaderboard.getType();
        String header = leaderboard.getHeader();
        String footer = leaderboard.getFooter();

        ConfigurationSection idSection = yamlConfiguration.getConfigurationSection("leaderboards-data." + id);

        idSection.set("type", type);
        idSection.set("header", header);
        idSection.set("footer", footer);

        ConfigurationSection locationSection = idSection.getConfigurationSection("location");

        locationSection.set("world", leaderboardLocation.getWorld().getName());
        locationSection.set("x", leaderboardLocation.getX());
        locationSection.set("y", leaderboardLocation.getY());
        locationSection.set("z", leaderboardLocation.getZ());

        configuration.save();

    }

    public void loadLeaderboards(@NonNull Configuration configuration) {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) configuration.getConfigurationFile();
        List<String> leaderboardsIdList = yamlConfiguration.getStringList("leaderboards");

        this.getLeaderboardsManager().getCollection().clear();
        ConfigurationSection leaderboardsData = yamlConfiguration.getConfigurationSection("leaderboards-data");
        for (String id : leaderboardsIdList) {
            ConfigurationSection idSection = leaderboardsData.getConfigurationSection(id);
            String type = idSection.getString("type");
            String header = idSection.getString("header");
            String footer = idSection.getString("footer");

            Location location = loadLocation(idSection.getConfigurationSection("location"));

            this.getLeaderboardsManager().getCollection().add(Leaderboard.of(id, type, header, footer, location));
        }

    }

    public void saveAll(@NonNull Configuration configuration) {
        YamlConfiguration yamlConfiguration = (YamlConfiguration) configuration.getConfigurationFile();
        List<String> leaderboardsIdList = yamlConfiguration.getStringList("leaderboards");

        yamlConfiguration.set("leaderboards", leaderboardsIdList);

        for (String id : leaderboardsIdList) {

            Leaderboard leaderboard = this.getLeaderboardById(id);
            Location leaderboardLocation = leaderboard.getLocation();

            String type = leaderboard.getType();
            String header = leaderboard.getHeader();
            String footer = leaderboard.getFooter();

            ConfigurationSection idSection = yamlConfiguration.createSection("leaderboards-data." + id);

            idSection.set("type", type);
            idSection.set("header", header);
            idSection.set("footer", footer);

            ConfigurationSection locationSection = idSection.createSection("location");

            locationSection.set("world", leaderboardLocation.getWorld().getName());
            locationSection.set("x", leaderboardLocation.getX());
            locationSection.set("y", leaderboardLocation.getY());
            locationSection.set("z", leaderboardLocation.getZ());

        }
        configuration.save();

    }

    private Location loadLocation(ConfigurationSection configurationSection) {
        String world = configurationSection.getString("world");
        int x = configurationSection.getInt("x");
        int y = configurationSection.getInt("y");
        int z = configurationSection.getInt("z");
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

}
