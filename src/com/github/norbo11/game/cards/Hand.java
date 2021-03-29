package com.github.norbo11.game.cards;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards = new ArrayList<Card>();

    // Clears the player's hand
    public void clearHand() {
        cards.clear();
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public String getEvalString() {
        String handString = "";

        if (cards.size() > 0) {
            for (Card card : cards) {
                handString += card.toEvalString() + " ";
            }
            handString.substring(0, handString.length() - 1);
        }

        return handString;
    }

    // Returns an array which represents the string representation of each card
    // in the player's hand
    public String[] getHand() {
        String[] returnValue = new String[cards.size()];
        int i = 0;
        // Go through all cards, add them to the temporary array and then return
        // the array.
        for (Card card : cards) {
            returnValue[i] = "[" + (i + 1) + "] " + card.toString();
            i++;
        }
        return returnValue;
    }
}
