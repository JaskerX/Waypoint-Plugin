package de.jaskerx.waypoints.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.jaskerx.waypoints.main.Main;

public class TpaCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String arg, String[] args) {
		
		if (args.length == 2) {
			Player requestingPlayer = sender.getServer().getPlayer(args[1]);
			if (Main.tpas.containsKey(requestingPlayer)) {
				if (Main.tpas.get(requestingPlayer).equals((Player) sender)) {
					if (args[0].equals("annehmen")) {
						
						requestingPlayer.sendMessage("Deine Anfrage wurde von " + sender.getName() + " angenommen!");
						requestingPlayer.teleport(Main.tpas.get(requestingPlayer));
						
					} else if (args[0].equals("ablehnen")) {
						
						Main.tpas.get(requestingPlayer).sendMessage("Deine Anfrage wurde von " + sender.getName() + " abgelehnt!");
					}
					Main.tpas.remove(requestingPlayer);
				} else {
					sender.sendMessage("Dieser Spieler hat keine Anfrage an dich gestellt!");
				}
			} else {
				sender.sendMessage("Dieser Spieler hat keine Anfragen gestellt!");
			}
		} else {
			sender.sendMessage("Bitte nutze den Command wie folgt: /tpa <annehmen/ablehnen> <Spielername>");
		}
		
		return false;
	}

}
