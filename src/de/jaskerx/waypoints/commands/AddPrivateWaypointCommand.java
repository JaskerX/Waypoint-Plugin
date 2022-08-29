package de.jaskerx.waypoints.commands;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.jaskerx.waypoints.main.Main;
import de.jaskerx.waypoints.main.Waypoint;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public class AddPrivateWaypointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		if (args.length >= 4) {
			if (Main.wpMan.getPrivateWps().containsKey(uuid) && Main.wpMan.getPrivateWps().get(uuid).containsKey(args[0])) {
				player.sendMessage("Leider existiert ein gleichnamiger Waypoint schon. Bitte wähle einen anderen Namen!");
			} else {
				String dimension = getShortWorldName(((Player) player).getWorld().getName());
				if (args.length == 6) {
					dimension = args[5];
				}
				//Main.addWaypoint(arg3[0], Double.valueOf(arg3[1]), Double.valueOf(arg3[2]), Double.valueOf(arg3[3]), arg0.getName(), dimension, true, null);
				if ((args.length <= 4)) {
					if(Main.wpMan.addPrivateWp(uuid, new Waypoint(args[0], new double[] {Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3])}, player.getUniqueId(), dimension, Material.TORCH, player.getLocation().getYaw(), player.getLocation().getPitch(), true)))
						player.sendMessage("Der Waypoint " + args[0] + " wurde hinzugefügt!");
						TextComponent comp = new TextComponent("Klicke hier, um das Waypoint-Menü zu öffnen!");
						comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wps"));
						player.spigot().sendMessage(comp);
				} else {
					args[4] = args[4].toUpperCase();
					if(Main.wpMan.addPrivateWp(uuid, new Waypoint(args[0], new double[] {Double.valueOf(args[1]), Double.valueOf(args[2]), Double.valueOf(args[3])}, player.getUniqueId(), dimension, Material.getMaterial(args[4]), player.getLocation().getYaw(), player.getLocation().getPitch(), true)))
						player.sendMessage("Der Waypoint " + args[0] + " wurde hinzugefügt!");
						TextComponent comp = new TextComponent("Klicke hier, um das Waypoint-Menü zu öffnen!");
						comp.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/wps"));
						player.spigot().sendMessage(comp);
				}
				//Inventory inv = Bukkit.createInventory(null, 54, arg0.getName() + " Waypoint " + arg3[0] + " Material auswählen");
				//inv.addItem(new ItemStack(Material.TORCH));
				//((Player) arg0).openInventory(inv);
			}
		} else {
			player.sendMessage("Bitte den Command folgendermaßen benutzen: /addPrivateWp <name> <x> <y> <z> (<material>) (<dimension>)");
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
