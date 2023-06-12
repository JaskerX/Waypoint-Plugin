package de.jaskerx.waypoints;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class Messenger {

    private final String prefix;
    private final ChatColor messageColor;
    private final ChatColor errorColor;

    public Messenger(String prefix, ChatColor messageColor, ChatColor errorColor) {
        this.prefix = prefix;
        this.messageColor = messageColor;
        this.errorColor = errorColor;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(this.prefix + " §r" + this.messageColor + message);
    }

    public void sendMessageError(CommandSender sender, String message) {
        sender.sendMessage(this.prefix + " " + this.errorColor + message);
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getMessageColor() {
        return messageColor;
    }

    public ChatColor getErrorColor() {
        return errorColor;
    }

}
