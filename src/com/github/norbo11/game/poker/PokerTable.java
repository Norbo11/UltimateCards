package com.github.norbo11.game.poker;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.ReturnMoney;

public class PokerTable extends CardsTable
{
    public static ArrayList<PokerTable> getPokerTables()
    {
        ArrayList<PokerTable> returnValue = new ArrayList<PokerTable>();

        for (CardsTable table : getTables())
        {
            returnValue.add((PokerTable) table);
        }

        return returnValue;
    }

    // Generic vars set by constructor

    // Generic vars
    private int button;         // Represents the index of the player that is on the button (in the list 'players')

    private ArrayList<Card> board = new ArrayList<Card>(); 
    private ArrayList<PokerPlayer> showdownPlayers = new ArrayList<PokerPlayer>();
    private ArrayList<PokerPlayer> playersThisHand = new ArrayList<PokerPlayer>();

    public PokerTable(Player owner, String name, int ID, Location location, double buyin) throws Exception
    {
        // Set the table core properties
        setOwner(new PokerPlayer(owner, this, buyin));
        setName(name);
        setID(ID);
        setLocation(location);

        getPlayers().add(getOwner()); // Add the owner to the sitting
                                      // player list
        setCardsTableSettings(new PokerTableSettings(this));
    }


    public boolean canPlay(CardsPlayer player)
    {
        return player.isEliminated() == true && player.getMoney() > getHighestBlind();
    }

    public void clearBets()
    {
        for (CardsPlayer player : getPlayers())
        {
            ((PokerPlayer) player).clearBet();
        }
    }

    // Goes through every player and clears their bets/pots/acted status.
    // This is called at the start of every phase too
    @Override
    public void clearPlayerVars()
    {
        for (CardsPlayer player : getPlayers())
        {
            PokerPlayer pokerPlayer = (PokerPlayer) player;

            pokerPlayer.clearBet();
            pokerPlayer.setFolded(false);
            pokerPlayer.setRevealed(false);
            pokerPlayer.setPot(0);
            pokerPlayer.setAllIn(0);
            pokerPlayer.setTotalBet(0);
        }
    }


    public void continueHand()
    {
        setToBeContinued(false);
        deal();
    }

    // Method to deal a brand new hand
    @Override
    public void deal()
    {
        shiftIDs();
        decidePlayersThisHand();
        // If there are enough players to play another hand, then do so
        if (canDeal())
        {
            setHandNumber(getHandNumber() + 1);
            
            Messages.sendToAllWithinRange(getLocation(), "Dealing hand number &6" + getHandNumber());
            Messages.sendToAllWithinRange(getLocation(), "&6" + UltimateCards.getLineString());
            
            setInProgress(true);
            
            getDeck().shuffle(); // Shuffle the deck
            board.clear(); // Clear the community cards
            clearPlayerVars(); // Goes through every player and clears their bets/pots/acted status, etc
            moveButton();
            dealCards();
            phasePreflop();
        }
    }

    public void decidePlayersThisHand()
    {
        playersThisHand.clear();
        
        for (PokerPlayer player : getPokerPlayers())
        {
            if (player.canPlay())
            {
                playersThisHand.add(player);
            }
        }
    }

    // Deal cards and clear variables for non-eliminated players
    @Override
    public void dealCards()
    {
        // Go through all players, clear their hands and add their cards.
        for (PokerPlayer pokerPlayer : getPokerPlayers())
            if (!pokerPlayer.isEliminated())
            {
                pokerPlayer.getHand().clearHand();
                pokerPlayer.addCards(getDeck().generateCards(2));
            }
    }

    @Override
    public void deleteTable()
    {
        if (getCurrentPhase() != PokerPhase.HAND_END)
        {
            // Displays a message, returns money for every player, and removes
            // the table
            Messages.sendToAllWithinRange(getLocation(), "Table ID '" + "&6" + getName() + "&f', ID #" + "&6" + getID() + " &fhas been deleted!");
            ReturnMoney.returnMoney(this);
            CardsTable.getTables().remove(this);
        } else
        {
            ErrorMessages.tableHasPots(getOwner().getPlayer());
        }
    }

