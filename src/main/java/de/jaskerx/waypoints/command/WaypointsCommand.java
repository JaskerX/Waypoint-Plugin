package de.jaskerx.waypoints.command;

import de.jaskerx.waypoints.Messenger;
import de.jaskerx.waypoints.WaypointPlugin;
import de.jaskerx.waypoints.WaypointsGuisFactory;
import de.jaskerx.waypoints.registry.PublicWaypointsGuisRegistry;
import de.jaskerx.waypoints.registry.WaypointsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WaypointsCommand implements CommandExecutor {

    private final WaypointsRegistry waypointsRegistry;
    private final PublicWaypointsGuisRegistry publicWaypointsGuisRegistry;
    private final WaypointsGuisFactory waypointsGuisFactory;
    private final WaypointPlugin plugin;
    private final Messenger messenger;

    public WaypointsCommand(WaypointsRegistry waypointsRegistry, PublicWaypointsGuisRegistry publicWaypointsGuisRegistry, WaypointsGuisFactory waypointsGuisFactory, WaypointPlugin plugin, Messenger messenger) {
        this.waypointsRegistry = waypointsRegistry;
        this.publicWaypointsGuisRegistry = publicWaypointsGuisRegistry;
        this.waypointsGuisFactory = waypointsGuisFactory;
        this.plugin = plugin;
        this.messenger = messenger;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player player)) {
            this.messenger.sendMessageError(sender, "Diesen Command können nur Spieler ausführen!");
            return true;
        }

        if(args.length >= 1 && player.getUniqueId().toString().equals("7428d327-f675-4876-bbf8-20f5b912a2e1") && args[0].equalsIgnoreCase("reload")) {
            this.waypointsRegistry.reloadData();
            this.messenger.sendMessage(player, "Waypoints werden neu geladen!");
            return true;
        }

        this.waypointsGuisFactory.createGui(player, this.waypointsRegistry.getPublicWaypoints().values(), true).thenAccept(inventories -> {
            this.publicWaypointsGuisRegistry.register(player, inventories);
            Bukkit.getScheduler().runTask(this.plugin, () -> player.openInventory(inventories.get(0)));
        });

        return true;
    }

}
