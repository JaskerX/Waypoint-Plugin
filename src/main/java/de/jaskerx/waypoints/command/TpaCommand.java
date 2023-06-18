package de.jaskerx.waypoints.command;

import de.jaskerx.waypoints.Messenger;
import de.jaskerx.waypoints.registry.TpasRegistry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TpaCommand implements CommandExecutor, TabExecutor {

	private final TpasRegistry tpasRegistry;
	private final Messenger messenger;

	public TpaCommand(TpasRegistry tpasRegistry, Messenger messenger) {
		this.tpasRegistry = tpasRegistry;
		this.messenger = messenger;
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!(sender instanceof Player player)) {
			this.messenger.sendMessageError(sender, "Diesen Command können nur Spieler ausführen!");
			return true;
		}

		// Anfrage stellen
		if(args.length == 1) {
			Player targetPlayer = player.getServer().getPlayer(args[0]);
			if(targetPlayer == null || !targetPlayer.isOnline()) {
				this.messenger.sendMessageError(player, "Der Spieler wurde nicht gefunden!");
				return true;
			}

			this.tpasRegistry.register(player, targetPlayer.getUniqueId());
			TextComponent textComponentAccept = new TextComponent("Tpa annehmen");
			textComponentAccept.setColor(ChatColor.DARK_GREEN);
			textComponentAccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa annehmen " + player.getName()));
			TextComponent textComponentDeny = new TextComponent("Tpa ablehnen");
			textComponentDeny.setColor(ChatColor.DARK_RED);
			textComponentDeny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpa ablehnen " + player.getName()));
			this.messenger.sendMessage(targetPlayer, "Du hast eine Tpa von " + player.getName() + " erhalten! Klicke, um diese anzunehmen oder abzulehnen.");
			targetPlayer.spigot().sendMessage(textComponentAccept);
			targetPlayer.spigot().sendMessage(textComponentDeny);
			this.messenger.sendMessage(player, "Tpa wurde gesendet.");
			return true;
		}

		// Anfrage annehmen/ablehnen
		if (args.length == 2) {
			Player requestingPlayer = player.getServer().getPlayer(args[1]);
			if(requestingPlayer == null || !requestingPlayer.isOnline()) {
				this.messenger.sendMessageError(player, "Der Spieler wurde nicht gefunden!");
				return true;
			}

			if (this.tpasRegistry.getTpas().containsKey(requestingPlayer.getUniqueId())) {
				if (this.tpasRegistry.getTpas().get(requestingPlayer.getUniqueId()).equals(player.getUniqueId())) {
					if (args[0].equalsIgnoreCase("annehmen")) {

						this.messenger.sendMessage(requestingPlayer, player.getName() + " hat deine Tpa angenommen!");
						requestingPlayer.teleport(player);
						
					} else if (args[0].equalsIgnoreCase("ablehnen")) {

						this.messenger.sendMessage(requestingPlayer, player.getName() + " hat deine Tpa abgelehnt!");
						this.messenger.sendMessage(player, "Du hast die Tpa abgelehnt!");
					} else {
						return false;
					}
					this.tpasRegistry.unregister(requestingPlayer.getUniqueId());
				} else {
					this.messenger.sendMessage(player, "Dieser Spieler hat keine Anfrage an dich gestellt!");
				}
			} else {
				this.messenger.sendMessage(player, "Dieser Spieler hat keine Anfragen gestellt!");
			}
			return true;
		}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

		if(!(sender instanceof Player player)) {
			return null;
		}

		List<String> result = new ArrayList<>();

		if (args.length == 1) {
			String[] completions = new String[] {"annehmen", "ablehnen"};
			for(String s : completions) {
				if(s.startsWith(args[0].toLowerCase())) {
					result.add(s);
				}
			}
		}
		if (args.length == 2) {
			// Spieler, die tpas gesendet haben, anzeigen
			this.tpasRegistry.getTpas().forEach((k, v) -> {
				if(v.equals(player.getUniqueId())) {
					Player requestingPlayer = player.getServer().getPlayer(k);
					if(requestingPlayer != null && requestingPlayer.isOnline() && requestingPlayer.getName().toLowerCase().startsWith(args[1])) {
						result.add(requestingPlayer.getName());
					}
				}
			});
		}

		return result;
	}

}
