package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.blackjack.BlackjackStand;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Sound;
import com.github.norbo11.util.Timers;

public class BlackjackPlayer extends CardsPlayer {
    public BlackjackPlayer(Player player, BlackjackTable table, double buyin) throws Exception {
        super(player);
        hands.add(new BlackjackHand(this, 0));
        setTable(table);
        setStartLocation(player.getLocation());
        setID(table.getEmptyPlayerID());
        setMoney(buyin);
        MapMethods.giveMap(player, "blackjack");
    }

    private boolean hitted;
    private boolean doubled;
    private double pushingAmount;
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

    public void bet(double amountToBet) {
        removeMoney(amountToBet);
        getTable().getDealer().addMoney(amountToBet);
        getHands().get(0).setAmountBet(getHands().get(0).getAmountBet() + amountToBet);
        getTable().sendTableMessage("&6" + getPlayerName() + "&f bets &6" + Formatter.formatMoney(getHands().get(0).getAmountBet()));
        getTable().autoStart();
    }

    @Override
    public boolean canPlay() {
        return getMoney() > getTable().getSettings().minBet.getValue() || getTable().getSettings().allowRebuys.getValue();
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

    public void doubleDown() {
        removeMoney(getTotalAmountBet());
        getTable().getDealer().addMoney(getTotalAmountBet());
        getHands().get(0).setAmountBet(getTotalAmountBet() * 2);
        getTable().sendTableMessage("&6" + getPlayerName() + "&f doubles down! New bet: &6" + Formatter.formatMoney(getTotalAmountBet()));
        getHands().get(0).addCards(getTable().getDeck().generateCards(1));
        displayScore();
        checkForBust();

        setDoubled(true);
        setHitted(true);
        getHands().get(0).setStayed(true);

        cancelTurnTimer();
        getTable().nextPersonTurn(this);
    }

    public ArrayList<BlackjackHand> getHands() {
        return hands;
    }

    public double getPushingAmount() {
        return pushingAmount;
    }

    @Override
    public BlackjackTable getTable() {
        return (BlackjackTable) super.getTable();
    }

    public double getTotalAmountBet() {
        double returnValue = 0;

        for (BlackjackHand hand : hands) {
            returnValue += hand.getAmountBet();
        }

        return returnValue;
    }

    public void hit(int hand) {
        getHands().get(hand).addCards(getTable().getDeck().generateCards(1));
        displayScore();
        checkForBust();
        setHitted(true);
        cancelTurnTimer();
        getTable().nextPersonTurn(this);
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
            if (hand.getScore() == getTable().getDealer().getScore()) {
                handsDrawing++;
            }
        return handsDrawing == hands.size();
    }

    public boolean isHitted() {
        return hitted;
    }

    public boolean isPushing() {
        return pushingAmount > 0;
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
            pushingAmount = 0;
        }

        getTable().getDealer().removeMoney(hand.getAmountBet() * multiplayer);
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

    public void setPushingAmount(double pushing) {
        pushingAmount += pushing;
    }

    public void split() {
        removeMoney(getHands().get(0).getAmountBet());
        getTable().getDealer().addMoney(getHands().get(0).getAmountBet());
        getHands().add(new BlackjackHand(this, getHands().get(0).getAmountBet()));

        getTable().sendTableMessage("&6" + getPlayerName() + "&f splits! New bet: &6" + Formatter.formatMoney(getTotalAmountBet()));

        Card card = getHands().get(0).getHand().getCards().get(0);
        getHands().get(0).getHand().getCards().remove(card);
        getHands().get(1).getHand().getCards().add(card);

        // Hit both hands
        getHands().get(0).recalculateScore();
        getHands().get(0).addCards(getTable().getDeck().generateCards(1));
        getHands().get(1).recalculateScore();
        getHands().get(1).addCards(getTable().getDeck().generateCards(1));
        displayScore();
        setHitted(true);

        cancelTurnTimer();
        getTable().nextPersonTurn(this);
    }

    public void stand(int hand) {
        getHands().get(hand).setStayed(true);
        getTable().sendTableMessage("&6" + getPlayerName() + "&f stands with hand score &6" + getHands().get(hand).getScore());
        cancelTurnTimer();
        getTable().nextPersonTurn(this);
    }

    @Override
    public void startTurnTimer() {
        BlackjackTableSettings settings = getTable().getSettings();
        if (settings.turnSeconds.getValue() > 0) {
            // Clear old timer
            if (getTurnTimer() != null) {
                getTurnTimer().cancel();
                setTurnTimer(null);
            }

            setTurnTimer(Timers.startTimerAsync(new Runnable() {
                @Override
                public void run() {
                    BlackjackStand stand = new BlackjackStand(getPlayer(), new String[] { "stand" });
                    getTable().sendTableMessage("&6" + getPlayerName() + "&f's turn timer has ended!");

                    try {
                        if (stand.conditions()) {
                            stand.perform();
                            return;
                        } else {
                            getTable().kick(getBlackjackPlayer(getPlayerName()));
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, settings.turnSeconds.getValue()));
        }
    }
}
