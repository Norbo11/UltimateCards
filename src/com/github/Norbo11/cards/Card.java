package com.github.Norbo11.cards;

<<<<<<< HEAD
import org.bukkit.ChatColor;

=======
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
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
<<<<<<< HEAD
    
    public String toString()
    {
        return ChatColor.GOLD + rank + ChatColor.WHITE + " of " + ChatColor.GOLD + suit + ChatColor.WHITE;
    }
=======
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
}
