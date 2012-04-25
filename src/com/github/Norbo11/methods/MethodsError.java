package com.github.Norbo11.methods;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;


public class MethodsError {

    UltimatePoker p;
    public MethodsError(UltimatePoker p)
    {
        this.p = p;
    }

    public void displayHelp(Player player, String command)
    {
        if (command.equals("table"))
        {
            player.sendMessage(p.pluginTag + "UltimatePoker v" + ChatColor.GOLD + p.version + ChatColor.WHITE + " by " + ChatColor.GOLD + "Norbo11" + ChatColor.WHITE + " Help:");
            player.sendMessage(p.pluginTag + "'|' represents 'or' (and alias).");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table list");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table create|new [name]");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table delete|del");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table open|o");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table close|c");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table set [setting] [value]");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table settings");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table listsettings");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table sit [ID] [Buy-in]");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table getup");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table tp [ID]");
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/table start");
        }
        if (command.equals("hand"))
        {
            player.sendMessage(p.pluginTag + ChatColor.GOLD + "/hand");
        }
    }

    public void noPermission(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "You don't have permission to do this.");
    }

    public void usage(Player player, String command)
    {
        if (command.equals("help")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table help or /table help [cmd].");
        if (command.equals("create")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table create|new [name]");
        if (command.equals("list")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table list");
        if (command.equals("open")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table open|o");
        if (command.equals("close")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table close");
        if (command.equals("delete")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table delete");
        if (command.equals("set")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table set [setting] [value]");
        if (command.equals("settings")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table settings.");
        if (command.equals("sit")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table sit [ID] [buy-in]. Check available tables with /table list.");
        if (command.equals("leave")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table leave");
        if (command.equals("getup")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table getup");
        if (command.equals("listsettings")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table listsettings");
        if (command.equals("teleport")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table tp [ID]");
        if (command.equals("start")) player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table start");
    }

    public void notANumber(Player player, String value)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "'" + ChatColor.GOLD + value + ChatColor.RED + "' is not a valid integer!");
    }

    public void notPokerPlayer(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "You are not currently sitting at any table!");
    }
    
    public void notPlayer(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Sorry, this command is only for players!");
    }

    public void notOwnerOfTable(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "You are not an owner of any table!");
    }

    public void alreadyOwnTable(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "You already own a table!");
    }

    public void noSuchTable(Player player, String id)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "There is no table with the ID of " + id + ".");
    }

    public void notWithinBuyinBounds(Player player, String buyin, double minbuy, double maxbuy)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "Buy-in amount " + ChatColor.GOLD + buyin + ChatColor.WHITE + " is not within the table buy-in boundries. Min: " + Double.toString(minbuy) + ". Max: " + Double.toString(maxbuy) + ".");
    }

    public void notOpen(Player player, String id)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "Table ID " + ChatColor.GOLD + id + ChatColor.WHITE  + " is not open!");
    }

    public void tableIsInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "Table is currently in progress!");
    }
}
