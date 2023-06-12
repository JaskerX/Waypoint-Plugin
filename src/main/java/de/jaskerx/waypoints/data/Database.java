package de.jaskerx.waypoints.data;

import de.jaskerx.waypoints.WaypointPlugin;

import java.io.File;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class Database {

    private final WaypointPlugin plugin;
    private Connection connection;

    public Database(WaypointPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        File dir = this.plugin.getDataFolder();
        if(!dir.exists()) dir.mkdir();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + new File(dir, "Waypoints.db").getAbsolutePath());
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTableIfNotExists(String sql) {
        try(PreparedStatement preparedStatement = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + sql)) {
            this.executeUpdateSync(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int executeUpdateSync(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public CompletableFuture<Integer> executeUpdateAsync(PreparedStatement preparedStatement) {
        return CompletableFuture.supplyAsync(() -> this.executeUpdateSync(preparedStatement));
    }

    public ResultSet executeQuerySync(PreparedStatement preparedStatement) {
        try {
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CompletableFuture<ResultSet> executeQueryAsync(PreparedStatement preparedStatement) {
        return CompletableFuture.supplyAsync(() -> this.executeQuerySync(preparedStatement));
    }

    public Connection getConnection() {
        return connection;
    }

}
