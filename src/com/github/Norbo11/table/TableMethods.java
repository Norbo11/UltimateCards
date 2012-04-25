package com.github.Norbo11.table;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.cards.PokerPlayer;


public class TableMethods {

    UltimatePoker p;
    public TableMethods(UltimatePoker p)
    {
        this.p = p;
    }

    public List<Table> tables = new ArrayList<Table>();

    public void createTable(Player player, String tableName)
    {
        if (isOwnerOfTable(player) == null)
        {
            PokerPlayer pokerPlayer = p.methodsMisc.isAPokerPlayer(player);
            if (pokerPlayer == null)
            {
                Table newTable = new Table(player, tableName, tables.size(), player.getLocation(), p);
                tables.add(newTable);
                player.sendMessage(p.pluginTag + "Created table named " + ChatColor.GOLD + "'" + tableName + "'" + ChatColor.WHITE + ", ID " + ChatColor.GOLD + Integer.toString(tables.size()-1) + ChatColor.WHITE + "!");
                player.sendMessage(p.pluginTag + "Edit the rules of your table with " + ChatColor.GOLD + "'/table set'" + ChatColor.WHITE + ", and open it with " + ChatColor.GOLD + "'/table open'" + ChatColor.WHITE + " when ready!");
            } else player.sendMessage(p.pluginTag + "You are currently playing at a table called '" + ChatColor.GOLD + pokerPlayer.table + ChatColor.WHITE + "', and can't make another one!");
        } else p.methodsError.alreadyOwnTable(player);
    }

    public void deleteTable(Player player)
    {
        Table table = isOwnerOfTable(player);
        if (table != null)
        {
            tables.remove(table.id);
            player.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " has been deleted!");
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void openTable(Player player)
    {
        Table table = isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == false)
            {
                table.open = true;
                player.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is now open! Players can now join!");
            } else player.sendMessage(p.pluginTag + ChatColor.RED + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.RED + "', ID #" + ChatColor.GOLD + table.id + ChatColor.RED + " is already open!");
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void closeTable(Player player)
    {
        Table table = isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == true)
            {
                table.open = false;
                player.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is now closed! Players now can't join!");
            } else player.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is already closed!");
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void sitTable(Player player, String id, String buyin)
    {
        if (p.methodsMisc.isInteger(id))
        {
            Table table = isATable(Integer.parseInt(id));
            if (table != null)
            {
                if (table.open == true)
                {
                    if (p.methodsMisc.isInteger(buyin))
                    {
                        PokerPlayer pokerPlayer = p.methodsMisc.isAPokerPlayer(player);
                        if (pokerPlayer == null)
                        {
                            int Buyin = Integer.parseInt(buyin);
                            if (Buyin >= table.minBuy && Buyin <= table.maxBuy)
                            {
                                table.players.add(new PokerPlayer(player, table));
                                player.teleport(table.location);
                                player.sendMessage(p.pluginTag + "You have sat at table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ".");
                            } else p.methodsError.notWithinBuyinBounds(player, buyin, table.minBuy, table.maxBuy);
                        } else player.sendMessage(p.pluginTag + "You are already sat at table '" + ChatColor.GOLD + pokerPlayer.table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + pokerPlayer.table.id + ChatColor.WHITE + "!");
                    } else p.methodsError.notANumber(player, buyin);
                } else p.methodsError.notOpen(player, id);
            } else p.methodsError.noSuchTable(player, id);
        } else p.methodsError.notANumber(player, id);
    }

    public void leaveTable(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsMisc.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            player.teleport(pokerPlayer.startLocation);
            pokerPlayer.table.players.remove(pokerPlayer);
            player.sendMessage(p.pluginTag + "You have left table '" + ChatColor.GOLD + pokerPlayer.table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + pokerPlayer.table.id + ChatColor.WHITE + ".");
        } else player.sendMessage(p.pluginTag + ChatColor.RED + "You are not sitting at any table! Sit with /table sit [ID].");
    }
    
    public void startTable(Player player)
    {
        Table table = isOwnerOfTable(player);
        if (table != null)
        {
            if (table.inProgress != true)
            {
                player.sendMessage(p.pluginTag + "You have started the game at table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ".");
                table.setInProgress(true);
            } else p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void tpToTable(Player player, String id)
    {
        if (p.methodsMisc.isInteger(id))
        {
            Table table = isATable(Integer.parseInt(id));
            if (table != null)
            {
                player.teleport(table.location);
                player.sendMessage(p.pluginTag + "You have teleported to table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ". Sit down with /table sit [ID]");
            } else p.methodsError.noSuchTable(player, id);
        } else p.methodsError.notANumber(player, id);
    }

    public Table isATable(int ID)
    {
        for (Table table : tables)
        {
            if (table.id == ID && table != null)
            return table;
        }
        return null;
    }

    public Table isAtATable(CommandSender sender)
    {
        Player player = (Player) sender;
        for (int i = 0; i < tables.size(); i++)
        {
            if (tables.get(i).players.contains(player) == true)
            return tables.get(i);
        }
        return null;
    }

    public Table isOwnerOfTable(Player player)
    {
        for (Table table : tables)
        {
            if (table.owner == player)
            return table;
        }
        return null;
    }

    public void setSetting(Player player, String setting, String value)
    {
        Table table = isOwnerOfTable(player);
        if (table != null)
        {
            switch (setting)
            {
                case "elimination": table.setElimination(player, value); break;
                case "minBuy": table.setNumberValue(player, "minBuy", value); break;
                case "maxBuy": table.setNumberValue(player, "maxBuy", value); break;
                case "sb": table.setNumberValue(player, "sb", value); break;
                case "bb": table.setNumberValue(player, "bb", value); break;
                case "ante": table.setNumberValue(player, "ante", value); break;
                case "dynamicAnteFrequency": table.setNumberValue(player, "dynamicAnteFrequency", value); break;
                default: player.sendMessage(p.pluginTag + ChatColor.RED + "Invalid setting. Check available settings with /table listsettings");
            }
        }
    }
}
