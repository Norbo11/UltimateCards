/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
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

import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.classes.Table;

public class MethodsPoker
{
    UltimatePoker p;
    public MethodsPoker(UltimatePoker p)
    {
        this.p = p;
    }
    
    //Sends a simple message to the specified player to invite
    public void invite(Player player, String toInvite)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            Player playerToInvite = p.methodsMisc.stringToPlayer(toInvite);
            if (playerToInvite != null) //If the player specified is an online player (ignoring the case), then send them the invite.
                pokerPlayer.invite(playerToInvite);
            else p.methodsError.playerNotFound(player, toInvite);
        } else p.methodsError.notAPokerPlayer(player);
    }
    
    //Sends the list of created tables to the specified player
    public void displayTables(Player player)
    {
        player.sendMessage(p.pluginTag + "List of currently created poker tables:");
        if (p.tables.size() > 0) //If there is at least one table
        {
            for (int i = 0; i < p.tables.size(); i++) //Goes through all tables and lists them. Displays the name in red if the table is closed, or green if its open.
            {
                if (p.tables.get(i).open == true)
                    player.sendMessage(p.pluginTag + ChatColor.GREEN + "#" + p.tables.get(i).id + " " + p.tables.get(i).name);
                else player.sendMessage(p.pluginTag + p.red + "#" + p.tables.get(i).id + " " + p.tables.get(i).name);
            }
            player.sendMessage(p.pluginTag + ChatColor.GREEN + "GREEN = Open. " + p.red + "RED = Closed.");
            player.sendMessage(p.pluginTag + "Use " + p.gold + "/poker sit [table ID] [buy-in] " + p.white + "to join a table.");
        } else p.methodsError.noTablesCreated(player); //If no tables were found simply display an error message.
    }
    
    //Teleports the specified player to the specified table ID.
    public void tpToTable(Player player, String id)
    {
        //If the ID is a number, and the ID is a valid table, teleport the player and display a message
        if (p.methodsCheck.isInteger(id))
        {
            Table table = p.methodsCheck.isATable(Integer.parseInt(id));
            if (table != null)
            {
                player.teleport(table.location);
                player.sendMessage(p.pluginTag + "You have teleported to table " + p.gold + table.name + p.white + ", ID #" + p.gold + table.id + p.white + ". Sit down with " + p.gold + "/poker sit [ID]");
            } else p.methodsError.notATable(player, id);
        } else p.methodsError.notANumber(player, id);
    }
    
    //Displays the list of players to the specified player
    public void displayPlayers(Player player, String tableID)
    {
        //If a table was not specified, make sure that the player is sitting at a table, then display all players on that table to the player
        if (tableID == null)
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer != null)
            {
                pokerPlayer.table.displayDetail(player, "players");
            } else p.methodsError.notAPokerPlayer(player);
        } else //If a table was specified, make sure that the specified table is a real table before displaying all it's players
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
    
    //Deletes the specified player from the table, if they are currently sitting at one. Doesnt allow the owner to leave
    public void leaveTable(Player player) throws SQLException
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            if (pokerPlayer.owner == false) //Make sure to check if the player is an owner of the table before leaving it.
                pokerPlayer.table.leave(pokerPlayer);
            else p.methodsError.playerIsOwnerSpecific(player);
        } else p.methodsError.notAPokerPlayer(player);
    }

    //Sits the player at the specified table with the specified buy-in
    public void sitTable(Player player, String id, String buyin)
    {
        //Firstly check if the player is already sitting at a table or not
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
        if (pokerPlayer == null)
        {
            //Check if the ID and buyIn are numbers
            if (p.methodsCheck.isInteger(id))
            {
                if (p.methodsCheck.isDouble(buyin))
                {
                    //Then check if the table exists
                    Table table = p.methodsCheck.isATable(Integer.parseInt(id));
                    if (table != null)
                    {
                        //Check if the player is banned
                        if (table.banned.contains(player.getName()) == false)
                        {
                            //Check if they have permission to teleport there, OR if they are close enough to see all of the table's messages
                            if (player.hasPermission("upoker.tp") || player.getLocation().distance(table.location) <= table.chatRange)
                            {
                                //Check if the table is open
                                if (table.open == true)
                                {
                                    //Check if the table is in progress
                                    if (table.inProgress == false)
                                    {
                                        //Check if the buy in is within the bounds of the table
                                        double Buyin = Double.parseDouble(buyin);
                                        if (Buyin >= table.minBuy && Buyin <= table.maxBuy)
                                        {
                                            //Check if the player even has that amount
                                            if (p.economy.has(player.getName(), Buyin))
                                            {
                                                //Creates a new poker player, adds them to the table, teleport them (if they have permission), withdraws money from them, logs the action and sends them a message
                                                table.players.add(new PokerPlayer(player, table, Buyin, p));
                                                if (player.hasPermission("upoker.tp")) player.teleport(table.location);
                                                p.economy.withdrawPlayer(player.getName(), Buyin);
                                                p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Withdrawing " + Buyin + " from " + player.getName());
                                                player.sendMessage(p.pluginTag + "You have sat at table '" + p.gold + table.name + p.white + "', ID #" + p.gold + table.id + p.white + ", with a buy-in of " + p.gold + p.methodsMisc.formatMoney(Buyin) + p.white + ". Make sure to stay within " + p.gold + p.getConfig().getInt("table.chatrange") + p.white + " blocks of it to see all of it's messages!");
                                            } else p.methodsError.notEnoughMoney(player, buyin, p.economy.getBalance(player.getName()));
                                        } else p.methodsError.notWithinBuyinBounds(player, Buyin, table.minBuy, table.maxBuy);
                                    } else p.methodsError.tableIsInProgress(player);
                                } else p.methodsError.notOpen(player, id);
                            } else p.methodsError.playerNotNearEnough(player);
                        } else p.methodsError.playerIsBanned(player);
                    } else p.methodsError.notATable(player, id);
                } else p.methodsError.notANumber(player, buyin);
            } else p.methodsError.notANumber(player, id);
        } else p.methodsError.playerIsPokerPlayer(player);
    }
    
    //Lists the specified details type of the specified table. If no table is specified, lists details of the table that the player is sitting on. If a type is not specified, lists all details.
    public void listDetails(Player player, String type, String tableID)
    {
        Table table = null;
        if (tableID != null) // If the user specified a table, make sure that its a number and a valid table.
        {
            if (p.methodsCheck.isInteger(tableID))
            {
                table = p.methodsCheck.isATable(Integer.parseInt(tableID));
                if (table == null)
                {
                    p.methodsError.notATable(player, tableID);
                    return;
                }
            } else
            {
                p.methodsError.notANumber(player, tableID);
                return;
            }
        } else
        // If the user didn't specify a table, make sure that he is a poker player.
        {
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer != null) table = pokerPlayer.table;
            else
            {
                player.sendMessage(p.pluginTag + p.red + "You have not specified a table, and are currently not sitting at one!");
                return;
            }
        }
        if (type == null) // If the user didn't specify a type of detail, display all details.
        table.displayAllDetails(player);
        else
        // If the user did specify a type of detail, display just that detail, is it is a valid detail type.
        {
            if (table.isADetail(type)) table.displayDetail(player, type);
            else p.methodsError.notADetailType(player, type);
        }
    }
}
