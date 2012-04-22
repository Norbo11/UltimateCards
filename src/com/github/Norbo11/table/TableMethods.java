package com.github.Norbo11.table;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.List;

import org.bukkit.ChatColor;
=======
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
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

<<<<<<< HEAD
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
=======
    public void createTable(CommandSender sender, String tableName)
    {
        if (sender instanceof Player)
        {
            if (isOwnerOfTable(sender) == null)
            {
                Table table = isAtATable(sender);
                if (table == null)
                {
                    Player player = (Player) sender;
                    Table newTable = new Table(player, tableName, tables.size(), player.getLocation(), p);
                    tables.add(newTable);
                    sender.sendMessage(p.pluginTag + "Created table named " + ChatColor.GOLD + "'" + tableName + "'" + ChatColor.WHITE + ", ID " + ChatColor.GOLD + Integer.toString(tables.size()-1) + ChatColor.WHITE + "!");
                    sender.sendMessage(p.pluginTag + "Edit the rules of your table with " + ChatColor.GOLD + "'/table set'" + ChatColor.WHITE + ", and open it with " + ChatColor.GOLD + "'/table open'" + ChatColor.WHITE + " when ready!");
                } else sender.sendMessage(p.pluginTag + "You are currently playing at a table called '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', and can't make another one!");
            } else p.errorDisplay.alreadyOwnTable(sender);
        } else p.errorDisplay.notPlayer(sender);
    }

    public void deleteTable(CommandSender sender)
    {
        Table table = isOwnerOfTable(sender);
        if (table != null)
        {
            tables.remove(table.id);
            sender.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " has been deleted!");
        } else p.errorDisplay.notOwnerOfTable(sender);
    }

    public void openTable(CommandSender sender)
    {
        Table table = isOwnerOfTable(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        if (table != null)
        {
            if (table.open == false)
            {
                table.open = true;
<<<<<<< HEAD
                player.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is now open! Players can now join!");
            } else player.sendMessage(p.pluginTag + ChatColor.RED + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.RED + "', ID #" + ChatColor.GOLD + table.id + ChatColor.RED + " is already open!");
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void closeTable(Player player)
    {
        Table table = isOwnerOfTable(player);
=======
                sender.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is now open! Players can now join!");
            } else sender.sendMessage(p.pluginTag + ChatColor.RED + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.RED + "', ID #" + ChatColor.GOLD + table.id + ChatColor.RED + " is already open!");
        } else p.errorDisplay.notOwnerOfTable(sender);
    }

    public void closeTable(CommandSender sender)
    {
        Table table = isOwnerOfTable(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        if (table != null)
        {
            if (table.open == true)
            {
                table.open = false;
<<<<<<< HEAD
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
            player.teleport(p.methodsMisc.isAPokerPlayer(player).startLocation);
            pokerPlayer.table.players.remove(player);
            player.sendMessage(p.pluginTag + "You have left table '" + ChatColor.GOLD + pokerPlayer.table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + pokerPlayer.table.id + ChatColor.WHITE + ".");
        } else player.sendMessage(p.pluginTag + ChatColor.RED + "You are not sitting at any table! Sit with /table sit [ID].");
    }

    public void startTable(Player player)
    {
        Table table = isOwnerOfTable(player);
=======
                sender.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is now closed! Players now can't join!");
            } else sender.sendMessage(p.pluginTag + "Table ID '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + " is already closed!");
        } else p.errorDisplay.notOwnerOfTable(sender);
    }

    public void sitTable(CommandSender sender, String id, String buyin)
    {
        if (sender instanceof Player)
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
                            Table atATable = isAtATable(sender);
                            if (isAtATable(sender) == null)
                            {
                                int Buyin = Integer.parseInt(buyin);
                                if (Buyin >= table.minBuy && Buyin <= table.maxBuy)
                                {
                                    Player player = (Player) sender;
                                    table.players.put(player, new PokerPlayer(player));
                                    player.teleport(table.location);
                                    sender.sendMessage(p.pluginTag + "You have sat at table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ".");
                                } else p.errorDisplay.notWithinBuyinBounds(sender, buyin, table.minBuy, table.maxBuy);
                            } else sender.sendMessage(p.pluginTag + "You are already sat at table '" + ChatColor.GOLD + atATable.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + atATable.id + ChatColor.WHITE + "!");
                        } else p.errorDisplay.notANumber(sender, buyin);
                    } else p.errorDisplay.notOpen(sender, id);
                } else p.errorDisplay.noSuchTable(sender, id);
            } else p.errorDisplay.notANumber(sender, id);
        } else p.errorDisplay.notPlayer(sender);
    }

    public void leaveTable(CommandSender sender)
    {
        Player player = (Player) sender;
        Table table = isAtATable(sender);
        if (table != null)
        {
            player.teleport(table.players.get(player).startLocation);
            table.players.remove(player);
            sender.sendMessage(p.pluginTag + "You have left table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ".");
        } else sender.sendMessage(p.pluginTag + ChatColor.RED + "You are not sitting at any table! Sit with /table sit [ID].");
    }

    public void startTable(CommandSender sender)
    {
        Table table = isOwnerOfTable(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        if (table != null)
        {
            if (table.inProgress != true)
            {
<<<<<<< HEAD
                player.sendMessage(p.pluginTag + "You have started the game at table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ".");
                table.setInProgress(true);
            } p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void tpToTable(Player player, String id)
    {
        if (player instanceof Player)
=======
                table.setInProgress();
                sender.sendMessage(p.pluginTag + "You have started the game at table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ".");
            } p.errorDisplay.tableIsInProgress(sender);
        } else p.errorDisplay.notOwnerOfTable(sender);
    }

    public void tpToTable(CommandSender sender, String id)
    {
        if (sender instanceof Player)
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        {
            if (p.methodsMisc.isInteger(id))
            {
                Table table = isATable(Integer.parseInt(id));
                if (table != null)
                {
<<<<<<< HEAD
                    player.teleport(table.location);
                    player.sendMessage(p.pluginTag + "You have teleported to table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ". Sit down with /table sit [ID]");
                } else p.methodsError.noSuchTable(player, id);
            } else p.methodsError.notANumber(player, id);
        } else p.methodsError.notPlayer(player);
=======
                    Player player = (Player) sender;
                    player.teleport(table.location);
                    sender.sendMessage(p.pluginTag + "You have teleported to table '" + ChatColor.GOLD + table.name + ChatColor.WHITE + "', ID #" + ChatColor.GOLD + table.id + ChatColor.WHITE + ". Sit down with /table sit [ID]");
                } else p.errorDisplay.noSuchTable(sender, id);
            } else p.errorDisplay.notANumber(sender, id);
        } else p.errorDisplay.notPlayer(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
    }

    public Table isATable(int ID)
    {
        for (int i = 0; i < tables.size(); i++)
        {
            if (tables.get(i).id == ID)
            return tables.get(i);
        }
        return null;
    }

<<<<<<< HEAD
    public Table isOwnerOfTable(Player player)
    {
=======
    public Table isAtATable(CommandSender sender)
    {
        Player player = (Player) sender;
        for (int i = 0; i < tables.size(); i++)
        {
            if (tables.get(i).players.containsKey(player) == true)
            return tables.get(i);
        }
        return null;
    }

    public Table isOwnerOfTable(CommandSender sender)
    {
        Player player = (Player) sender;
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        for (int i = 0; i < tables.size(); i++)
        {
            if (tables.get(i).owner == player)
            return tables.get(i);
        }
        return null;
    }

<<<<<<< HEAD
    public void setSetting(Player player, String setting, String value)
    {
        Table table = isOwnerOfTable(player);
=======
    public void setSetting(CommandSender sender, String setting, String value)
    {
        Table table = isOwnerOfTable(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
        if (table != null)
        {
            switch (setting)
            {
<<<<<<< HEAD
                case "elimination": table.setElimination(player, value); break;
                case "minBuy": table.setMinBuy(player, value); break;
                case "maxBuy": table.setMaxBuy(player, value); break;
                case "sb": table.setSB(player, value); break;
                case "bb": table.setBB(player, value); break;
                case "ante": table.setAnte(player, value); break;
                case "dynamicAnteFrequency": table.setDynamicAnteFreq(player, value); break;
                default: player.sendMessage(p.pluginTag + ChatColor.RED + "Invalid setting. Check available settings with /table listsettings");
            }
        } else p.methodsError.notOwnerOfTable(player);
=======
                case "elimination": table.setElimination(sender, value); break;
                case "minBuy": table.setMinBuy(sender, value); break;
                case "maxBuy": table.setMaxBuy(sender, value); break;
                case "sb": table.setSB(sender, value); break;
                case "bb": table.setBB(sender, value); break;
                case "ante": table.setAnte(sender, value); break;
                case "dynamicAnteFrequency": table.setDynamicAnteFreq(sender, value); break;
                default: sender.sendMessage(p.pluginTag + ChatColor.RED + "Invalid setting. Check available settings with /table listsettings");
            }
        } else p.errorDisplay.notOwnerOfTable(sender);
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
    }
}
