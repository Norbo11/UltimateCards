package com.github.Norbo11.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class PokerPlayer
{

    UltimatePoker p;

    public Player player;
    public Location startLocation;
    public Table table;
    public boolean action;
    public boolean folded;
    public double currentBet;
    public double totalBet;
    public double money;
    public int id;

    public List<Card> cards = new ArrayList<Card>();

    public PokerPlayer(Player player, Table table, double buyin, UltimatePoker p)
    {
        this.p = p;

        this.player = player;
        this.table = table;
        startLocation = player.getLocation();
        action = false;
        currentBet = 0;
        totalBet = 0;
        id = table.players.size();
        money = buyin;
    }

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

    public void bet(double amount)
    {
        action = false;
        currentBet = amount;
        totalBet = totalBet + currentBet;
        table.pot = table.pot + amount;
        table.currentBet = currentBet;
        money = money - amount;
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has bet " + p.gold + p.methodsMisc.formatMoney(amount));
        table.nextPersonTurn(this);
    }

    public void call()
    {
        action = false;
        currentBet = table.currentBet;
        totalBet = totalBet + currentBet;
        table.pot = table.pot + table.currentBet;
        money = money - table.currentBet;
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has called the bet of " + p.gold + p.methodsMisc.formatMoney(table.currentBet) + " (");
        table.nextPersonTurn(this);
    }

    public void check()
    {
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has checked.");
        action = false;
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

    public void fold()
    {
        action = false;
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

    public void payPot()
    {
        double rake = 0;
        if (table.rake > 0)
        {
            rake = table.pot * table.rake;
            p.economy.depositPlayer(table.owner.getName(), rake);
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + table.owner.getName() + p.white + " has been paid a rake of " + p.gold + p.methodsMisc.formatMoney(rake));
        } 
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.getName() + p.white + " has won the pot of " + p.gold + p.methodsMisc.formatMoney(table.pot - rake) + p.white + "!");
        money = money + table.pot - rake;
        table.pot = 0;
        table.deal();
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
        sendMessage(p.pluginTag + "It's your turn to act!");
        action = true;
    }
}
