package com.github.Norbo11;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
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
            p.tableDisplay.displayTables(sender);
            return true;
        }
        if (command.getName().equalsIgnoreCase("table") || command.getName().equalsIgnoreCase("pk"))
        {
            if (sender instanceof Player)
            {
                if (args.length > 0)
                {
                    String action = args[0];
                    if (action.equalsIgnoreCase("help"))
                    {
                        if (args.length == 1) p.errorDisplay.displayHelp(sender, "table");
                        else p.errorDisplay.usage(sender, "tablehelp");
                        return true;
                    }
                    if (action.equalsIgnoreCase("list"))
                    {
                        if (args.length == 1) p.tableDisplay.displayTables(sender);
                        else p.errorDisplay.usage(sender, "tablelist");
                        return true;
                    }
                    if (action.equalsIgnoreCase("create") || action.equalsIgnoreCase("new"))
                    {
                        if (args.length == 2) p.tableMethods.createTable(sender, args[1]);
                        else p.errorDisplay.usage(sender, "tablecreate");
                        return true;
                    }
                    if (action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("del"))
                    {
                        if (args.length == 1) p.tableMethods.deleteTable(sender);
                         else p.errorDisplay.usage(sender, "tabledelete");
                        return true;
                    }
                    if (action.equalsIgnoreCase("open") || action.equalsIgnoreCase("o"))
                    {
                        if (args.length == 1) p.tableMethods.openTable(sender);
                        else p.errorDisplay.usage(sender, "tableopen");
                        return true;
                    }
                    if (action.equalsIgnoreCase("close") || action.equalsIgnoreCase("c"))
                    {
                        if (args.length == 1) p.tableMethods.closeTable(sender);
                        else p.errorDisplay.usage(sender, "tableclose");
                        return true;
                    }

                    if (action.equalsIgnoreCase("set"))
                    {
                        if (args.length == 3) p.tableMethods.setSetting(sender, args[1], args[2]);
                        else p.errorDisplay.usage(sender, "tableset");
                        return true;
                    }
                    if (action.equalsIgnoreCase("listsettings"))
                    {
                        if (args.length == 1) p.tableDisplay.availableSettings(sender);
                        else p.errorDisplay.usage(sender, "tablelistsettings");
                        return true;
                    }
                    if (action.equalsIgnoreCase("settings"))
                    {
                        if (args.length == 1) p.tableDisplay.listSettings(sender);
                        else p.errorDisplay.usage(sender, "tablesettings");
                        return true;
                    }
                    if (action.equalsIgnoreCase("sit"))
                    {
                        if (args.length == 3) p.tableMethods.sitTable(sender, args[1], args[2]);
                        else p.errorDisplay.usage(sender, "tablesit");
                        return true;
                    }
                    if (action.equalsIgnoreCase("getup") || action.equalsIgnoreCase("standup"))
                    {
                        if (args.length == 1) p.tableMethods.leaveTable(sender);
                        else p.errorDisplay.usage(sender, "tablegetup");
                        return true;
                    }
                    if (action.equalsIgnoreCase("tp"))
                    {
                        if (args.length == 2) p.tableMethods.tpToTable(sender, args[1]);
                        else p.errorDisplay.usage(sender, "tableteleport");
                        return true;
                    }
                    if (action.equalsIgnoreCase("start"))
                    {
                        if (args.length == 2) p.tableMethods.startTable(sender);
                        else p.errorDisplay.usage(sender, "tablestart");
                        return true;
                    }
                    sender.sendMessage(p.pluginTag + ChatColor.RED + "No such table command. Check help with /table help.");
                } else p.errorDisplay.displayHelp(sender, "table");
            } else p.errorDisplay.notPlayer(sender);
        }
        return true;
    }
}
