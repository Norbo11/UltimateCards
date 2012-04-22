package com.github.Norbo11.methods;

import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.cards.PokerPlayer;
import com.github.Norbo11.table.Table;

public class MethodsMisc {

    UltimatePoker p;
    public MethodsMisc(UltimatePoker p) {
        this.p = p;
    }

    public boolean isInteger(String string)
    {
        try
        {
            int integer = Integer.parseInt(string);
            if (integer >= 0)
            {
                return true;
            } else return false;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    
    public PokerPlayer isAPokerPlayer(Player player)
    {
        for (Table table : p.tableMethods.tables)
        {
            //Go through all players in that table
            for (PokerPlayer pokerPlayer : table.players)
            {
                //If the player list contains the arugment player
                if (pokerPlayer.player == player)
                {
                    //Return the poker player
                    return pokerPlayer;
                }
            }
        }
        //If no match is found, retun null
        return null;
    }
}
