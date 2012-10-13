package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.ReturnMoney;

public class BlackjackTable extends CardsTable
{
    public static ArrayList<BlackjackTable> getBlackjackTables()
    {
        ArrayList<BlackjackTable> returnValue = new ArrayList<BlackjackTable>();

        for (CardsTable table : getTables())
        {
            returnValue.add((BlackjackTable) table);
        }

        return returnValue;
    }

    // Generic vars
    private int button; // Represents the index of the player that is on the
                        // button (in the list 'players')

    private BlackjackDealer dealer = new BlackjackDealer(this);

    public BlackjackTable(Player owner, String name, int ID, Location location, double buyin) throws Exception
    {
        // Set the table core properties
        setOwner(new BlackjackPlayer(owner, this, buyin));
        setName(name);
        setID(ID);
        setLocation(location);

        getPlayers().add(getOwner()); // Add the owner to the sitting players
                                      // list
        setCardsTableSettings(new BlackjackTableSettings(this));
    }

    @Override
    public boolean canDeal()
    {
        // If there are not enough players to continue the hand
        if (getPlayersThisHand().size() < getMinPlayers())
        {
            Messages.sendToAllWithinRange(getLocation(), "&cNobody has placed a bet (" + PluginExecutor.blackjackBet.getCommandString() + " [amount]&c)! Cannot start the game.");
            return false;
        }

        return true;
    }

    public boolean canPlay(CardsPlayer player)
    {
        return player.getMoney() > getSettings().getMinBet();
    }

    public void clearDealerVars()
    {
        dealer.getHand().getCards().clear();
        dealer.setBust(false);
        dealer.setScore(0);
    }

    @Override
    public void clearPlayerVars()
    {
        for (BlackjackPlayer player : getBlackjackPlayers())
        {
            player.clearHandBets();
            player.setHitted(false);
            player.setDoubled(false);
        }
        setActionPlayer(null);
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

        // If there are enough players to play another hand, then do so
        if (canDeal())
        {
            setHandNumber(getHandNumber() + 1);
            Messages.sendToAllWithinRange(getLocation(), "Dealing hand number &6" + getHandNumber());
            Messages.sendToAllWithinRange(getLocation(), "&6" + UltimateCards.getLineString());

            setInProgress(true);
            getDeck().shuffle();
            clearDealerVars();
            moveButton();
            dealCards();
            displayScores();

            Messages.sendToAllWithinRange(getLocation(), "&6" + UltimateCards.getLineString());
            nextPersonTurn(getNextPlayer(button));
        }
    }

    @Override
    public void dealCards()
    {
        // Go through all players, clear their hands and add their cards.
        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand())
        {
            blackjackPlayer.clearHands();
            blackjackPlayer.getHands().get(0).addCards(getDeck().generateCards(2));
        }

