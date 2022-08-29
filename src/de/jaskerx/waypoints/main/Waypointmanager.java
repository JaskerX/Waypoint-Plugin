package de.jaskerx.waypoints.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Waypointmanager {

	private FileConfiguration config;
	private HashMap<UUID, TreeMap<String, Waypoint>> privateWaypoints = new HashMap<>();
	private TreeMap<String, Waypoint> publicWaypoints = new TreeMap<>();
	private ArrayList<UUID> playersWithPrivateWps = new ArrayList<>();
	
	public Waypointmanager(FileConfiguration config) {
		this.config = config;

		readWpsFromConfig();
	}
	
	//returns true if wp was successfully added, otherwise false
	public boolean addPrivateWp(UUID uuid, Waypoint waypoint) {
		TreeMap<String, Waypoint> tempMap = new TreeMap<>();
		if(privateWaypoints.containsKey(uuid)) {
			tempMap = privateWaypoints.get(uuid);
		}
		tempMap.put(waypoint.getName(), waypoint);
		config.set("waypointsPrivate_" + uuid + "." + waypoint.getName(), waypoint.getCoords()[0] + "," + waypoint.getCoords()[1] + "," + waypoint.getCoords()[2] + "," + waypoint.getCreator() + "," + waypoint.getDimension() + "," + waypoint.getMaterial() + "," + waypoint.getYaw() + "," + waypoint.getPitch());
		Main.instance.getServer().getOnlinePlayers().stream().forEach((player) -> {
			if(player.getUniqueId().equals(uuid)) {
				config.set("playersWithPrivateWps." + uuid, player.getName());
			}
		});
		Main.instance.saveConfig();
		privateWaypoints.put(uuid, tempMap);
		if(privateWaypoints.get(uuid).containsValue(waypoint)) {
			return true;
		}
		return false;
	}
	//returns true if wp was successfully added, otherwise false
	public boolean addPublicWp(Waypoint waypoint) {
		config.set("waypointsPublic." + waypoint.getName(), waypoint.getCoords()[0] + "," + waypoint.getCoords()[1] + "," + waypoint.getCoords()[2] + "," + waypoint.getCreator() + "," + waypoint.getDimension() + "," + waypoint.getMaterial() + "," + waypoint.getYaw() + "," + waypoint.getPitch());
		Main.instance.saveConfig();
		publicWaypoints.put(waypoint.getName(), waypoint);
		if(publicWaypoints.containsValue(waypoint)) {
			return true;
		}
		return false;
	}
	public boolean removePrivateWp(UUID uuid, String name) {
		privateWaypoints.get(uuid).remove(name);
		config.set("waypointsPrivate_" + uuid + "." + name, null);
		Main.instance.saveConfig();
		return false;
	}
	public boolean removePublicWp(String name) {
		publicWaypoints.remove(name);
		config.set("waypointsPublic." + name, null);
		Main.instance.saveConfig();
		return false;
	}
	
	public void readWpsFromConfig() {
		if (config.contains("playersWithPrivateWps")) {
			config.getConfigurationSection("playersWithPrivateWps").getKeys(false).forEach(key -> {
				playersWithPrivateWps.add(UUID.fromString(key));
			});
			playersWithPrivateWps.forEach((uuid) -> {
				TreeMap<String, Waypoint> tempMap = new TreeMap<>();
				config.getConfigurationSection("waypointsPrivate_" + uuid.toString()).getKeys(false).forEach(key -> {
					String[] args = ((String) config.get("waypointsPrivate_" + uuid.toString() + "." + key)).split(",");
					Double x = Double.valueOf(args[0]);
					Double y = Double.valueOf(args[1]);
					Double z = Double.valueOf(args[2]);
					String dimension = args[4];
					Material material = Material.getMaterial(args[5]);
					float yaw = Float.valueOf(args[6]);
					float pitch = Float.valueOf(args[7]);
					tempMap.put(key, new Waypoint(key, new double[]{x, y, z}, uuid, dimension, material, yaw, pitch, true));
				});
				privateWaypoints.put(uuid, tempMap);
			});
		}
		if (config.contains("waypointsPublic")) {
			config.getConfigurationSection("waypointsPublic").getKeys(false).forEach(key -> {
				String[] args = ((String) config.get("waypointsPublic." + key)).split(",");
				Double x = Double.valueOf(args[0]);
				Double y = Double.valueOf(args[1]);
				Double z = Double.valueOf(args[2]);
				UUID creator = UUID.fromString(args[3]);
				String dimension = args[4];
				Material material = Material.getMaterial(args[5]);
				float yaw = Float.valueOf(args[6]);
				float pitch = Float.valueOf(args[7]);
				publicWaypoints.put(key, new Waypoint(key, new double[] {x, y, z}, creator, dimension, material, yaw, pitch, false));
			});
		}
	}
	
	public HashMap<UUID, TreeMap<String, Waypoint>> getPrivateWps() {
		return privateWaypoints;
	}
	public TreeMap<String, Waypoint> getPublicWps() {
		return publicWaypoints;
	}
}
