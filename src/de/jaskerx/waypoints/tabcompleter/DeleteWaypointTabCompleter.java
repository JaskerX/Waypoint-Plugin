package de.jaskerx.waypoints.tabcompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.jaskerx.waypoints.commands.OpenWaypointMenuCommand;
import de.jaskerx.waypoints.main.Main;
import de.jaskerx.waypoints.main.Waypoint;

public class DeleteWaypointTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {

		List<String> suggestions = new ArrayList<>();
		if (arg3.length == 1) {
			for (Map.Entry<String, Waypoint> entry : Main.wpMan.getPublicWps().entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(arg3[0].toLowerCase()) && (entry.getValue().getCreator().equals(((Player) arg0).getUniqueId()) || arg0.getName().equals("JaskerX"))) {
					suggestions.add(entry.getKey() + " (öffentlich)");
				}
			}
			for (Map.Entry<String, Waypoint> entry : Main.wpMan.getPrivateWps().get(((Player) arg0).getUniqueId()).entrySet()) {
				if (entry.getKey().toLowerCase().startsWith(arg3[0].toLowerCase()) && (entry.getValue().getCreator().equals(((Player) arg0).getUniqueId()) || arg0.getName().equals("JaskerX"))) {
					suggestions.add(entry.getKey() + " (privat)");
				}
			}
		}
		
		return suggestions;
	}

}
