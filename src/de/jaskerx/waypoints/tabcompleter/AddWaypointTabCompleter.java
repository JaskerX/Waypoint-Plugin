package de.jaskerx.waypoints.tabcompleter;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class AddWaypointTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String arg, String[] args) {
		
		Player player = (Player) sender;
		
		List<String> suggestions = new ArrayList<String>();
		if (args.length > 1) {
			
			switch (args.length) {
				case 2: suggestions.add(String.valueOf((int) player.getLocation().getX()));
						System.out.println(player.getLocation().getX());
					break;
				case 3: suggestions.add(String.valueOf((int) player.getLocation().getY()));
					break;
				case 4: suggestions.add(String.valueOf((int) player.getLocation().getZ()));
					break;
				case 6: //if (player.getWorld().getName().equals("world")) {
						if ("overworld".startsWith(args[5].toLowerCase())) {
							suggestions.add("overworld");
						}
						//} else if (player.getWorld().getName().equals("world_nether")) {
						if ("nether".startsWith(args[5].toLowerCase())) {
							suggestions.add("nether");
						}
						//} else if (player.getWorld().getName().equals("world_the_end")) {
						if ("end".startsWith(args[5].toLowerCase())) {
							suggestions.add("end");
						}
						//}
					break;
			}
			
		}
		
		return suggestions;
	}

}
