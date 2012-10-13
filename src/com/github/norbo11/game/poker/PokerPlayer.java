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
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
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

    private boolean acted;      // True if the player has acted at least once
    private boolean folded;
    private boolean revealed;
    private double allIn;
    private double currentBet;  // This simply represents the player's current bet in the phase. (phase = flop, turn, river, etc)
    private double totalBet;    // This is the total amount that the player  has bet in the hand
    
    private double pot; // This players personal pot
    private double deltaPot; // This players personal pot this round

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
    
    public void updatePot() {
    	for (PokerPlayer p : getPokerTable().getNonFoldedPlayers()) {
    		System.out.println("Comparing " + this.getPlayerName() + "s current bet of " + this.getCurrentBet() + " with " + p.getPlayerName() + "s current bet of " + p.getCurrentBet());
    		if (p.getCurrentBet() >= this.getCurrentBet()) {
    			this.deltaPot += this.getCurrentBet();
    		} else {
    			this.deltaPot += p.getCurrentBet();
    		}
    		System.out.println("Outcome resulted in: " + this.deltaPot);
    	}
    }
    
    public void phaseOver() {
    	this.pot += this.deltaPot;
    	this.deltaPot = 0;
    	
    	setTotalBet(getTotalBet() + getCurrentBet());
    	setCurrentBet(0);
    	setMoney(getMoney() - getTotalBet());
    }
    
    public void payPot()
    {
        double rake = 0;
        PokerTable pokerTable = getPokerTable();
        if (pokerTable.getSettings().getRake() > 0)
        {
            rake = getPot() * pokerTable.getSettings().getRake();
            
            if (!UltimateCards.getPluginConfig().isRakeToStack())
            {
                UltimateCards.getEconomy().depositPlayer(pokerTable.getOwner().getPlayerName(), rake); 
                Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + rake + " to " + pokerTable.getOwner().getPlayerName());
            } else 
            {
                pokerTable.getOwner().giveMoney(rake);
            }
            
            Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + pokerTable.getOwner().getPlayerName() + "&f has been paid a rake of " + "&6" + Formatter.formatMoney(rake));
        }

        Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + getPlayerName() + "&f has won the pot of " + "&6" + Formatter.formatMoney(getPot() - rake));
       
        // Get the actual amount that the player wins by subtracting the rake from the pot, then give it to the player's stack
        giveMoney(getPot() - rake);

        double potAmount = this.getPot();
        boolean roundOver = true;
        for (PokerPlayer p : pokerTable.getNonFoldedPlayers()) {
        	p.setPot(p.getPot() - potAmount);
        	if (p.getPot() > 0) {
        		roundOver = false;
        	}
        }
        
        if (roundOver) pokerTable.endHand();
    }
    
    public void resetDeltaPot() {
    	this.deltaPot = 0;
    }
    
    public void bet(double bet)
    {
    	if (getPokerTable().noBetsThisRound()) {
    		Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f bet " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
    	} else if (bet > getPokerTable().getCurrentBet()) {
    		Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f raised to " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
    	} else if (bet == getPokerTable().getCurrentBet()) {
    		Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f called a bet of " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
    	} else {
    		Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f went all in with " + "&6" + Formatter.formatMoney(bet) + "&f (Total: " + "&6" + Formatter.formatMoney(bet + getTotalBet()) + "&f)");
    	}
    	
    	setCurrentBet(bet);
    	setActed(true);
    	
    	for (PokerPlayer p : getPokerTable().getNonFoldedPlayers()) {
    		p.resetDeltaPot();
    		p.updatePot();
    	}
    	
    	getPokerTable().nextPersonTurn(this);
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
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getPlayerName() + "&f folds.");
        if (getPokerTable().getActionPlayer() == this) getPokerTable().nextPersonTurn(this);
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

    public double getPot()
    {
        return pot;
    }
    
    public double getTotalPot() {
    	return pot + deltaPot;
    }

    public double getTotalBet()
    {
        return totalBet;
    }

    /*public double getWonThisHand()
    {
        return wonThisHand;
    }*/

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
    
    @Override
    public double getMoney() {
    	return money - getCurrentBet();
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
            amount += settings.getSb();
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f has posted the small blind (" + "&6" + Formatter.formatMoney(amount) + "&f)");
        } 
        else if (blind.equals("big blind"))
        {
            amount = settings.getBb();
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f has posted the big blind (" + "&6" + Formatter.formatMoney(amount) + "&f)");
        }
        if (blind.equals("ante"))
        {
            amount += settings.getAnte();
            Messages.sendToAllWithinRange(getPokerTable().getLocation(), "&6" + getPlayerName() + "&f has posted the ante (" + "&6" + Formatter.formatMoney(amount) + "&f)");
        }

        currentBet = amount;
        
        for (PokerPlayer p : getPokerTable().getNonFoldedPlayers()) {
    		p.resetDeltaPot();
    		p.updatePot();
    	}
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

    public void setPot(double pot)
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

    /*public void setWonThisHand(double wonThisHand)
    {
        this.wonThisHand = wonThisHand;
    }*/

    public void tableLeave(CardsPlayer cardsPlayer) throws Exception
    {
        PokerPlayer pokerPlayer = (PokerPlayer) cardsPlayer;
        if (pokerPlayer.getTable().isInProgress() && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated())
        {
            fold();
        }
    }
}
