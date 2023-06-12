package de.jaskerx.waypoints.command;

import de.jaskerx.waypoints.Messenger;
import de.jaskerx.waypoints.Visibility;
import de.jaskerx.waypoints.Waypoint;
import de.jaskerx.waypoints.registry.WaypointsRegistry;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WaypointCommand implements CommandExecutor, TabCompleter {

    private final WaypointsRegistry waypointsRegistry;
    private final Messenger messenger;
    private final String[] allMaterials;

    public WaypointCommand(WaypointsRegistry waypointsRegistry, Messenger messenger) {
        this.waypointsRegistry = waypointsRegistry;
        this.messenger = messenger;
        this.allMaterials = new String[Material.values().length];
        for(int i = 0; i < this.allMaterials.length; i++) {
            this.allMaterials[i] = Material.values()[i].toString();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length < 2) {
            this.messenger.sendMessageError(sender, "Zu wenige Argumente!");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info" -> {
                if(!args[1].matches("\\d+")) {
                    if(!(sender instanceof Player player)) {
                        this.messenger.sendMessageError(sender, "Diesen Command können nur Spieler ausführen!");
                        return true;
                    }
                    this.waypointsRegistry.getWaypointsByName(args[1]).thenAccept(waypoints -> {
                        if(waypoints.size() == 0) {
                            this.messenger.sendMessageError(player, "Es wurde kein Waypoint gefunden!");
                        } else if(waypoints.size() == 1) {
                            this.infoWaypoint(waypoints.get(0), player);
                        } else {
                            this.messenger.sendMessage(player, "Bitte wähle einen Waypoint:");
                            for(Waypoint waypoint : waypoints) {
                                if(!waypoint.getCreator().equals(player.getUniqueId())) {
                                    continue;
                                }
                                TextComponent textComponent = new TextComponent("Name: " + waypoint.getName() + " Sichtbarkeit: " + waypoint.getVisibility().getDisplayString() + " Id: " + waypoint.getId());
                                textComponent.setColor(ChatColor.BLUE);
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoint info " + waypoint.getId()));
                                player.spigot().sendMessage(textComponent);
                            }
                        }
                    });
                    return true;
                }
                int id = Integer.parseInt(args[1]);
                this.waypointsRegistry.getWaypointById(id).thenAccept(waypoint -> this.infoWaypoint(waypoint, sender));
            }
            case "delete" -> {
                if(!(sender instanceof Player player)) {
                    this.messenger.sendMessageError(sender, "Diesen Command können nur Spieler ausführen!");
                    return true;
                }
                if(!args[1].matches("\\d+")) {
                    this.waypointsRegistry.getWaypointsByName(args[1]).thenAccept(waypoints -> {
                        if(waypoints.size() == 0) {
                            this.messenger.sendMessageError(player, "Es wurde kein Waypoint gefunden!");
                        } else if(waypoints.size() == 1) {
                            this.deleteWaypoint(waypoints.get(0), player);
                        } else {
                            this.messenger.sendMessage(player, "Bitte wähle, welchen Waypoint du löschen möchtest:");
                            for(Waypoint waypoint : waypoints) {
                                if(!waypoint.getCreator().equals(player.getUniqueId())) {
                                    continue;
                                }
                                TextComponent textComponent = new TextComponent("Name: " + waypoint.getName() + " Sichtbarkeit: " + waypoint.getVisibility().getDisplayString() + " Id: " + waypoint.getId());
                                textComponent.setColor(ChatColor.BLUE);
                                textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoint delete " + waypoint.getId()));
                                player.spigot().sendMessage(textComponent);
                            }
                        }
                    });
                    return true;
                }
                int id = Integer.parseInt(args[1]);
                this.waypointsRegistry.getWaypointById(id).thenAccept(waypoint -> this.deleteWaypoint(waypoint, player));
            }
            case "add" -> {
                if(!(sender instanceof Player player)) {
                    this.messenger.sendMessageError(sender, "Diesen Command können nur Spieler ausführen!");
                    return true;
                }
                if(args.length < 3) {
                    this.messenger.sendMessageError(player, "Zu wenige Argumente!");
                    return false;
                }
                Material material = Material.TORCH;
                if(args.length >= 4) {
                    try {
                        material = Material.valueOf(args[3].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        this.messenger.sendMessageError(player, "Bitte gib ein gültiges Item an!");
                        return true;
                    }
                }
                Waypoint waypoint = new Waypoint.WaypointBuilder()
                        .setName(args[1])
                        .setX(player.getLocation().getX())
                        .setY(player.getLocation().getY())
                        .setZ(player.getLocation().getZ())
                        .setYaw(player.getLocation().getYaw())
                        .setPitch(player.getLocation().getPitch())
                        .setMaterial(material)
                        .setWorld(player.getWorld())
                        .setCreator(player.getUniqueId())
                        .setVisibility(Visibility.valueOf(args[2].toUpperCase()))
                        .build();
                this.waypointsRegistry.registerAsync(waypoint, true).thenAccept(rows -> {
                    if(rows > 0) {
                        TextComponent comp = new TextComponent("Klicke hier, um das Waypoint-Menü zu öffnen!");
                        comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoints"));
                        this.messenger.sendMessage(player, "Der Waypoint wurde erfolgreich erstellt!");
                        player.spigot().sendMessage(comp);
                        return;
                    }
                    if(rows == 0) {
                        this.messenger.sendMessageError(player, "Es gibt bereits einen Waypoint mit diesem Namen und der gleichen Sichtbarkeit!");
                        return;
                    }
                    this.messenger.sendMessageError(player, "Ein Fehler ist aufgetreten!");
                    System.out.println("INSERT rows: " + rows);
                });
            }
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        List<String> result = new ArrayList<>();
        switch (args.length) {
            case 1 -> {
                String[] options = new String[] {"add", "info", "delete"};
                for(String option : options) {
                    if(option.startsWith(args[0].toLowerCase())) {
                        result.add(option);
                    }
                }
            }
            case 3 -> {
                String[] options = new String[] {"PUBLIC", "PRIVATE"};
                if(args[0].equalsIgnoreCase("add")) {
                    for (String option : options) {
                        if (option.startsWith(args[2].toUpperCase())) {
                            result.add(option);
                        }
                    }
                }
            }
            case 4 -> {
                for(String material : this.allMaterials) {
                    if(material.startsWith(args[3].toUpperCase())) {
                        result.add(material);
                    }
                }
            }
        }

        return result;
    }

    private void deleteWaypoint(Waypoint waypoint, Player player) {
        if(waypoint == null) {
            this.messenger.sendMessage(player, "Der Waypoint wurde nicht gefunden!");
            return;
        }
        if(!waypoint.getCreator().equals(player.getUniqueId())) {
            this.messenger.sendMessage(player, "Du bist nicht berechtigt, diesen Waypoint zu löschen!");
            return;
        }
        this.waypointsRegistry.unregisterAsync(waypoint.getId()).thenAccept(rows -> {
            if(rows > 0) {
                this.messenger.sendMessage(player, "Der Waypoint wurde erfolgreich gelöscht!");
                return;
            }
            this.messenger.sendMessageError(player, "Ein Fehler ist aufgetreten!");
            System.out.println("DELETE rows: " + rows);
        });
    }

    private void infoWaypoint(Waypoint waypoint, CommandSender sender) {
        if(waypoint == null) {
            this.messenger.sendMessageError(sender, "Es konnte kein Waypoint mit dieser Id gefunden werden!");
            return;
        }
        Player creator = sender.getServer().getPlayer(waypoint.getCreator());
        this.messenger.sendMessage(sender, "Name: " + waypoint.getName());
        this.messenger.sendMessage(sender, "Id: " + waypoint.getId());
        this.messenger.sendMessage(sender, "Koordinaten: " + waypoint.getX() + "," + waypoint.getY() + "," + waypoint.getZ());
        this.messenger.sendMessage(sender, "Welt: " + waypoint.getWorld().getName());
        this.messenger.sendMessage(sender, "Sichtbarkeit: " + waypoint.getVisibility().getDisplayString());
        this.messenger.sendMessage(sender, "Item: " + waypoint.getMaterial().toString());
        this.messenger.sendMessage(sender, "Erstellt von: " + (creator != null ? creator.getName() : waypoint.getCreator().toString()));
    }

}
