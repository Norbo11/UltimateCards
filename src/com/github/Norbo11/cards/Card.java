package com.github.Norbo11.cards;

import org.bukkit.ChatColor;
import com.github.Norbo11.UltimatePoker;

public class Card {

    UltimatePoker p;

    String rank;
    String suit;

    public Card(String rank, String suit, UltimatePoker p)
    {
        this.p = p;

        this.rank = rank;
        this.suit = suit;
    }
    
    public String toString()
    {
        return ChatColor.GOLD + rank + ChatColor.WHITE + " of " + ChatColor.GOLD + suit + ChatColor.WHITE;
    }
}
