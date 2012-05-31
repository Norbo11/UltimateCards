/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Stat.java
 * -This is a class that represents a single statistic.
 * -It includes methods for adjusting the statistic, returning it's string representation, and more.
 * ===================================================================================================
 */

package com.github.norbo11.stats;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.database.SQLValue;

public class Stat
{
    public Stat(Stats stats, String name, String owner, ResultSet rs, UltimatePoker p)
    {
        this.name = name;
        this.stats = stats;
        this.owner = owner;
        value = 0;
        this.p = p;
        sqlvalue = new SQLValue(p, Double.toString(value));
        try
        {
            value = rs.getDouble(name);
        } catch (SQLException e)
        {
            p.methodsMisc.catchException(e);
        }
    }

    UltimatePoker p;

    public double value;    // The actual value of the stat
    public Stats stats;     // The object that is holding this stat
    public String owner;    // The owner of this stat
    public String name;     // The name of this stat
    SQLValue sqlvalue;      // The SQLValue representation of this stat

    //This method holds all the operations to currently adjust every possible stat. Make counter true if you just want to add value to the original value. Set to false if the stat is more complex.
    public void adjustStat(double value, boolean counter)
    {
        if (counter == false)
        {
            if (name.equalsIgnoreCase("profit")) this.value = stats.getStat("totalWinnings").value - stats.getStat("totalLosses").value;
            if (name.equalsIgnoreCase("biggestWin") && value > this.value)
            {
                this.value = value;
                p.methodsMisc.sendToAllWithinRange(stats.pokerPlayer.table.location, p.PLUGIN_TAG + p.gold + stats.pokerPlayer.name + p.white + " has just won his/her biggest pot EVER!");
            }
            if (name.equalsIgnoreCase("biggestLoss") && value > this.value)
            {
                this.value = value;
                p.methodsMisc.sendToAllWithinRange(stats.pokerPlayer.table.location, p.PLUGIN_TAG + p.gold + stats.pokerPlayer.name + p.white + " has just lost his/her biggest amount EVER of " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
            }
            if (name.equalsIgnoreCase("averageBetSize"))
            {
                if (stats.getStat("amountBet").value > 0 || stats.getStat("amountRaised").value > 0) this.value = (this.value * ((stats.getStat("amountBet").value + stats.getStat("amountRaised").value) - 1) + value) / (stats.getStat("amountBet").value + stats.getStat("amountRaised").value);
                else this.value = value;
            }
            if (name.equalsIgnoreCase("aggressionFactor"))
            {
                if (stats.getStat("amountCalled").value > 0) this.value = p.methodsMisc.roundDouble(((stats.getStat("amountBet").value + stats.getStat("amountRaised").value) / stats.getStat("amountCalled").value), 1);
                else this.value = stats.getStat("amountBet").value + stats.getStat("amountRaised").value;
            }
            if (name.equalsIgnoreCase("percentageVPIP")) this.value = (stats.getStat("amountRaisedPreflop").value + stats.getStat("amountCalledPreflop").value + stats.getStat("amountBetPreflop").value) / stats.getStat("amountPlayed").value;
            if (name.equalsIgnoreCase("percentagePFR")) this.value = stats.getStat("amountRaisedPreflop").value / stats.getStat("amountPlayed").value;
            if (name.equalsIgnoreCase("percentageWins")) this.value = stats.getStat("amountWon").value / stats.getStat("amountPlayed").value;
            if (name.equalsIgnoreCase("percentageWinsAtShowdown") && stats.getStat("amountWentToShowdown").value > 0) this.value = stats.getStat("amountWonAtShowdown").value / stats.getStat("amountWentToShowdown").value;
            if (name.equalsIgnoreCase("percentageWinsDuringAllIn") && stats.getStat("amountAllIn").value > 0) this.value = stats.getStat("amountWonDuringAllIn").value / stats.getStat("amountAllIn").value;
        }
        if (counter == true) this.value = this.value + value;
        // p.methodsMisc.sendToAllWithinRange(stats.pokerPlayer.table.location, p.PLUGIN_TAG + "Changed " + p.gold + name + p.white + " to " + p.gold + this.value + p.white + " for " + p.gold + stats.owner);
    }

    public SQLValue getSQLValue()
    {
        sqlvalue.value = Double.toString(value);
        return sqlvalue;
    }

    public String toString()
    {
        String returnValue = null;

        // General
        if (name.equalsIgnoreCase("totalWinnings")) returnValue = p.white + "Total Winnings: " + p.gold + p.methodsMisc.formatMoney(value);
        if (name.equalsIgnoreCase("totalLosses")) returnValue = p.white + "Total Losses: " + p.gold + p.methodsMisc.formatMoney(value);
        if (name.equalsIgnoreCase("profit")) returnValue = p.white + "Profit/Loss: " + p.gold + p.methodsMisc.formatMoney(value);

        if (name.equalsIgnoreCase("biggestWin")) returnValue = p.white + "Biggest pot won: " + p.gold + p.methodsMisc.formatMoney(value);
        if (name.equalsIgnoreCase("biggestLoss")) returnValue = p.white + "Biggest loss in one hand: " + p.gold + p.methodsMisc.formatMoney(value);

        // Counters
        if (name.equalsIgnoreCase("amountPlayed")) returnValue = p.white + "Amount of hands played: " + p.gold + (int) value;

        // Start comment
        // if (name.equalsIgnoreCase("amountWon")) returnValue = p.white + "Amount of hands won: " + p.gold + value;

        // if (name.equalsIgnoreCase("amountBet")) returnValue = p.white + "Amount of times bet: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountRaised")) returnValue = p.white + "Amount of times raised: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountCalled")) returnValue = p.white + "Amount of times called: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountAllin")) returnValue = p.white + "Amount of times all-in: " + p.gold + value;

        // if (name.equalsIgnoreCase("amountRaisedPreflop")) returnValue = p.white + "Amount of times raised pre-flop: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountCalledPreflop")) returnValue = p.white + "Amount of times called pre-flop: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountBetPreflop")) returnValue = p.white + "Amount of times bet pre-flop: " + p.gold + value;

        // if (name.equalsIgnoreCase("amountWonAtShowdown")) returnValue = p.white + "Amount of times won at showdown: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountWentToShowdown")) returnValue = p.white + "Amount of times went to showdown: " + p.gold + value;
        // if (name.equalsIgnoreCase("amountWonDuringAllIn")) returnValue = p.white + "Amount of times won at during all-in: " + p.gold + value;
        // end Comment

        // Averages
        if (name.equalsIgnoreCase("averageBetSize")) returnValue = p.white + "Average bet/raise size: " + p.gold + p.methodsMisc.formatMoney(value);
        if (name.equalsIgnoreCase("aggressionFactor")) returnValue = p.white + "Aggression factor: " + p.gold + value;

        if (name.equalsIgnoreCase("percentageVPIP")) returnValue = p.white + "Percentage VPIP: " + p.gold + p.methodsMisc.convertToPercentage(value);
        if (name.equalsIgnoreCase("percentagePFR")) returnValue = p.white + "Percentage of pre-flop raises: " + p.gold + p.methodsMisc.convertToPercentage(value);
        if (name.equalsIgnoreCase("percentageWins")) returnValue = p.white + "Percentage of wins: " + p.gold + p.methodsMisc.convertToPercentage(value);
        if (name.equalsIgnoreCase("percentageWinsAtShowdown")) returnValue = p.white + "Percentage of wins at showdown: " + p.gold + p.methodsMisc.convertToPercentage(value);
        if (name.equalsIgnoreCase("percentageWinsDuringAllIn")) returnValue = p.white + "Percentage of all-in wins: " + p.gold + p.methodsMisc.convertToPercentage(value);

        return returnValue;
    }
}
