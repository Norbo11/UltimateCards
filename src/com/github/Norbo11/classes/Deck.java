/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Deck.java
 * -Provides a deck for a table
 * -Contains methods to generate cards and add cards
 * ===================================================================================================
 */

package com.github.norbo11.classes;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.norbo11.UltimatePoker;

public class Deck
{

    public Deck(UltimatePoker p)
    {
        this.p = p;
        addCards(); //When the deck is created, add the initial cards.
    }

    UltimatePoker p;
    public List<Card> cards = new ArrayList<Card>(); //Cards left in the deck
    String[] ranks = { "Ace", "King", "Queen", "Jack", "10", "9", "8", "7", "6", "5", "4", "3", "2" };
    String[] suits = { "Spades", "Clubs", "Hearts", "Diamonds" };

    //Goes through ranks and suits, then adds them all to the deck
    public void addCards()
    {
        for (String rank : ranks)
        {
            for (String suit : suits)
            {
                cards.add(new Card(rank, suit, p)); 
            }
        }
    }

    //Generates the specified number of cards, returning an array
    public Card[] generateCards(int number)
    {
        Card[] returnValue = new Card[number];
        Random randGen = new SecureRandom();
        int rand = 0;
        // Repeat number times (effectively generating the required number of cards)
        for (int i = 0; i < number; i++)
        {
            // Make a temporary card
            Card tmp = null;
            // While that card is null
            while (tmp == null)
            {
                // Randomize a number, put it in the temp card
                rand = randGen.nextInt(51);
                tmp = cards.get(rand);
                // If that card is not null then add the card to the return value
                if (tmp != null)
                {
                    returnValue[i] = tmp;
                    // Set that card to null in the deck
                    cards.set(rand, null);
                    break;
                }
            }
        }
        return returnValue;
    }

    public void shuffle()
    {
        cards.clear();
        addCards();
    }
}
