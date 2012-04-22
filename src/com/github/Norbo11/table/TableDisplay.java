package com.github.Norbo11.table;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class TableDisplay {

    UltimatePoker p;
    public TableDisplay(UltimatePoker p)
    {
        this.p = p;
    }

    public void displayTables(Player player)
    {
        player.sendMessage(p.pluginTag + "List of currently created poker tables:");
        if (p.tableMethods.tables.size() > 0)
        {
            for (int i = 0; i < p.tableMethods.tables.size(); i++)
            {
                if (p.tableMethods.tables.get(i).open == true)
                {
                    player.sendMessage(p.pluginTag + ChatColor.GREEN + "#" + p.tableMethods.tables.get(i).id + " " + p.tableMethods.tables.get(i).name);
                } else player.sendMessage(p.pluginTag + ChatColor.RED + "#" + p.tableMethods.tables.get(i).id + " " + p.tableMethods.tables.get(i).name);
            }
            player.sendMessage(p.pluginTag + ChatColor.GREEN + "GREEN = Open. " + ChatColor.RED + "RED = Closed.");
            player.sendMessage(p.pluginTag + "Use " + ChatColor.GOLD + "/table sit [table ID] [buy-in] " + ChatColor.WHITE + "to join a table.");
        } else player.sendMessage(p.pluginTag + ChatColor.RED + "No tables available.");
    }

    public void listSettings(Player player)
    {
        Table table = p.tableMethods.isOwnerOfTable(player);
        if (table != null)
        {
            player.sendMessage(p.pluginTag + "Elimination mode: " + ChatColor.GOLD + table.elimination);
            player.sendMessage(p.pluginTag + "Minimum buy-in: " + ChatColor.GOLD + table.minBuy);
            player.sendMessage(p.pluginTag + "Maxiumm buy-in: " + ChatColor.GOLD + table.maxBuy);
            player.sendMessage(p.pluginTag + "Small blind: " + ChatColor.GOLD + table.sb);
            player.sendMessage(p.pluginTag + "Big blind: " + ChatColor.GOLD + table.bb);
            player.sendMessage(p.pluginTag + "Ante: " + ChatColor.GOLD + table.ante);
            if (table.dynamicAnteFreq > 0) player.sendMessage(p.pluginTag + "Dynamic ante frequency: every " + ChatColor.GOLD + table.dynamicAnteFreq + " hands.");
            else player.sendMessage(p.pluginTag + "Dynamic ante is turned " + ChatColor.GOLD + "OFF");
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void availableSettings(Player player)
    {
        player.sendMessage(p.pluginTag + ChatColor.RED + "Usage: /table set [setting] [value]");
        player.sendMessage(p.pluginTag + ChatColor.WHITE + "Available settings:");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "elimination [true|false] - " + ChatColor.WHITE + "If true, players can't re-buy.");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "minBuy [number] - " + ChatColor.WHITE + "The mininmum number that players can buy-in (and re-buy) for.");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "maxBuy [number] - " + ChatColor.WHITE + "The maximum number that players can buy-in (and re-buy) for.");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "sb [number] - " + ChatColor.WHITE + "Set the small blind.");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "bb [number] - " + ChatColor.WHITE + "Set the big blind");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "ante [number] - " + ChatColor.WHITE + "Sets the ante.");
        player.sendMessage(p.pluginTag + ChatColor.GOLD + "dynamicAnteFrequency [number] - " + ChatColor.WHITE + "This decides that every [number] hands, the ante + blinds will increase by themselves. 0 = disabled.");
    }
}
