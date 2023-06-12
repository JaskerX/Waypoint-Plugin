package de.jaskerx.waypoints.listener;

import de.jaskerx.waypoints.Messenger;
import de.jaskerx.waypoints.WaypointsGuisFactory;
import de.jaskerx.waypoints.registry.PrivateWaypointsGuisRegistry;
import de.jaskerx.waypoints.registry.PublicWaypointsGuisRegistry;
import de.jaskerx.waypoints.registry.WaypointsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import de.jaskerx.waypoints.WaypointPlugin;
import de.jaskerx.waypoints.Waypoint;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryClickListener implements Listener {

	private final PublicWaypointsGuisRegistry publicWaypointsGuisRegistry;
	private final WaypointPlugin plugin;
	private final WaypointsRegistry waypointsRegistry;
	private final WaypointsGuisFactory waypointsGuisFactory;
	private final PrivateWaypointsGuisRegistry privateWaypointsGuisRegistry;
	private final Messenger messenger;

	public InventoryClickListener(PublicWaypointsGuisRegistry publicWaypointsGuisRegistry, WaypointPlugin plugin, WaypointsRegistry waypointsRegistry, WaypointsGuisFactory waypointsGuisFactory, PrivateWaypointsGuisRegistry privateWaypointsGuisRegistry, Messenger messenger) {
		this.publicWaypointsGuisRegistry = publicWaypointsGuisRegistry;
		this.plugin = plugin;
		this.waypointsRegistry = waypointsRegistry;
		this.waypointsGuisFactory = waypointsGuisFactory;
		this.privateWaypointsGuisRegistry = privateWaypointsGuisRegistry;
		this.messenger = messenger;
	}

	@EventHandler
	public void onInventoryClicked (InventoryClickEvent event) {
		
		if(event.getView().getTitle().contains("Waypoints")) {
			
			if(!event.isLeftClick()) {
				event.setCancelled(true);
			}

			Player player = (Player) event.getWhoClicked();
			ItemStack currentItem = event.getCurrentItem();
			
			if (event.isLeftClick() && currentItem != null) {

				event.setCancelled(true);

				if (currentItem.getItemMeta() != null && currentItem.getItemMeta().getLore() != null && currentItem.getItemMeta().getLore().stream().anyMatch(s -> s.startsWith("Id:"))) {
					int id = Integer.parseInt(currentItem.getItemMeta().getLore().get(5).substring("Id: ".length()));
					this.waypointsRegistry.getWaypointById(id).thenAccept(waypoint -> {

						if (waypoint == null) {
							this.messenger.sendMessageError(player, "Der Waypoint wurde nicht gefunden!");
							return;
						}

						this.messenger.sendMessage(player, "Du wirst teleportiert...");

						double x = waypoint.getX();
						double z = waypoint.getZ();

						Location telLoc = new Location(waypoint.getWorld(), x, waypoint.getY(), z, waypoint.getYaw(), waypoint.getPitch());

						if (x >= 0) {
							telLoc.add(0.5, 0, 0);
						} else {
							telLoc.add(-0.5, 0, 0);
						}
						if (z >= 0) {
							telLoc.add(0, 0, 0.5);
						} else {
							telLoc.add(0, 0, -0.5);
						}

						Bukkit.getScheduler().runTask(this.plugin, () -> player.teleport(telLoc));
					});

				} else {

					switch (currentItem.getItemMeta().getDisplayName()) {

						case "Nächste Seite" -> {
							int pageIndexCurrent = Integer.parseInt(event.getView().getTitle().substring("Waypoints Seite ".length())) - 1;
							Inventory pageNext = this.publicWaypointsGuisRegistry.getInventories(player).get(pageIndexCurrent + 1);
							player.openInventory(pageNext);
						}

						case "Vorherige Seite" -> {
							int pageIndexCurrent = Integer.parseInt(event.getView().getTitle().substring("Waypoints Seite ".length())) - 1;
							Inventory pageBefore = this.publicWaypointsGuisRegistry.getInventories(player).get(pageIndexCurrent - 1);
							player.openInventory(pageBefore);
						}

						case "Private Waypoints öffnen" -> {
							Map<Integer, Waypoint> privateWaypoints = this.waypointsRegistry.getPrivateWaypoints().get(player.getUniqueId());
							if(privateWaypoints == null) {
								Bukkit.getScheduler().runTask(this.plugin, player::closeInventory);
								this.messenger.sendMessageError(player, "Du besitzt keine privaten Waypoints!");
								return;
							}
							this.waypointsGuisFactory.createGui(player, privateWaypoints.values(), false).thenAccept(inventories -> {
								this.privateWaypointsGuisRegistry.register(player, inventories);
								Bukkit.getScheduler().runTask(this.plugin, () -> player.openInventory(inventories.get(0)));
							});
						}

					}
				}
			}
		}
	}
	
}