        // Gives the dealer 2 cards
        dealer.addInitialCards();
    }

    @Override
    public void deleteTable()
    {
        // Displays a message, returns money for every player, and removes the
        // table
        Messages.sendToAllWithinRange(getLocation(), "Table ID '" + "&6" + getName() + "&f', ID #" + "&6" + getID() + " &fhas been deleted!");
        ReturnMoney.returnMoney(this);
        CardsTable.getTables().remove(this);
    }

    public void displayScores()
    {
        for (BlackjackPlayer player : getBjPlayersThisHand())
        {
            player.displayScore();
        }
    }

    public BlackjackPlayer getActionBlackjackPlayer()
    {
        return (BlackjackPlayer) getActionPlayer();
    }

    public ArrayList<BlackjackPlayer> getBjPlayersThisHand()
    {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<BlackjackPlayer>();

        for (CardsPlayer player : getPlayersThisHand())
        {
            returnValue.add((BlackjackPlayer) player);
        }

        return returnValue;
    }

    public ArrayList<BlackjackPlayer> getBlackjackPlayers()
    {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<BlackjackPlayer>();

        for (CardsPlayer player : getPlayers())
        {
            returnValue.add((BlackjackPlayer) player);
        }

        return returnValue;
    }

    public ArrayList<BlackjackPlayer> getBustedPlayers()
    {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<BlackjackPlayer>();

        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand())
            if (blackjackPlayer.isBustOnAllHands())
            {
                returnValue.add(blackjackPlayer);
            }

        return returnValue;
    }

    public int getButton()
    {
        return button;
    }

    public BlackjackDealer getDealer()
    {
        return dealer;
    }

    @Override
    public int getMinPlayers()
    {
        return 1;
    }

    // Method to get the player 1 after the index specified, and loop back to
    // the beginning if the end is reached
    @Override
    public BlackjackPlayer getNextPlayer(int index)
    {
        if (index + 1 >= getPlayersThisHand().size()) return getBjPlayersThisHand().get((index + 1) % getBjPlayersThisHand().size()); // If
        else return getBjPlayersThisHand().get(index + 1); // If the end of the players is not reached simply return the player 1 after the given index
    }

    @Override
    public ArrayList<CardsPlayer> getPlayersThisHand()
    {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>();

        for (BlackjackPlayer player : getBlackjackPlayers())
        {
            if (player.playingThisHand())
            {
                returnValue.add(player);
            }
        }

        return returnValue;
    }

    public ArrayList<BlackjackPlayer> getReadyPlayers()
    {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<BlackjackPlayer>();

        for (BlackjackPlayer player : getBjPlayersThisHand())
            if (player.isDoubled() || player.isStayedOnAllHands() || player.isBustOnAllHands())
            {
                returnValue.add(player);
            }

        return returnValue;
    }

    @Override
    public BlackjackTableSettings getSettings()
    {
        return (BlackjackTableSettings) getCardsTableSettings();
    }

    public ArrayList<BlackjackPlayer> getStayedPlayers()
    {
        ArrayList<BlackjackPlayer> returnValue = new ArrayList<BlackjackPlayer>();

        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand())
            if (blackjackPlayer.isStayedOnAllHands())
            {
                returnValue.add(blackjackPlayer);
            }

        return returnValue;
    }

    public void handEnd()
    {
        if (getBustedPlayers().size() != getPlayersThisHand().size())
        {
            Messages.sendToAllWithinRange(getLocation(), "&6" + UltimateCards.getLineString());
            dealer.reveal();
            while (dealer.isUnderStayValue())
            {
                dealer.hit();
            }
        }

        Messages.sendToAllWithinRange(getLocation(), "&6" + UltimateCards.getLineString());
        for (BlackjackPlayer pushingPlayer : payPlayers())
        {
            Messages.sendToAllWithinRange(getLocation(), "&6" + pushingPlayer.getPlayerName() + "&f is pushing for &6" + UltimateCards.getEconomy().format(pushingPlayer.getPushing()) + "&f next hand.");
        }

        setToBeContinued(true);
        setInProgress(false);
        clearPlayerVars();
        Messages.sendToAllWithinRange(getLocation(), "Hand ended. Please place bets again (" + PluginExecutor.blackjackBet.getCommandString() + " [amount]&f), then table dealer will start a new hand.");
    }

    @Override
    public void kick(CardsPlayer player)
    {
        BlackjackPlayer blackjackPlayer = (BlackjackPlayer) player;

        Messages.sendToAllWithinRange(getLocation(), "&6" + getOwner().getPlayerName() + "&f has kicked &6" + blackjackPlayer.getPlayerName() + "&f from the table!");

        returnMoney(blackjackPlayer);
        removePlayer(blackjackPlayer);
        shiftIDs();

        if (blackjackPlayer.isOnline())
        {
            blackjackPlayer.getPlayer().teleport(blackjackPlayer.getStartLocation());
            Messages.sendMessage(blackjackPlayer.getPlayer(), "&6" + getOwner().getPlayerName() + "&c has kicked you from his/her blackjack table! You receive your remaining stack of &6" + Formatter.formatMoney(blackjackPlayer.getMoney()));
        }

        if (blackjackPlayer == getActionPlayer())
        {
            nextPersonTurn(blackjackPlayer);
        }
        removePlayer(player);
    }

    @Override
    public ArrayList<String> listPlayers()
    {
        ArrayList<String> list = new ArrayList<String>();

        for (BlackjackPlayer player : getBlackjackPlayers())
        {
            BlackjackPlayer blackjackPlayer = player;
            String temp = "[" + blackjackPlayer.getID() + "] ";

            if (blackjackPlayer.isOnline())
            {
                temp = temp + "&6" + blackjackPlayer.getPlayerName() + " - ";
            } else
            {
                temp = temp + "&c" + blackjackPlayer.getPlayerName() + "&f - ";
            }

            if (blackjackPlayer.isAction())
            {
                temp = temp.replace(blackjackPlayer.getPlayerName(), ChatColor.UNDERLINE + blackjackPlayer.getPlayerName() + "&6");
            }

            if (getChipLeader() == blackjackPlayer)
            {
                temp = temp.replace(blackjackPlayer.getPlayerName(), ChatColor.BOLD + blackjackPlayer.getPlayerName() + "&6");
            }

            temp = temp + "&6" + Formatter.formatMoney(blackjackPlayer.getMoney());
            list.add(temp);
        }

        list.add("Average stack size: &6" + Formatter.formatMoney(getAverageStack()));
        list.add("&cOFFLINE &f| &6&nACTION&r &f| &6&lCHIP LEADER");

        return list;
    }

    // Moves the button to the next player (call this when starting a new hand)
    public void moveButton()
    {
        // If the button is not the last player in the list, increment the
        // button. Otherwise set button to 0.
        if (++button >= getPlayersThisHand().size())
        {
            button = 0;
        }
        Messages.sendToAllWithinRange(getLocation(), "Button moved to &6" + getPlayersThisHand().get(button).getPlayerName());
    }

    @Override
    // Move the action to the players after the one specified in the argument
    public void nextPersonTurn(CardsPlayer lastPlayer)
    {
        if (getReadyPlayers().size() == getPlayersThisHand().size())
        {
            handEnd();
            return;
        }

        for (CardsPlayer cardsPlayer : getRearrangedPlayers(getNextPlayer(getPlayersThisHand().indexOf(lastPlayer))))
        {
            BlackjackPlayer player = (BlackjackPlayer) cardsPlayer;
            if (!player.isBustOnAllHands() && !player.isStayedOnAllHands() && !player.isDoubled())
            {
                setActionPlayer(player);
                break;
            }
        }

        getActionBlackjackPlayer().takeAction();
    }

    public ArrayList<BlackjackPlayer> payPlayers()
    {
        ArrayList<BlackjackPlayer> pushingPlayers = new ArrayList<BlackjackPlayer>();

        for (BlackjackPlayer blackjackPlayer : getBjPlayersThisHand())
        {
            for (BlackjackHand hand : blackjackPlayer.getHands())
                if (!hand.isBust())
                {
                    if (!dealer.isBust())
                    {
                        if (hand.getScore() > dealer.getScore())
                        {
                            blackjackPlayer.pay(hand);
                        } else if (hand.getScore() == dealer.getScore() && !(hand.getScore() == 21 && !blackjackPlayer.isHitted()))
                        {
                            blackjackPlayer.setPushing(hand.getAmountBet());
                            pushingPlayers.add(blackjackPlayer);
                        } else
                        {
                            dealer.pay(blackjackPlayer, hand);
                        }
                    } else
                    {
                        blackjackPlayer.pay(hand);
                    }
                } else
                {
                    dealer.pay(blackjackPlayer, hand);
                }
        }

        return pushingPlayers;
    }

    // Showdown method
    public void phaseShowdown()
    {
        Messages.sendToAllWithinRange(getLocation(), "Showdown time!");
        Messages.sendToAllWithinRange(getLocation(), dealer.getHand().getHand());
        nextPersonTurn(getNextPlayer(button));
    }

    @Override
    public void playerLeave(CardsPlayer player)
    {
        UltimateCards.mapMethods.restoreMap(player.getPlayer());
    }

    @Override
    public void playerSit(Player player, double buyin) throws Exception
    {
        getPlayers().add(new BlackjackPlayer(player, this, buyin));
    }

    @Override
    public void returnMoney(CardsPlayer cardsPlayer)
    {
        BlackjackPlayer blackjackPlayer = (BlackjackPlayer) cardsPlayer;

        if (blackjackPlayer.isOnline())
        {
            blackjackPlayer.getPlayer().teleport(blackjackPlayer.getStartLocation());
            Messages.sendMessage(blackjackPlayer.getPlayer(), "You have been paid your remaining stack of &6" + Formatter.formatMoney(blackjackPlayer.getMoney() + blackjackPlayer.getTotalAmountBet()));
        }
        UltimateCards.getEconomy().depositPlayer(blackjackPlayer.getPlayerName(), blackjackPlayer.getMoney() + blackjackPlayer.getTotalAmountBet());
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + Double.toString(blackjackPlayer.getMoney() + blackjackPlayer.getTotalAmountBet()) + " to " + blackjackPlayer.getPlayerName());
    }
}
