/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
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
import java.util.List;

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
    Table table;                //The table which this pot belongs to
    PokerPlayer playerAllIn;    //Holds the player that has created this pot by going all in. This is null if the pot is the main pot
    public double pot;          //The actual amount of the pot
    public int id;              //The ID of the pot on the table
    public boolean main;        //Is true if this pot is the main pot of the table (the first one). This also means playerAllIn is null.
    int called;                 //Holds the number of people that have called the all-in
    int phase;                  //Represents the phase in which this pot was created
    List<PokerPlayer> eligible = new ArrayList<PokerPlayer>(); // This list holds all the players that can currently win this pot.

    //This method adjusts the list of eligible players. This is called when the pot is created and when the pot is viewed
    public void adjustEligible()
    {
        //Go through all players at the table to which this pot belongs to, and if the player still has money, say that they are eligible for this pot by adding them to the eligible list
        for (PokerPlayer player : table.players)
            //player != playerAllIn
            //Make sure that the player isn't already on the list and has money
            if (player.money > 0 && !eligible.contains(player)) eligible.add(player);
    }
    
    //Converts this pot into a string in the format of "[Side Pot #X] 1,213,412 Dollars - Player1, Player2, Player3
    //The "Players's" are the people that are eligible to win that pot. 
    public String toString()
    {
        //If this is the main pot, say that it is for everyone
        if (main == true) return p.white + "[Main Pot #" + id + "] " + p.gold + p.methodsMisc.formatMoney(pot) + " - For everyone";
        
        adjustEligible();
        String eligible = ""; //The string which represents the eligible players
        for (PokerPlayer player : this.eligible)
            eligible = eligible + player.name + ", "; //Add to the String which represents eligible players, then put a comma at the end
        
        eligible = eligible.substring(0, eligible.length() - 2);  //Delete the last comma at the end of the list of eligible players, after putting them all in
        return p.white + "[Side Pot #" + id + "] " + p.gold + p.methodsMisc.formatMoney(pot) + " - " + eligible; //Return the string that represents this pot
    }

    //Pays the pot to the specified poker player. Make sure to delete the pot from table.pots after calling this method (NEVER delete the main pot! ( pots.get(0) ) )
    public void payPot(PokerPlayer player)
    {
        double rake = 0;
        if (table.rake > 0) //If the table which this pot belongs to has a rake
        {
            rake = pot * table.rake; //Set the local rake variable by getting the rake from the amount of this pot
            p.economy.depositPlayer(table.owner.name, rake); //Give the rake to the owner of the table (not to his stack, to his actual money), then announce the message to people around the table
            p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + rake + " to " + table.owner.name);
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + table.owner.name + p.white + " has been paid a rake of " + p.gold + p.methodsMisc.formatMoney(rake));
        }
        
        //If this is a main pot, announce a message saying that the player has won the main pot. If its not the main pot, say that they won the side pot.
        if (main == true) p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.player.getName() + p.white + " has won the main pot of " + p.gold + p.methodsMisc.formatMoney(pot - rake));
        else p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.player.getName() + p.white + " has won the side pot of " + p.gold + p.methodsMisc.formatMoney(pot - rake));

        player.money = player.money + (pot - rake); //Get the actual amount that the player wins by subtracting the rake from the pot, then give it to the player's stack

        if (table.pots.size() == 1 && table.pots.get(0).pot == 0) table.deal(); //If after paying this pot there is only 1 pot left (the main pot), and that main pot is empty, deal the next hand.
    }
}
