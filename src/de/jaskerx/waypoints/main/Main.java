package de.jaskerx.waypoints.main;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.jaskerx.waypoints.commands.AddPrivateWaypointCommand;
import de.jaskerx.waypoints.commands.AddWaypointCommand;
import de.jaskerx.waypoints.commands.DeleteWaypointCommand;
import de.jaskerx.waypoints.commands.OpenWaypointMenuCommand;
import de.jaskerx.waypoints.commands.TpaCommand;
import de.jaskerx.waypoints.listeners.InventoryClickListener;
import de.jaskerx.waypoints.tabcompleter.AddWaypointTabCompleter;
import de.jaskerx.waypoints.tabcompleter.DeleteWaypointTabCompleter;
import de.jaskerx.waypoints.tabcompleter.TpaTabCompleter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;


public class Main extends JavaPlugin {

	public static HashMap<String, Waypoint> pendingWps = new HashMap<>();
	//public static HashMap<String, Waypoint> waypoints = new HashMap<>();
	public static HashMap<Player, Player> tpas = new HashMap<>();
	public static Waypointmanager wpMan;
	
	public static Main instance;
	Thread timeThread = new Thread() {
		@Override
		public void run() {
			while (!timeThread.isInterrupted()) {
				try {
					for(Player player : Bukkit.getOnlinePlayers()) {
						long hours = Bukkit.getWorld("world").getTime()/1000;
						hours = (hours + 6) % 24;
						instance.getServer().getPlayer(player.getName()).setPlayerListFooter("Uhrzeit " + hours + ":" + (Math.round(Bukkit.getWorld("world").getTime() % 1000 / 16.6) % 60));
					}
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	
	public void onEnable() {
		this.instance = this;
		
		this.saveDefaultConfig();
		
		getCommand("wps").setExecutor(new OpenWaypointMenuCommand());
		getCommand("addWp").setExecutor(new AddWaypointCommand());
		getCommand("addWp").setTabCompleter(new AddWaypointTabCompleter());
		getCommand("addPrivateWp").setExecutor(new AddPrivateWaypointCommand());
		getCommand("addPrivateWp").setTabCompleter(new AddWaypointTabCompleter());
		getCommand("delWp").setExecutor(new DeleteWaypointCommand());
		getCommand("delWp").setTabCompleter(new DeleteWaypointTabCompleter());
		getCommand("tpa").setExecutor(new TpaCommand());
		getCommand("tpa").setTabCompleter(new TpaTabCompleter());
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		pluginManager.registerEvents(new InventoryClickListener(), this);
		
		wpMan = new Waypointmanager(this.getConfig());
		
		/*if (this.getConfig().contains("waypointData")) {
			Main.getInstance().getConfig().getConfigurationSection("waypointData").getKeys(false).forEach(key -> {
				waypoints.put(key, new Waypoint(key, new double[] {Double.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[0]),
											Double.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[1]),
											Double.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[2])},
											UUID.fromString(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[3]),
											((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[4]));
				waypoints.get(key).setMaterial(Material.getMaterial(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[5]));
				waypoints.get(key).setYaw(Float.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[6]));
				waypoints.get(key).setPitch(Float.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[7]));
				waypoints.get(key).setPrivate(Boolean.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[8]));
			});
		}*/
		
		timeThread.start();
	}
	
	public void onDisable() {
		timeThread.interrupt();
	}
	
	/*public static void saveWaypointsToConfig () {
		
		Main.getInstance().getConfig().set("waypointData", null);
		for (Map.Entry<String, Waypoint> entry : waypoints.entrySet()) {
			Main.getInstance().getConfig().set("waypointData." + entry.getKey(), String.valueOf(entry.getValue().getCoords()[0])
																		+ "," + String.valueOf(entry.getValue().getCoords()[1])
																		+ "," + String.valueOf(entry.getValue().getCoords()[2])
																		+ "," + entry.getValue().getCreator()
																		+ "," + entry.getValue().getDimension()
																		+ "," + entry.getValue().getMaterial().name()
																		+ "," + entry.getValue().getYaw()
																		+ "," + entry.getValue().getPitch()
																		+ "," + entry.getValue().isPrivate());
		}
		Main.getInstance().saveConfig();
	}
	
	public static void addWaypoint (String name, double x1, double x2, double x3, UUID creator, String dimension, boolean pending, @Nullable Material material, float yaw, float pitch, boolean isPrivate) {
		
		if (pending) {
			pendingWps.put(Bukkit.getPlayer(creator).getName(), new Waypoint(name, new double[] {x1, x2, x3}, creator, dimension));
		} else {
			waypoints.put(name, new Waypoint(name, new double[] {x1, x2, x3}, creator, dimension));
			waypoints.get(name).setMaterial(material);
			waypoints.get(name).setYaw(yaw);
			waypoints.get(name).setPitch(pitch);
			waypoints.get(name).setPrivate(isPrivate);
			Main.getInstance().getConfig().set("waypointData." + name, x1 + "," + x2 + "," + x3 + "," + creator + "," + dimension + "," + material.name() + "," + yaw + "," + pitch + "," + isPrivate);
			Main.getInstance().saveConfig();
		}
	}
	
	public static void deleteWaypoint (String name, UUID uuid) {
		
		if(Main.waypoints.containsKey(name)) {
			Main.waypoints.remove(name);
			/*Main.getInstance().getConfig().getConfigurationSection("waypointData").getKeys(false).forEach(key -> {
				waypoints.put(key, new double[] {Double.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[0]),
											Double.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[1]),
											Double.valueOf(((String) Main.getInstance().getConfig().get("waypointData." + key)).split(",")[2])});
			});*/
		/*}
		if(OpenWaypointMenuCommand.privateWps.containsKey(uuid)) {
			OpenWaypointMenuCommand.privateWps.get(uuid).remove(name);
		}
		saveWaypointsToConfig();
	}*/
	
	public static Main getInstance () {
		
		return instance;
	}
	
}
