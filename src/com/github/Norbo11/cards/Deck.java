package com.github.Norbo11.cards;

<<<<<<< HEAD
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
=======
import java.util.List;
import java.util.ArrayList;
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed

import com.github.Norbo11.UltimatePoker;

public class Deck {

    UltimatePoker p;
<<<<<<< HEAD
    List<Card> cards = new ArrayList<Card>(51);
=======
    List<Card> cards = new ArrayList<Card>();
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed

    String[] ranks = {"Ace","King","Queen","Jack","10","9","8","7","6","5","4","3","2"};
    String[] suits = {"Spades","Clubs","Hearts","Diamonds"};

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

    public void shuffle()
    {
        int i = 0;
        for (String rank : ranks)
        {
            for (String suit : suits)
            {
                cards.set(i, new Card(rank, suit, p));
                i++;
            }
        }
    }
<<<<<<< HEAD
    
    public Card[] generateCards(int number)
    {
        Card[] returnValue = new Card[number];
        Random randGen = new SecureRandom();
        int rand = 0;
        for (int i = 0; i < number; i++)
        {
            rand = randGen.nextInt(51);
            returnValue[i] = cards.get(rand);
            cards.remove(i);
        }
        return returnValue;
    }
=======
>>>>>>> dc2025a689dd38f6adc063612beeb247891ae7ed
}
