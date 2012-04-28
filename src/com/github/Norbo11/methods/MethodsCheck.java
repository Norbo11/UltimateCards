package com.github.Norbo11.methods;

import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.classes.PokerPlayer;
import com.github.Norbo11.classes.Table;

public class MethodsCheck
{

    UltimatePoker p;

    public MethodsCheck(UltimatePoker p)
    {
        this.p = p;
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
                if (pokerPlayer.player == player)
                {
                    return pokerPlayer;
                }
            }
        }
        // If no match is found, return null
        return null;
    }

    public PokerPlayer isAPokerPlayer(Table table, int id)
    {
        // Go through all players in that table
        for (PokerPlayer pokerPlayer : table.players)
        {
            // If the player list contains the player we are looking for
            if (pokerPlayer.id == id)
            {
                return pokerPlayer;
            }
        }
        // If no match is found, return null
        return null;
    }

    public Table isATable(int ID)
    {
        for (Table table : p.tables)
        {
            if (table.id == ID && table != null)
            {
                return table;
            }
        }
        return null;
    }

    public boolean isDouble(String amount)
    {
        try
        {
            double dbl = Double.parseDouble(amount);
            if (dbl >= 0)
            {
                return true;
            } else
            {
                return false;
            }
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
            if (integer >= 0)
            {
                return true;
            } else
            {
                return false;
            }
        } catch (Exception e)
        {
            return false;
        }
    }

    public Table isOwnerOfTable(Player player)
    {
        for (Table table : p.tables)
        {
            if (table.owner == player)
            {
                return table;
            }
        }
        return null;
    }
}
