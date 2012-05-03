package com.github.Norbo11.classes;

import com.github.Norbo11.UltimatePoker;

public class Pot
{
    UltimatePoker p;
    PokerPlayer playerAllIn; 
    double pot;
    public int id;
    Table table;
    public boolean main;
    int called;
    int phase;

    
    public Pot(PokerPlayer playerAllIn, Table table, double amount, int id, UltimatePoker p)
    {
        this.p = p;
        this.pot = amount;
        this.playerAllIn = playerAllIn;
        this.table = table;
        this.id = id;
        called = 0;
        phase = table.currentPhase;
        main = false;
        table.latestPot = this;
        //adjustPot();
    }

    public void payPot(PokerPlayer player)
    {
        double rake = 0;
        if (table.rake > 0)
        {
            rake = pot * table.rake;
            p.economy.depositPlayer(table.owner.getName(), rake);
            p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + table.owner.getName() + p.white + " has been paid a rake of " + p.gold + p.methodsMisc.formatMoney(rake));
        }
        if (main == true)
        p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.player.getName() + p.white + " has won the main pot of " + p.gold + p.methodsMisc.formatMoney(pot - rake));
        else p.methodsMisc.sendToAllWithinRange(table.location, p.pluginTag + p.gold + player.player.getName() + p.white + " has won the side pot of " + p.gold + p.methodsMisc.formatMoney(pot - rake));

        player.money = player.money + pot - rake;
        
        if (table.pots.size() == 1)
        table.deal();
    }
    
    public String convertToString()
    {
        String eligible = "";
        if (main == true)
        return p.white + "[Main Pot #" + id + "] " + p.gold + p.methodsMisc.formatMoney(pot) + " - For everyone";
        
        for (PokerPlayer player : table.players)
        {
            if (player != playerAllIn && player.money > 0)
            eligible = eligible + player.player.getName() + ", ";
        }
        eligible = eligible.substring(0, eligible.length() - 2);
        return p.white + "[Side Pot #" + id + "] " + p.gold + p.methodsMisc.formatMoney(pot) + " - " + eligible;
    }
}
