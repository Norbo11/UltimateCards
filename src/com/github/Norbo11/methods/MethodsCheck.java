/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsCheck.java
 * -Holds various methods that check if something is true.
 * -Instead of returning booleans, most return the actual item that is being looked for, or null
 * if the item was not found
 * ===================================================================================================
 */

package com.github.norbo11.methods;

import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.classes.Pot;
import com.github.norbo11.classes.Table;

public class MethodsCheck
{

    public MethodsCheck(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    // Converts a string to a player, if they are online. Returns null if no player was found. Ignores case
    public Player isAPlayer(String toFind)
    {
        for (Player player : p.methodsMisc.getOnlinePlayers())
            // Go through all online players, if the player's name equals to the player we are looking for, return the player
            if (player.getName().equalsIgnoreCase(toFind)) return player;
        return null; // If no player was found return null
    }

    public PokerPlayer isAPokerPlayer(Player player)
    {
        // Go through all tables
        for (Table table : p.tables)
        {
            // Go through all players in that table
            for (PokerPlayer pokerPlayer : table.players)
            {
                // If the player list contains the player we are looking for
                if (pokerPlayer.name.equals(player.getName())) return pokerPlayer;
            }
        }
        // If no match is found, return null
        return null;
    }

    public PokerPlayer isAPokerPlayer(String player)
    {
        for (Table table : p.tables)
        {
            for (PokerPlayer pokerPlayer : table.players)
            {
                if (pokerPlayer.name.equalsIgnoreCase(player)) return pokerPlayer;
            }
        }
        return null;
    }

    public PokerPlayer isAPokerPlayer(Table table, int id)
    {
        // Go through all players in that table
        for (PokerPlayer pokerPlayer : table.players)
        {
            // If the player list contains the player we are looking for
            if (pokerPlayer != null)
            {
                if (pokerPlayer.id == id) return pokerPlayer;
            }
        }
        // If no match is found, return null
        return null;
    }

    public Pot isAPot(Table table, int id)
    {
        for (Pot pot : table.pots)
        {
            if (pot.id == id) return pot;
        }
        return null;
    }

    public Table isATable(int ID)
    {
        for (Table table : p.tables)
        {
            if (table.id == ID && table != null) return table;
        }
        return null;
    }

    public boolean isDouble(String amount)
    {
        try
        {
            double dbl = Double.parseDouble(amount);
            if (dbl >= 0) return true;
            else return false;
        } catch (Exception e)
        {
            return false;
        }
    }

    public boolean isInteger(String string)
    {
        try
        {
            int integer = Integer.parseInt(string);
            if (integer >= 0) return true;
            else return false;
        } catch (Exception e)
        {
            return false;
        }
    }

    public Table isOwnerOfTable(Player player)
    {
        PokerPlayer pokerPlayer = isAPokerPlayer(player);
        for (Table table : p.tables)
        {
            if (table.owner == pokerPlayer) return table;
        }
        return null;
    }
}
