/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
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

    // Lists all valid setting types to the player.
    public void availableSettings(Player player) throws Exception
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Usage: /table set [setting] [value]");
        player.sendMessage(p.PLUGIN_TAG + p.white + "Available settings:");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "elimination [true|false] - " + p.white + "If true, players can't re-buy.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "minBuy [number] - " + p.white + "The minimum (re)buy-in amount.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "maxBuy [number] - " + p.white + "The maximum (re)buy-in amount.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "sb [number] - " + p.white + "The small blind.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "bb [number] - " + p.white + "The big blind");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "ante [number] - " + p.white + "The ante.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "dynamicFrequency [number] - " + p.white + "Every [number] hands, the ante + blinds will increase by their original setting. 0 = OFF.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "rake [number] - " + p.white + "How much of the pot you will get every hand, in percentages. Example: 0.05 = 5% rake.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "minRaise [number] - " + p.white + "The minimum raise at the table.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "minRaiseIsAlwaysBB [true|false] - " + p.white + "If true, the minimum raise will always be equal big blind.");
        player.sendMessage(p.PLUGIN_TAG + p.gold + "displayTurnsPublicly [true|false] - " + p.white + "If true, the player turn announcments will be displayed publicly.");
    }

    // Bans the specified player
    public void ban(Player player, String toBan) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (!table.banned.contains(toBan)) // Check if the player is not already banned
            {
                if (p.methodsCheck.isAPlayer(toBan) != null) // Check if the player is online
                table.ban(toBan);
                else p.methodsError.playerNotFound(player, toBan);
            } else p.methodsError.playerAlreadyBanned(player, toBan);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Closes the table of the player
    public void closeTable(Player player) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == true) // Only allow closing of the table if its already open
            table.close();
            else p.methodsError.tableAlreadyClosed(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    public void continueHand(Player player) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.toBeContinued == true)
            {
                table.continueHand();
            } else p.methodsError.cantContinue(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Creates a table for the player, with the specified name, and automatically sits the player at the table with the specified buy in.
    public void createTable(Player player, String tableName, String buyIn) throws Exception
    {
        if (p.methodsCheck.p.methodsCheck.isOwnerOfTable(player) == null)
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer == null)
            {
                if (p.methodsCheck.isDouble(buyIn))
                {
                    double buyin = Double.parseDouble(buyIn);
                    if (p.ECONOMY.has(player.getName(), Double.parseDouble(buyIn)))
                    {
                        // Makes a newTable, adds that table to the table list, displays messages and withdraws money from the owner.
                        Table newTable = new Table(player, tableName, p.methodsMisc.getFreeTableID(), player.getLocation(), buyin, p);
                        p.tables.add(newTable);
                        p.methodsMisc.sendToAllWithinRange(newTable.location, p.PLUGIN_TAG + p.gold + player.getName() + p.white + " has created a poker table named " + p.gold + "'" + tableName + "'" + p.white + ", ID " + p.gold + Integer.toString(newTable.id));
                        p.methodsMisc.sendToAllWithinRange(newTable.location, p.PLUGIN_TAG + "Bought in for " + p.gold + p.methodsMisc.formatMoney(buyin));
                        player.sendMessage(p.PLUGIN_TAG + "Edit the rules of your table with " + p.gold + "'/table set'" + p.white + ", and open it with " + p.gold + "'/table open'" + p.white + " when ready!");
                        p.ECONOMY.withdrawPlayer(player.getName(), buyin);
                        p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Withdrawing " + buyin + " from " + player.getName());
                    } else p.methodsError.notEnoughMoney(player, buyIn, p.ECONOMY.getBalance(player.getName()) - buyin);
                } else p.methodsError.notANumber(player, buyIn);
            } else p.methodsError.playerIsPokerPlayer(player);
        } else p.methodsError.playerIsOwnerGeneral(player);
    }

    // Deletes the player's table
    public void deleteTable(Player player) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.currentPhase != 5 || table.countPotAmounts() == 0)
            {
                // Displays a message, returns money for every player, and removes the table
                p.methodsMisc.sendToAllWithinRange(table.location, p.PLUGIN_TAG + "Table ID '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + " has been deleted!");
                p.methodsMisc.returnMoney(table);
                p.tables.remove(table);
            } else p.methodsError.tableHasPots(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Kicks the specified player from the owner's table
    public void kick(Player player, String toKick) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (p.methodsCheck.isInteger(toKick))
            {
                PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(toKick));
                if (pokerPlayer != null) // Check if the ID specified is a real poker player ID.
                {
                    if (pokerPlayer.owner == false) // This is the player to kick, not the command sender!
                    {
                        table.kick(pokerPlayer);
                        table.shiftIDs();
                    } else p.methodsError.playerIsOwnerSpecific(player);
                } else p.methodsError.notAPokerPlayerID(player, toKick);
            } else p.methodsError.notANumber(player, toKick);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Opens the specified player's table
    public void openTable(Player player) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.open == false) // Only allow the player to open the table if its already closed
            table.open();
            else p.methodsError.tableAlreadyOpen(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Pays the specified pot ID to the specified player ID. The first player argument is the owner typing the /table pay command.
    public void payPot(Player player, String potID, String playerID) throws Exception
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
                        if (table.pots.size() == 1) // If there is only 1 pot (the main one)
                        {
                            if (table.pots.get(0).pot > 0) // If the main pot is not 0
                            {
                                PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(table, Integer.parseInt(playerID));
                                if (pokerPlayer != null)
                                {
                                    table.pots.get(0).payPot(pokerPlayer);
                                    table.pots.remove(0);
                                } else p.methodsError.notAPokerPlayerID(player, playerID);
                            } else p.methodsError.potIsEmpty(player, table.pots.get(0));
                        } else p.methodsError.tableMultiplePots(player);
                    } else
                    // If there is more that one pot, require a pot ID.
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
                                    } else p.methodsError.notAPokerPlayerID(player, playerID);
                                } else p.methodsError.potIsEmpty(player, pot);
                            } else p.methodsError.notAPotID(player, potID);
                        } else p.methodsError.notANumber(player, potID);
                    } // No else here
                } else p.methodsError.tableIsInProgress(player);
            } else p.methodsError.notANumber(player, playerID);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Sets the specified setting on the player's table, to the specified value.
    public void setSetting(Player player, String setting, String value) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.inProgress == false)
            {
                if (setting.equalsIgnoreCase("elimination"))
                {
                    table.setBooleanValue(player, "elimination", value);
                    return;
                }
                if (setting.equalsIgnoreCase("minRaiseIsAlwaysBB"))
                {
                    table.setBooleanValue(player, "minRaiseIsAlwaysBB", value);
                    return;
                }
                if (setting.equalsIgnoreCase("displayTurnsPublicly"))
                {
                    table.setBooleanValue(player, "displayTurnsPublicly", value);
                    return;
                }
                if (setting.equalsIgnoreCase("minBuy"))
                {
                    table.setNumberValue(player, "minBuy", value);
                    return;
                }
                if (setting.equalsIgnoreCase("maxBuy"))
                {
                    table.setNumberValue(player, "maxBuy", value);
                    return;
                }
                if (setting.equalsIgnoreCase("sb"))
                {
                    table.setNumberValue(player, "sb", value);
                    return;
                }
                if (setting.equalsIgnoreCase("bb"))
                {
                    table.setNumberValue(player, "bb", value);
                    return;
                }
                if (setting.equalsIgnoreCase("ante"))
                {
                    table.setNumberValue(player, "ante", value);
                    return;
                }
                if (setting.equalsIgnoreCase("dynamicFrequency"))
                {
                    table.setNumberValue(player, "dynamicFrequency", value);
                    return;
                }
                if (setting.equalsIgnoreCase("rake"))
                {
                    table.setNumberValue(player, "rake", value);
                    return;
                }
                if (setting.equalsIgnoreCase("minRaise"))
                {
                    table.setNumberValue(player, "minRaise", value);
                    return;
                }
                player.sendMessage(p.PLUGIN_TAG + p.red + "Invalid setting. Check available settings with /table listsettings");
            } else p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Starts the player's table if they are the owner.
    public void startTable(Player player) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.inProgress != true)
            {
                if (table.stopped == false)
                {
                    if (table.players.size() >= 2) // Make sure that there are at least 2 players.
                    table.start();
                    else p.methodsError.notEnoughPlayers(player);
                } else p.methodsError.tableIsStopped(player);
            } else p.methodsError.tableIsInProgress(player);
        } else p.methodsError.notOwnerOfTable(player);
    }

    // Unbans the specified player from the player's table specified in the first argument
    public void unBan(Player player, String toUnBan) throws Exception
    {
        Table table = p.methodsCheck.isOwnerOfTable(player);
        if (table != null)
        {
            if (table.banned.contains(toUnBan)) table.unBan(toUnBan);
            else player.sendMessage(p.PLUGIN_TAG + p.gold + toUnBan + p.red + " is not banned from this table!");
        } else p.methodsError.notOwnerOfTable(player);
    }
}
