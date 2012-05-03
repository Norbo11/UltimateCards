package com.github.Norbo11.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class PokerPlayer
{

    public PokerPlayer(Player player, Table table, double buyin, UltimatePoker p)
    {
        this.p = p;
        this.player = player;
        this.table = table;
        startLocation = player.getLocation();
        
        action = false;
        acted = false;
        folded = false;
        eliminated = false;
        blind = false;
        online = true;
        pot = null;
        currentBet = 0;
        totalBet = 0;
        id = table.getEmptyID();
        money = buyin;
        name = player.getName();
    }

    UltimatePoker p;
    public Player player;
    public String name;
    public Location startLocation;
    public Table table;
    public Pot pot;
    public boolean action;
    public boolean acted;
    public boolean folded;
    public boolean eliminated;
    public boolean blind;
    public boolean online;
    public double currentBet;
    public double totalBet;
    public double money;

    public int id;

    public List<Card> cards = new ArrayList<Card>();

    public void addCards(Card[] cards)
    {
        List<Card> addedCards = new ArrayList<Card>();
        for (int i = 0; i < cards.length; i++)
        {
            addedCards.add(cards[i]);
            sendMessage(p.pluginTag + "You have been dealt the " + addedCards.get(i).toString());
        }
        this.cards.addAll(addedCards);
    }

    public void allIn()
    {
        action = false;
        acted = true;
        
        double amount = money;
        
        // Bet
        currentBet = currentBet + amount;        
        totalBet = totalBet + amount;
        
        double sidePot = 0;

        double[] temp = new double[table.players.size()];
        int i = 0;
        for (PokerPlayer player : table.getNonFoldedPlayers())
        {
            if (player.currentBet >= currentBet && player != this)
            temp[i] = player.currentBet - currentBet;
            i++;
        }
        for (double temp2 : temp)
        {
            sidePot = sidePot + temp2;
        }
        
        pot = new Pot(this, table, sidePot, table.pots.size(), p);
        table.pots.add(pot);
        table.latestPot = pot;
        table.pots.get(table.pots.indexOf(table.latestPot) - 1).pot = (table.pots.get(table.pots.indexOf(table.latestPot) - 1).pot - sidePot) + amount;

        // Deduct
        money = 0;
        
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " is all in for " + p.gold + p.methodsMisc.formatMoney(amount) + p.white + "! (Total: " + p.gold + p.methodsMisc.formatMoney(totalBet) + p.white + ")");
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + "Created side pot of " + p.gold + p.methodsMisc.formatMoney(sidePot) + p.white + "!");
        
        if (table.players.size() - 1 == table.getAllInPlayers().size())
        {
            table.showdown();
            return;
        }
        
        table.nextPersonTurn(this);
    }

    public void bet(double amount)
    {
        action = false;
        acted = true;
        double tableCurrentBet = table.currentBet;
        double alreadyContributed = currentBet;
        double raised = amount - alreadyContributed;
        double allInCover = raised - table.latestPot.pot;
        
        // Bet
        currentBet = amount;
        totalBet = totalBet + raised;

        table.currentBet = amount;
        if (table.pots.size() > 1 && table.latestPot.phase == table.currentPhase)
        {
            table.pots.get(0).pot = table.pots.get(0).pot + allInCover;           
            table.latestPot.pot = table.latestPot.pot + (raised - allInCover);
        } else table.latestPot.pot = table.latestPot.pot + raised;
        
        if (tableCurrentBet == 0)
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " bets " + p.gold + p.methodsMisc.formatMoney(amount) + p.white + " (Total: " + p.gold + p.methodsMisc.formatMoney(totalBet) + p.white + ")");
        else
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " raises to " + p.gold + p.methodsMisc.formatMoney(amount) + p.white + " (Total: " + p.gold + p.methodsMisc.formatMoney(totalBet) + p.white + ")");

        // Deduct
        money = money - raised;

        table.nextPersonTurn(this);
    }

    public void call()
    {
        action = false;
        acted = true;
        
        double called = table.currentBet - currentBet; // Amount of money that is being called
        double allInCover = called - table.latestPot.pot;

        // Call
        currentBet = currentBet + called;   
        totalBet = totalBet + called;   
        
        if (table.pots.size() > 1 && table.currentPhase == table.latestPot.phase)
        {
            table.pots.get(table.pots.indexOf(table.latestPot) - 1).pot = table.pots.get(table.pots.indexOf(table.latestPot) - 1).pot + allInCover;           
            table.latestPot.pot = table.latestPot.pot + (called - allInCover);
        } else table.latestPot.pot = table.latestPot.pot + called;
        
        // Deduct
        money = money - called;

        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " calls " + p.gold + p.methodsMisc.formatMoney(called) + p.white
        + " (Total: " + p.gold + p.methodsMisc.formatMoney(totalBet) + p.white + ")");
        table.nextPersonTurn(this);
    }

    public void check()
    {
        action = false;
        acted = true;
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has checked.");
        table.nextPersonTurn(this);
    }

    public void clearHand()
    {
        cards.clear();
    }

    public void displayHand()
    {
        sendMessage(p.pluginTag + "Your hand:");
        sendMessage(p.pluginTag + p.gold + p.lineString);
        sendMessage(hand());
    }

    public void displayMoney()
    {
        sendMessage(p.pluginTag + "You have " + p.gold + p.methodsMisc.formatMoney(money) + p.white + " left on this table.");
    }

    public void displayPot()
    {
        sendMessage(table.listPots());
    }

    public void eliminate()
    {
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has been eliminated!");
        eliminated = true;
        player.teleport(startLocation);
        p.economy.depositPlayer(player.getName(), money);
    }

    public void fold()
    {
        action = false;
        acted = true;
        folded = true;
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has folded.");
        clearHand();
        table.nextPersonTurn(this);
    }

    public double getBalance()
    {
        return p.economy.getBalance(player.getName());
    }

    public String[] hand()
    {
        String[] returnValue = new String[cards.size()];
        int i = 0;
        for (Card card : cards)
        {
            returnValue[i] = p.pluginTag + "[" + i + "] " + card.toString();
            i++;
        }
        return returnValue;
    }

    public void postBlind(String blind)
    {
        double amount = 0;
        if (blind.equals("small"))
        {
            amount = table.sb;
            p.methodsMisc.sendToAllWithinRange(table.location,
            p.pluginTag + p.gold + player.getName() + p.white + " has posted the small blind (" + p.gold + p.methodsMisc.formatMoney(amount) + p.white + ")");
        }
        if (blind.equals("big"))
        {
            amount = table.bb;
            p.methodsMisc.sendToAllWithinRange(table.location,
            p.pluginTag + p.gold + player.getName() + p.white + " has posted the big blind (" + p.gold + p.methodsMisc.formatMoney(amount) + p.white + ")");
        }
        if (blind.equals("ante"))
        {
            amount = table.ante;
            p.methodsMisc.sendToAllWithinRange(table.location,
            p.pluginTag + p.gold + player.getName() + p.white + " has posted the ante (" + p.gold + p.methodsMisc.formatMoney(amount) + p.white + ")");
        }
        
        currentBet = amount;
        totalBet = totalBet + amount;
        table.currentBet = amount;
        table.pots.get(0).pot = table.pots.get(0).pot + amount;
        money = money - amount;
    }

    public void reBuy(double amount)
    {
        p.economy.withdrawPlayer(player.getName(), amount);
        money = money + amount;
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has added " + p.gold + p.methodsMisc.formatMoney(amount) + p.white + " to his stack. New balance: " + p.gold + p.methodsMisc.formatMoney(money));
        if (eliminated == true)
        {
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " is now back in the game!");
            eliminated = false;
        }
    }

    public void revealHand()
    {
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + "'s hand:");
        p.methodsMisc.sendToAllWithinRange(table.location, hand());
    }

    public void sendMessage(String message)
    {
        player.sendMessage(message);
    }

    public void sendMessage(String[] message)
    {
        player.sendMessage(message);
    }
    
    public void takeAction()
    {
        sendMessage(p.pluginTag + ChatColor.DARK_PURPLE + ChatColor.BOLD + ChatColor.UNDERLINE + "It's your turn to act!");
        action = true;
    }
}
