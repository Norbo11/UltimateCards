package com.github.norbo11.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.util.config.PluginConfig;

public class Messages {
    public static String convertColors(String message) {
        message = PluginConfig.getColorTag() + UltimateCards.getPluginTag() + " " + message;

        message = message.replace("&l", ChatColor.BOLD + "");
        message = message.replace("&m", ChatColor.STRIKETHROUGH + "");
        message = message.replace("&n", ChatColor.UNDERLINE + "");
        message = message.replace("&o", ChatColor.ITALIC + "");
        message = message.replace("&r", ChatColor.RESET + "");
        message = message.replace("&k", ChatColor.MAGIC + "");

        message = message.replace("&c", PluginConfig.getColorErrorMessage() + "");
        message = message.replace("&f", PluginConfig.getColorNormalMessage() + "");
        message = message.replace("&6", PluginConfig.getColorHighlight() + "");
        message = message.replaceAll("&([a-f0-9])", "\u00A7$1");
        return message;
    }

    public static void sendMessage(Player player, ArrayList<String> messages) {
        for (String message : messages) {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(convertColors(message));
        }
    }

    @SuppressWarnings("deprecation")
    public static void sendMessage(String player, String message) {
        sendMessage(Bukkit.getPlayer(player), message);
    }

    // Sends a single message to all players that are close to the specified location.
    public static void sendToAllWithinRange(Location location, int range, String message, ArrayList<String> ignore) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getWorld().equals(location.getWorld()) && !ignore.contains(player.getName())) {
                if (player.getLocation().distance(location) <= range) {
                    Messages.sendMessage(player, message);
                }
            }
    }

    // Sends an array of messages to all players that are close to the specified
    // location. The maximum distance is specified in the config
    public static void sendToAllWithinRange(Location location, int range, String[] messages, ArrayList<String> ignore) {
        for (String message : messages) {
            sendToAllWithinRange(location, range, message, ignore);
        }
    }
}