package com.github.norbo11.game.cards;

import java.util.ArrayList;

import com.github.norbo11.util.NumberMethods;

public class Deck {
    public Deck(int amountOfDecks) {
        this.amountOfDecks = amountOfDecks;
        addCards(); // When the deck is created, add the initial cards.
    }

    private int amountOfDecks;
    private ArrayList<Card> cards = new ArrayList<Card>(); // Cards left in the
                                                           // deck

    // Goes through ranks and suits, then adds them all to the deck
    public void addCards() {
        // Decks
        for (int d = 1; d <= amountOfDecks; d++) {
            // Ranks
            for (int i = 1; i <= 13; i++) {
                // Suits
                for (int j = 0; j <= 3; j++) {
                    cards.add(new Card(i, j));
                }
            }
        }
    }

    // Generates the specified number of cards, returning an array
    public Card[] generateCards(int number) {
        Card[] returnValue = new Card[number];
        for (int i = 0; i < number; i++) {
            returnValue[i] = cards.get(NumberMethods.getRandomInteger(cards.size() - 1));

            // Set that card to null in the deck
            cards.remove(returnValue[i]);
        }
        return returnValue;
    }

    public int getAmountOfDecks() {
        return amountOfDecks;
    }

    public void setAmountOfDecks(int amountOfDecks) {
        this.amountOfDecks = amountOfDecks;
    }

    public void shuffle() {
        cards.clear();
        addCards();
    }
}
