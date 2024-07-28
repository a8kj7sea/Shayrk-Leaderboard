package dev.shayrk.leaderboards.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DatabaseConnector {

    private final DatabaseCredentials databaseCredentials;
    private Connection connection;

    public void openConnection() {
        String jdbcUrl = "jdbc:mysql://" + databaseCredentials.getHost() + ":" + "3306" + "/"
                + databaseCredentials.getDatabase() + "?characterEncoding=latin1";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, databaseCredentials.getUserName(),
                    databaseCredentials.getPassword());
            System.out.println("[leaderboards] Connected to mysql successfully !");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public ResultSet query(String sql) {
        if (!isConnected()) {
            return null;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet getResultSet(String sqlQuery) {
        if (!isConnected()) {
            return null;
        }

        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(sqlQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public int update(String sql) {
        if (!isConnected()) {
            return -1;
        }

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void createTable(String tableLabel, String tableStructure) {
        if (!isConnected()) {
            return;
        }

        String createTableQuery = "create table if not exists " + tableLabel + " (" + tableStructure + ");";

        try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
