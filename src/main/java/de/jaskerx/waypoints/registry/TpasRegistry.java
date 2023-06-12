package de.jaskerx.waypoints.registry;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TpasRegistry {

    private final Map<UUID, UUID> tpas;

    public TpasRegistry() {
        this.tpas = new HashMap<>();
    }

    public void register(Player player, UUID targetUUID) {
        this.tpas.put(player.getUniqueId(), targetUUID);
    }

    public void unregister(UUID uuid) {
        this.tpas.remove(uuid);
    }

    public Map<UUID, UUID> getTpas() {
        return tpas;
    }

}
