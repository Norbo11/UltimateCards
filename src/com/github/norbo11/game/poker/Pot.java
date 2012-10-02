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

package com.github.norbo11.game.poker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;

public class Pot
{
    private Map<PokerPlayer, Double> contributed = new HashMap<PokerPlayer, Double>(); // This
                                                                                       // hash
                                                                                       // map
                                                                                       // assigns
                                                                                       // values
                                                                                       // to
                                                                                       // every
                                                                                       // player
                                                                                       // depending
                                                                                       // on
                                                                                       // how
                                                                                       // much
                                                                                       // they
                                                                                       // contributed
                                                                                       // to
                                                                                       // this
                                                                                       // pot.

    private PokerTable pokerTable; // The table which this pot belongs to
    private PokerPlayer playerAllIn; // Holds the player that has created this
                                     // pot by going all in. This is null if the
                                     // pot is the main pot
    private ArrayList<PokerPlayer> eligible = new ArrayList<PokerPlayer>(); // This
                                                                            // list
                                                                            // holds
                                                                            // all
                                                                            // the
                                                                            // players
                                                                            // that
                                                                            // can
                                                                            // currently
                                                                            // win
                                                                            // this
                                                                            // pot
    private double pot; // The actual amount of the pot

    private boolean main; // Is true if this pot is the main pot of the table
                          // (the first one). This also means playerAllIn is
                          // null.
    private int id; // The ID of the pot on the table

    private int called; // Holds the number of people that have called the
                        // all-in
    private PokerPhase pokerPhase; // Represents the phase in which this pot was
                                   // created

    public Pot(PokerPlayer playerAllIn, PokerTable pokerTable, double amount, int id)
    {
        this.playerAllIn = playerAllIn;
        this.pokerTable = pokerTable;
        this.id = id;
        pot = amount;
        called = 0;
        pokerPhase = pokerTable.getCurrentPhase();
        main = false;
        pokerTable.setLatestPot(this);
    }

    // This method adjusts the list of eligible players. This is called when the
    // pot is created and when the pot is viewed
    public void adjustEligible()
    {
        if (!main) if (pokerTable.getCurrentPhase().getNumber() >= PokerPhase.SHOWDOWN.getNumber())
        {
            eligible.clear();
            // Go through all players at the table to which this pot belongs
            // to, and if the player still has money, say that they are
            // eligible for this pot by adding them to the eligible list
            for (PokerPlayer player : pokerTable.getNonFoldedPlayers())
                if (player.getAllIn() > 0)
                {
                    // Make sure the player's pot was created after this pot
                    if (player.getPot().id > id) // Make sure that the player isn't already on the
                    // list, hasn't created this pot and this pot was
                    // created before the player's pot (which he
                    // shouldnt be eligible for)
                    if (player != playerAllIn && !player.isFolded() && !player.isEliminated())
                    {
                        eligible.add(player);
                    }
                } else if (player != playerAllIn && !player.isFolded() && !player.isEliminated() && player.getCurrentBet() == pokerTable.getCurrentBet())
                {
                    eligible.add(player);
                }
        }
    }

    // Counts all contributions and sets the pot to the total amount contributed
    // by everyone
    public void adjustPot()
    {
        double newPot = 0;
        Collection<Double> contributions = contributed.values();
        for (Double temp : contributions)
        {
            newPot = newPot + temp;
        }
        pot = newPot;
    }

