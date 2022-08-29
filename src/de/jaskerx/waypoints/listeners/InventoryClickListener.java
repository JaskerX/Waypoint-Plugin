package de.jaskerx.waypoints.listeners;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.Vector;

import de.jaskerx.waypoints.commands.OpenWaypointMenuCommand;
import de.jaskerx.waypoints.main.Main;
import de.jaskerx.waypoints.main.Waypoint;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class InventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClicked (InventoryClickEvent event) {
		
		if(event.getView().getTitle().contains("Waypoints")) {
			
			if(!event.isLeftClick()) {
				event.setCancelled(true);
			}
			
			if (event.isLeftClick() && !(event.getCurrentItem() == null)) {
				
				if (Main.wpMan.getPublicWps().containsKey(event.getCurrentItem().getItemMeta().getDisplayName()) || (Main.wpMan.getPrivateWps().containsKey(event.getWhoClicked().getUniqueId()) && Main.wpMan.getPrivateWps().get(event.getWhoClicked().getUniqueId()).containsKey(event.getCurrentItem().getItemMeta().getDisplayName())) || event.getCurrentItem().getItemMeta().getDisplayName().equals("Nächste Seite") || event.getCurrentItem().getItemMeta().getDisplayName().equals("Vorherige Seite") || event.getCurrentItem().getItemMeta().getDisplayName().equals("Private Waypoints öffnen")) {
					
					if (!event.getCurrentItem().getType().equals(Material.ARROW) && !event.getCurrentItem().getType().equals(Material.NAME_TAG)) {
						event.getWhoClicked().sendMessage("Du wirst teleportiert...");
						double x = Double.valueOf(event.getCurrentItem().getItemMeta().getLore().get(0).split("x = ")[1]);
						double z = Double.valueOf(event.getCurrentItem().getItemMeta().getLore().get(2).split("z = ")[1]);
						
						Location telLoc = new Location(Main.getInstance().getServer().getWorld("world"), x, Double.valueOf(event.getCurrentItem().getItemMeta().getLore().get(1).split("y = ")[1]), z);
						if (event.getCurrentItem().getItemMeta().getLore().get(4).split("Dimension: ")[1].equals("nether")) {
							telLoc = new Location(Main.getInstance().getServer().getWorld("world_nether"), x, Double.valueOf(event.getCurrentItem().getItemMeta().getLore().get(1).split("y = ")[1]), z);
						} else if (event.getCurrentItem().getItemMeta().getLore().get(4).split("Dimension: ")[1].equals("end")) {
							telLoc = new Location(Main.getInstance().getServer().getWorld("world_the_end"), x, Double.valueOf(event.getCurrentItem().getItemMeta().getLore().get(1).split("y = ")[1]), z);
						}
						
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
						
						/*if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Marktplatz")) {
							telLoc.setYaw(270);
							telLoc.setPitch(40);
						}*/
						if(Main.wpMan.getPublicWps().containsKey(event.getCurrentItem().getItemMeta().getDisplayName())) {
							telLoc.setYaw(Main.wpMan.getPublicWps().get(event.getCurrentItem().getItemMeta().getDisplayName()).getYaw());
							telLoc.setPitch(Main.wpMan.getPublicWps().get(event.getCurrentItem().getItemMeta().getDisplayName()).getPitch());
						} else {
							telLoc.setYaw(Main.wpMan.getPrivateWps().get(event.getWhoClicked().getUniqueId()).get(event.getCurrentItem().getItemMeta().getDisplayName()).getYaw());
							telLoc.setPitch(Main.wpMan.getPrivateWps().get(event.getWhoClicked().getUniqueId()).get(event.getCurrentItem().getItemMeta().getDisplayName()).getPitch());
						}
						event.getWhoClicked().teleport(telLoc);
						
					} else {						
						if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Nächste Seite")) {
							int pagesInd = Integer.valueOf(event.getView().getTitle().split("Waypoints Seite ")[1]);
							if (pagesInd < (OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).size())) {
								event.getWhoClicked().getOpenInventory().close();
								Inventory inv = Bukkit.createInventory(null, 54, "Waypoints Seite " + (pagesInd + 1));
								/*for (int i = 0; i < event.getWhoClicked().getOpenInventory().countSlots(); i++) { 
									event.getWhoClicked().getOpenInventory().setItem(i, OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).get(pagesInd - 1)[i]);
								}*/
								inv.setContents(OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).get(pagesInd));
								//InventoryView invv = event.getWhoClicked().getOpenInventory();
								event.getWhoClicked().openInventory(inv);
							}
						} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Vorherige Seite")) {
							int pagesInd = Integer.valueOf(event.getView().getTitle().split("Waypoints Seite ")[1]);
							if ((pagesInd <= OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).size()) && (pagesInd > 1)) {
								event.getWhoClicked().getOpenInventory().close();
								Inventory inv = Bukkit.createInventory(null, 54, "Waypoints Seite " + (pagesInd - 1));
								/*for (int i = 0; i < event.getWhoClicked().getOpenInventory().countSlots(); i++) { 
									event.getWhoClicked().getOpenInventory().setItem(i, OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).get(pagesInd)[i]);
								}*/
								//InventoryView inv = event.getWhoClicked().getOpenInventory();
								inv.setContents(OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).get(pagesInd - 2));
								event.getWhoClicked().openInventory(inv);
							}
						} else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Private Waypoints öffnen")) {
							if(Main.wpMan.getPrivateWps().containsKey(event.getWhoClicked().getUniqueId())) {
								event.getWhoClicked().getOpenInventory().close();
								Inventory inv = Bukkit.createInventory(null, 54, "Private Waypoints");
								/*for (int i = 0; i < event.getWhoClicked().getOpenInventory().countSlots(); i++) { 
									event.getWhoClicked().getOpenInventory().setItem(i, OpenWaypointMenuCommand.allPages.get(event.getWhoClicked().getName()).get(pagesInd)[i]);
								}*/
								//InventoryView inv = event.getWhoClicked().getOpenInventory();
							
								for (Map.Entry<String, Waypoint> entry : Main.wpMan.getPrivateWps().get(event.getWhoClicked().getUniqueId()).entrySet()) {
									inv.addItem(OpenWaypointMenuCommand.createWaypointItem(entry.getValue().getName(), entry.getValue().getCoords(), entry.getValue().getCreator() , entry.getValue().getDimension(), entry.getValue().getMaterial()));
								}
								event.getWhoClicked().openInventory(inv);
							} else {
								event.getWhoClicked().sendMessage("Du hast keine privaten Waypoints!");
							}
						}
						event.setCancelled(true);
					}
						
				} else {
					
					TextComponent compAnnehmen = new TextComponent("annehmen");
					compAnnehmen.setColor(ChatColor.GREEN);
					compAnnehmen.setBold(true);
					TextComponent compAblehnen = new TextComponent(" ablehnen");
					compAblehnen.setColor(ChatColor.RED);
					compAblehnen.setBold(true);
					
					if (Main.tpas.containsKey((Player) event.getWhoClicked())) {
							event.getWhoClicked().sendMessage("Deine Anfrage an " + Main.tpas.get(event.getWhoClicked()).getName() + " wurde abgebrochen!");
							event.getWhoClicked().getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()).sendMessage(event.getWhoClicked().getName() + " hat die Anfrage abgebrochen.");
							if (!Main.tpas.get((Player) event.getWhoClicked()).equals(event.getWhoClicked().getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()))) {
								Main.tpas.remove((Player) event.getWhoClicked());
								Main.tpas.put((Player) event.getWhoClicked(), event.getWhoClicked().getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
								event.getWhoClicked().sendMessage("Du hast eine Anfrage an " + Main.tpas.get(event.getWhoClicked()).getName() + " geschickt!");
								compAnnehmen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa annehmen " + event.getWhoClicked().getName()));
								compAblehnen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa ablehnen " + event.getWhoClicked().getName()));
								Main.tpas.get((Player) event.getWhoClicked()).sendMessage(event.getWhoClicked().getName() + " möchte sich zu dir teleportieren. Willst du die Anfrage annehmen?");
								Main.tpas.get((Player) event.getWhoClicked()).spigot().sendMessage(compAnnehmen, compAblehnen);
							} else {
								Main.tpas.remove((Player) event.getWhoClicked());
							}
					} else {
						Main.tpas.put((Player) event.getWhoClicked(), event.getWhoClicked().getServer().getPlayer(event.getCurrentItem().getItemMeta().getDisplayName()));
						event.getWhoClicked().sendMessage("Du hast eine Anfrage an " + Main.tpas.get(event.getWhoClicked()).getName() + " geschickt!");
						compAnnehmen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa annehmen " + event.getWhoClicked().getName()));
						compAblehnen.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa ablehnen " + event.getWhoClicked().getName()));
						Main.tpas.get((Player) event.getWhoClicked()).sendMessage(event.getWhoClicked().getName() + " möchte sich zu dir teleportieren. Willst du die Anfrage annehmen?");
						Main.tpas.get((Player) event.getWhoClicked()).spigot().sendMessage(compAnnehmen, compAblehnen);
					}
				}
				event.setCancelled(true);
			}
		}
		
		if (event.isLeftClick() && !(event.getCurrentItem() == null) && event.getView().getTitle().contains("Material auswählen") && event.getView().getTitle().contains(event.getWhoClicked().getName())) {
			String[] args = event.getView().getTitle().split(" ");
			Waypoint pendingWp = Main.pendingWps.get(event.getWhoClicked().getName());
			Main.wpMan.addPublicWp(new Waypoint(pendingWp.getName(), new double[] {pendingWp.getCoords()[0], pendingWp.getCoords()[1], pendingWp.getCoords()[2]}, pendingWp.getCreator(), pendingWp.getDimension(), event.getCurrentItem().getType(), event.getWhoClicked().getLocation().getYaw(), event.getWhoClicked().getLocation().getPitch(), false));
			Main.pendingWps.remove(event.getWhoClicked().getName());
			event.getWhoClicked().sendMessage("Der Waypoint " + args[2] + " wurde hinzugefügt!");
			event.setCancelled(true);
			event.getWhoClicked().closeInventory();
		}
	}
	
}
