/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Pot.java
 * -This class is created whenever a player goes all in
 * -It holds the pot amount, the player that created the pot by going all in, and a "main" boolean
 * which decides if this pot is the main pot of the table (in which case, the "owner" of this pot is
 * null)
 * ===================================================================================================
 */

package com.github.norbo11.classes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.norbo11.UltimatePoker;

public class Pot
{
    public Pot(PokerPlayer playerAllIn, Table table, double amount, int id, UltimatePoker p)
    {
        this.p = p;
        this.playerAllIn = playerAllIn;
        this.table = table;
        this.id = id;
        pot = amount;
        called = 0;
        phase = table.currentPhase;
        main = false;
        table.latestPot = this;
    }

    UltimatePoker p;
    public Table table;         // The table which this pot belongs to
    PokerPlayer playerAllIn;    // Holds the player that has created this pot by going all in. This is null if the pot is the main pot
    public double pot;          // The actual amount of the pot
    public int id;              // The ID of the pot on the table
    public boolean main;        // Is true if this pot is the main pot of the table (the first one). This also means playerAllIn is null.
    int called;                 // Holds the number of people that have called the all-in
    int phase;                  // Represents the phase in which this pot was created
    List<PokerPlayer> eligible = new ArrayList<PokerPlayer>();                  // This list holds all the players that can currently win this pot
    Map<PokerPlayer, Double> contributed = new HashMap<PokerPlayer, Double>();  // This hash map assigns values to every player depending on how much they contributed to this pot.

    // This method adjusts the list of eligible players. This is called when the pot is created and when the pot is viewed
    public void adjustEligible()
    {
        if (!main)
        {
            if (table.currentPhase <= 4)
            {
                eligible.clear();
                // Go through all players at the table to which this pot belongs to, and if the player still has money, say that they are eligible for this pot by adding them to the eligible list
                for (PokerPlayer player : table.getNonFoldedPlayers())
                {
                    if (player.allIn > 0)
                    {
                        // Make sure the player's pot was created after this pot
                        if (player.pot.id > id)
                        {
                            // Make sure that the player isn't already on the list, hasn't created this pot and this pot was created before the player's pot (which he shouldnt be eligible for)
                            if (player != playerAllIn && !player.folded && !player.eliminated) eligible.add(player);
                        }
                    } else if (player != playerAllIn && !player.folded && !player.eliminated && player.currentBet == table.currentBet) eligible.add(player);
                }
            }
        }
    }

    // Counts all contributions and sets the pot to the total amount contributed by everyone
    public void adjustPot()
    {
        double newPot = 0;
        Collection<Double> contributions = contributed.values();
        for (Double temp : contributions)
            newPot = newPot + temp;
        pot = newPot;
    }

    // Contributes the specified amount to the specified player. If set = true, it sets the contribution. Otherwise it adds to the contribution. If the player hasn't got a contributed entry, we create it.
    public void contribute(PokerPlayer player, double amount, boolean set)
    {
        // Try to get the specified player. If an exception is throw he doesnt exist, so create him. (we also check inside the try statement just in case)
        try
        {
            if (contributed.get(player) == null) contributed.put(player, new Double(0));
        } catch (Exception e)
        {
            contributed.put(player, new Double(0));
        }
        if (set)
        {
            contributed.remove(player);
            contributed.put(player, amount);
        } else
        {
            double temp = contributed.get(player);
            contributed.remove(player);
            contributed.put(player, temp + amount);
        }
    }

    // Gets the contribution of the specified player. Creates a contribution of 0 if the player doesnt exist
    public double getContribution(PokerPlayer player)
    {
        try
        {
            if (contributed.get(player) == null) contributed.put(player, new Double(0));
        } catch (Exception e)
        {
            contributed.put(player, new Double(0));
        }
        return contributed.get(player);
    }

    // Pays the pot to the specified poker player. Make sure to delete the pot from table.pots after calling this method (NEVER delete the main pot! ( pots.get(0) ) )
    public void payPot(PokerPlayer player)
    {
        double rake = 0;
        if (table.rake > 0) // If the table which this pot belongs to has a rake
        {
            rake = pot * table.rake; // Set the local rake variable by getting the rake from the amount of this pot
            p.ECONOMY.depositPlayer(table.owner.name, rake); // Give the rake to the owner of the table (not to his stack, to his actual money), then announce the message to people around the table
            p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + rake + " to " + table.owner.name);
            p.methodsMisc.sendToAllWithinRange(table.location, p.PLUGIN_TAG + p.gold + table.owner.name + p.white + " has been paid a rake of " + p.gold + p.methodsMisc.formatMoney(rake));
        }

        // If this is a main pot, announce a message saying that the player has won the main pot. If its not the main pot, say that they won the side pot.
        if (main == true) p.methodsMisc.sendToAllWithinRange(table.location, p.PLUGIN_TAG + p.gold + player.name + p.white + " has won the main pot of " + p.gold + p.methodsMisc.formatMoney(pot - rake));
        else p.methodsMisc.sendToAllWithinRange(table.location, p.PLUGIN_TAG + p.gold + player.name + p.white + " has won the side pot of " + p.gold + p.methodsMisc.formatMoney(pot - rake));

        player.money = player.money + (pot - rake); // Get the actual amount that the player wins by subtracting the rake from the pot, then give it to the player's stack
        p.log.info(player.totalBet + "");
        p.log.info(Double.toString((pot - rake) - player.totalBet) + "");
        player.stats.adjustStats("potWon", (pot - rake) - player.totalBet);
        player.wonThisHand = player.wonThisHand + (pot - rake);
        pot = 0;

        // If after paying this pot there is only 1 pot left (the main pot), and that main pot is empty, deal the next hand.
        if (table.pots.size() == 1 && table.pots.get(0).pot == 0)
        {
            for (PokerPlayer temp : table.showdownPlayers)
                if (temp.wonThisHand == 0) temp.stats.adjustStats("handLost", temp.totalBet);
            table.toBeContinued = true;
            for (PokerPlayer temp : table.players) temp.totalBet = 0;
            table.showdownPlayers.clear();
            p.methodsMisc.sendToAllWithinRange(table.location, p.PLUGIN_TAG + "All pots paid! Table owner: use " + p.gold + "/table continue" + p.white + " to deal the next hand.");
            table.saveStats();
        }
    }

    // Converts this pot into a string in the format of "[Side Pot #X] 1,213,412 Dollars - Player1, Player2, Player3
    // The "Players's" are the people that are eligible to win that pot.
    public String toString()
    {
        String eligible = "";
        adjustEligible(); // Only adjust the list of eligible players if its the pre flop, flop, turn or river

        for (PokerPlayer player : this.eligible)
            eligible = eligible + player.name + ", "; // Add to the String which represents eligible players, then put a comma at the end
        if (!main) eligible = eligible.substring(0, eligible.length() - 2);  // Delete the last comma at the end of the list of eligible players, after putting them into the eligible list

        if (main == true) return p.white + "[Main Pot #" + id + "] " + p.gold + p.methodsMisc.formatMoney(pot) + " - For everyone";
        else return p.white + "[Side Pot #" + id + "] " + p.gold + p.methodsMisc.formatMoney(pot) + " - " + eligible; // Return the string that represents this pot
    }
}