    // Contributes the specified amount to the specified player. If set = true,
    // it sets the contribution. Otherwise it adds to the contribution. If the
    // player hasn't got a contributed entry, we create it.
    public void contribute(PokerPlayer player, double amount, boolean set)
    {
        // Try to get the specified player. If an exception is throw he doesnt
        // exist, so create him. (we also check inside the try statement just in
        // case)
        try
        {
            if (contributed.get(player) == null)
            {
                contributed.put(player, new Double(0));
            }
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

    public int getCalled()
    {
        return called;
    }

    public Map<PokerPlayer, Double> getContributed()
    {
        return contributed;
    }

    // Gets the contribution of the specified player. Creates a contribution of
    // 0 if the player doesnt exist
    public double getContribution(PokerPlayer player)
    {
        try
        {
            if (contributed.get(player) == null)
            {
                contributed.put(player, new Double(0));
            }
        } catch (Exception e)
        {
            contributed.put(player, new Double(0));
        }
        return contributed.get(player);
    }

    public ArrayList<PokerPlayer> getEligible()
    {
        return eligible;
    }

    public int getId()
    {
        return id;
    }

    public PokerPlayer getPlayerAllIn()
    {
        return playerAllIn;
    }

    public PokerPhase getPokerPhase()
    {
        return pokerPhase;
    }

    public PokerTable getPokerTable()
    {
        return pokerTable;
    }

    public double getPot()
    {
        return pot;
    }

    public boolean isMain()
    {
        return main;
    }

    public void payPot(PokerPlayer playerToPay)
    {
        double rake = 0;
        if (pokerTable.getSettings().getRake() > 0) // If the table which this
                                                    // pot belongs to has a rake
        {
            rake = getPot() * pokerTable.getSettings().getRake(); // Set the
                                                                  // local rake
                                                                  // variable by
                                                                  // getting the
                                                                  // rake from
                                                                  // the amount
                                                                  // of this pot
            UltimateCards.getEconomy().depositPlayer(pokerTable.getOwner().getPlayerName(), rake); // Give
                                                                                                   // the
                                                                                                   // rake
                                                                                                   // to
                                                                                                   // the
                                                                                                   // owner
                                                                                                   // of
                                                                                                   // the
                                                                                                   // table
                                                                                                   // (not
                                                                                                   // to
                                                                                                   // his
                                                                                                   // stack,
                                                                                                   // to
                                                                                                   // his
                                                                                                   // actual
                                                                                                   // money),
                                                                                                   // then
                                                                                                   // announce
                                                                                                   // the
                                                                                                   // message
                                                                                                   // to
                                                                                                   // people
                                                                                                   // around
                                                                                                   // the
                                                                                                   // table
            Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + rake + " to " + pokerTable.getOwner().getPlayerName());
            Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + pokerTable.getOwner().getPlayerName() + "&f has been paid a rake of " + "&6" + Formatter.formatMoney(rake));
        }

        // If this is a main pot, announce a message saying that the player has
        // won the main pot. If its not the main pot, say that they won the side
        // pot.
        if (isMain())
        {
            Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + playerToPay.getPlayerName() + "&f has won the main pot of " + "&6" + Formatter.formatMoney(getPot() - rake));
        } else
        {
            Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + playerToPay.getPlayerName() + "&f has won the side pot of " + "&6" + Formatter.formatMoney(getPot() - rake));
        }

        playerToPay.setMoney(playerToPay.getMoney() + (getPot() - rake)); // Get
                                                                          // the
                                                                          // actual
                                                                          // amount
                                                                          // that
                                                                          // the
                                                                          // player
                                                                          // wins
                                                                          // by
                                                                          // subtracting
                                                                          // the
                                                                          // rake
                                                                          // from
                                                                          // the
                                                                          // pot,
                                                                          // then
                                                                          // give
                                                                          // it
                                                                          // to
                                                                          // the
                                                                          // player's
                                                                          // stack
        playerToPay.setWonThisHand(playerToPay.getWonThisHand() + (getPot() - rake));
        setPot(0);

        // If after paying this pot there is only 1 pot left (the main pot), and
        // that main pot is empty, deal the next hand.
        if (pokerTable.getPots().size() == 1 && pokerTable.getPots().get(0).getPot() == 0)
        {
            pokerTable.endHand();
        }
    }

    public void setCalled(int called)
    {
        this.called = called;
    }

    public void setContributed(Map<PokerPlayer, Double> contributed)
    {
        this.contributed = contributed;
    }

    public void setEligible(ArrayList<PokerPlayer> eligible)
    {
        this.eligible = eligible;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setMain(boolean main)
    {
        this.main = main;
    }

    public void setPlayerAllIn(PokerPlayer playerAllIn)
    {
        this.playerAllIn = playerAllIn;
    }

    public void setPokerPhase(PokerPhase pokerPhase)
    {
        this.pokerPhase = pokerPhase;
    }

    public void setPokerTable(PokerTable pokerTable)
    {
        this.pokerTable = pokerTable;
    }

    public void setPot(double pot)
    {
        this.pot = pot;
    }

    // Converts this pot into a string in the format of "[Side Pot #X] 1,213,412
    // Dollars - Player1, Player2, Player3
    // The "Players's" are the people that are eligible to win that pot.
    @Override
    public String toString()
    {
        String eligible = "";
        adjustEligible(); // Only adjust the list of eligible players if its the
                          // pre flop, flop, turn or river

        for (PokerPlayer player : this.eligible)
        {
            eligible = eligible + player.getPlayerName() + ", "; // Add to the
        }
        // String which
        // represents
        // eligible
        // players,
        // then put a
        // comma at the
        // end
        if (!main)
        {
            eligible = eligible.substring(0, eligible.length() - 2); // Delete
            // the last
            // comma at
            // the end
            // of the
            // list of
            // eligible
            // players,
            // after
            // putting
            // them
            // into the
            // eligible
            // list
        }

        if (main == true) return "&f[Main Pot #" + id + "] " + "&6" + Formatter.formatMoney(pot) + " - For everyone";
        else return "&f[Side Pot #" + id + "] " + "&6" + Formatter.formatMoney(pot) + " - " + eligible; // Return
        // the
        // string
        // that
        // represents
        // this
        // pot
    }
}
