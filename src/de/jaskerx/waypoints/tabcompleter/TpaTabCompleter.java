package de.jaskerx.waypoints.tabcompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.jaskerx.waypoints.main.Main;

public class TpaTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String arg, String[] args) {
		
		List<String> suggestions = new ArrayList<>();
		
		if (args.length == 1) {
			suggestions.add("ablehnen");
			suggestions.add("annehmen");
		}
		if (args.length == 2) {
			// Spieler, die tpas gesendet haben, anzeigen
			for (Map.Entry<Player, Player> entry : Main.tpas.entrySet()) {
				if (entry.getValue().getName().equals(sender.getName()) && entry.getKey().getName().toLowerCase().startsWith(args[1].toLowerCase())) {
					suggestions.add(entry.getKey().getName());
				}
			}
		}
		
		return suggestions;
	}

}
