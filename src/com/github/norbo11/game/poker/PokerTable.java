package com.github.norbo11.game.poker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.cards.Hand;
import com.github.norbo11.game.poker.eval.HandEvaluator;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.Timers;

public class PokerTable extends CardsTable {
    @SuppressWarnings("deprecation")
    public PokerTable(String owner, String name, int id, Location location, double buyin) throws Exception {
        super(owner, name, id);
        
        if (buyin != 0) {
            if (Bukkit.getPlayer(owner) != null) {
                setOwnerPlayer(new PokerPlayer(Bukkit.getPlayer(owner), this, buyin));
                getPlayers().add(getOwnerPlayer()); // Add the owner to the sitting players list
            }
        }

        setCardsTableSettings(new PokerTableSettings(this));
        getSettings().startLocation.setValue(location);
    }

    public PokerTable(String owner, String name, int id, Location location) throws Exception {
        this(owner, name, id, location, 0);
    }

    // Generic vars
    private Hand board = new Hand();
    private ArrayList<PokerPlayer> showdownPlayers = new ArrayList<PokerPlayer>();
    private ArrayList<PokerPlayer> playersThisHand = new ArrayList<PokerPlayer>();

    public static ArrayList<PokerTable> getPokerTables() {
        ArrayList<PokerTable> returnValue = new ArrayList<PokerTable>();

        for (CardsTable table : getTables()) {
            returnValue.add((PokerTable) table);
        }

        return returnValue;
    }

    @Override
    public void autoStart() {
        if (getSettings().autoStart.getValue() > 0) {
            sendTableMessage("Next round in &6" + getSettings().autoStart.getValue() + "&f seconds... or use &6/table start");

            if (getTimerTask() != null) {
                getTimerTask().cancel();
                setTimerTask(null);
            }

            setTimerTask(Timers.startTimerAsync(new Runnable() {
                @Override
                public void run() {
                    deal();
                }
            }, getSettings().autoStart.getValue()));
        }
    }

    @Override
    public boolean canDeal() {
        // If there are not enough players to continue the hand (less than 2 non eliminated players are left)
        if (getPlayersThisHand().size() < getMinPlayers()) {
            sendTableMessage("&cLess than " + getMinPlayers() + " non-eliminated left, cannot start table!");
            return false;
        }

        if (getPlayersThisHand().size() >= 23) {
            sendTableMessage("&cA poker game of 23+ players!? Are you nuts!? Cannot start table!");
            return false;
        }

        return true;
    }

    public boolean canPlay(CardsPlayer player) {
        return player.isEliminated() == true && player.getMoney() > getHighestBlind();
    }

    public void clearBets() {
        for (CardsPlayer player : getPlayers()) {
            ((PokerPlayer) player).clearBet();
        }
    }

    // Goes through every player and clears their bets/pots/acted status.
    // This is called at the start of every phase too
    @Override
    public void clearPlayerVars() {
        for (CardsPlayer player : getPlayers()) {
            PokerPlayer pokerPlayer = (PokerPlayer) player;

            pokerPlayer.clearBet();
            pokerPlayer.setFolded(false);
            pokerPlayer.setRevealed(false);
            pokerPlayer.setPot(0);
            pokerPlayer.setTotalBet(0);
        }
    }

    public void continueHand() {
        setToBeContinued(false);
        deal();
    }

    // Method to deal a brand new hand
    @Override
    public void deal() {
        shiftIDs();
        decidePlayersThisHand();
        // If there are enough players to play another hand, then do so
        if (canDeal()) {
            setHandNumber(getHandNumber() + 1);

            sendTableMessage("Dealing hand number &6" + getHandNumber());
            sendTableMessage("&6" + UltimateCards.getLineString());

            setInProgress(true);

            getDeck().shuffle();    // Shuffle the deck
            board.getCards().clear();          // Clear the community cards
            clearPlayerVars();      // Goes through every player and clears their bets/pots/acted status, etc
            moveButton();
            dealCards();
            phasePreflop();
        }
    }

    // Deal cards and clear variables for non-eliminated players
    @Override
    public void dealCards() {
        // Go through all players, clear their hands and add their cards.
        for (PokerPlayer pokerPlayer : getPokerPlayers())
            if (!pokerPlayer.isEliminated()) {
                pokerPlayer.getHand().clearHand();
                pokerPlayer.addCards(getDeck().generateCards(2));
            }
    }

    public void decidePlayersThisHand() {
        playersThisHand.clear();

        for (PokerPlayer player : getPokerPlayers()) {
            if (player.canPlay()) {
                playersThisHand.add(player);
            }
        }
    }

