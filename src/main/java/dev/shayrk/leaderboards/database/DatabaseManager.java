package dev.shayrk.leaderboards.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import dev.shayrk.leaderboards.entity.player.IUserData;
import dev.shayrk.leaderboards.entity.player.TopUser;
import dev.shayrk.leaderboards.entity.player.User;
import dev.shayrk.leaderboards.enums.PlayerDataType;
import dev.shayrk.leaderboards.manager.UsersManager;
import dev.shayrk.leaderboards.program.plugin.LeaderboardsPlugin;
import lombok.Getter;

@Getter
public class DatabaseManager implements IUserData {

    private final LeaderboardsPlugin plugin;
    private final DatabaseConnector databaseConnector;
    private UsersManager usersManager;

    public DatabaseManager(LeaderboardsPlugin plugin) {
        this.plugin = plugin;
        this.databaseConnector = plugin.getDatabaseConnector();
        this.usersManager = plugin.getUsersManager();
    }

    synchronized public void createUsersTable() {
        databaseConnector.createTable("users", new StringBuilder()
                .append("kills int unsigned not null default 0,")
                .append("deaths int unsigned not null default 0,")
                .append("name varchar(20) not null primary key")
                .toString());
    }

    public boolean exists(User user) {
        String query = "SELECT name FROM users WHERE name=?";
        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getName());
            try (ResultSet resultSet = stmt.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void insertPlayerAsNew(Player player) {
        User user = new User(player.getName());
        user.setPlayerDataAmount(PlayerDataType.DEATHS, 0);
        user.setPlayerDataAmount(PlayerDataType.KILLS, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (exists(user)) {
                    this.cancel();
                    return;
                }

                String insertSQL = "INSERT INTO users (name, kills, deaths) VALUES (?, 0, 0)";
                try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(insertSQL)) {
                    stmt.setString(1, player.getName());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }

    public void insertPlayerAsNewByName(String name) {
        User user = new User(name);
        user.setPlayerDataAmount(PlayerDataType.DEATHS, 0);
        user.setPlayerDataAmount(PlayerDataType.KILLS, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (exists(user)) {
                    this.cancel();
                    return;
                }

                String insertSQL = "INSERT INTO users (name, kills, deaths) VALUES (?, 0, 0)";
                try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(insertSQL)) {
                    stmt.setString(1, name);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin.getPlugin());
    }

    @Override
    public int getData(User user, PlayerDataType targetType) {
        if (!exists(user))
            return -1;

        String type = targetType.name().toLowerCase();
        String query = "SELECT " + type + " FROM users WHERE name=?";
        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(query)) {
            stmt.setString(1, user.getName());
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(type);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setData(User user, PlayerDataType type, int amount) {
        String updateSQL = "UPDATE users SET " + type.name().toLowerCase() + "=? WHERE name=?";
        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(updateSQL)) {
            stmt.setInt(1, Math.abs(amount));
            stmt.setString(2, user.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void storeUser(User user) {
        for (PlayerDataType dataType : PlayerDataType.values()) {
            int currentDataAmount = user.getPlayerDataAmount(dataType);
            addData(user, dataType, currentDataAmount - getData(user, dataType));
        }
    }

    @Override
    public void addData(User user, PlayerDataType type, int amount) {
        String updateSQL = "UPDATE users SET " + type.name().toLowerCase() + " = " + type.name().toLowerCase()
                + " + ? WHERE name = ?";
        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(updateSQL)) {
            stmt.setInt(1, Math.abs(amount));
            stmt.setString(2, user.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeData(User user, PlayerDataType type, int amount) {
        String updateSQL = "UPDATE users SET " + type.name().toLowerCase() + " = " + type.name().toLowerCase()
                + " - ? WHERE name = ?";
        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(updateSQL)) {
            stmt.setInt(1, Math.abs(amount));
            stmt.setString(2, user.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User loadUser(Player player) {
        User user = new User(player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                user.setPlayerDataAmount(PlayerDataType.KILLS, getData(user, PlayerDataType.KILLS));
                user.setPlayerDataAmount(PlayerDataType.DEATHS, getData(user, PlayerDataType.DEATHS));
            }
        }.runTaskAsynchronously(plugin.getPlugin());
        return user;
    }

    public void saveUser(Player player) {
        User user = usersManager.getUserByName(player.getName());
        if (!exists(user)) {
            insertPlayerAsNew(player);
        } else {
            storeUser(user);
        }
    }

    public void saveUserByName(String name) {
        User user = usersManager.getUserByName(name);
        if (!exists(user)) {
            insertPlayerAsNewByName(name);
        } else {
            storeUser(user);
        }
    }

    public Set<TopUser> getTopUsers(int limit, PlayerDataType targetType) {
        Set<TopUser> topUsers = Sets.newHashSet();
        String type = targetType.name().toLowerCase();
        String query = "SELECT name, " + type + " FROM users ORDER BY ? DESC LIMIT ?";

        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(query)) {
            stmt.setString(1, type);
            stmt.setInt(2, limit);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    AtomicInteger pos = new AtomicInteger(0);
                    String name = resultSet.getString("name");
                    int amount = resultSet.getInt(type);

                    topUsers.add(new TopUser(amount, name, pos.getAndIncrement()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<TopUser> sortedList = new ArrayList<>(topUsers);
        sortedList.sort(Comparator.comparingInt(TopUser::getDataAmount).reversed());
        return new LinkedHashSet<>(sortedList);

    }

    public Map<String, User> getAllUsers() {
        Map<String, User> users = Maps.newHashMap();
        String query = "SELECT name, kills, deaths FROM users";

        try (PreparedStatement stmt = databaseConnector.getConnection().prepareStatement(query);
                ResultSet resultSet = stmt.executeQuery()) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int kills = resultSet.getInt("kills");
                int deaths = resultSet.getInt("deaths");

                User user = new User(name);
                user.setPlayerDataAmount(PlayerDataType.KILLS, kills);
                user.setPlayerDataAmount(PlayerDataType.DEATHS, deaths);

                users.put(name, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    // public void saveAll() {
    // UsersManager.getUsers().keySet().forEach(userName ->
    // saveUserByName(userName));
    // }

}
