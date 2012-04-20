package com.github.Norbo11.table;

import java.awt.List;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.Norbo11.UltimatePoker;
import com.google.common.collect.Tables;

public class TableDisplay {

    UltimatePoker p;
    public TableDisplay(UltimatePoker p)
    {
        this.p = p;
    }

    public void displayTables(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + "List of currently created poker tables:");
        if (p.tableMethods.tables.size() > 0)
        {
            for (int i = 0; i < p.tableMethods.tables.size(); i++)
            {
                if (p.tableMethods.tables.get(i).open == true)
                {
                    sender.sendMessage(p.pluginTag + ChatColor.GREEN + "#" + p.tableMethods.tables.get(i).id + " " + p.tableMethods.tables.get(i).name);
                } else sender.sendMessage(p.pluginTag + ChatColor.RED + "#" + p.tableMethods.tables.get(i).id + " " + p.tableMethods.tables.get(i).name);
            }
            sender.sendMessage(p.pluginTag + ChatColor.GREEN + "GREEN = Open. " + ChatColor.RED + "RED = Closed.");
            sender.sendMessage(p.pluginTag + "Use " + ChatColor.GOLD + "/table sit [table ID] [buy-in] " + ChatColor.WHITE + "to join a table.");
        } else sender.sendMessage(p.pluginTag + ChatColor.RED + "No tables available.");
    }

    public void listSettings(CommandSender sender)
    {
        Table table = p.tableMethods.isOwnerOfTable(sender);
        if (table != null)
        {
            sender.sendMessage(p.pluginTag + "Elimination mode: " + ChatColor.GOLD + table.elimination);
            sender.sendMessage(p.pluginTag + "Minimum buy-in: " + ChatColor.GOLD + table.minBuy);
            sender.sendMessage(p.pluginTag + "Maxiumm buy-in: " + ChatColor.GOLD + table.maxBuy);
            sender.sendMessage(p.pluginTag + "Small blind: " + ChatColor.GOLD + table.sb);
            sender.sendMessage(p.pluginTag + "Big blind: " + ChatColor.GOLD + table.bb);
            sender.sendMessage(p.pluginTag + "Ante: " + ChatColor.GOLD + table.ante);
            if (table.dynamicAnteFreq > 0) sender.sendMessage(p.pluginTag + "Dynamic ante frequency: every " + ChatColor.GOLD + table.dynamicAnteFreq + " hands.");
            else sender.sendMessage(p.pluginTag + "Dynamic ante is turned " + ChatColor.GOLD + "OFF");
        } else p.errorDisplay.notOwnerOfTable(sender);
    }

    public void availableSettings(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table set [setting] [value]");
        sender.sendMessage(p.pluginTag + ChatColor.WHITE + "Available settings:");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "elimination [true|false] - " + ChatColor.WHITE + "If true, players can't re-buy.");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "minBuy [number] - " + ChatColor.WHITE + "The mininmum number that players can buy-in (and re-buy) for.");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "maxBuy [number] - " + ChatColor.WHITE + "The maximum number that players can buy-in (and re-buy) for.");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "sb [number] - " + ChatColor.WHITE + "Set the small blind.");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "bb [number] - " + ChatColor.WHITE + "Set the big blind");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "ante [number] - " + ChatColor.WHITE + "Sets the ante.");
        sender.sendMessage(p.pluginTag + ChatColor.GOLD + "dynamicAnteFrequency [number] - " + ChatColor.WHITE + "This decides that every [number] hands, the ante + blinds will increase by themselves. 0 = disabled.");
    }
}
