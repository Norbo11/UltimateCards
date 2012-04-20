package com.github.Norbo11.methods;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.Norbo11.UltimatePoker;


public class ErrorDisplay {

    UltimatePoker p;
    public ErrorDisplay(UltimatePoker p)
    {
        this.p = p;
    }

    public void displayHelp(CommandSender sender, String command)
    {
        if (command.equals("table"))
        {
            sender.sendMessage(p.pluginTag + "UltimatePoker v" + ChatColor.GOLD + p.version + ChatColor.WHITE + " by " + ChatColor.GOLD + "Norbo11" + ChatColor.WHITE + " Help:");
            sender.sendMessage(p.pluginTag + "'|' represents 'or' (and alias).");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table list");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table create|new [name]");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table delete|del");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table open|o");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table close|c");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table set [setting] [value]");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table settings");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table listsettings");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table sit [ID] [Buy-in]");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table getup");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/table tp [ID]");
        }
        if (command.equals("hand"))
        {

        }
    }

    public void noPermission(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You don't have permission to do this.");
    }

    public void usage(CommandSender sender, String command)
    {
        if (command.equals("tablehelp")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table help");
        if (command.equals("tablecreate")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table create|new [name]");
        if (command.equals("tablelist")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table list");
        if (command.equals("tableopen")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table open|o");
        if (command.equals("tableclose")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table close");
        if (command.equals("tabledelete")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table delete");
        if (command.equals("tableset")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table set [setting] [value]");
        if (command.equals("tablesettings")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table settings.");
        if (command.equals("tablesit")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table sit [ID] [buy-in]. Check available tables with /table list.");
        if (command.equals("tableleave")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table leave");
        if (command.equals("tablegetup")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table getup");
        if (command.equals("tablelistsettings")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table listsettings");
        if (command.equals("tableteleport")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table tp [ID]");
    }

    public void notANumber(CommandSender sender, String value)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "'" + ChatColor.GOLD + value + ChatColor.RED + "' is not a valid integer!");
    }

    public void notPlayer(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Sorry, this command is only for players!");
    }

    public void notOwnerOfTable(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You are not an owner of any table!");
    }

    public void alreadyOwnTable(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You already own a table!");
    }

    public void noSuchTable(CommandSender sender, String id)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "There is no table with the ID of " + id + ".");
    }

    public void notWithinBuyinBounds(CommandSender sender, String buyin, int minbuy, int maxbuy)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Buy-in amount " + ChatColor.GOLD + buyin + ChatColor.WHITE + " is not within the table buy-in boundries. Min: " + Integer.toString(minbuy) + ". Max: " + Integer.toString(maxbuy) + ".");
    }

    public void notOpen(CommandSender sender, String id)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Table ID " + ChatColor.GOLD + id + ChatColor.WHITE  + " is not open!");
    }

    public void tableIsInProgress(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Table is currently in progress!");
    }
}
