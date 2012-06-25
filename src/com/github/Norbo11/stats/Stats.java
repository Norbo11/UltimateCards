/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Stats.java
 * -This class contains general methods for a player's statistic.
 * -Each player should have 1 instance of this class.
 * -This class has a list of the Stat object which contains every single ACTUAL stat.
 * ===================================================================================================
 */

package com.github.norbo11.stats;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.database.SQLColumn;
import com.github.norbo11.database.SQLValue;

public class Stats
{
    public Stats(String owner, PokerPlayer pokerPlayer, ResultSet rs, UltimatePoker p)
    {
        this.owner = owner;
        this.pokerPlayer = pokerPlayer;
        this.p = p;

        // This result set is supplied to every stat we create so that it can fetch it's value from the result set.
        // getStatHeaders returns a list of SQLColumn(s) which we iterate through to make our stats.
        for (SQLColumn column : p.methodsStats.getStatHeaders())
            if (!column.header.equalsIgnoreCase("playerName")) statsList.add(new Stat(this, column.header, owner, rs, p));
    }

    UltimatePoker p;

    String owner;                                           //Owner of these stats, but in a string form (this is needed when making a temporary stat object when trying to check a non poker player's stats
    PokerPlayer pokerPlayer;                                //Owner of these stats
    public List<Stat> statsList = new ArrayList<Stat>();    //This holds all the actual stats
    public boolean vpipAdjusted = false;                    //This is needed to be set to true whenever a player bets, calls or raises pre flop. This is because this statistic is counted once per hand.

    //This method should be called at every event which triggers a statistic change. The action should be bet, raise, or any of the below.
    public void adjustStats(String action, double value)
    {
        if (action.equalsIgnoreCase("bet"))
        {
            getStat("amountBet").adjustStat(1, true);
            getStat("averageBetSize").adjustStat(value, false);
            if (pokerPlayer.table.currentPhase == 0 && !vpipAdjusted)
            {
                getStat("amountBetPreflop").adjustStat(1, true);
                getStat("percentageVPIP").adjustStat(value, false);
                vpipAdjusted = true;
            }
            getStat("aggressionFactor").adjustStat(value, false);
        }

        if (action.equalsIgnoreCase("raise"))
        {
            getStat("amountRaised").adjustStat(1, true);
            getStat("averageBetSize").adjustStat(value, false);
            if (pokerPlayer.table.currentPhase == 0 && !vpipAdjusted)
            {
                getStat("amountRaisedPreflop").adjustStat(1, true);
                getStat("percentageVPIP").adjustStat(value, false);
                getStat("percentagePFR").adjustStat(0, false);
                vpipAdjusted = true;
            }
            getStat("aggressionFactor").adjustStat(value, false);
        }

        if (action.equalsIgnoreCase("call"))
        {
            getStat("amountCalled").adjustStat(1, true);
            if (pokerPlayer.table.currentPhase == 0 && !vpipAdjusted)
            {
                getStat("amountCalledPreflop").adjustStat(1, true);
                getStat("percentageVPIP").adjustStat(0, false);
                vpipAdjusted = true;
            }
            getStat("aggressionFactor").adjustStat(value, false);
        }

        if (action.equalsIgnoreCase("allIn"))
        {
            getStat("amountAllIn").adjustStat(1, true);
            getStat("percentageWinsDuringAllIn").adjustStat(value, false);
        }

        if (action.equalsIgnoreCase("potWon"))
        {
            getStat("amountWon").adjustStat(1, true);
            if (pokerPlayer.wonThisHand == 0)
            {
                getStat("amountWonAtShowdown").adjustStat(1, true);
                getStat("percentageWinsAtShowdown").adjustStat(value, false);
            }

            getStat("biggestWin").adjustStat(value, false);
            getStat("totalWinnings").adjustStat(value - pokerPlayer.wonThisHand, true);
            getStat("profit").adjustStat(value, false);

            if (pokerPlayer.allIn > 0)
            {
                getStat("amountWonDuringAllIn").adjustStat(1, true);
                getStat("percentageWinsDuringAllIn").adjustStat(value, false);
            }

            getStat("percentageWins").adjustStat(value, false);
        }

        if (action.equalsIgnoreCase("handLost"))
        {
            getStat("totalLosses").adjustStat(value, true);
            getStat("biggestLoss").adjustStat(value, false);
            getStat("profit").adjustStat(value, false);
        }

        if (action.equalsIgnoreCase("enteredShowdown"))
        {
            getStat("amountWentToShowdown").adjustStat(1, true);
            getStat("percentageWinsAtShowdown").adjustStat(0, false);
        }

        if (action.equalsIgnoreCase("startedHand"))
        {
            getStat("amountPlayed").adjustStat(1, true);
            getStat("percentageWins").adjustStat(0, false);
            getStat("percentageVPIP").adjustStat(0, false);
            getStat("percentagePFR").adjustStat(0, false);
        }
    }

    public Stat getStat(String stat)
    {
        for (Stat temp : statsList)
            if (temp.name.equalsIgnoreCase(stat)) return temp;
        return null;
    }

    public void saveStats()
    {
        List<SQLValue> temp = new ArrayList<SQLValue>();
        for (Stat stat : statsList)
            temp.add(stat.getSQLValue());
        p.methodsDatabase.modifyRow(owner, p.methodsStats.getStatHeaders(), temp);
    }
}
