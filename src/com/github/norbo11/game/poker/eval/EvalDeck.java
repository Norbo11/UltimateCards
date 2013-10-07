package com.github.norbo11.game.poker.eval;

/***************************************************************************
 Copyright (c) 2000:
 University of Alberta,
 Deptartment of Computing Science
 Computer Poker Research Group

 See "Liscence.txt"
 ***************************************************************************/

import java.util.Random;

/**
 * A Deck of 52 Cards which can be dealt and shuffled
 * 
 * @author Aaron Davidson
 */

@SuppressWarnings("static-access")
public class EvalDeck {
    /**
     * Constructor.
     */
    public EvalDeck() {
        position = 0;
        for (int i = 0; i < NUM_CARDS; i++) {
            gCards[i] = new EvalCard(i);
        }
    }

    /**
     * Constructor w/ shuffle seed.
     * 
     * @param seed
     *            the seed to use in randomly shuffling the deck.
     */
    public EvalDeck(long seed) {
        this();
        if (seed == 0) {
            seed = System.currentTimeMillis();
        }
        r.setSeed(seed);
    }

    public static final int NUM_CARDS = 52;
    private EvalCard[] gCards = new EvalCard[NUM_CARDS];

    private char position; // top of deck

    private Random r = new Random();

    /**
     * Obtain the number of cards left in the deck
     */
    public synchronized int cardsLeft() {
        return NUM_CARDS - position;
    }

    /**
     * Obtain the next card in the deck. If no cards remain, a null card is returned
     * 
     * @return the card dealt
     */
    public synchronized EvalCard deal() {
        return position < NUM_CARDS ? gCards[position++] : null;
    }

    /**
     * Obtain the next card in the deck. If no cards remain, a null card is returned
     * 
     * @return the card dealt
     */
    public synchronized EvalCard dealCard() {
        return extractRandomCard();
    }

    /**
     * Remove a card from within the deck.
     * 
     * @param c
     *            the card to remove.
     */
    public synchronized void extractCard(EvalCard c) {
        int i = findCard(c);
        if (i != -1) {
            EvalCard t = gCards[i];
            gCards[i] = gCards[position];
            gCards[position] = t;
            position++;
        } else {
            System.err.println("*** ERROR: could not find card " + c);
            Thread.currentThread().dumpStack();
        }
    }

    /**
     * Remove all cards in the given hand from the Deck.
     */
    public synchronized void extractHand(EvalHand h) {
        for (int i = 1; i <= h.size(); i++) {
            extractCard(h.getCard(i));
        }
    }

    /**
     * Remove and return a randomly selected card from within the deck.
     */
    public synchronized EvalCard extractRandomCard() {
        int pos = position + randInt(NUM_CARDS - position);
        EvalCard c = gCards[pos];
        gCards[pos] = gCards[position];
        gCards[position] = c;
        position++;
        return c;
    }

    /**
     * Find position of Card in Deck.
     */
    public synchronized int findCard(EvalCard c) {
        int i = position;
        int n = c.getIndex();
        while (i < NUM_CARDS && n != gCards[i].getIndex()) {
            i++;
        }
        return i < NUM_CARDS ? i : -1;
    }

    private synchronized int findDiscard(EvalCard c) {
        int i = 0;
        int n = c.getIndex();
        while (i < position && n != gCards[i].getIndex()) {
            i++;
        }
        return n == gCards[i].getIndex() ? i : -1;
    }

    /**
     * Obtain the card at a specific index in the deck. Does not matter if card has been dealt or not. If i < topCardIndex it has been dealt.
     * 
     * @param i
     *            the index into the deck (0..51)
     * @return the card at position i
     */
    public synchronized EvalCard getCard(int i) {
        return gCards[i];
    }

    /**
     * Obtain the position of the top card. (the number of cards dealt from the deck)
     * 
     * @return the top card index
     */
    public synchronized int getTopCardIndex() {
        return position;
    }

    /**
     * Return a randomly selected card from within the deck without removing it.
     */
    public synchronized EvalCard pickRandomCard() {
        return gCards[position + randInt(NUM_CARDS - position)];
    }

    private int randInt(int range) {
        return (int) (r.nextDouble() * range);
    }

    /**
     * Place a card back into the deck.
     * 
     * @param c
     *            the card to insert.
     */
    public synchronized void replaceCard(EvalCard c) {
        int i = findDiscard(c);
        if (i != -1) {
            position--;
            EvalCard t = gCards[i];
            gCards[i] = gCards[position];
            gCards[position] = t;
        }
    }

    /**
     * Places all cards back into the deck. Note: Does not sort the deck.
     */
    public synchronized void reset() {
        position = 0;
    }

    /**
     * Shuffles the cards in the deck.
     */
    public synchronized void shuffle() {
        EvalCard tempCard;
        int i, j;
        for (i = 0; i < NUM_CARDS; i++) {
            j = i + randInt(NUM_CARDS - i);
            tempCard = gCards[j];
            gCards[j] = gCards[i];
            gCards[i] = tempCard;
        }
        position = 0;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("* ");
        for (int i = 0; i < position; i++) {
            s.append(gCards[i].toString() + " ");
        }
        s.append("\n* ");
        for (int i = position; i < NUM_CARDS; i++) {
            s.append(gCards[i].toString() + " ");
        }
        return s.toString();
    }

}
