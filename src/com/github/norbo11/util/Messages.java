package com.github.norbo11.util;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;

public class Messages
{
    public Messages(UltimateCards p)
    {
        Messages.p = p;

        // Uncoment if you want delays between messages
        /*
         * Bukkit.getScheduler().scheduleAsyncRepeatingTask(p, new Runnable(){
         * 
         * @Override public void run() { String message = null; Location location = null;
         * 
         * for (Entry<String, Location> entry : messageBuffer.entrySet()) { message = entry.getKey(); location = entry.getValue(); break; }
         * 
         * for (Player player : Bukkit.getOnlinePlayers()) { if (player.getWorld().equals(location.getWorld())) { if (player.getLocation().distance(location) <= UltimateCards.getPluginConfig().getChatRange()) { Messages.sendMessage(player, message); messageBuffer.remove(message); } } } }
         * 
         * }, 0L, 10L);
         */
    }

    // public static HashMap<String, Location> messageBuffer = new HashMap<String, Location>();

    public static UltimateCards p;

    public static String convertColors(String message)
    {
        message = UltimateCards.getPluginConfig().getColorTag() + UltimateCards.getPluginTag() + " " + message;

        message = message.replace("&l", ChatColor.BOLD + "");
        message = message.replace("&m", ChatColor.STRIKETHROUGH + "");
        message = message.replace("&n", ChatColor.UNDERLINE + "");
        message = message.replace("&o", ChatColor.ITALIC + "");
        message = message.replace("&r", ChatColor.RESET + "");
        message = message.replace("&k", ChatColor.MAGIC + "");

        message = message.replace("&c", UltimateCards.getPluginConfig().getColorErrorMessage() + "");
        message = message.replace("&f", UltimateCards.getPluginConfig().getColorNormalMessage() + "");
        message = message.replace("&6", UltimateCards.getPluginConfig().getColorHighlight() + "");
        message = message.replaceAll("&([a-f0-9])", "\u00A7$1");
        return message;
    }

    public static void sendMessage(Player player, ArrayList<String> messages)
    {
        for (String message : messages)
        {
            sendMessage(player, message);
        }
    }

    public static void sendMessage(Player player, String message)
    {
        player.sendMessage(convertColors(message));
    }

    public static void sendMessage(String player, String message)
    {
        sendMessage(Bukkit.getPlayer(player), message);
    }

    // Sends a single message to all players that are close to the specified
    // location. The maximum distance is specified in the config
    public static void sendToAllWithinRange(Location location, String message)
    {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.getWorld().equals(location.getWorld())) if (player.getLocation().distance(location) <= UltimateCards.getPluginConfig().getChatRange())
            {
                Messages.sendMessage(player, message);
            }
    }

    // Sends an array of messages to all players that are close to the specified
    // location. The maximum distance is specified in the config
    public static void sendToAllWithinRange(Location location, String[] messages)
    {
        for (String message : messages)
        {
            sendToAllWithinRange(location, message);
        }
    }
}