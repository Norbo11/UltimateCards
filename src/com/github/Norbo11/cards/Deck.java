package com.github.Norbo11.cards;

import java.util.List;
import java.util.ArrayList;

import com.github.Norbo11.UltimatePoker;

public class Deck {

    UltimatePoker p;
    List<Card> cards = new ArrayList<Card>();

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
}
