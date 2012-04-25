package com.github.Norbo11.cards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.table.Table;

public class PokerPlayer {

    UltimatePoker p;

    public Player player;
    public Location startLocation;
    public Table table;
    public boolean action;
    public boolean folded;
    public double currentBet;
    public double totalBet;
    
    public List<Card> cards = new ArrayList<Card>();

    public PokerPlayer(Player player, Table table)
    {
        p = new UltimatePoker();

        this.player = player;
        this.table = table;
        startLocation = player.getLocation();
        action = false;
        currentBet = 0;
        totalBet = 0;
    }
    
    public void addCards(Card[] cards)
    {
        for (Card card : cards)
        {
            this.cards.add(card);
            sendMessage(p.pluginTag + "You have been dealt the " + card.toString());
        }
    }
    
    public void clearHand()
    {
        cards.clear();
    }
    
    public void sendMessage(String message)
    {
        player.sendMessage(message);
    }
    
    public void displayHand()
    {
        sendMessage(p.pluginTag + "Your hand:");
        sendMessage(p.pluginTag + "-----------------------------------");
        int i = 0;
        for (Card card : cards)
        {
            sendMessage(p.pluginTag + "[" + i + "] " + card.toString());
            i++;
        }
    }
    
    public void bet(double amount)
    {
        if (action == true)
        {
            if (amount >= (table.currentBet + table.minRaise))
            {
                double balance = p.economy.getBalance(player.getDisplayName());
                if (balance > amount)
                {
                    currentBet = amount;
                    totalBet = totalBet + currentBet;
                    table.pot = table.pot + amount - table.currentBet;
                    table.currentBet = currentBet;
                    p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has bet " + ChatColor.GOLD + amount + ChatColor.WHITE + p.economy.currencyNamePlural());
                    table.nextPersonTurn();
                    action = false;
                } else sendMessage(p.pluginTag + "You do not have enough money to bet this much. You need " + Double.toString(amount - balance) + " more.");
            } else sendMessage(p.pluginTag + "You cannot raise that amount. Min Raise: " + Double.toString(table.minRaise) + " (on top of the current bet)");
        } else p.errorDisplay.notYourTurn(player);
    }   
    
    public void call()
    {
        if (action == true)
        {
            double balance = p.economy.getBalance(player.getName());
            if (balance >= (table.currentBet))
            {
                currentBet = table.currentBet;
                totalBet = totalBet + currentBet;
                table.pot = table.pot + table.currentBet;
                p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has called the bet of " + ChatColor.GOLD + table.currentBet + ChatColor.WHITE + p.economy.currencyNamePlural());
                table.nextPersonTurn();
                action = false;
            }
        }
    }
    
    public void fold()
    {
        if (folded == false)
        {
            p.log.info("The table ante is " + table.ante);
            p.log.info("The player name is " + player.getDisplayName());
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + ChatColor.GOLD + player.getName() + ChatColor.WHITE + " has folded.");
            clearHand();
            action = false;
            folded = true;
            table.nextPersonTurn();
        } else sendMessage(p.pluginTag + ChatColor.RED + "You have already folded!");
    }
    
    public void takeAction()
    {
        sendMessage(p.pluginTag + "It's your turn to act!");
        action = true;
    }
}
