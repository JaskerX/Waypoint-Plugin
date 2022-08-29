package de.jaskerx.waypoints.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.jaskerx.waypoints.main.Main;

public class DeleteWaypointCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmnd, String arg, String[] args) {
			
		Player player = (Player) sender;
		
		if(args.length < 2) {
			player.sendMessage("Bitte gib den Namen des Waypoints an und ob er (öffentlich) oder (privat) ist!");
			return false;
		}
		
		if(args[1].equals("(privat)")) {
			if(Main.wpMan.getPrivateWps().containsKey(player.getUniqueId()) && Main.wpMan.getPrivateWps().get(player.getUniqueId()).containsKey(args[0])) {
				Main.wpMan.removePrivateWp(player.getUniqueId(), args[0]);
			} else {
				player.sendMessage("Du besitzt keinen solchen Waypoint!");
			}
			if(player.getUniqueId().equals(UUID.fromString("7428d327-f675-4876-bbf8-20f5b912a2e1")) && args.length == 3) {
				Main.wpMan.removePrivateWp(UUID.fromString(args[2]), args[0]);
			}
		} else {
			if(Main.wpMan.getPublicWps().containsKey(args[0])) {
				if(Main.wpMan.getPublicWps().get(args[0]).getCreator().equals(player.getUniqueId()) || player.getUniqueId().equals(UUID.fromString("7428d327-f675-4876-bbf8-20f5b912a2e1"))) {
					Main.wpMan.removePublicWp(args[0]);
				} else {
					player.sendMessage("Dieser Waypoint gehört dir nicht!");
				}
			} else {
				player.sendMessage("Es existiert kein solcher Waypoint!");
			}
		}
		
		/*if (Bukkit.getPlayer(Main.waypoints.get(arg3[0]).getCreator()).getName().equals(arg0.getName()) || arg0.getName().equals("JaskerX")) {
			Main.wpMan.deleteWaypoint(arg3[0], ((Player) arg0).getUniqueId());
			arg0.sendMessage("Der Waypoint " + arg3[0] + " wurde gelöscht!");
		} else {	
			arg0.sendMessage("Du hast keine Berechtigung dazu!");
		}*/
		
		return false;
	}

}
