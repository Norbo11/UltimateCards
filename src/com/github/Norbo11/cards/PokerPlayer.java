package com.github.Norbo11.cards;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.table.Table;

public class PokerPlayer {

    UltimatePoker p;

    public Player player;
    public Location startLocation;
    public Table table;
    
    public List<Card> cards = new ArrayList<Card>();

    public PokerPlayer(Player player, Table table)
    {
        p = new UltimatePoker();

        this.player = player;
        startLocation = player.getLocation();
        this.table = table;
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
            sendMessage(p.pluginTag + "[" + i + "]" + card.toString());
            i++;
        }
    }
    
    public void takeAction()
    {
    }
}