    public void displayBoard(Player who, ArrayList<Card> cards) {
        if (who == null) {
            sendTableMessage("&6" + UltimateCards.getLineString());
            sendTableMessage("Community Cards: ");
            int i = 1;
            for (Card card : cards) {
                sendTableMessage("[" + i + "] " + card.toString());
                i++;
            }
        } else {
            Messages.sendMessage(who, "&6" + UltimateCards.getLineString());
            Messages.sendMessage(who, "Community Cards: ");
            int i = 1;
            for (Card card : cards) {
                Messages.sendMessage(who, "[" + i + "] " + card.toString());
                i++;
            }
        }
    }

    public void endPhaseForPlayers() {
        for (PokerPlayer p : getNonFoldedPlayers()) {
            p.phaseOver();
        }
    }

    public ArrayList<PokerPlayer> getActedPlayers() {
        ArrayList<PokerPlayer> acted = new ArrayList<PokerPlayer>(); // List to hold all the players that have acted

        // Go through all players and add them to the acted list or if they are all in
        for (PokerPlayer player : getNonFoldedPlayers())
            if (player.isActed() == true || player.isAllIn()) {
                acted.add(player);
            }

        return acted;
    }

    public PokerPlayer getActionPokerPlayer() {
        return (PokerPlayer) getActionPlayer();
    }

