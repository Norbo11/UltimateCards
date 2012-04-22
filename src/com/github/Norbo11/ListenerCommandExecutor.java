package com.github.Norbo11;

import org.bukkit.ChatColor;
<<<<<<< HEAD
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
=======
import org.bukkit.command.*;
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
import org.bukkit.entity.Player;


public class ListenerCommandExecutor implements CommandExecutor {

    UltimatePoker p;
    ListenerCommandExecutor(UltimatePoker p) {
        this.p = p;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (command.getName().equalsIgnoreCase("tables"))
        {
<<<<<<< HEAD
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
            } else p.methodsError.notPlayer(sender);
=======
            p.tableDisplay.displayTables(sender);
            return true;
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        }
        if (command.getName().equalsIgnoreCase("table") || command.getName().equalsIgnoreCase("pk"))
        {
            if (sender instanceof Player)
            {
<<<<<<< HEAD
                Player player = (Player) sender;
=======
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                if (args.length > 0)
                {
                    String action = args[0];
                    if (action.equalsIgnoreCase("help"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.methodsError.displayHelp(player, "table");
                        else if (args.length == 2) p.methodsError.displayHelp(player, args[1]);
                        else p.methodsError.usage(player, "help");
=======
                        if (args.length == 1) p.errorDisplay.displayHelp(sender, "table");
                        else p.errorDisplay.usage(sender, "tablehelp");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("list"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableDisplay.displayTables(player);
                        else p.methodsError.usage(player, "list");
=======
                        if (args.length == 1) p.tableDisplay.displayTables(sender);
                        else p.errorDisplay.usage(sender, "tablelist");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("create") || action.equalsIgnoreCase("new"))
                    {
<<<<<<< HEAD
                        if (args.length == 2) p.tableMethods.createTable(player, args[1]);
                        else p.methodsError.usage(player, "create");
=======
                        if (args.length == 2) p.tableMethods.createTable(sender, args[1]);
                        else p.errorDisplay.usage(sender, "tablecreate");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("del"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableMethods.deleteTable(player);
                         else p.methodsError.usage(player, "delete");
=======
                        if (args.length == 1) p.tableMethods.deleteTable(sender);
                         else p.errorDisplay.usage(sender, "tabledelete");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("open") || action.equalsIgnoreCase("o"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableMethods.openTable(player);
                        else p.methodsError.usage(player, "open");
=======
                        if (args.length == 1) p.tableMethods.openTable(sender);
                        else p.errorDisplay.usage(sender, "tableopen");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("close") || action.equalsIgnoreCase("c"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableMethods.closeTable(player);
                        else p.methodsError.usage(player, "close");
=======
                        if (args.length == 1) p.tableMethods.closeTable(sender);
                        else p.errorDisplay.usage(sender, "tableclose");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }

                    if (action.equalsIgnoreCase("set"))
                    {
<<<<<<< HEAD
                        if (args.length == 3) p.tableMethods.setSetting(player, args[1], args[2]);
                        else p.methodsError.usage(player, "set");
=======
                        if (args.length == 3) p.tableMethods.setSetting(sender, args[1], args[2]);
                        else p.errorDisplay.usage(sender, "tableset");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("listsettings"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableDisplay.availableSettings(player);
                        else p.methodsError.usage(player, "listsettings");
=======
                        if (args.length == 1) p.tableDisplay.availableSettings(sender);
                        else p.errorDisplay.usage(sender, "tablelistsettings");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("settings"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableDisplay.listSettings(player);
                        else p.methodsError.usage(player, "settings");
=======
                        if (args.length == 1) p.tableDisplay.listSettings(sender);
                        else p.errorDisplay.usage(sender, "tablesettings");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("sit"))
                    {
<<<<<<< HEAD
                        if (args.length == 3) p.tableMethods.sitTable(player, args[1], args[2]);
                        else p.methodsError.usage(player, "sit");
=======
                        if (args.length == 3) p.tableMethods.sitTable(sender, args[1], args[2]);
                        else p.errorDisplay.usage(sender, "tablesit");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("getup") || action.equalsIgnoreCase("standup"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableMethods.leaveTable(player);
                        else p.methodsError.usage(player, "getup");
=======
                        if (args.length == 1) p.tableMethods.leaveTable(sender);
                        else p.errorDisplay.usage(sender, "tablegetup");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("tp"))
                    {
<<<<<<< HEAD
                        if (args.length == 2) p.tableMethods.tpToTable(player, args[1]);
                        else p.methodsError.usage(player, "teleport");
=======
                        if (args.length == 2) p.tableMethods.tpToTable(sender, args[1]);
                        else p.errorDisplay.usage(sender, "tableteleport");
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
                        return true;
                    }
                    if (action.equalsIgnoreCase("start"))
                    {
<<<<<<< HEAD
                        if (args.length == 1) p.tableMethods.startTable(player);
                        else p.methodsError.usage(player, "start");
                        return true;
                    }
                    player.sendMessage(p.pluginTag + ChatColor.RED + "No such table command. Check help with /table help.");
                } else p.methodsError.displayHelp(player, "table");
            } else p.methodsError.notPlayer(sender);
=======
                        if (args.length == 2) p.tableMethods.startTable(sender);
                        else p.errorDisplay.usage(sender, "tablestart");
                        return true;
                    }
                    sender.sendMessage(p.pluginTag + ChatColor.RED + "No such table command. Check help with /table help.");
                } else p.errorDisplay.displayHelp(sender, "table");
            } else p.errorDisplay.notPlayer(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        }
        return true;
    }
}
