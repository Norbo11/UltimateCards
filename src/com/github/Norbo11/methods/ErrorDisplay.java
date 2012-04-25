package com.github.Norbo11.methods;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/hand");
            sender.sendMessage(p.pluginTag + ChatColor.GOLD + "/hand bet [amount[]");
        }
    }

    public void noPermission(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "You don't have permission to do this.");
    }

    public void usage(CommandSender sender, String command)
    {
        //Help
        if (command.equals("tablehelp")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table help. Displays all table commands.");
        if (command.equals("handhelp")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /hand help. Displays all hand commands.");
        
        //Table
        if (command.equals("create")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table create|new [name]. Creates a new table.");
        if (command.equals("list")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table list. List all created tables.");
        if (command.equals("open")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table open|o. Opens the table that you currently own.");
        if (command.equals("close")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table close. Closes the table that you currently own.");
        if (command.equals("delete")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table delete. Deletes the table that you currently own.");
        if (command.equals("set")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table set [setting] [value]. Sets the setting to the value specified in the table that you currently own. Use /table listsettings to list all possible settings.");
        if (command.equals("settings")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table settings. Lists the current table settings.");
        if (command.equals("sit")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table sit [ID] [buy-in]. Sits at table [ID] and teleports you there. Check available tables with /table list.");
        if (command.equals("leave")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table leave. Gets up from your current table.");
        if (command.equals("listsettings")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table listsettings. Lists all available settings for /table set.");
        if (command.equals("teleport")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table tp [ID]. Teleports you to the table with the respective ID.");
        
        //Hand
        if (command.equals("bet")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /hand bet [amount]. Bets an amount of money.");
        if (command.equals("fold")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /hand fold. Folds your hand.");
        if (command.equals("call")) sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /hand call. Calls the latest bet.");
    }

    public void notANumber(CommandSender sender, String value)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "'" + ChatColor.GOLD + value + ChatColor.RED + "' is not a valid number!");
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

    public void notYourTurn(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "It's not your turn to act!");
    }
}
