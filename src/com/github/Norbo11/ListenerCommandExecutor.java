package com.github.Norbo11;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;


public class ListenerCommandExecutor implements CommandExecutor {

    UltimatePoker p;
    ListenerCommandExecutor(UltimatePoker p) {
        this.p = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("tables"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                p.tableDisplay.displayTables(player);
                return true;
            } else p.methodsError.notPlayer(sender);
        }
        if (command.getName().equalsIgnoreCase("hand"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (args.length == 0) p.methodsHand.displayHand(player);
                else p.methodsError.usage(player, "hand");
                return true;
            } else { p.methodsError.notPlayer(sender); return true; }
        }
        if (command.getName().equalsIgnoreCase("table"))
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                if (args.length > 0)
                {
                    String action = args[0];
                    if (action.equalsIgnoreCase("help"))
                    {
                        if (args.length == 1) p.methodsError.displayHelp(player, "table");
                        else if (args.length == 2) p.methodsError.displayHelp(player, args[1]);
                        else p.methodsError.usage(player, "help");
                    }
                    if (action.equalsIgnoreCase("list"))
                    {
                        if (args.length == 1) p.tableDisplay.displayTables(player);
                        else p.methodsError.usage(player, "list");
                        return true;
                    }
                    if (action.equalsIgnoreCase("create") || action.equalsIgnoreCase("new"))
                    {
                        if (args.length == 2) p.tableMethods.createTable(player, args[1]);
                        else p.methodsError.usage(player, "create");
                        return true;
                    }
                    if (action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("del"))
                    {
                        if (args.length == 1) p.tableMethods.deleteTable(player);
                        else p.methodsError.usage(player, "delete");
                        return true;
                    }
                    if (action.equalsIgnoreCase("open") || action.equalsIgnoreCase("o"))
                    {
                        if (args.length == 1) p.tableMethods.openTable(player);
                        else p.methodsError.usage(player, "open");
                        return true;
                    }
                    if (action.equalsIgnoreCase("close") || action.equalsIgnoreCase("c"))
                    {
                        if (args.length == 1) p.tableMethods.closeTable(player);
                        else p.methodsError.usage(player, "close");
                        return true;
                    }

                    if (action.equalsIgnoreCase("set"))
                    {
                        if (args.length == 3) p.tableMethods.setSetting(player, args[1], args[2]);
                        else p.methodsError.usage(player, "set");
                        return true;
                    }
                    if (action.equalsIgnoreCase("listsettings"))
                    {
                        if (args.length == 1) p.tableDisplay.availableSettings(player);
                        else p.methodsError.usage(player, "listsettings");
                        return true;
                    }
                    if (action.equalsIgnoreCase("settings"))
                    {
                        if (args.length == 1) p.tableDisplay.listSettings(player);
                        else p.methodsError.usage(player, "settings");
                        return true;
                    }
                    if (action.equalsIgnoreCase("sit"))
                    {
                        if (args.length == 3) p.tableMethods.sitTable(player, args[1], args[2]);
                        else p.methodsError.usage(player, "sit");
                        return true;
                    }
                    if (action.equalsIgnoreCase("getup") || action.equalsIgnoreCase("standup"))
                    {
                        if (args.length == 1) p.tableMethods.leaveTable(player);
                        else p.methodsError.usage(player, "getup");
                        return true;
                    }
                    if (action.equalsIgnoreCase("tp"))
                    {
                        if (args.length == 2) p.tableMethods.tpToTable(player, args[1]);
                        else p.methodsError.usage(player, "teleport");
                        return true;
                    }
                    if (action.equalsIgnoreCase("start"))
                    {
                        if (args.length == 1) p.tableMethods.startTable(player);
                        else p.methodsError.usage(player, "start");
                        return true;
                    }
                    player.sendMessage(p.pluginTag + ChatColor.RED + "No such table command. Check help with /table help.");
                    return true;
                } else p.methodsError.displayHelp(player, "table");
            } else p.errorDisplay.notPlayer(sender);
        }
        return true;
    }
}
