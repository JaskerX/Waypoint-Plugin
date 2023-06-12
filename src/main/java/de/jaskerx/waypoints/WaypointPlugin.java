package de.jaskerx.waypoints;

import de.jaskerx.waypoints.command.WaypointCommand;
import de.jaskerx.waypoints.command.WaypointsCommand;
import de.jaskerx.waypoints.data.Database;
import de.jaskerx.waypoints.registry.PrivateWaypointsGuisRegistry;
import de.jaskerx.waypoints.registry.PublicWaypointsGuisRegistry;
import de.jaskerx.waypoints.registry.TpasRegistry;
import de.jaskerx.waypoints.registry.WaypointsRegistry;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.jaskerx.waypoints.command.TpaCommand;
import de.jaskerx.waypoints.listener.InventoryClickListener;

public class WaypointPlugin extends JavaPlugin {

	private Database database;

	@Override
	public void onEnable() {
		this.database = new Database(this);
		this.database.connect();
		this.database.createTableIfNotExists("waypoints ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' TEXT, 'visibility' TEXT, 'creator_id' TEXT, 'created_at' DATETIME DEFAULT CURRENT_TIMESTAMP, 'x' DOUBLE, 'y' DOUBLE, 'z' DOUBLE, 'yaw' FLOAT, 'pitch' FLOAT, 'world' TEXT, 'material' TEXT)");

		WaypointsRegistry waypointsRegistry = new WaypointsRegistry(this.database, this);
		waypointsRegistry.reloadData();
		PublicWaypointsGuisRegistry publicWaypointsGuisRegistry = new PublicWaypointsGuisRegistry();
		PrivateWaypointsGuisRegistry privateWaypointsGuisRegistry = new PrivateWaypointsGuisRegistry();
		TpasRegistry tpasRegistry = new TpasRegistry();
		WaypointsGuisFactory waypointsGuisFactory = new WaypointsGuisFactory();
		Messenger messenger = new Messenger("§8[§9Waypoints§8]", ChatColor.GRAY, ChatColor.RED);

		getCommand("waypoints").setExecutor(new WaypointsCommand(waypointsRegistry, publicWaypointsGuisRegistry, waypointsGuisFactory, this, messenger));
		getCommand("waypoint").setExecutor(new WaypointCommand(waypointsRegistry, messenger));
		getCommand("tpa").setExecutor(new TpaCommand(tpasRegistry, messenger));

		PluginManager pluginManager = this.getServer().getPluginManager();
		pluginManager.registerEvents(new InventoryClickListener(publicWaypointsGuisRegistry, this, waypointsRegistry, waypointsGuisFactory, privateWaypointsGuisRegistry, messenger), this);
	}

	@Override
	public void onDisable() {
		this.database.disconnect();
	}
	
}
