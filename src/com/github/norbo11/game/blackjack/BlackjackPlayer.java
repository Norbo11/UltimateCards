package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Sound;

public class BlackjackPlayer extends CardsPlayer {
    public BlackjackPlayer(Player player, BlackjackTable table, double buyin) throws Exception {
        hands.add(new BlackjackHand(this, 0));
        setTable(table);
        setStartLocation(player.getLocation());
        setName(player.getName());
        setID(table.getEmptyPlayerID());
        setMoney(buyin);
        MapMethods.giveMap(player, "blackjack");
    }

    private boolean hitted;

    private boolean doubled;
    private double pushing;
    private ArrayList<BlackjackHand> hands = new ArrayList<BlackjackHand>();

    public static BlackjackPlayer getBlackjackPlayer(int id, BlackjackTable table) {
        if (table != null) {
            for (BlackjackPlayer blackjackPlayer : table.getBlackjackPlayers())
                if (blackjackPlayer.getID() == id) return blackjackPlayer;
        }
        return null;
    }

    public static BlackjackPlayer getBlackjackPlayer(String name) {
        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(name);
        return cardsPlayer instanceof BlackjackPlayer ? (BlackjackPlayer) CardsPlayer.getCardsPlayer(name) : null;
    }

    @Override
    public boolean canPlay() {
        return getMoney() > getBlackjackTable().getSettings().getMinBet() || getBlackjackTable().getSettings().isAllowRebuys();
    }

    public void checkForBust() {
        for (BlackjackHand hand : hands)
            if (hand.getScore() > 21) {
                hand.bust();
            }
    }

    public void clearHandBets() {
        for (BlackjackHand hand : hands) {
            if (!isPushing()) {
                hand.setAmountBet(0);
            }
            hand.setStayed(false);
        }
    }

    public void clearHands() {
        if (hands.size() > 1) {
            hands.remove(1);
        }
        hands.get(0).getHand().clearHand();
    }

    public void displayScore() {
        getTable().sendTableMessage("&6" + getPlayerName() + "&f's score: &6" + scoreToString());
    }

    public BlackjackTable getBlackjackTable() {
        return (BlackjackTable) getTable();
    }

    public ArrayList<BlackjackHand> getHands() {
        return hands;
    }

    public double getPushing() {
        return pushing;
    }

    public double getTotalAmountBet() {
        double returnValue = 0;

        for (BlackjackHand hand : hands) {
            returnValue += hand.getAmountBet();
        }

        return returnValue;
    }

    public boolean isBustOnAllHands() {
        int handsBusted = 0;
        for (BlackjackHand hand : hands)
            if (hand.isBust()) {
                handsBusted++;
            }
        return handsBusted == hands.size();
    }

    public boolean isDoubled() {
        return doubled;
    }

    public boolean isDrawing() {
        int handsDrawing = 0;
        for (BlackjackHand hand : hands)
            if (hand.getScore() == getBlackjackTable().getDealer().getScore()) {
                handsDrawing++;
            }
        return handsDrawing == hands.size();
    }

    public boolean isHitted() {
        return hitted;
    }

    public boolean isPushing() {
        return pushing > 0;
    }

    public boolean isSplit() {
        return hands.size() == 2;
    }

    public boolean isStayedOnAllHands() {
        int handsStayed = 0;

        for (BlackjackHand hand : hands) {
            if (hand.isStayed() || hand.isBust()) {
                handsStayed++;
            }
        }

        return handsStayed == hands.size();
    }

    public void pay(BlackjackHand hand) {
        double multiplayer = hand.getScore() == 21 && !hand.getPlayer().isHitted() ? 2.5 : 2;
        if (isPushing()) {
            pushing = 0;
        }

        getBlackjackTable().getDealer().removeMoney(hand.getAmountBet() * multiplayer);
        giveMoney(hand.getAmountBet() * multiplayer);

        getTable().sendTableMessage("&6" + getPlayerName() + "&f has won &6" + Formatter.formatMoney(hand.getAmountBet() * multiplayer) + "&f (" + multiplayer + "x) for hand score &6" + hand.getScore());
        Sound.won(getPlayer());
    }

    public boolean playingThisHand() {
        for (BlackjackHand hand : hands)
            if (hand.getAmountBet() > 0) return true;
        return false;
    }

    public void recalculateScore() {
        for (BlackjackHand hand : hands) {
            hand.recalculateScore();
        }
    }

    public boolean sameHoleCards() {
        try {
            return hands.get(0).getHand().getCards().get(0).getRank() == hands.get(0).getHand().getCards().get(1).getRank();
        } catch (Exception e) {
            return false;
        }
    }

    public String scoreToString() {
        String returnValue = hands.get(0).getScore() + "";
        if (hands.size() == 2) {
            returnValue += " or " + hands.get(1).getScore();
        }
        return returnValue;
    }

    public void setDoubled(boolean doubled) {
        this.doubled = doubled;
    }

    public void setHitted(boolean hitted) {
        this.hitted = hitted;
    }

    public void setPushing(double pushing) {
        this.pushing += pushing;
    }
}
