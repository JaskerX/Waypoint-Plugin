package de.jaskerx.waypoints.registry;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrivateWaypointsGuisRegistry {

    private final Map<UUID, List<Inventory>> guis;

    public PrivateWaypointsGuisRegistry() {
        this.guis = new HashMap<>();
    }

    public void register(Player player, List<Inventory> inventories) {
        this.guis.put(player.getUniqueId(), inventories);
    }

    public void unregister(Player player) {
        this.guis.remove(player.getUniqueId());
    }

    public List<Inventory> getInventories(Player player) {
        return this.guis.get(player.getUniqueId());
    }

}
