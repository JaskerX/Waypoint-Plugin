package de.jaskerx.waypoints.registry;

import de.jaskerx.waypoints.Visibility;
import de.jaskerx.waypoints.Waypoint;
import de.jaskerx.waypoints.WaypointPlugin;
import de.jaskerx.waypoints.data.Database;
import org.bukkit.Material;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class WaypointsRegistry {

    private final Map<Integer, Waypoint> publicWaypoints;
    private final Map<UUID, Map<Integer, Waypoint>> privateWaypoints;
    private final Database database;
    private final WaypointPlugin plugin;

    public WaypointsRegistry(Database database, WaypointPlugin plugin) {
        this.publicWaypoints = new HashMap<>();
        this.privateWaypoints = new HashMap<>();
        this.database = database;
        this.plugin = plugin;
    }

    public int registerSync(Waypoint waypoint, boolean setCreatedId) {
        switch (waypoint.getVisibility()) {
            case PUBLIC -> {
                try (PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO waypoints (name, visibility, creator_id, x, y, z, yaw, pitch, world, material) SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM waypoints WHERE (name = ? AND visibility = 'PUBLIC'))")) {
                    preparedStatement.setString(1, waypoint.getName());
                    preparedStatement.setString(2, waypoint.getVisibility().toString());
                    preparedStatement.setString(3, waypoint.getCreator().toString());
                    preparedStatement.setDouble(4, waypoint.getX());
                    preparedStatement.setDouble(5, waypoint.getY());
                    preparedStatement.setDouble(6, waypoint.getZ());
                    preparedStatement.setFloat(7, waypoint.getYaw());
                    preparedStatement.setFloat(8, waypoint.getPitch());
                    preparedStatement.setString(9, waypoint.getWorld().getName());
                    preparedStatement.setString(10, waypoint.getMaterial().toString());
                    preparedStatement.setString(11, waypoint.getName());
                    int rows = this.database.executeUpdateSync(preparedStatement);
                    if(setCreatedId) {
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                        if(generatedKeys.next()) {
                            waypoint.setId(generatedKeys.getInt(1));
                            if(rows > 0) {
                                this.publicWaypoints.put(waypoint.getId(), waypoint);
                            }
                        }
                    }
                    return rows;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            case PRIVATE -> {
                try (PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("INSERT INTO waypoints (name, visibility, creator_id, x, y, z, yaw, pitch, world, material) SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM waypoints WHERE (name = ? AND visibility = 'PRIVATE' AND creator_id = ?))")) {
                    preparedStatement.setString(1, waypoint.getName());
                    preparedStatement.setString(2, waypoint.getVisibility().toString());
                    preparedStatement.setString(3, waypoint.getCreator().toString());
                    preparedStatement.setDouble(4, waypoint.getX());
                    preparedStatement.setDouble(5, waypoint.getY());
                    preparedStatement.setDouble(6, waypoint.getZ());
                    preparedStatement.setFloat(7, waypoint.getYaw());
                    preparedStatement.setFloat(8, waypoint.getPitch());
                    preparedStatement.setString(9, waypoint.getWorld().getName());
                    preparedStatement.setString(10, waypoint.getMaterial().toString());
                    preparedStatement.setString(11, waypoint.getName());
                    preparedStatement.setString(12, waypoint.getCreator().toString());
                    int rows = this.database.executeUpdateSync(preparedStatement);
                    if(setCreatedId) {
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            waypoint.setId(generatedKeys.getInt(1));
                            if(rows > 0) {
                                if (!this.privateWaypoints.containsKey(waypoint.getCreator())) {
                                    this.privateWaypoints.put(waypoint.getCreator(), new HashMap<>());
                                }
                                this.privateWaypoints.get(waypoint.getCreator()).put(waypoint.getId(), waypoint);
                            }
                        }
                    }
                    return rows;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }

    public CompletableFuture<Integer> registerAsync(Waypoint waypoint, boolean setCreatedId) {
        return CompletableFuture.supplyAsync(() -> this.registerSync(waypoint, setCreatedId));
    }

    public int unregisterSync(int id) {
        if(this.publicWaypoints.containsKey(id)) {
            this.publicWaypoints.remove(id);
        } else {
            for(Map<Integer, Waypoint> waypoints : this.privateWaypoints.values()) {
                if(!waypoints.containsKey(id)) {
                    continue;
                }
                waypoints.remove(id);
                break;
            }
        }
        try(PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("DELETE FROM waypoints WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            return database.executeUpdateSync(preparedStatement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public CompletableFuture<Integer> unregisterAsync(int id) {
        return CompletableFuture.supplyAsync(() -> this.unregisterSync(id));
    }

    public void reloadData() {
        try {
            PreparedStatement preparedStatement = this.database.getConnection().prepareStatement("SELECT id, name, visibility, creator_id, x, y, z, yaw, pitch, world, material FROM waypoints");
            this.database.executeQueryAsync(preparedStatement).thenAccept(resultSet -> {
               try {
                   this.publicWaypoints.clear();
                   this.privateWaypoints.clear();
                   while(resultSet.next()) {
                       Waypoint waypoint = new Waypoint.WaypointBuilder()
                               .setId(resultSet.getInt("id"))
                               .setName(resultSet.getString("name"))
                               .setVisibility(Visibility.valueOf(resultSet.getString("visibility")))
                               .setCreator(UUID.fromString(resultSet.getString("creator_id")))
                               .setX(resultSet.getDouble("x"))
                               .setY(resultSet.getDouble("y"))
                               .setZ(resultSet.getDouble("z"))
                               .setYaw(resultSet.getFloat("yaw"))
                               .setPitch(resultSet.getFloat("pitch"))
                               .setWorld(this.plugin.getServer().getWorld(resultSet.getString("world")))
                               .setMaterial(Material.valueOf(resultSet.getString("material")))
                               .build();
                       switch (waypoint.getVisibility()) {
                           case PUBLIC -> this.publicWaypoints.put(waypoint.getId(), waypoint);
                           case PRIVATE -> {
                               if(!this.privateWaypoints.containsKey(waypoint.getCreator())) {
                                   this.privateWaypoints.put(waypoint.getCreator(), new HashMap<>());
                               }
                               this.privateWaypoints.get(waypoint.getCreator()).put(waypoint.getId(), waypoint);
                           }
                       }
                   }
                   this.plugin.getLogger().info("Waypoints wurden geladen!");
                   preparedStatement.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<UUID, Map<Integer, Waypoint>> getPrivateWaypoints() {
        return this.privateWaypoints;
    }

    public Map<Integer, Waypoint> getPublicWaypoints() {
        return this.publicWaypoints;
    }

    public CompletableFuture<Waypoint> getWaypointById(int id) {
        return CompletableFuture.supplyAsync(() -> {
            if (this.publicWaypoints.containsKey(id)) {
                return this.publicWaypoints.get(id);
            } else {
                for (Map<Integer, Waypoint> waypoints : this.privateWaypoints.values()) {
                    if (!waypoints.containsKey(id)) {
                        continue;
                    }
                    return waypoints.get(id);
                }
            }
            return null;
        });
    }

    public CompletableFuture<List<Waypoint>> getWaypointsByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            Predicate<Waypoint> waypointPredicate = waypoint -> waypoint.getName().equalsIgnoreCase(name);
            List<Waypoint> result = new ArrayList<>(this.publicWaypoints.values().stream().filter(waypointPredicate).toList());
            for(Map<Integer, Waypoint> waypoints : this.privateWaypoints.values()) {
                result.addAll(waypoints.values().stream().filter(waypointPredicate).toList());
            }
            return result;
        });
    }

}
