package com.github.Norbo11.classes;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.Norbo11.UltimatePoker;

public class Deck
{

    UltimatePoker p;
    public List<Card> cards = new ArrayList<Card>();
    String[] ranks = { "Ace", "King", "Queen", "Jack", "10", "9", "8", "7", "6", "5", "4", "3", "2" };
    String[] suits = { "Spades", "Clubs", "Hearts", "Diamonds" };

    public Deck(UltimatePoker p)
    {
        this.p = p;

        addCards();
    }

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

    public Card[] generateCards(int number)
    {
        Card[] returnValue = new Card[number];
        Random randGen = new SecureRandom();
        int rand = 0;
        //Repeat number times
        for (int i = 0; i < number; i++)
        {
            //Make a temporary card
            Card tmp = null;
            //While that card is null
            while (tmp == null)
            {
                //Randomize a number, put it in the temp card
                rand = randGen.nextInt(51);
                tmp = cards.get(rand);
                //If that card is not null then add the card to the return value
                if (tmp != null) 
                {   
                    returnValue[i] = tmp;
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
