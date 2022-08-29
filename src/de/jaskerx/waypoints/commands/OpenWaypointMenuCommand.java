package de.jaskerx.waypoints.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import de.jaskerx.waypoints.main.Main;
import de.jaskerx.waypoints.main.Waypoint;

public class OpenWaypointMenuCommand implements CommandExecutor {
	
static int n;
public static HashMap<String, ArrayList<ItemStack[]>> allPages = new HashMap<>();
TreeMap<String, Waypoint> remainingWps = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//TreeMap<String, Waypoint> wpsPrivate = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//public static HashMap<UUID, TreeMap<String, Waypoint>> privateWps = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender instanceof Player) {
			Player player = (Player) sender;
						
			if(Main.wpMan.getPublicWps().size() == 0 && Main.wpMan.getPrivateWps().get(player.getUniqueId()).size() == 0) {
				player.sendMessage("Es gibt noch keine Waypoints!");
				return false;
			}
			
			//sortWaypoints();
			//remainingWps = Main.waypoints;
			if (allPages.containsKey(player.getName())) {
				allPages.remove(player.getName());
			}
			
			//remainingWps.putAll(Main.waypoints);
			TreeMap<String, Waypoint> privateWpsPlayer = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
//			if(privateWps.containsKey(player.getUniqueId())) privateWpsPlayer = privateWps.get(player.getUniqueId());
			
			for (Map.Entry<String, Waypoint> entry : Main.wpMan.getPublicWps().entrySet()) {
/*				if(entry.getValue().isPrivate()) {
					if(entry.getValue().getCreator().equals(player.getUniqueId())) {
						privateWpsPlayer.put(entry.getKey(), entry.getValue());
					}
				} else {
*/					remainingWps.put(entry.getKey(), entry.getValue());
//				}
			}
//			privateWps.put(player.getUniqueId(), privateWpsPlayer);
			
			ArrayList<ItemStack[]> pages = new ArrayList<>();
			
			Inventory waypointInv = Bukkit.createInventory(null, 54, "Waypoints Seite 1");
			int maxWaypointsOnPage = 36;
			
			/*int amountPages = 1;
			System.out.println(Main.waypoints.size());
			System.out.println(Math.ceil(Main.waypoints.size()/maxWaypointsPage));
			System.out.println(Main.waypoints.size()/maxWaypointsPage);
			System.out.println((int) Math.ceil(Main.waypoints.size()/maxWaypointsPage));
			if (Main.waypoints.size() >= 36) {*/
				int amountPages = (remainingWps.size() - (remainingWps.size() % maxWaypointsOnPage))/maxWaypointsOnPage;
				if ((remainingWps.size() % maxWaypointsOnPage) > 0) amountPages++;
			//}
			ItemStack buttonPageForward = new ItemStack(Material.ARROW);
			ItemStack buttonPageBackwards = new ItemStack(Material.ARROW);
			ItemStack buttonOpenPrivateWps = new ItemStack(Material.NAME_TAG);
				ItemMeta metaFore = buttonPageForward.getItemMeta();
				metaFore.setDisplayName("Nächste Seite");
				ItemMeta metaBack = buttonPageBackwards.getItemMeta();
				metaBack.setDisplayName("Vorherige Seite");
				ItemMeta metaPrivate = buttonOpenPrivateWps.getItemMeta();
				metaPrivate.setDisplayName("Private Waypoints öffnen");
				buttonPageForward.setItemMeta(metaFore);
				buttonPageBackwards.setItemMeta(metaBack);
				buttonOpenPrivateWps.setItemMeta(metaPrivate);
				
			for (int i = 0; i < amountPages; i++) {
				n = 0;
				ItemStack[] page = new ItemStack[54];
				ArrayList<String> wpsToRemove = new ArrayList<>();
				for (Map.Entry<String, Waypoint> entry : remainingWps.entrySet()) {
					if (n < maxWaypointsOnPage) {
						page[n] = createWaypointItem(entry.getKey(), entry.getValue().getCoords(), entry.getValue().getCreator(), entry.getValue().getDimension(), entry.getValue().getMaterial());
						wpsToRemove.add(entry.getKey());
						n++;
					}
					
				}
				wpsToRemove.forEach(key -> {
					remainingWps.remove(key);
				});
				n = 0;
				for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
					if (!p.getName().equals(sender.getName()) && (n < 7)) {
						page[45 + n] = createPlayerItem(p.getName());
						n++;
					}
				}
				page[44] = buttonOpenPrivateWps; 
				if (amountPages > 1) {
					page[52] = buttonPageBackwards;
					page[53] = buttonPageForward;
				}
				pages.add(page);
			}
			allPages.put(player.getName(), pages);
			waypointInv.setStorageContents(pages.get(0));
			/*for (Map.Entry<String, Waypoint> entry : Main.waypoints.entrySet()) {
				if (n < 45) {
					waypointInv.addItem(createWaypointItem(entry.getKey(), entry.getValue().getCoords(), entry.getValue().getCreator(), entry.getValue().getDimension(), entry.getValue().getMaterial()));
					n++;
				}
			}*/
			
			/*n = 0;
			for (Player p : Main.getInstance().getServer().getOnlinePlayers()) {
				if (!p.getName().equals(sender.getName())) {
					waypointInv.setItem(45 + n, createPlayerItem(p.getName()));
					n++;
				}
			}*/
			
			player.openInventory(waypointInv);
		}
		
		
		return false;
	}
	
	
	public static ItemStack createWaypointItem (String name, double[] coords, UUID creator, String dimension, Material material) {
		
		ItemStack item = new ItemStack(material);
		List<String> loreList = new ArrayList<String>();
		loreList.add("x = " + coords[0]);
		loreList.add("y = " + coords[1]);
		loreList.add("z = " + coords[2]);
		loreList.add("Erstellt von " + Bukkit.getOfflinePlayer(creator).getName());
		loreList.add("Dimension: " + dimension);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(loreList);
		item.setItemMeta(meta);
		
		return item;
	}
	
	private ItemStack createPlayerItem (String name) {
		
		
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
		
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();
		headMeta.setOwningPlayer(Main.getInstance().getServer().getPlayer(name));
		
		headMeta.setDisplayName(name);
		List<String> loreList = new ArrayList<>();
		loreList.add("* Eine Teleport-Anfrage an diesen Spieler schicken.");
		loreList.add("* Die Teleport-Anfrage zurücknehmen.");
		headMeta.setLore(loreList);
		head.setItemMeta(headMeta);
		
		return head;
	}
	
}
