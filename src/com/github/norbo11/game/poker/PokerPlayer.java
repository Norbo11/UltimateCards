package com.github.norbo11.game.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.cards.Hand;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.Sound;

public class PokerPlayer extends CardsPlayer {
    public PokerPlayer(Player player, CardsTable table, double buyin) throws Exception {
        setTable(table);
        setStartLocation(player.getLocation());
        setName(player.getName());
        setID(table.getEmptyPlayerID());
        setMoney(buyin);
        MapMethods.giveMap(player, "poker");
    }

    private boolean acted; // True if the player has acted at least once
    private boolean folded;
    private boolean revealed;
    private double currentBet; // This simply represents the player's current bet in the phase. (phase = flop, turn, river, etc)
    private double totalBet; // This is the total amount that the player has bet in the hand
    private double pot; // This players personal pot
    private double deltaPot; // This players personal pot this round
    private Hand hand = new Hand();

    public static PokerPlayer getPokerPlayer(int id, PokerTable table) {
        if (table != null) {
            for (PokerPlayer pokerPlayer : table.getPokerPlayers())
                if (pokerPlayer.getID() == id) return pokerPlayer;
        }
        return null;
    }

    public static PokerPlayer getPokerPlayer(String name) {
        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(name);
        return cardsPlayer instanceof PokerPlayer ? (PokerPlayer) CardsPlayer.getCardsPlayer(name) : null;
    }

    public void addCards(Card[] cards) {
        for (Card card : cards) {
            getHand().getCards().add(card);
            Messages.sendMessage(getPlayer(), "You have been dealt the " + card.toString());
        }
    }
    

    public void bet(double bet, String blind) {
        if (blind != null) {
            setCurrentBet(getCurrentBet() + bet); // If this is a blind, add to the current bet (for antes to work)
            getPokerTable().sendTableMessage("&6" + getPlayerName() + "&f posts " + Formatter.formatMoney(bet) + "&f " + blind + "&f (Total: " + "&6" + Formatter.formatMoney(getCurrentBet()) + "&f)");
        } else if (money - bet == 0) {
            getPokerTable().sendTableMessage("&6" + getPlayerName() + "&f went all in with " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
        } else if (getPokerTable().noBetsThisRound()) {
            getPokerTable().sendTableMessage("&6" + getPlayerName() + "&f bets " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
        } else if (bet > getPokerTable().getCurrentBet()) {
            getPokerTable().sendTableMessage("&6" + getPlayerName() + "&f raises to " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
        } else if (bet == getPokerTable().getCurrentBet()) {
            getPokerTable().sendTableMessage("&6" + getPlayerName() + "&f calls " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
        }

        if (blind == null) {
            setCurrentBet(bet);
        }

        for (PokerPlayer p : getPokerTable().getNonFoldedPlayers()) {
            p.resetDeltaPot();
            p.updatePot();
        }

        if (blind == null) {
            setActed(true);
            getPokerTable().nextPersonTurn(this);
        }
    }

    @Override
    public boolean canPlay() {
        return getMoney() > getPokerTable().getHighestBlind();
    }

    public void clearBet() {
        currentBet = 0;
        acted = false;
    }

    public void fold() {
        setActed(true);
        setFolded(true);
        setTotalBet(0);
        getTable().sendTableMessage("&6" + getPlayerName() + "&f folds.");
        if (getPokerTable().getActionPlayer() == this) {
            getPokerTable().nextPersonTurn(this);
        }
        Sound.lost(getPlayer());
    }

    public double getCurrentBet() {
        return currentBet;
    }

    // Converts this player's hand + the community cards into the special format used by the hand evaluator
    public com.github.norbo11.game.poker.eval.EvalHand getEvalHand() {
        return new com.github.norbo11.game.poker.eval.EvalHand(getHand().getEvalString() + " " + getPokerTable().getBoard().getEvalString());
    }

    public Hand getHand() {
        return hand;
    }

    @Override
    public double getMoney() {
        return money - getCurrentBet();
    }

    public PokerTable getPokerTable() {
        return (PokerTable) getTable();
    }

    public double getPot() {
        return pot;
    }

    public double getTotalBet() {
        return totalBet;
    }

    public double getTotalPot() {
        return pot + deltaPot;
    }

    public boolean isActed() {
        return acted;
    }

    public boolean isAllIn() {
        return getMoney() == 0;
    }

    public boolean isFolded() {
        return folded;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public void payPot() {
        payPot(1);
    }

    public void payPot(int divide) {
        double potToPay = getPot() / divide;
        double rake = 0;
        PokerTable pokerTable = getPokerTable();
        if (pokerTable.getSettings().getRake() > 0) {
            rake = potToPay * pokerTable.getSettings().getRake();

            UltimateCards.getEconomy().depositPlayer(pokerTable.getOwner(), rake);
            Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + rake + " to " + pokerTable.getOwner());
        }

        pokerTable.sendTableMessage("&6" + getPlayerName() + "&f wins &6" + Formatter.formatMoney(potToPay - rake) + (pokerTable.getSettings().getRake() > 0 ? "&f - Rake " + Formatter.formatMoney(rake) : ""));

        // Get the actual amount that the player wins by subtracting the rake from the pot, then give it to the player's stack
        giveMoney(potToPay - rake);

        for (PokerPlayer p : pokerTable.getNonFoldedPlayers()) {
            p.setPot(p.getPot() - potToPay);
        }

        Sound.won(getPlayer());
    }

    public void phaseOver() {
        pot += deltaPot;
        deltaPot = 0;

        setTotalBet(getTotalBet() + getCurrentBet());
        removeMoney(getCurrentBet());
        setCurrentBet(0);
    }

    // Makes this player posts a blind. The argument should be one of the
    // three: "small" - for the small blind "big" - for the big blind "ante" -
    // for the ante
    public void postBlind(String blind) {
        PokerTableSettings settings = (PokerTableSettings) getPokerTable().getCardsTableSettings();

        double amount = 0;
        if (blind.equals("small blind")) {
            amount = settings.getSb();
        } else if (blind.equals("big blind")) {
            amount = settings.getBb();
        } else if (blind.equals("ante")) {
            amount = settings.getAnte();
        }

        bet(amount, blind);
    }

    public void resetDeltaPot() {
        deltaPot = 0;
    }

    public void setActed(boolean acted) {
        this.acted = acted;
    }

    public void setCurrentBet(double currentBet) {
        this.currentBet = currentBet;
    }

    public void setFolded(boolean folded) {
        this.folded = folded;
    }

    public void setPot(double pot) {
        this.pot = pot;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    public void setTotalBet(double totalBet) {
        this.totalBet = totalBet;
    }

    public void tableLeave(CardsPlayer cardsPlayer) throws Exception {
        PokerPlayer pokerPlayer = (PokerPlayer) cardsPlayer;
        if (pokerPlayer.getTable().isInProgress() && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated()) {
            fold();
        }
    }

    @Override
    public String toString() {
        return getPlayerName();
    }

    public void updatePot() {
        for (PokerPlayer p : getPokerTable().getNonFoldedPlayers()) {
            if (p.getCurrentBet() >= getCurrentBet()) {
                deltaPot += getCurrentBet();
            } else {
                deltaPot += p.getCurrentBet();
            }
        }
    }
}
