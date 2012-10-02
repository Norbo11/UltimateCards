/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: PokerPlayer.java
 * -Provides necessary methods which are called when the plaeyr is playing
 * -Stuff like calling, folding, checking, going all in, and much more are all in here
 * -Note: all checks for the methods (for example, checking if the getPlayer() has enough money to
 * call) are done in the MethodsHand class.
 * ===================================================================================================
 */

package com.github.norbo11.game.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.cards.Hand;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class PokerPlayer extends CardsPlayer
{
    public static PokerPlayer getPokerPlayer(int id, PokerTable table)
    {
        if (table != null)
        {
            for (PokerPlayer pokerPlayer : table.getPokerPlayers())
                if (pokerPlayer.getID() == id) return pokerPlayer;
        }
        return null;
    }

    public static PokerPlayer getPokerPlayer(String name)
    {
        CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(name);
        return cardsPlayer instanceof PokerPlayer ? (PokerPlayer) CardsPlayer.getCardsPlayer(name) : null;
    }

    private Pot pot; // This is null if the player is not all in. It turns to an actual pot which represents the pot which the player created by going all in.
    private boolean acted; // True if the player has acted at least once
    private boolean folded;
    private boolean revealed;
    private double allIn;
    private double currentBet; // This simply represents the player's
                               // current bet in the phase. (phase = flop, turn,
                               // river, etc)
    private double totalBet; // This is the total amount that the player
                             // has bet in the hand

    private double wonThisHand;
    private Hand hand = new Hand();

    public PokerPlayer(Player player, CardsTable table, double buyin) throws Exception
    {
        setTable(table);
        setStartLocation(player.getLocation());
        setName(player.getName());
        setID(table.getEmptyPlayerID());
        setMoney(buyin);
        UltimateCards.mapMethods.giveMap(player, "poker");
    }

    public void addCards(Card[] cards)
    {
        for (Card card : cards)
        {
            getHand().getCards().add(card);
            Messages.sendMessage(getPlayer(), "You have been dealt the " + card.toString());
        }
    }

    public void allIn()
    {
        double amount = getMoney(); // Amount that the player is going all in for (all his stack)

        setAllIn(getTotalBet() + amount);
        setActed(true);

        // Bet
        setTotalBet(getTotalBet() + amount); // Increase his total bet by that amount also
        setCurrentBet(getCurrentBet() + amount); // Increase the player's current bet my the amount that he's going all in for

        double sidePot = 0; // This variable decides what the sidepot amount is
                            // going to be

        double[] temp = new double[getPokerTable().getPlayers().size()]; // New array of doubles, with the size of the  amount of players.
                                                                         // It holds all players that have bet at least the amount that the player is going all in for in that phase
        int i = 0;
        // Gets all non folded players who have bet at least the amount that the player is going all in for (in that phase) and stores the amounts that they have bet in excess in temp
        for (PokerPlayer pokerPlayer : getPokerTable().getNonFoldedPlayers())
        {
            if (getCurrentBet() >= getCurrentBet() && pokerPlayer != this)
            {
                temp[i] = getCurrentBet() - getCurrentBet();
            }
            i++;
        }
        // Iterates through temp and makes the sidepot equal to the amount that the player is short (that is, all the current bets of the players that are over the all in amount, summed up)
        for (double temp2 : temp)
        {
            sidePot = sidePot + temp2;
        }

        boolean sameAmount = false;
        for (Pot pot : getPokerTable().getPots())
            if (!pot.isMain()) if (pot.getPlayerAllIn().getAllIn() == getAllIn())
            {
                double allInCover = 0;
                for (Pot pot2 : getPokerTable().getPots())
                    if (!pot2.isMain() && pot2.getPlayerAllIn() != this) if (pot2.getPlayerAllIn().getAllIn() > getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).getContribution(this))
                    {
                        allInCover = allInCover + (getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).getContribution(pot2.getPlayerAllIn()) - getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).getContribution(this));
                        getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).contribute(this, getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).getContribution(pot2.getPlayerAllIn()) - getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).getContribution(this), false);
                        getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot2) - 1).adjustPot();
                    }
                getPokerTable().getPots().get(getPokerTable().getPots().indexOf(getPokerTable().getLatestPot()) - 1).contribute(this, amount - allInCover, false);
                getPokerTable().getPots().get(getPokerTable().getPots().indexOf(getPokerTable().getLatestPot()) - 1).adjustPot();
                sameAmount = true;
                break;
            }
        if (sidePot > 0 && !sameAmount)
        {
            // Makes a new pot with the obtained sidepot amount, adds the pot to
            // the list of table pots, and sets the latestPot variable to the
            // created pot.
            setPot(new Pot(this, getPokerTable(), sidePot, getPokerTable().getPots().size()));
            getPokerTable().getPots().add(getPot());
            getPokerTable().setLatestPot(getPot());
            // This for loop goes through every non folded player. If the
            // player has bet more on the current phase than the
            // player going all in,
            // take how much we are short of their current bet. Then lower their
            // contribution amount on the pot before the side pot just created,
            // created and put it in the side pot.
            // double boosack = 0;
            for (PokerPlayer pokerPlayer : getPokerTable().getNonFoldedPlayers())
                if (getCurrentBet() >= this.getCurrentBet() && pokerPlayer != this)
                {
                    getPokerTable().getPots().get(getPokerTable().getPots().indexOf(getPot()) - 1).contribute(pokerPlayer, getPokerTable().getPots().get(getPokerTable().getPots().indexOf(getPot()) - 1).getContribution(pokerPlayer) - (getCurrentBet() - this.getCurrentBet()), true);
                    this.getPot().contribute(pokerPlayer, getCurrentBet() - this.getCurrentBet(), false);
                    // boosack = boosack + (getCurrentBet() -
                    // this.getCurrentBet());
                }
            // This goes through every single pot and contributes whatever is
            // needed to each pot (whatever is needed is defined by the
            // player's all in amount of that pot)
            // We have to match that amount so we set our contribution level to
            // that amount. We also store the total amount of money we covered
            // so that we can take it
            // away from our all in amount once we contribute to the pot before
            // our side pot.
            double allInCover = 0;
            for (Pot pot : getPokerTable().getPots())
                if (!pot.isMain() && pot.getPlayerAllIn() != this) if (pot.getPlayerAllIn().getAllIn() > getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(this))
                {
                    allInCover = allInCover + (getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(pot.getPlayerAllIn()) - getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(this));
                    getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).contribute(this, getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(pot.getPlayerAllIn()) - getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(this), false);
                    getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).adjustPot();
                }
            getPokerTable().getPots().get(getPokerTable().getPots().indexOf(getPot()) - 1).contribute(this, amount - allInCover, false);
            getPokerTable().getPots().get(getPokerTable().getPots().indexOf(getPot()) - 1).adjustPot();
        } else if (sidePot == 0 && !sameAmount)
        {
            getPokerTable().setCurrentBet(getCurrentBet());
            double allInCover = 0;
            for (Pot pot : getPokerTable().getPots())
                if (!pot.isMain() && pot.getPlayerAllIn() != this) if (pot.getPlayerAllIn().getAllIn() > getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(this))
                {
                    allInCover = allInCover + (getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(pot.getPlayerAllIn()) - getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(this));
                    getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).contribute(this, getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(pot.getPlayerAllIn()) - getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).getContribution(this), false);
                    getPokerTable().getPots().get(getPokerTable().getPots().indexOf(pot) - 1).adjustPot();
                }
            getPokerTable().getLatestPot().contribute(this, amount - allInCover, false);
            getPokerTable().getLatestPot().adjustPot();
        }

        getPokerTable().adjustPots();
        // Since the player is going all in, deduct all of his money
        setMoney(0);

        // Send messages
        Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f is all in for " + "&6" + Formatter.formatMoney(amount) + "&f! (Total: " + "&6" + Formatter.formatMoney(getTotalBet()) + "&f)");
        if (sidePot > 0 && !sameAmount)
        {
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "Created side pot of " + "&6" + Formatter.formatMoney(sidePot) + "&f!");
        }

        getPokerTable().nextPersonTurn(this); // Go to the next person's turn
    }

    @Override
    public boolean canPlay()
    {
        return getMoney() > getPokerTable().getHighestBlind();
    }

    public void clearBet()
    {
        currentBet = 0;
        acted = false;
    }

    public void fold()
    {
        setActed(true);
        setFolded(true);
        setTotalBet(0);
        getPokerTable().adjustPots();
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getPlayerName() + "&f folds.");
        getPokerTable().nextPersonTurn(this);
    }

    public double getAllIn()
    {
        return allIn;
    }

    public double getCurrentBet()
    {
        return currentBet;
    }

    public Hand getHand()
    {
        return hand;
    }

    public PokerTable getPokerTable()
    {
        return (PokerTable) getTable();
    }

    public Pot getPot()
    {
        return pot;
    }

    public double getTotalBet()
    {
        return totalBet;
    }

    public double getWonThisHand()
    {
        return wonThisHand;
    }

    public boolean isActed()
    {
        return acted;
    }

    public boolean isFolded()
    {
        return folded;
    }

    public boolean isRevealed()
    {
        return revealed;
    }

    // Makes this player posts a blind. The argument should be one of the
    // three: "small" - for the small blind "big" - for the big blind "ante" -
    // for the ante
    public void postBlind(String blind)
    {
        PokerTable table = getPokerTable();
        PokerTableSettings settings = (PokerTableSettings) table.getCardsTableSettings();
        // Go through all possible blind types and set the amount variable to
        // the corresponding value in the table settings
        double amount = 0;
        if (blind.equals("small blind"))
        {
            amount = settings.getSb();
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f has posted the small blind (" + "&6" + Formatter.formatMoney(amount) + "&f)");
        }
        if (blind.equals("big blind"))
        {
            amount = settings.getBb();
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f has posted the big blind (" + "&6" + Formatter.formatMoney(amount) + "&f)");
        }
        if (blind.equals("ante"))
        {
            amount = settings.getAnte();
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f has posted the ante (" + "&6" + Formatter.formatMoney(amount) + "&f)");
        }

        // Set the player's current bet to the blind, add the blind to
        // their total amount, set the table's current bet to the amount, add
        // the blind to the main pot, deduct money from the player
        currentBet = amount;
        totalBet = totalBet + amount;
        table.setCurrentBet(amount);
        table.getLatestPot().contribute(this, amount, false);
        table.getLatestPot().setPot(table.getLatestPot().getPot() + amount);
        setMoney(getMoney() - amount);
    }

    public void setActed(boolean acted)
    {
        this.acted = acted;
    }

    public void setAllIn(double allIn)
    {
        this.allIn = allIn;
    }

    public void setCurrentBet(double currentBet)
    {
        this.currentBet = currentBet;
    }

    public void setFolded(boolean folded)
    {
        this.folded = folded;
    }

    public void setPot(Pot pot)
    {
        this.pot = pot;
    }

    public void setRevealed(boolean revealed)
    {
        this.revealed = revealed;
    }

    public void setTotalBet(double totalBet)
    {
        this.totalBet = totalBet;
    }

    public void setWonThisHand(double wonThisHand)
    {
        this.wonThisHand = wonThisHand;
    }

    public void tableLeave(CardsPlayer cardsPlayer) throws Exception
    {
        PokerPlayer pokerPlayer = (PokerPlayer) cardsPlayer;
        if (pokerPlayer.getTable().isInProgress() && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated())
        {
            fold();
        }
    }
}
