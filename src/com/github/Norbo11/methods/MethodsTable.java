/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsTable.java
 * -Contains methods that handle /table commands. These methods mostly point to the Table methods
 * and simply act as "checkers" - they simply test for conditions with if statements.
 * ===================================================================================================
 */

package com.github.norbo11.methods;

import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.classes.Pot;
import com.github.norbo11.classes.Table;

public class MethodsTable
{

    public MethodsTable(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    //Lists all valid setting types to the player.
    public void availableSettings(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Usage: /table set [setting] [value]");
        player.sendMessage(p.pluginTag + p.white + "Available settings:");
        player.sendMessage(p.pluginTag + p.gold + "elimination [true|false] - " + p.white + "If true, players can't re-buy.");
        player.sendMessage(p.pluginTag + p.gold + "minBuy [number] - " + p.white + "The mininmum number that players can buy-in (and re-buy) for.");
        player.sendMessage(p.pluginTag + p.gold + "maxBuy [number] - " + p.white + "The maximum number that players can buy-in (and re-buy) for.");
        player.sendMessage(p.pluginTag + p.gold + "sb [number] - " + p.white + "Set the small blind.");
        player.sendMessage(p.pluginTag + p.gold + "bb [number] - " + p.white + "Set the big blind");
        player.sendMessage(p.pluginTag + p.gold + "ante [number] - " + p.white + "Sets the ante.");
        player.sendMessage(p.pluginTag + p.gold + "dynamicFrequency [number] - " + p.white + "This decides that every [number] hands, the ante + blinds will increase by themselves. 0 = disabled.");
        player.sendMessage(p.pluginTag + p.gold + "rake [number] - " + p.white + "How much of the pot you will get every hand, in percentages. Example: 0.05 = 5% rake.");
        player.sendMessage(p.pluginTag + p.gold + "minRaise [number] - " + p.white + "Sets the minimum raise at the table.");
        player.sendMessage(p.pluginTag + p.gold + "minRaiseIsAlwaysBB [true|false] - " + p.white + "If true, the minimum raise will always be the big blind.");
    }

    //Bans the specified player
    public void ban(Player player, String toBan)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (!table.banned.contains(toBan)) //Check if the player is not already banned
            {
                if (p.methodsMisc.stringToPlayer(toBan) != null) //Check if the player is online
                    table.ban(toBan);
                else p.methodsError.playerNotFound(player, toBan);
            } else p.methodsError.playerAlreadyBanned(player, toBan);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Closes the table of the player
    public void closeTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == true) //Only allow closing of the table if its already open
                table.close();
            else p.methodsError.tableAlreadyClosed(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Creates a table for the player, with the specified name, and automatically sits the player at the table with the specified buy in.
    public void createTable(Player player, String tableName, String buyIn)
    {
        if (p.methodsCheck.p.methodsCheck.isOwnerOfTable(player) == null)
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer == null)
            {
                if (p.methodsCheck.isDouble(buyIn))
                {
                    if (p.economy.has(player.getName(), Double.parseDouble(buyIn)))
                    {
                        //Makes a newTable, adds that table to the table list, displays messages and withdraws money from the owner.
                        Table newTable = new Table(player, tableName, p.tables.size(), player.getLocation(), Double.parseDouble(buyIn), p);
                        p.tables.add(newTable);
                        player.sendMessage(p.pluginTag + "Created table named " + p.gold + "'" + tableName + "'" + p.white + ", ID " + p.gold + Integer.toString(p.tables.size() - 1) + p.white + "!");
                        player.sendMessage(p.pluginTag + "Edit the rules of your table with " + p.gold + "'/table set'" + p.white + ", and open it with " + p.gold + "'/table open'" + p.white + " when ready!");
                        p.economy.withdrawPlayer(player.getName(), Double.parseDouble(buyIn));
                        p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Withdrawing " + Double.parseDouble(buyIn) + " from " + player.getName());
                    } else p.methodsError.notEnoughMoney(player, buyIn, p.economy.getBalance(player.getName()) - Double.parseDouble(buyIn));
                } else p.methodsError.notANumber(player, buyIn);
            } else p.methodsError.playerIsPokerPlayer(player);
        } else p.methodsError.playerIsOwnerGeneral(player);
    }

    //Deletes the player's table
    public void deleteTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            //Displays a message, returns money for every player, and removes the table
            player.sendMessage(p.pluginTag + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " has been deleted!");
            p.methodsMisc.returnMoney(table);
            p.tables.remove(table);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Kicks the specified player from the owner's table
    public void kick(Player player, String toKick) throws SQLException
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (p.methodsCheck.isInteger(toKick))
            {
                PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(toKick));
                if (pokerPlayer != null) //Check if the ID specified is a real poker player ID.
                {
                    if (pokerPlayer.owner == false) //This is the player to kick, not the command sender!
                    {
                        table.kick(pokerPlayer);
                        table.shiftIDs();
                    } else p.methodsError.playerIsOwnerGeneral(player);
                } else p.methodsError.notAPokerPlayerID(player, toKick);
            } else p.methodsError.notANumber(player, toKick);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Opens the specified player's table
    public void openTable(Player player)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == false) //Only allow the player to open the table if its already closed
                table.open();
            else p.methodsError.tableAlreadyOpen(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Pays the specified pot ID to the specified player ID. The first player argument is the owner typing the /table pay command.
    public void payPot(Player player, String potID, String playerID) throws SQLException
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (p.methodsCheck.isInteger(playerID))
            {
                if (table.inProgress == false)
                {
                    if (potID == null)
                    {
                        if (table.pots.size() == 1) //If there is only 1 pot (the main one)
                        {
                            if (table.pots.get(0).pot > 0) //If the main pot is not 0
                            {
                                PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(playerID));
                                if (pokerPlayer != null)
                                {
                                    table.pots.get(0).payPot(pokerPlayer);
                                    table.pots.remove(0);
                                    table.deal();
                                } else p.methodsError.notAPokerPlayerID(player, playerID);
                            } else p.methodsError.potIsEmpty(player, table.pots.get(0));
                        } else p.methodsError.tableMultiplePots(player);
                    } else //If there is more that one pot, require a pot ID.
                    {
                        if (p.methodsCheck.isInteger(potID))
                        {
                            Pot pot = p.methodsCheck.isAPot(table, Integer.parseInt(potID));
                            if (pot != null)
                            {
                                if (pot.pot > 0)
                                {
                                    PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(playerID));
                                    if (pokerPlayer != null)
                                    {
                                        pot.payPot(pokerPlayer);
                                        table.pots.remove(pot);
                                        if (table.pots.size() == 0) table.deal();
                                    } else p.methodsError.notAPokerPlayerID(player, playerID);
                                } else p.methodsError.potIsEmpty(player, pot);
                            } else p.methodsError.notAPotID(player, potID);
                        } else p.methodsError.notANumber(player, potID);
                    } //No else here
                } else p.methodsError.tableIsInProgress(player);
            } else p.methodsError.notANumber(player, playerID);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Sets the specified setting on the player's table, to the specified value.
    public void setSetting(Player player, String setting, String value)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (setting.equalsIgnoreCase("elimination")) { table.setBooleanValue(player, "elimination", value); return; }
            if (setting.equalsIgnoreCase("minRaiseIsAlwaysBB")) { table.setBooleanValue(player, "minRaiseIsAlwaysBB", value); return; }
            if (setting.equalsIgnoreCase("minBuy")) { table.setNumberValue(player, "minBuy", value); return; }
            if (setting.equalsIgnoreCase("maxBuy")) { table.setNumberValue(player, "maxBuy", value); return; }
            if (setting.equalsIgnoreCase("sb")) { table.setNumberValue(player, "sb", value); return; }
            if (setting.equalsIgnoreCase("bb")) { table.setNumberValue(player, "bb", value); return; }
            if (setting.equalsIgnoreCase("ante")) { table.setNumberValue(player, "ante", value); return; }
            if (setting.equalsIgnoreCase("dynamicFrequency")) { table.setNumberValue(player, "dynamicFrequency", value); return; }
            if (setting.equalsIgnoreCase("rake")) { table.setNumberValue(player, "rake", value); return; }
            if (setting.equalsIgnoreCase("minRaise")) { table.setNumberValue(player, "mineRaise", value); return; }
            player.sendMessage(p.pluginTag + p.red + "Invalid setting. Check available settings with /table listsettings");
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Starts the player's table if they are the owner.
    public void startTable(Player player) throws SQLException
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.inProgress != true)
            {
                if (table.stopped == false)
                {
                    if (table.players.size() >= 2) //Make sure that there are at least 2 players.
                        table.start();
                    else p.methodsError.notEnoughPlayers(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    //Unbans the specified player from the player's table specified in the first argument
    public void unBan(Player player, String toUnBan)
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.banned.contains(toUnBan))
                table.unBan(toUnBan);
            else player.sendMessage(p.pluginTag + p.gold + toUnBan + p.red + " is not banned from this table!");
        } else p.methodsError.notOwnerOfTable(player);
    }
}