    // Method to display the board. If who is null, display to everyone around
    // the table. Otherwise, display to just the player specified in "who".
    public void displayBoard(Player who)
    {
        if (who == null)
        {
            Messages.sendToAllWithinRange(getLocation(), "&6" + UltimateCards.getLineString());
            Messages.sendToAllWithinRange(getLocation(), "Community Cards: ");
            int i = 1;
            for (Card card : board)
            {
                Messages.sendToAllWithinRange(getLocation(), "[" + i + "] " + card.toString());
                i++;
            }
        } else
        {
            Messages.sendMessage(who, "&6" + UltimateCards.getLineString());
            Messages.sendMessage(who, "Community Cards: ");
            int i = 1;
            for (Card card : board)
            {
                Messages.sendMessage(who, "[" + i + "] " + card.toString());
                i++;
            }
        }
    }

    @Override
    public boolean canDeal()
    {
        // Go through all players, if the player is unable to pay the biggest blind then eliminate them.
        for (PokerPlayer pokerPlayer : getPokerPlayers())   
        {
            if (pokerPlayer.getMoney() < getHighestBlind())
            {
                Messages.sendToAllWithinRange(getLocation(), "&6" + pokerPlayer.getPlayerName() + "&f has been eliminated!");
            }
        }

        // If there are not enough players to continue the hand (less than 2 non eliminated players are left)
        if (getPlayersThisHand().size() < getMinPlayers())
        {
            Messages.sendToAllWithinRange(getLocation(), "&cLess than " + getMinPlayers() + " non-eliminated left, cannot start table!");
            return false;
        }

        if (getPlayersThisHand().size() >= 23)
        {
            Messages.sendToAllWithinRange(getLocation(), "&cA poker game of 23+ players!? Are you nuts!? Cannot start table!");
            return false;
        }

        return true;
    }

    public void endHand()
    {
        setToBeContinued(true);
        for (PokerPlayer temp : getPokerPlayers())
        {
            temp.setTotalBet(0);
        }
        getShowdownPlayers().clear();
        Messages.sendToAllWithinRange(getLocation(), "All pots paid! Table owner: use " + "&6/table start" + "&f to deal the next hand.");
    }

    public ArrayList<PokerPlayer> getActedPlayers()
    {
        ArrayList<PokerPlayer> acted = new ArrayList<PokerPlayer>(); // List to hold all the players that have acted

        // Go through all players and add them to the acted list or if they are all in
        for (PokerPlayer player : getNonFoldedPlayers())
            if (player.isActed() == true || player.getAllIn() > 0)
            {
                acted.add(player);
            }

        return acted;
    }

    public PokerPlayer getActionPokerPlayer()
    {
        return (PokerPlayer) getActionPlayer();
    }

