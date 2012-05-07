/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Card.java
 * -Class file that holds a simple card
 * -Contains a toString() method which automatically returns a colored string representing the card.
 * ===================================================================================================
 */

package com.github.norbo11.classes;

import com.github.norbo11.UltimatePoker;

public class Card
{

    public Card(String rank, String suit, UltimatePoker p)
    {
        this.p = p;

        this.rank = rank;
        this.suit = suit;
    }

    UltimatePoker p;
    String rank;

    String suit;

    public String toString()
    {
        if (suit.equals("Spades") || suit.equals("Clubs")) return p.gray + rank + " of " + suit + p.white;
        if (suit.equals("Diamonds") || suit.equals("Hearts")) return p.darkRed + rank + " of " + suit + p.white;
        return "";
    }
}
