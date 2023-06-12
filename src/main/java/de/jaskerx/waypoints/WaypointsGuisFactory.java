package de.jaskerx.waypoints;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class WaypointsGuisFactory {

    public CompletableFuture<List<Inventory>> createGui(Player player, Collection<Waypoint> waypoints, boolean addPrivateWaypointsItem) {
        return CompletableFuture.supplyAsync(() -> {
            List<Inventory> inventories = new ArrayList<>();

            Inventory currentInventory = Bukkit.createInventory(player, 54, "Keine Waypoints verfügbar");
            for(int i = 0; i < waypoints.size(); i++) {
                if(i % 45 == 0) {
                    if(i > 0) {
                        inventories.add(completeInventory(currentInventory, (i - 1) / 45, (waypoints.size() / 45) + 1, addPrivateWaypointsItem));
                    }
                    currentInventory = Bukkit.createInventory(player, 54, "Waypoints Seite " + (i / 45 + 1));
                }
                Waypoint waypoint = (Waypoint) waypoints.toArray()[i];
                currentInventory.setItem(i % 45, this.createWaypointItem(waypoint));
            }
            if(waypoints.size() % 45 > 0 || inventories.size() == 0) {
                inventories.add(completeInventory(currentInventory, (waypoints.size() - 1) / 45, (waypoints.size() / 45) + 1, addPrivateWaypointsItem));
            }
            return inventories;
        });
    }

    public ItemStack createWaypointItem (Waypoint waypoint) {
        ItemStack item = new ItemStack(waypoint.getMaterial());
        List<String> loreList = new ArrayList<>();
        loreList.add("x = " + waypoint.getX());
        loreList.add("y = " + waypoint.getY());
        loreList.add("z = " + waypoint.getZ());
        loreList.add("Erstellt von " + Bukkit.getOfflinePlayer(waypoint.getCreator()).getName());
        loreList.add("Welt: " + waypoint.getWorld().getName());
        loreList.add("Id: " + waypoint.getId());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(waypoint.getName());
        meta.setLore(loreList);
        item.setItemMeta(meta);
        return item;
    }

    public Inventory completeInventory(Inventory inventory, int page, int pages, boolean addPrivateWaypointsItem) {

        ItemStack buttonPageForward = this.getSkullFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTU2YTM2MTg0NTllNDNiMjg3YjIyYjdlMjM1ZWM2OTk1OTQ1NDZjNmZjZDZkYzg0YmZjYTRjZjMwYWI5MzExIn19fQ==");
        ItemStack buttonPageBackwards = this.getSkullFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2RjOWU0ZGNmYTQyMjFhMWZhZGMxYjViMmIxMWQ4YmVlYjU3ODc5YWYxYzQyMzYyMTQyYmFlMWVkZDUifX19");
        ItemStack buttonOpenPrivateWps = this.getSkullFromValue("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFkOTQzZDA2MzM0N2Y5NWFiOWU5ZmE3NTc5MmRhODRlYzY2NWViZDIyYjA1MGJkYmE1MTlmZjdkYTYxZGIifX19");
        ItemMeta metaFore = buttonPageForward.getItemMeta();
        metaFore.setDisplayName("Nächste Seite");
        ItemMeta metaBack = buttonPageBackwards.getItemMeta();
        metaBack.setDisplayName("Vorherige Seite");
        ItemMeta metaPrivate = buttonOpenPrivateWps.getItemMeta();
        metaPrivate.setDisplayName("Private Waypoints öffnen");
        buttonPageForward.setItemMeta(metaFore);
        buttonPageBackwards.setItemMeta(metaBack);
        buttonOpenPrivateWps.setItemMeta(metaPrivate);

        if(pages > 1) {
            if (page == 0) {
                inventory.setItem(53, buttonPageForward);
            } else if (page == pages - 1) {
                inventory.setItem(52, buttonPageBackwards);
            } else {
                inventory.setItem(52, buttonPageBackwards);
                inventory.setItem(53, buttonPageForward);
            }
        }
        if(addPrivateWaypointsItem) {
            inventory.setItem(45, buttonOpenPrivateWps);
        }

        return inventory;
    }

    public ItemStack getSkullFromValue(String value) {

        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        if(value == null || value.equals("")) {
            return head;
        }

        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", value));
        try {
            Field field = meta.getClass().getDeclaredField("profile");
            field.setAccessible(true);
            field.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        head.setItemMeta(meta);

        return head;
    }

}
