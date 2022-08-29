package de.jaskerx.waypoints.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.jaskerx.waypoints.main.Main;
import de.jaskerx.waypoints.main.Waypoint;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class AddWaypointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		
		Player player = (Player) arg0;
		if (arg3.length >= 4) {
			if (!Main.wpMan.getPublicWps().containsKey(arg3[0])) {
				
				String dimension = getShortWorldName(((Player) player).getWorld().getName());
				if (arg3.length == 6 && (arg3[5].equals("overworld") || arg3[5].equals("nether") || arg3[5].equals("end"))) {
					dimension = arg3[5];
				}
				String name;
				try {
					Double.valueOf(arg3[0]);
					name = String.valueOf("KeinName" + new Random().nextLong());
				} catch(NumberFormatException e) {
					name = arg3[0];
				}
				//Main.addWaypoint(arg3[0], Double.valueOf(arg3[1]), Double.valueOf(arg3[2]), Double.valueOf(arg3[3]), arg0.getName(), dimension, true, null);
				if ((arg3.length <= 4) || (arg3.length > 4 && Material.getMaterial(arg3[4]) == null)) {
					if(Main.wpMan.addPublicWp(new Waypoint(name, new double[] {Double.valueOf(arg3[1]), Double.valueOf(arg3[2]), Double.valueOf(arg3[3])}, player.getUniqueId(), dimension, Material.TORCH, player.getLocation().getYaw(), player.getLocation().getPitch(), false)))
						player.sendMessage("Der Waypoint " + name + " wurde hinzugefügt!");
						TextComponent comp = new TextComponent("Klicke hier, um das Waypoint-Menü zu öffnen!");
						comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wps"));
						player.spigot().sendMessage(comp);
				} else {
					arg3[4] = arg3[4].toUpperCase();
					if(Main.wpMan.addPublicWp(new Waypoint(name, new double[] {Double.valueOf(arg3[1]), Double.valueOf(arg3[2]), Double.valueOf(arg3[3])}, player.getUniqueId(), dimension, Material.getMaterial(arg3[4]), player.getLocation().getYaw(), player.getLocation().getPitch(), false)))
						player.sendMessage("Der Waypoint " + name + " wurde hinzugefügt!");
						TextComponent comp = new TextComponent("Klicke hier, um das Waypoint-Menü zu öffnen!");
						comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wps"));
						player.spigot().sendMessage(comp);
				}
				//Inventory inv = Bukkit.createInventory(null, 54, arg0.getName() + " Waypoint " + arg3[0] + " Material auswählen");
				//inv.addItem(new ItemStack(Material.TORCH));
				//((Player) arg0).openInventory(inv);
				
			} else {
				player.sendMessage("Leider existiert ein gleichnamiger Waypoint schon. Bitte wähle einen anderen Namen!");
			}
		} else {
			player.sendMessage("Bitte den Command folgendermaßen benutzen: /addWp <name> <x> <y> <z> (<material>) (<dimension>)");
		}
		
		return false;
	}
	
	private String getShortWorldName (String old) {
		
		if (old.equals("world")) {
			return "overworld";
		} else if (old.equals("world_nether")) {
			return "nether";
		} else {
			return "end";
		}
		
	}

}
