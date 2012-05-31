/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsStats.java
 * -Contains methods that handle statistics.
 * -This is everything that starts with /poker stats and a bit more.
 * ===================================================================================================
 */

package com.github.norbo11.stats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.database.SQLColumn;
import com.github.norbo11.database.SQLValue;

public class MethodsStats
{

    public MethodsStats(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    //Checks the specified player's stats
    public void check(Player player, String toCheck) throws Exception
    {
        ResultSet rs = null;
        // If the player has not specified a player that he wants to check the stats of
        if (toCheck == null)
        {
            // Check if he is a poker player at the time. If he is, display his stats from his stats object in his pokerPlayer class.
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(player);
            if (pokerPlayer != null)
            {
                displayStats(player, pokerPlayer);
                return;
            }

            // If he is not a poker player at the time, check if he has any entries in the database. If not, display a message. If yes, display those stats by creating
            // a temporary Stats object in the displayStats method.
            rs = p.methodsDatabase.getRow("playerName", player.getName());
            if (rs.next() == true) displayStats(player, player.getName(), rs);
            else p.methodsError.noStatsAvailable(player);
        }
        // If the player DID specify a player that he wants to check the stats of
        else
        {
            // Check if that player is a poker player at the time. If he is, display his stats from his stats object in his pokerPlayer class.
            PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(toCheck);
            if (pokerPlayer != null)
            {
                displayStats(player, pokerPlayer);
                return;
            }

            // If he is not a poker player at the time, check if he has any entries in the database. If not, display a message. If yes, display those stats by creating
            // a temporary Stats object in the displayStats method.
            rs = p.methodsDatabase.getRow("playerName", toCheck);
            if (rs.next() == true) displayStats(player, toCheck, rs);
            else p.methodsError.noStatsAvailable(player, toCheck);
        }
    } 
    
    public Stats createStats(PokerPlayer player)
    {
        try
        {
            if (p.methodsDatabase.getRow("playerName", player.name).next() == false) p.methodsDatabase.addRow(getStatHeaders(), new SQLValue(p, player.name));
            return new Stats(player.name, player, p.methodsDatabase.getRow("playerName", player.name), p);
        } catch (SQLException e)
        {
            p.methodsMisc.catchException(e);
            return null;
        }
    }

    public Stats createStats(String player)
    {
        try
        {
            if (p.methodsDatabase.getRow("playerName", player).next() == false) p.methodsDatabase.addRow(getStatHeaders(), new SQLValue(p, player));
            return new Stats(player, null, p.methodsDatabase.getRow("playerName", player), p);
        } catch (SQLException e)
        {
            p.methodsMisc.catchException(e);
            return null;
        }
    }

    public void displayStats(Player player, PokerPlayer toCheck)
    {
        for (Stat stat : toCheck.stats.statsList)
            if (stat.toString() != null) player.sendMessage(p.PLUGIN_TAG + stat.toString());
    }

    public void displayStats(Player player, String toCheck, ResultSet rs) throws Exception
    {
        Stats stats = new Stats(toCheck, null, rs, p);
        for (Stat stat : stats.statsList)
            if (stat.toString() != null) player.sendMessage(p.PLUGIN_TAG + stat.toString());
    }

    //This creates all the headers for the database and returns them as a list.
    public List<SQLColumn> getStatHeaders()
    {
        List<SQLColumn> returnValue = new ArrayList<SQLColumn>();

        returnValue.add(new SQLColumn("playerName", "varchar(255)"));   // The name of the player that owns the stats

        returnValue.add(new SQLColumn("totalWinnings", "double"));      // Total money won
        returnValue.add(new SQLColumn("totalLosses", "double"));        // Total money lost
        returnValue.add(new SQLColumn("profit", "double"));             // Profit (totalWinnings - totalLosses)

        returnValue.add(new SQLColumn("biggestWin", "double"));         // Biggest win in 1 hand
        returnValue.add(new SQLColumn("biggestLoss", "double"));        // Biggest lost in 1 hand

        returnValue.add(new SQLColumn("amountPlayed", "int"));          // Amount of hands played
        returnValue.add(new SQLColumn("amountWon", "int"));             // Amount of times won the main pot

        returnValue.add(new SQLColumn("amountBet", "int"));             // Amount of times bet
        returnValue.add(new SQLColumn("amountRaised", "int"));          // Amount of times raised
        returnValue.add(new SQLColumn("amountCalled", "int"));          // Amount of times called
        returnValue.add(new SQLColumn("amountAllIn", "int"));           // Amount of times went all in

        returnValue.add(new SQLColumn("amountRaisedPreflop", "int"));   // Amount of times raised preflop
        returnValue.add(new SQLColumn("amountCalledPreflop", "int"));   // Amount of times called preflop
        returnValue.add(new SQLColumn("amountBetPreflop", "int"));      // Amount of times bet preflop

        returnValue.add(new SQLColumn("amountWonAtShowdown", "int"));   // Amount of times won by having the best hand at showdown
        returnValue.add(new SQLColumn("amountWentToShowdown", "int"));  // Amount of times went to showdown
        returnValue.add(new SQLColumn("amountWonDuringAllIn", "int"));  // Amount of times you win a pot when going all in

        returnValue.add(new SQLColumn("averageBetSize", "double"));     // Average bet size (averageBetSize * amountBet + betAmount) / amountBet++
        returnValue.add(new SQLColumn("aggressionFactor", "double"));   // (amountBet + amountRaised) / amountCalled

        returnValue.add(new SQLColumn("percentageVPIP", "double"));     // (amountRaised + amountCalled + amountBet (all before the flop) / amountPlayed
        returnValue.add(new SQLColumn("percentagePFR", "double"));      // (amountRaised (in preflop) / amountPlayed
        returnValue.add(new SQLColumn("percentageWins", "double"));     // amountWon / amountPlayed
        returnValue.add(new SQLColumn("percentageWinsAtShowdown", "double"));     // amountWonAtShowdown / amountWentToShowdown
        returnValue.add(new SQLColumn("percentageWinsDuringAllIn", "double"));     // amountWonDuringAlliN / amountAllIn

        return returnValue;
    }

    public void rank(Player player, String stat, String name) throws Exception
    {
        String[] columns = new String[2];
        columns[0] = "";
        if (stat.equalsIgnoreCase("winnings"))
        {
            columns[0] = "totalWinnings";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("losses"))
        {
            columns[0] = "totalLosses";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("profit"))
        {
            columns[0] = "profit";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("biggestWin"))
        {
            columns[0] = "biggestWin";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("biggestLoss"))
        {
            columns[0] = "biggestLoss";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("played"))
        {
            columns[0] = "amountPlayed";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("averageBet"))
        {
            columns[0] = "averageBetSize";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("aggressionFactor"))
        {
            columns[0] = "aggressionFactor";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("vpip"))
        {
            columns[0] = "percentageVPIP";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("pfr"))
        {
            columns[0] = "percentagePFR";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("wins"))
        {
            columns[0] = "percentageWins";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("winsShowdown"))
        {
            columns[0] = "percentageWinsAtShowdown";
            columns[1] = "playerName";
        }
        if (stat.equalsIgnoreCase("winsAllin"))
        {
            columns[0] = "percentageWinsDuringAllIn";
            columns[1] = "playerName";
        }

        if (!columns[0].equals(""))
        {
            ResultSet rs = p.methodsDatabase.getColumn(columns, true);
            int rank = 1;
            while (rs.next())
            {
                if (rs.getString("playerName").equalsIgnoreCase(name))
                {
                    if (player.getName().equalsIgnoreCase(name)) player.sendMessage(p.PLUGIN_TAG + "You are currently ranked " + p.gold + rank + p.white + " on the " + stat + " leaderboards.");
                    else player.sendMessage(p.PLUGIN_TAG + p.gold + name + p.white + " is currently ranked " + p.gold + rank + p.white + " on the " + stat + " leaderboards.");
                    return;
                }
                rank++;
            }
            p.methodsError.noStatsAvailable(player, name);
        } else p.methodsError.noSuchTopStat(player);
    }

    public void top(Player player, String stat) throws Exception
    {
        String[] columns = new String[2]; // This is an array with 2 elements - first element is the header of the column of the stat, 2nd is the player name. This is necessary because it is supplied to the getColumn method.
        String type = "";                 // This will hold the type of the stat (money, int, double or percentage) for formatting purposes.

        // Check if the stat is a valid stat then set the columns array and type.
        if (stat.equalsIgnoreCase("winnings"))
        {
            columns[0] = "totalWinnings";
            columns[1] = "playerName";
            type = "money";
        }
        if (stat.equalsIgnoreCase("losses"))
        {
            columns[0] = "totalLosses";
            columns[1] = "playerName";
            type = "money";
        }
        if (stat.equalsIgnoreCase("profit"))
        {
            columns[0] = "profit";
            columns[1] = "playerName";
            type = "money";
        }
        if (stat.equalsIgnoreCase("biggestWin"))
        {
            columns[0] = "biggestWin";
            columns[1] = "playerName";
            type = "money";
        }
        if (stat.equalsIgnoreCase("biggestLoss"))
        {
            columns[0] = "biggestLoss";
            columns[1] = "playerName";
            type = "money";
        }
        if (stat.equalsIgnoreCase("played"))
        {
            columns[0] = "amountPlayed";
            columns[1] = "playerName";
            type = "int";
        }
        if (stat.equalsIgnoreCase("averageBet"))
        {
            columns[0] = "averageBetSize";
            columns[1] = "playerName";
            type = "money";
        }
        if (stat.equalsIgnoreCase("aggressionFactor"))
        {
            columns[0] = "aggressionFactor";
            columns[1] = "playerName";
            type = "double";
        }
        if (stat.equalsIgnoreCase("vpip"))
        {
            columns[0] = "percentageVPIP";
            columns[1] = "playerName";
            type = "percentage";
        }
        if (stat.equalsIgnoreCase("pfr"))
        {
            columns[0] = "percentagePFR";
            columns[1] = "playerName";
            type = "percentage";
        }
        if (stat.equalsIgnoreCase("wins"))
        {
            columns[0] = "percentageWins";
            columns[1] = "playerName";
            type = "percentage";
        }
        if (stat.equalsIgnoreCase("winsShowdown"))
        {
            columns[0] = "percentageWinsAtShowdown";
            columns[1] = "playerName";
            type = "percentage";
        }
        if (stat.equalsIgnoreCase("winsAllin"))
        {
            columns[0] = "percentageWinsDuringAllIn";
            columns[1] = "playerName";
            type = "percentage";
        }
        if (!type.equals("")) // If the type isnt null (it would be null if none of the above conditions were true). If it is null we display a message saying that the stat type is invalid.
        {
            ResultSet rs = p.methodsDatabase.getColumn(columns, true); // Store a results set by using the getcolumn method which will sort the results for us.
            List<String> temp = new ArrayList<String>();               // This is a temporary list which will hold the player names and the values in the leaderboard
            player.sendMessage(p.PLUGIN_TAG + "Displaying poker leaderboard of " + p.gold + stat);
            player.sendMessage(p.PLUGIN_TAG + p.gold + p.LINE_STRING);
            for (int i = 1; i <= 10; i++) // Go through the first 10 rows, break if the end of the rows is reached. If the row is a valid row, read its player name and value of stat.
            {
                if (rs.next() == false) break;
                if (type.equalsIgnoreCase("int")) temp.add(p.gold + rs.getString(columns[1]) + p.white + " - " + p.gold + rs.getInt(columns[0]) + " hand(s)");
                if (type.equalsIgnoreCase("double")) temp.add(p.gold + rs.getString(columns[1]) + p.white + " - " + p.gold + rs.getDouble(columns[0]));
                if (type.equalsIgnoreCase("money")) temp.add(p.gold + rs.getString(columns[1]) + p.white + " - " + p.gold + p.methodsMisc.formatMoney(rs.getDouble(columns[0])));
                if (type.equalsIgnoreCase("percentage")) temp.add(p.gold + rs.getString(columns[1]) + p.white + " - " + p.gold + p.methodsMisc.convertToPercentage(rs.getDouble(columns[0])));
            }
            for (int i = 1; i <= 10; i++) // Display 10 messages which show the actual leaderboard to the player
            {
                String string = p.PLUGIN_TAG + i + ". ";
                try
                {
                    string = string + temp.get(i - 1);
                } catch (Exception e)
                {
                } // If we have less than 10 rows, we cant get all of them, because an exception will be thrown (the element in the list will null). So we just catch and do nothing.
                player.sendMessage(string);

            }

        } else p.methodsError.noSuchTopStat(player);
    }

}
