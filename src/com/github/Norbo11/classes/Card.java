package com.github.Norbo11.classes;

import com.github.Norbo11.UltimatePoker;

public class Card
{

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
        if (suit.equals("Spades") || suit.equals("Clubs")) return p.gray + rank + p.white + " of " + p.gray + suit + p.white;
        if (suit.equals("Diamonds") || suit.equals("Hearts")) return p.darkRed + rank + p.white + " of " + p.darkRed + suit + p.white;
        return " ";
    }
}
