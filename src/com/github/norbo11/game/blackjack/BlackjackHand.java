package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.cards.Hand;

public class BlackjackHand {
    public BlackjackHand(BlackjackPlayer player, double amountBet) {
        this.player = player;
        this.amountBet = amountBet;
        id = player.getHands().size();
    }

    private Hand hand = new Hand();
    private boolean stayed;

    private double amountBet;

    private int score = 0;
    private int id;

    private BlackjackPlayer player;

    public void addCards(ArrayList<Card> cards) {
        for (Card card : cards) {
            hand.getCards().add(card);
            player.getTable().sendTableMessage("&6" + player.getPlayerName() + "&f has been dealt the " + card.toString() + (player.isSplit() ? " for hand ID &6" + id : ""));
        }
        recalculateScore();
    }

    public void bust() {
        if (!player.isSplit()) {
            player.getTable().sendTableMessage("&6" + player.getPlayerName() + "&f has gone bust!");
        } else {
            player.getTable().sendTableMessage("&6" + player.getPlayerName() + "&f's hand score &6" + score + "&f has gone bust!");
        }
    }

    public double getAmountBet() {
        return amountBet;
    }

    public Hand getHand() {
        return hand;
    }

    public BlackjackPlayer getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }

    public boolean isBust() {
        return score > 21;
    }

    public boolean isStayed() {
        return stayed;
    }

    public void recalculateScore() {
        int newScore = 0;

        for (Card card : hand.getCards()) {
            int cardScore = card.getBlackjackScore();
            newScore += cardScore;
            if (cardScore == 1) {
                newScore += 10;
                if (newScore > 21) {
                    newScore -= 10;
                }
            }
        }

        score = newScore;
    }

    public void setAmountBet(double amountBet) {
        this.amountBet = amountBet;
    }

    public void setStayed(boolean stayed) {
        this.stayed = stayed;
    }
}
