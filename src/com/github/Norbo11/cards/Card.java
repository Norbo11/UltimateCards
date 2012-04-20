package com.github.Norbo11.cards;

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
}