    // Returns a list of players that are all in
    public ArrayList<PokerPlayer> getAllInPlayers()
    {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : getNonFoldedPlayers())
            // Go through all players, if their money is 0, add them to the eventually returned value
            if (player.getMoney() <= 0)
            {
                returnValue.add(player);
            }
        return returnValue;
    }

    public ArrayList<Card> getBoard()
    {
        return board;
    }

    public int getButton()
    {
        return button;
    }

    public ArrayList<PokerPlayer> getContributedPlayers()
    {
    	ArrayList<PokerPlayer> contributed = new ArrayList<PokerPlayer>(); // List to hold all the players that have contributed the required amount

        // Go through all players that have not folded
        for (PokerPlayer nonFolded : getNonFoldedPlayers())
            // And add the person to contributed list if they have contributed
            // the right amount, or if they are all in
            if (nonFolded.getCurrentBet() == getCurrentBet() || nonFolded.getMoney() == 0)
            {
                contributed.add(nonFolded);
            }

        return contributed;
    }

    public double getCurrentBet()
    {
    	return getHighestCurrentBet();
        //return currentBet;
    }

    public double getHighestBlind()
    {

        double returnValue = getPokerSettings().getAnte();

        if (getPokerSettings().getBb() > returnValue)
        {
            returnValue = getPokerSettings().getBb();
        }
        if (getPokerSettings().getSb() > returnValue)
        {
            returnValue = getPokerSettings().getSb();
        }

        return returnValue;
    }

    public double getHighestCallingAmount(PokerPlayer exclude)
    {
        double highestCallingAmount = 0;
        for (CardsPlayer player : getPokerPlayersThisHand())
        {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            if (pokerPlayer.getCurrentBet() + pokerPlayer.getMoney() >= highestCallingAmount && pokerPlayer != exclude && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated())
            {
                highestCallingAmount = pokerPlayer.getCurrentBet() + pokerPlayer.getMoney();
            }
        }
        return highestCallingAmount;
    }

    public ArrayList<PokerPlayer> getPokerPlayersThisHand()
    {
        return playersThisHand;
    }


    @Override
    public int getMinPlayers()
    {
        return 2;
    }

    // Method to get the player 1 after the index specified, and loop back to the beginning if the end is reached
    @Override
    public PokerPlayer getNextPlayer(int index)
    {
        if (index + 1 >= getPokerPlayersThisHand().size()) return (PokerPlayer) getPokerPlayersThisHand().get((index + 1) % getPokerPlayersThisHand().size()); // If
        else return (PokerPlayer) getPokerPlayersThisHand().get(index + 1); // If the end of the players is not reached simply return the player 1 after the given index
    }

    // Returns a list of non folded players sitting at the table
    public ArrayList<PokerPlayer> getNonFoldedPlayers()
    {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();

        // Go through all players, if their folded flag is true, add them to the eventually returned value
        for (CardsPlayer player : getPokerPlayersThisHand())
        {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            if (!pokerPlayer.isFolded())
            {
                returnValue.add(pokerPlayer);
            }
        }

        return returnValue;
    }

    public ArrayList<PokerPlayer> getPokerPlayers()
    {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();

        for (CardsPlayer player : getPlayers())
        {
            returnValue.add((PokerPlayer) player);
        }

        return returnValue;
    }

    public PokerTableSettings getPokerSettings()
    {
        return (PokerTableSettings) getCardsTableSettings();
    }

    private ArrayList<PokerPlayer> getRevealedPlayers()
    {
        ArrayList<PokerPlayer> revealed = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : getNonFoldedPlayers())
            if (player.isRevealed())
            {
                revealed.add(player);
            }
        return revealed;
    }

    @Override
    public PokerTableSettings getSettings()
    {
        return (PokerTableSettings) getCardsTableSettings();
    }

    public ArrayList<PokerPlayer> getShowdownPlayers()
    {
        return showdownPlayers;
    }

    @Override
    public void kick(CardsPlayer player)
    {
        PokerPlayer pokerPlayer = (PokerPlayer) player;

        Messages.sendToAllWithinRange(getLocation(), "&6" + getOwner().getPlayerName() + "&f has kicked &6" + pokerPlayer.getPlayerName() + "&f from the table!");

        if (!pokerPlayer.isFolded() && isInProgress())
        {
            pokerPlayer.fold();
        }

        UltimateCards.getEconomy().depositPlayer(pokerPlayer.getPlayerName(), pokerPlayer.getMoney() + pokerPlayer.getTotalBet());
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + pokerPlayer.getMoney() + " to " + pokerPlayer.getPlayerName());

        if (pokerPlayer.isOnline())
        {
            pokerPlayer.getPlayer().teleport(pokerPlayer.getStartLocation());
            Messages.sendMessage(pokerPlayer.getPlayer(), "&6" + getOwner().getPlayerName() + "&c has kicked you from his/her poker table! You receive your remaining stack of &6" + Formatter.formatMoney(pokerPlayer.getMoney()));
        }

        removePlayer(pokerPlayer);
        shiftIDs();
    }

    @Override
    public ArrayList<String> listPlayers()
    {
        ArrayList<String> list = new ArrayList<String>();

        for (CardsPlayer player : getPlayers()) // Display all the players. If the player is offline make their name appear in red
        {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            String temp = "[" + pokerPlayer.getID() + "] ";

            if (pokerPlayer.isOnline())
            {
                temp = temp + "&6" + pokerPlayer.getPlayerName() + " - ";
            } else
            {
                temp = temp + "&c" + pokerPlayer.getPlayerName() + "&f - ";
            }

            if (pokerPlayer.isAction())
            {
                temp = temp.replace(pokerPlayer.getPlayerName(), ChatColor.UNDERLINE + pokerPlayer.getPlayerName() + "&6");
            }

            if (pokerPlayer.isFolded())
            {
                temp = temp.replace(pokerPlayer.getPlayerName(), ChatColor.ITALIC + pokerPlayer.getPlayerName() + "&6");
            }

            if (getChipLeader() == pokerPlayer)
            {
                temp = temp.replace(pokerPlayer.getPlayerName(), ChatColor.BOLD + pokerPlayer.getPlayerName() + "&6");
            }

            temp = temp + "&6" + Formatter.formatMoney(pokerPlayer.getMoney());
            
            list.add(temp);
        }
        
        list.add("Average stack size: &6" + Formatter.formatMoney(getAverageStack()));
        list.add("&cOFFLINE &f| &6&nACTION&r &f| &6&lCHIP LEADER");

        return list;
    }

    // Moves the button to the next player (call this when starting a new
    // hand)
    public void moveButton()
    {
        // If the button is not the last player in the list, increment the
        // button. Otherwise set button to 0.
        if (++button >= getPokerPlayersThisHand().size())
        {
            button = 0;
        }
        Messages.sendToAllWithinRange(getLocation(), "Button moved to &6" + getPlayersThisHand().get(button).getPlayerName());
    }

    @Override
    // Move the action to the players after the one specified in the argument
    public void nextPersonTurn(CardsPlayer lastPlayer)
    {
        // If there is only 1 non-folded player left, announce him the winner
        if (getNonFoldedPlayers().size() == 1)
        {
            winner(getNonFoldedPlayers().get(0));
            return;
        }

        if (getCurrentPhase() != PokerPhase.SHOWDOWN)
        {

            // If there is 1 or less non-allin players left
            if (getAllInPlayers().size() >= getNonFoldedPlayers().size() - 1 && getNonFoldedPlayers().size() == getContributedPlayers().size())
            {
                phaseShowdown();
                return;
            }

            // If every non-folded player has contributed the right amount
            if (getContributedPlayers().size() == getNonFoldedPlayers().size()) // If it's preflop
            {
                if (getCurrentPhase() == PokerPhase.PREFLOP)
                {
                    PokerPlayer blind = getNextPlayer(button + 1);
                    if (blind.isActed()) // If they are the big blind and theyhave acted
                    {
                        nextPhase(); // Go to the next phase if the big blind and everyone else have acted
                        return;
                    } else
                    // If the big blind hasn't acted, then take his action and quit the method
                    {
                        setActionPlayer(blind);
                        getActionPokerPlayer().takeAction();
                        return;
                    }
                } else if (getActedPlayers().size() == getNonFoldedPlayers().size())
                {
                    nextPhase(); // If every non folded player has acted, go to the next phase
                    return;
                }
            }

            setNewActionPlayer(false, (PokerPlayer) lastPlayer);
        } else
        {
            if (getRevealedPlayers().size() == getNonFoldedPlayers().size())
            {
                phaseHandEnd();
                return;
            }

            setNewActionPlayer(true, (PokerPlayer) lastPlayer);
        }
    }

    // Go to the next phase depending on what the current phase is
    public void nextPhase()
    {
    	for (PokerPlayer p : getNonFoldedPlayers()) {
        	p.phaseOver();
        }
        if (getCurrentPhase() == PokerPhase.PREFLOP)
        {
            phaseFlop();
            return;
        }
        if (getCurrentPhase() == PokerPhase.FLOP)
        {
            phaseTurn();
            return;
        }
        if (getCurrentPhase() == PokerPhase.TURN)
        {
            phaseRiver();
            return;
        }
        if (getCurrentPhase() == PokerPhase.RIVER)
        {
            phaseShowdown();
            return;
        }
        if (getCurrentPhase() == PokerPhase.SHOWDOWN)
        {
            phaseHandEnd();
            return;
        }
    }

    // Deals the flop
    public void phaseFlop()
    {
        setCurrentPhase(PokerPhase.FLOP);
        clearBets();
        Card[] cards = getDeck().generateCards(3);
        board.add(cards[0]);
        board.add(cards[1]);
        board.add(cards[2]);
        displayBoard(null); // Specifying null in the argument displays the board to everyone
        Messages.sendToAllWithinRange(getLocation(), "Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getPokerPlayersThisHand().get(button)); // Take the action from the player AFTER the button (that would be the small blind)
    }
    
    public boolean noBetsThisRound() {
    	boolean noBetsThisRound = true;
    	for (PokerPlayer p : getNonFoldedPlayers()) {
    		if (p.getCurrentBet() > 0) {
    			noBetsThisRound = false;
    		}
    	}
    	return noBetsThisRound;
    }

    // This is called once everyone has revealed their hand
    public void phaseHandEnd()
    {
        setCurrentPhase(PokerPhase.HAND_END);
        Messages.sendToAllWithinRange(getLocation(), "List of pots:");
        int i = 0;
        for (PokerPlayer p : getNonFoldedPlayers()) {
        	Messages.sendToAllWithinRange(getLocation(), "[ID: " + i + "] " + p.getPlayerName() + " - " + Formatter.formatMoney(p.getTotalPot()));
        	i++;
        }
        Messages.sendToAllWithinRange(getLocation(), "Table owner: Please use " + "&6/poker pay [player ID]" + "&f to pay the winner(s). You can now also modify settings of the table.");
        Messages.sendToAllWithinRange(getLocation(), "Players: use " + "&6/cards rebuy [amount]" + "&f to add more money to your stacks.");
        raiseBlinds();

        setInProgress(false);
    }

    // Deals the preflop
    public void phasePreflop()
    {
        setCurrentPhase(PokerPhase.PREFLOP);
        postBlinds();
        int i = 0;
        for (PokerPlayer p : getNonFoldedPlayers()) {
        	Messages.sendToAllWithinRange(getLocation(), "[ID: " + i + "] " + p.getPlayerName() + " - " + Formatter.formatMoney(p.getTotalPot()));
        	i++;
        }
        Messages.sendToAllWithinRange(getLocation(), "Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getNextPlayer(button + 1));
    }

    // Deals the river
    public void phaseRiver()
    {
        setCurrentPhase(PokerPhase.RIVER);
        clearBets();
        board.add(getDeck().generateCards(1)[0]);
        displayBoard(null); // Null in the argument makes it display the board
                            // to everyone
        Messages.sendToAllWithinRange(getLocation(), "Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getPokerPlayersThisHand().get(button));
    }

    // Showdown method
    public void phaseShowdown()
    {
        setCurrentPhase(PokerPhase.SHOWDOWN);
        Messages.sendToAllWithinRange(getLocation(), "Showdown time!");

        for (PokerPlayer player : getNonFoldedPlayers())
        {
            showdownPlayers.add(player);
        }

        if (board.size() != 5) // If somehow the board doesnt have 5 cards (an
                               // all in made the hand end early, for example)
        {
            // Generate required cards
            Card[] cards = getDeck().generateCards(5 - board.size());
            for (Card card : cards)
            {
                board.add(card);
            }
        }

        displayBoard(null);

        Messages.sendToAllWithinRange(getLocation(), "Use " + "&6/poker reveal" + "&f to reveal your hand, or " + "&6/poker muck" + "&f to muck.");
        nextPersonTurn(getNextPlayer(button)); // Get the action of the player AFTER the button (the small blind)
    }

    // Deal the turn
    public void phaseTurn()
    {
        setCurrentPhase(PokerPhase.TURN);
        clearBets();
        board.add(getDeck().generateCards(1)[0]);
        displayBoard(null); // Specifying null displays the board to everyone
        Messages.sendToAllWithinRange(getLocation(), "Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getPokerPlayersThisHand().get(button));
    }

    @Override
    public void playerLeave(CardsPlayer player)
    {
        PokerPlayer pokerPlayer = (PokerPlayer) player;
        if (isInProgress() && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated())
        {
            pokerPlayer.fold();
        }
        UltimateCards.mapMethods.restoreMap(player.getPlayer());
    }

    @Override
    public void playerSit(Player player, double buyin) throws Exception
    {
        getPlayers().add(new PokerPlayer(player, this, buyin));
    }

    // Post the blinds for every player on the table
    public void postBlinds()
    {
        // Post antes if there is one
        if (((PokerTableSettings) getCardsTableSettings()).getAnte() > 0)
        {
            for (CardsPlayer player : getPlayers())
            {
                ((PokerPlayer) player).postBlind("ante");
            }
        }
        getNextPlayer(button).postBlind("small blind"); // Find the player 1 after the button and post his small blind
        getNextPlayer(button + 1).postBlind("big blind"); // Find the player 2 after the button and post his big blind
    }

    // Raise the blinds if the dynamic frequency is set
    public void raiseBlinds()
    {
        PokerTableSettings settings = (PokerTableSettings) getCardsTableSettings();
        // If the current hand number is a multiple of the dynamic ante
        // frequency, and dynamic ante frequency is turned on, increase the
        // blinds/ante by what it was set to most recently
        if (settings.getDynamicFrequency() > 0) if (getHandNumber() % settings.getDynamicFrequency() == 0 && getHandNumber() != 1)
        {
            settings.setAnte(settings.getAnte() + settings.getOriginalAnte());
            settings.setBB(settings.getBb() + settings.getOriginalBB());
            settings.setSB(settings.getSb() + settings.getOriginalSB());
            Messages.sendToAllWithinRange(getLocation(), "New ante: &6" + settings.getAnte() + "&f. New SB: &6" + settings.getSb() + ". New BB: &6" + settings.getBb() + "&f.");
        }
        if (settings.isMinRaiseAlwaysBB())
        {
            settings.setMinRaise(settings.getBb()); // If the min raise is
            // always the big blind,
            // then also change that
            // value.
        }
    }


    @Override
    public void returnMoney(CardsPlayer cardsPlayer)
    {
        PokerPlayer pokerPlayer = (PokerPlayer) cardsPlayer;
        if (pokerPlayer.isOnline())
        {
            pokerPlayer.getPlayer().teleport(pokerPlayer.getStartLocation());
            Messages.sendMessage(pokerPlayer.getPlayer(), "You have been paid your remaining stack of &6" + Formatter.formatMoney(pokerPlayer.getMoney() + pokerPlayer.getTotalBet()));
        }
        UltimateCards.getEconomy().depositPlayer(pokerPlayer.getPlayerName(), pokerPlayer.getMoney() + pokerPlayer.getTotalBet());
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + Double.toString(pokerPlayer.getMoney() + pokerPlayer.getTotalBet()) + " to " + pokerPlayer.getPlayerName());
    }

    public void setNewActionPlayer(boolean ignoreAllIn, PokerPlayer lastPlayer)
    {
        for (CardsPlayer cardsPlayer : getRearrangedPlayers(getNextPlayer(getPlayersThisHand().indexOf(lastPlayer))))
        {
            PokerPlayer player = (PokerPlayer) cardsPlayer;
            System.out.println("Testing player " + player.getPlayerName());
            System.out.println("Reavealed " + player.isRevealed());
            System.out.println("Folded " + player.isFolded());
            System.out.println("Ignote " + ignoreAllIn);
            System.out.println("Allin " + player.getAllIn());
            
            if (!player.isRevealed() && 
                !player.isFolded() && 
                (ignoreAllIn || player.getAllIn() == 0)
            )
            {
                setActionPlayer(player);
                break;
            }
        }

        getActionPokerPlayer().takeAction();
    }

    public void setShowdownPlayers(ArrayList<PokerPlayer> showdownPlayers)
    {
        this.showdownPlayers = showdownPlayers;
    }

    // This method makes sure that every player ID is equal to their index in the player list. This should be called whenever a player is removed.
    @Override
    public void shiftIDs()
    {
        for (int i = 0; i < getPlayers().size(); i++)
            if (getPlayers().get(i).getID() != i)
            {
                getPlayers().get(i).setID(i);
            }
    }

    // Method used to pay a winner if everyone else has folded
    public void winner(PokerPlayer player)
    {
        setInProgress(false);
        setToBeContinued(true);
        Messages.sendToAllWithinRange(getLocation(), "Everybody except &6" + player.getPlayerName() + "&f folded!");

        player.payPot();
    }

    public ArrayList<PokerPlayer> getCallablePlayers(double amountToBet, PokerPlayer exclude)
    {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();

        for (PokerPlayer temp : getNonFoldedPlayers())
        {
            if (temp.hasMoney(amountToBet - temp.getCurrentBet()) && temp != exclude)
            {
                returnValue.add(temp);
            }
        }
        
        return returnValue;
    }

    @Override
    public ArrayList<CardsPlayer> getPlayersThisHand()
    {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>();
        
        for (PokerPlayer player : playersThisHand)
        {
            returnValue.add(player);
        }
        
        return returnValue;
    }
    
    public double getHighestPot() {
    	double highest = 0;
    	for (PokerPlayer p : getNonFoldedPlayers()) {
    		if (p.getTotalPot() > highest) {
    			highest = p.getTotalPot();
    		}
    	}
    	return highest;
    }
    
    public double getHighestCurrentBet() {
    	double highest = 0;
    	for (PokerPlayer p : getNonFoldedPlayers()) {
    		if (p.getCurrentBet() > highest) {
    			highest = p.getCurrentBet();
    		}
    	}
    	return highest;
    }
}
