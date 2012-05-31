/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsPoker.java
 * -Contains methods that handle /poker commands. Unlike MethodsHand and MethodsTable, they dont
 * really point to the methods in the Table or PokerPlayer classes, instead just handle the
 * action themselves.
 * ===================================================================================================
 */

package com.github.norbo11.methods;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.classes.Table;

public class MethodsPoker
{
    public MethodsPoker(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    public void check(Player player, String toCheck) throws Exception
    {
        Player playerToCheck = p.methodsCheck.isAPlayer(toCheck);
        if (playerToCheck != null)
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(playerToCheck);
            if (pokerPlayer != null)
            {
                player.sendMessage(p.PLUGIN_TAG + p.gold + toCheck + p.white + "'s stack: " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money));
            } else p.methodsError.pokerPlayerNotFound(player, toCheck);
        } else p.methodsError.playerNotFound(player, toCheck);
    }

    // Displays the list of players to the specified player
    public void displayPlayers(Player player, String tableID) throws Exception
    {
        // If a table was not specified, make sure that the player is sitting at a table, then display all players on that table to the player
        if (tableID == null)
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer != null)
            {
                pokerPlayer.table.displayDetail(player, "players");
            } else p.methodsError.notAPokerPlayer(player);
        } else
        // If a table was specified, make sure that the specified table is a real table before displaying all it's players
        {
            if (p.methodsCheck.isInteger(tableID))
            {
                Table table = p.methodsCheck.isATable(Integer.parseInt(tableID));
                if (table != null)
                {
                    table.displayDetail(player, "players");
                } else p.methodsError.notATable(player, tableID);
            } else p.methodsError.notANumber(player, tableID);
        }
    }

    // Sends the list of created tables to the specified player
    public void displayTables(Player player) throws Exception
    {
        player.sendMessage(p.PLUGIN_TAG + "List of currently created poker tables:");
        if (p.tables.size() > 0) // If there is at least one table
        {
            for (int i = 0; i < p.tables.size(); i++) // Goes through all tables and lists them. Displays the name in red if the table is closed, or green if its open.
            {
                if (p.tables.get(i).open == true) player.sendMessage(p.PLUGIN_TAG + ChatColor.GREEN + "#" + p.tables.get(i).id + " " + p.tables.get(i).name);
                else player.sendMessage(p.PLUGIN_TAG + p.red + "#" + p.tables.get(i).id + " " + p.tables.get(i).name);
            }
            player.sendMessage(p.PLUGIN_TAG + ChatColor.GREEN + "GREEN = Open. " + p.red + "RED = Closed.");
            player.sendMessage(p.PLUGIN_TAG + "Use " + p.gold + "/poker sit [table ID] [buy-in] " + p.white + "to join a table.");
        } else p.methodsError.noTablesCreated(player); // If no tables were found simply display an error message.
    }

    // Sends a simple message to the specified player to invite
    public void invite(Player player, String toInvite) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            Player playerToInvite = p.methodsCheck.isAPlayer(toInvite);
            if (playerToInvite != null) // If the player specified is an online player (ignoring the case), then send them the invite.
            pokerPlayer.invite(playerToInvite, pokerPlayer.table.id);
            else p.methodsError.playerNotFound(player, toInvite);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Deletes the specified player from the table, if they are currently sitting at one. Doesnt allow the owner to leave
    public void leaveTable(Player player) throws Exception
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.owner == false) // Make sure to check if the player is an owner of the table before leaving it.
            pokerPlayer.table.leave(pokerPlayer);
            else p.methodsError.playerIsOwnerSpecific(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    // Lists the specified details type of the specified table. If no table is specified, lists details of the table that the player is sitting on. If a type is not specified, lists all details.
    public void listDetails(Player player, String type, String tableID) throws Exception
    {
        if (tableID == null && type == null) // If the player didnt specify a type or a table ID, make sure he is sitting at a table, then display all details.
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer != null) pokerPlayer.table.displayAllDetails(player);
            else player.sendMessage(p.PLUGIN_TAG + p.red + "You have not specified a table and are currently not sitting at one!");
            return;
        }

        if (tableID == null && type != null) // If the player specified a type but not a table, make sure he is sitting at a table, then display the type of detail to him.
                                             // If he is not sitting at a table, assume that the type is the table ID.
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer != null)
            {
                if (pokerPlayer.table.isADetail(type)) pokerPlayer.table.displayDetail(player, type);
                else player.sendMessage(p.PLUGIN_TAG + p.red + "Invalid detail type! Valid types: " + p.gold + "all, settings, players, general | other");
                return;
            }
            if (p.methodsCheck.isInteger(type))
            {
                Table table = p.methodsCheck.isATable(Integer.parseInt(type));
                if (table != null)
                {
                    table.displayAllDetails(player);
                    return;
                } else player.sendMessage(p.PLUGIN_TAG + p.red + "You are not currently sitting at any table, and the specified value " + p.gold + type + p.white + " is not a table!");
            } else player.sendMessage(p.PLUGIN_TAG + p.red + "You are not currently sitting at any table, and the specified value " + p.gold + type + p.white + " is not a number!");
        }

        if (tableID != null && type != null) // If the player specified a table ID and a type, make sure that the type is a type and the table is a table, then display the detail type to him.
        {
            if (p.methodsCheck.isInteger(tableID))
            {
                Table table = p.methodsCheck.isATable(Integer.parseInt(tableID));
                if (table != null)
                {
                    if (table.isADetail(type))
                    {
                        table.displayDetail(player, type);
                        return;
                    }
                    player.sendMessage(p.PLUGIN_TAG + p.red + "Invalid detail type! Valid types: " + p.gold + "all, settings, players, general | other");
                } else p.methodsError.notATable(player, tableID);
            } else p.methodsError.notATable(player, tableID);
        }
    }

    // Sits the player at the specified table with the specified buy-in
    public void sitTable(Player player, String id, String buyin) throws Exception
    {
        // Firstly check if the player is already sitting at a table or not
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer == null)
        {
            // Check if the ID and buyIn are numbers
            if (p.methodsCheck.isInteger(id))
            {
                if (p.methodsCheck.isDouble(buyin))
                {
                    // Then check if the table exists
                    Table table = p.methodsCheck.isATable(Integer.parseInt(id));
                    if (table != null)
                    {
                        // Check if the player is banned
                        if (table.banned.contains(player.getName()) == false)
                        {
                            boolean notNearEnough = false;
                            // Check if they have permission to teleport there, OR if they are close enough to see all of the table's messages
                            if (player.hasPermission("upoker.tp") == false)
                            {
                                if (player.getWorld() == table.location.getWorld())
                                {
                                    if (player.getLocation().distance(table.location) <= table.chatRange) notNearEnough = false;
                                    else notNearEnough = true;
                                } else notNearEnough = true;
                            }
                            if (notNearEnough == true)
                            {
                                p.methodsError.playerNotNearEnough(player);
                                return;
                            }
                            // Check if the table is open
                            if (table.open == true)
                            {
                                // Check if the table is in progress
                                if (table.inProgress == false)
                                {
                                    // Check if the buy in is within the bounds of the table
                                    double Buyin = Double.parseDouble(buyin);
                                    if (Buyin >= table.minBuy && Buyin <= table.maxBuy)
                                    {
                                        // Check if the player even has that amount
                                        if (p.ECONOMY.has(player.getName(), Buyin))
                                        {
                                            table.sitPlayer(player, Buyin);
                                        } else p.methodsError.notEnoughMoney(player, buyin, p.ECONOMY.getBalance(player.getName()));
                                    } else p.methodsError.notWithinBuyinBounds(player, Buyin, table.minBuy, table.maxBuy);
                                } else p.methodsError.tableIsInProgress(player);
                            } else p.methodsError.notOpen(player, id);
                        } else p.methodsError.playerIsBanned(player);
                    } else p.methodsError.notATable(player, id);
                } else p.methodsError.notANumber(player, buyin);
            } else p.methodsError.notANumber(player, id);
        } else p.methodsError.playerIsPokerPlayer(player);
    }

    // Teleports the specified player to the specified table ID.
    public void tpToTable(Player player, String id) throws Exception
    {
        // If the ID is a number, and the ID is a valid table, teleport the player and display a message
        if (p.methodsCheck.isInteger(id))
        {
            Table table = p.methodsCheck.isATable(Integer.parseInt(id));
            if (table != null)
            {
                player.teleport(table.location);
                player.sendMessage(p.PLUGIN_TAG + "You have teleported to table " + p.gold + table.name + p.white + ", ID #" + p.gold + table.id + p.white + ". Sit down with " + p.gold + "/poker sit [ID]");
            } else p.methodsError.notATable(player, id);
        } else p.methodsError.notANumber(player, id);
    }
}