    // Returns a list of players that are all in
    public ArrayList<PokerPlayer> getAllInPlayers() {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : getNonFoldedPlayers())
            // Go through all players, if their money is 0, add them to the eventually returned value
            if (player.getMoney() <= 0) {
                returnValue.add(player);
            }
        return returnValue;
    }

    public Hand getBoard() {
        return board;
    }

    public ArrayList<PokerPlayer> getContributedPlayers() {
        ArrayList<PokerPlayer> contributed = new ArrayList<PokerPlayer>(); // List to hold all the players that have contributed the required amount

        // Go through all players that have not folded
        for (PokerPlayer nonFolded : getNonFoldedPlayers())
            // And add the person to contributed list if they have contributed
            // the right amount, or if they are all in
            if (nonFolded.getCurrentBet() == getCurrentBet() || nonFolded.getMoney() == 0) {
                contributed.add(nonFolded);
            }

        return contributed;
    }

    public double getCurrentBet() {
        return getHighestCurrentBet();
        // return currentBet;
    }

    public double getHighestBlind() {

        double returnValue = getSettings().ante.getValue();

        if (getSettings().bb.getValue() > returnValue) {
            returnValue = getSettings().bb.getValue();
        }
        if (getSettings().sb.getValue() > returnValue) {
            returnValue = getSettings().sb.getValue();
        }

        return returnValue;
    }

    public double getHighestCallingAmount(PokerPlayer exclude) {
        double highestCallingAmount = 0;
        for (CardsPlayer player : getPokerPlayersThisHand()) {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            if (pokerPlayer.getCurrentBet() + pokerPlayer.getMoney() >= highestCallingAmount && pokerPlayer != exclude && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated()) {
                highestCallingAmount = pokerPlayer.getCurrentBet() + pokerPlayer.getMoney();
            }
        }
        return highestCallingAmount;
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

    public double getHighestPot() {
        double highest = 0;
        for (PokerPlayer p : getNonFoldedPlayers()) {
            if (p.getTotalPot() > highest) {
                highest = p.getTotalPot();
            }
        }
        return highest;
    }

    @Override
    public int getMinPlayers() {
        return 2;
    }

    // Method to get the player 1 after the index specified, and loop back to the beginning if the end is reached
    @Override
    public PokerPlayer getNextPlayer(int index) {
        if (index + 1 >= getPokerPlayersThisHand().size()) return getPokerPlayersThisHand().get((index + 1) % getPokerPlayersThisHand().size()); // If
        else return getPokerPlayersThisHand().get(index + 1); // If the end of the players is not reached simply return the player 1 after the given index
    }

    // Returns a list of non folded players sitting at the table
    public ArrayList<PokerPlayer> getNonFoldedPlayers() {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();

        // Go through all players, if their folded flag is true, add them to the eventually returned value
        for (CardsPlayer player : getPokerPlayersThisHand()) {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            if (!pokerPlayer.isFolded()) {
                returnValue.add(pokerPlayer);
            }
        }

        return returnValue;
    }

    @Override
    public ArrayList<CardsPlayer> getPlayersThisHand() {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>();

        for (PokerPlayer player : playersThisHand) {
            returnValue.add(player);
        }

        return returnValue;
    }

    public ArrayList<PokerPlayer> getPokerPlayers() {
        ArrayList<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();

        for (CardsPlayer player : getPlayers()) {
            returnValue.add((PokerPlayer) player);
        }

        return returnValue;
    }

    public ArrayList<PokerPlayer> getPokerPlayersThisHand() {
        return playersThisHand;
    }

    private ArrayList<PokerPlayer> getRevealedPlayers() {
        ArrayList<PokerPlayer> revealed = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : getNonFoldedPlayers())
            if (player.isRevealed()) {
                revealed.add(player);
            }
        return revealed;
    }

    @Override
    public PokerTableSettings getSettings() {
        return (PokerTableSettings) getCardsTableSettings();
    }

    public ArrayList<PokerPlayer> getShowdownPlayers() {
        return showdownPlayers;
    }

    @Override
    public void kick(CardsPlayer player) {
        PokerPlayer pokerPlayer = (PokerPlayer) player;

        playerLeave(pokerPlayer);

        sendTableMessage("&6" + pokerPlayer + " &fhas been kicked from the table!", player.getPlayerName());
        if (pokerPlayer.isOnline()) {
            pokerPlayer.getPlayer().teleport(pokerPlayer.getStartLocation());
            UltimateCards.getEconomy().depositPlayer(pokerPlayer.getPlayerName(), pokerPlayer.getMoney());
            Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + Double.toString(pokerPlayer.getMoney() + pokerPlayer.getTotalBet()) + " to " + pokerPlayer);
            Messages.sendMessage(pokerPlayer.getPlayer(), "&cYou have been kicked from the table! You receive your remaining stack of &6" + Formatter.formatMoney(pokerPlayer.getMoney()));
        }

        removePlayer(player);

    }

    @Override
    public ArrayList<String> listPlayers() {
        ArrayList<String> list = new ArrayList<String>();

        for (CardsPlayer player : getPlayers()) // Display all the players. If the player is offline make their name appear in red
        {
            PokerPlayer pokerPlayer = (PokerPlayer) player;
            String temp = "[ID" + pokerPlayer.getID() + "] ";

            if (pokerPlayer.isOnline()) {
                temp = temp + "&6" + pokerPlayer.getPlayerName() + " - ";
            } else {
                temp = temp + "&c" + pokerPlayer.getPlayerName() + "&f - ";
            }

            if (pokerPlayer.isFolded()) {
                temp = temp.replace(pokerPlayer.getPlayerName(), ChatColor.ITALIC + pokerPlayer.getPlayerName() + "&6");
            }

            if (getChipLeader() == pokerPlayer) {
                temp = temp.replace(pokerPlayer.getPlayerName(), ChatColor.BOLD + pokerPlayer.getPlayerName() + "&6");
            }

            temp = temp + "&6" + Formatter.formatMoney(pokerPlayer.getMoney());

            list.add(temp);
        }

        list.add("Average stack size: &6" + Formatter.formatMoney(getAverageStack()));
        list.add("&cOFFLINE &f| &6&lCHIP LEADER");

        return list;
    }

    @Override
    // Move the action to the players after the one specified in the argument
    public void nextPersonTurn(CardsPlayer lastPlayer) {
        if (getCurrentPhase() != PokerPhase.SHOWDOWN) {

            // If there is 1 or less non-allin players left
            if (getAllInPlayers().size() >= getNonFoldedPlayers().size() - 1 && getNonFoldedPlayers().size() == getContributedPlayers().size()) {
                endPhaseForPlayers();
                phaseShowdown();
                return;
            }

            // If every non-folded player has contributed the right amount
            if (getContributedPlayers().size() == getNonFoldedPlayers().size()) {
                if (getCurrentPhase() == PokerPhase.PREFLOP) {
                    PokerPlayer blind = getNextPlayer(getButton() + 1);
                    if (blind.isActed()) {
                        nextPhase(); // Go to the next phase if the big blind and everyone else have acted
                        return;
                    } else
                    // If the big blind hasn't acted, then take his action and quit the method
                    {
                        setActionPlayer(blind);
                        getActionPokerPlayer().takeAction();
                        return;
                    }
                } else if (getActedPlayers().size() == getNonFoldedPlayers().size()) {
                    nextPhase(); // If every non folded player has acted, go to the next phase
                    return;
                }
            }

            setNewActionPlayer(false, (PokerPlayer) lastPlayer);
        } else {
            if (getRevealedPlayers().size() == getNonFoldedPlayers().size()) {
                endPhaseForPlayers();
                phaseHandEnd();
                return;
            }

            setNewActionPlayer(true, (PokerPlayer) lastPlayer);
        }
    }

    // Go to the next phase depending on what the current phase is
    public void nextPhase() {
        endPhaseForPlayers();
        if (getCurrentPhase() == PokerPhase.PREFLOP) {
            phaseFlop();
            return;
        }
        if (getCurrentPhase() == PokerPhase.FLOP) {
            phaseTurn();
            return;
        }
        if (getCurrentPhase() == PokerPhase.TURN) {
            phaseRiver();
            return;
        }
        if (getCurrentPhase() == PokerPhase.RIVER) {
            phaseShowdown();
            return;
        }
        if (getCurrentPhase() == PokerPhase.SHOWDOWN) {
            phaseHandEnd();
            return;
        }
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

    // Deals the flop
    public void phaseFlop() {
        setCurrentPhase(PokerPhase.FLOP);
        clearBets();
        ArrayList<Card> cards = getDeck().generateCards(3);
        board.getCards().addAll(cards);
        displayBoard(null, cards); // Specifying null in the argument displays the board to everyone
        sendTableMessage("Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getPokerPlayersThisHand().get(getButton())); // Take the action from the player AFTER the button (that would be the small blind)
    }

    // This is called once everyone has revealed their hand
    public void phaseHandEnd() {
        setCurrentPhase(PokerPhase.HAND_END);

        if (getPlayersThisHand().size() > 1)
        {
            /* Evaluate all hands */
            HashMap<PokerPlayer, Integer> handRanks = new HashMap<PokerPlayer, Integer>();
            for (PokerPlayer player : getNonFoldedPlayers()) {
                handRanks.put(player, HandEvaluator.rankHand(player.getEvalHand()));
            }
    
            /* Sort hand ranks */
            ArrayList<Integer> sortedRanks = new ArrayList<Integer>();
            for (Integer value : handRanks.values()) {
                sortedRanks.add(value);
            }
            Collections.sort(sortedRanks);
    
            /* Pick winners with the highest ranked hands */
            int highestRank = sortedRanks.get(sortedRanks.size() - 1);
            ArrayList<PokerPlayer> winners = new ArrayList<PokerPlayer>();
            for (Entry<PokerPlayer, Integer> entry : handRanks.entrySet()) {
                if (highestRank == entry.getValue()) {
                    winners.add(entry.getKey());
                }
            }
    
            /* Pay pots to winners */
            if (winners.size() == 1) {
                winners.get(0).payPot();
            } else {
                for (PokerPlayer player : winners) {
                    player.payPot(winners.size());
                }
            }
    
            /* Pay pots to any people that still have money invested (from split pots and such) */
            for (PokerPlayer player : getNonFoldedPlayers()) {
                if (player.getPot() > 0) {
                    player.payPot();
                }
            }
    
            /* Cleanup */
            raiseBlinds();
            setInProgress(false);
            setToBeContinued(true);
    
            for (PokerPlayer player : getPokerPlayers()) {
                player.setTotalBet(0);
                if (player.getMoney() < getHighestBlind()) {
                    sendTableMessage("&6" + player + "&f has been eliminated!");
                }
            }
    
            getShowdownPlayers().clear();
            playersThisHand.clear();
            setCurrentPhase(PokerPhase.HAND_END);

            autoStart();
        }
    }

    // Deals the preflop
    public void phasePreflop() {
        setCurrentPhase(PokerPhase.PREFLOP);
        postBlinds();
        nextPersonTurn(getNextPlayer(getButton() + 1));
    }

    // Deals the river
    public void phaseRiver() {
        setCurrentPhase(PokerPhase.RIVER);
        clearBets();
        ArrayList<Card> cards = getDeck().generateCards(1);
        board.getCards().addAll(cards);
        displayBoard(null, cards); 
        sendTableMessage("Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getPokerPlayersThisHand().get(getButton()));
    }

    // Showdown method
    public void phaseShowdown() {
        setCurrentPhase(PokerPhase.SHOWDOWN);
        sendTableMessage("Showdown time!");

        for (PokerPlayer player : getNonFoldedPlayers()) {
            showdownPlayers.add(player);
        }

        if (board.getCards().size() != 5) // If somehow the board doesnt have 5 cards (an all in made the hand end early, for example)
        {
            // Generate required cards
            ArrayList<Card> cards = getDeck().generateCards(5 - board.getCards().size());
            for (Card card : cards) {
                board.getCards().add(card);
            }
        }

        displayBoard(null, board.getCards());

        sendTableMessage("Use " + PluginExecutor.pokerReveal.getCommandString() + "&f to reveal your hand, or " + "&6/poker muck" + "&f to muck.");
        nextPersonTurn(getNextPlayer(getButton())); // Get the action of the player AFTER the button (the small blind)
    }

    // Deal the turn
    public void phaseTurn() {
        setCurrentPhase(PokerPhase.TURN);
        clearBets();
        ArrayList<Card> cards = getDeck().generateCards(1);
        board.getCards().addAll(cards);
        displayBoard(null, cards); // Specifying null displays the board to everyone
        sendTableMessage("Total amount in pots: &6" + Formatter.formatMoney(getHighestPot()));
        nextPersonTurn(getPokerPlayersThisHand().get(getButton()));
    }

    @Override
    public void playerLeave(CardsPlayer player) {
        PokerPlayer pokerPlayer = (PokerPlayer) player;
        if (isInProgress() && !pokerPlayer.isFolded() && !pokerPlayer.isEliminated()) {
            pokerPlayer.fold();
        }
        playersThisHand.remove(player);
        MapMethods.restoreMap(player.getPlayer().getName(), true);
    }

    @Override
    public PokerPlayer playerSit(Player player, double buyin) throws Exception {
        PokerPlayer pokerPlayer = new PokerPlayer(player, this, buyin);
        getPlayers().add(pokerPlayer);
        return pokerPlayer;
    }

    // Post the blinds for every player on the table
    public void postBlinds() {
        // Post antes if there is one
        if (((PokerTableSettings) getCardsTableSettings()).ante.getValue() > 0) {
            for (CardsPlayer player : getPlayers()) {
                ((PokerPlayer) player).postBlind("ante");
            }
        }
        getNextPlayer(getButton()).postBlind("small blind"); // Find the player 1 after the button and post his small blind
        getNextPlayer(getButton() + 1).postBlind("big blind"); // Find the player 2 after the button and post his big blind
    }

    // Raise the blinds if the dynamic frequency is set
    public void raiseBlinds() {
        PokerTableSettings settings = (PokerTableSettings) getCardsTableSettings();
        // If the current hand number is a multiple of the dynamic ante frequency, and dynamic ante frequency is turned on, increase the blinds/ante by what it was set to most recently
        if (settings.dynamicFrequency.getValue() > 0) {
            if (getHandNumber() % settings.dynamicFrequency.getValue() == 0 && getHandNumber() != 1) {
                settings.raiseBlinds();
                sendTableMessage("Raising blinds!");
                sendTableMessage("New ante: &6" + settings.ante.getValue() + "&f. New SB: &6" + settings.sb.getValue() + ". New BB: &6" + settings.bb.getValue() + "&f.");
            }
        }
        if (settings.minRaiseAlwaysBB.getValue()) {
            settings.updateMinRaise();
        }
    }

    @Override
    public void returnMoney(CardsPlayer cardsPlayer) {
        PokerPlayer pokerPlayer = (PokerPlayer) cardsPlayer;
        if (pokerPlayer.isOnline()) {
            pokerPlayer.getPlayer().teleport(pokerPlayer.getStartLocation());
            Messages.sendMessage(pokerPlayer.getPlayer(), "You have been paid your remaining stack of &6" + Formatter.formatMoney(pokerPlayer.getMoney() + pokerPlayer.getTotalBet()));
        }
        UltimateCards.getEconomy().depositPlayer(cardsPlayer.getPlayerName(), pokerPlayer.getMoney() + pokerPlayer.getTotalBet());
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + Double.toString(pokerPlayer.getMoney() + pokerPlayer.getTotalBet()) + " to " + pokerPlayer);
    }

    public void setNewActionPlayer(boolean ignoreAllIn, PokerPlayer lastPlayer) {
        for (CardsPlayer cardsPlayer : getRearrangedPlayers(getNextPlayer(getPlayersThisHand().indexOf(lastPlayer)))) {
            PokerPlayer player = (PokerPlayer) cardsPlayer;

            if (!player.isRevealed() && !player.isFolded() && (ignoreAllIn || !player.isAllIn())) {
                setActionPlayer(player);
                break;
            }
        }

        getActionPokerPlayer().takeAction();
    }

    public void setShowdownPlayers(ArrayList<PokerPlayer> showdownPlayers) {
        this.showdownPlayers = showdownPlayers;
    }

    // This method makes sure that every player ID is equal to their index in the player list. This should be called whenever a player is removed.
    @Override
    public void shiftIDs() {
        for (int i = 0; i < getPlayers().size(); i++)
            if (getPlayers().get(i).getID() != i) {
                getPlayers().get(i).setID(i);
            }
    }
}
