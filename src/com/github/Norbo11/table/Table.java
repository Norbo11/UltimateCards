package com.github.Norbo11.table;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.cards.Card;
import com.github.Norbo11.cards.Deck;
import com.github.Norbo11.cards.PokerPlayer;

public class Table {

    UltimatePoker p;

    //Generic vars
    public Player owner;
    public String name;
    public int id;
    public Location location;
    public boolean inProgress;

    public boolean open = false;

    //Settings
    public boolean minRaiseIsAlwaysBB;
    public boolean elimination;
    public double minBuy;
    public double maxBuy;
    public double minRaise;
    public double originalSB;
    public double originalBB;
    public double originalAnte;
    public double sb;
    public double bb;
    public double ante;
    public double dynamicAnteFreq;
    
    //Bet related stuff
    public double pot;
    public double currentBet;
    public int turn;
    
    //Important shit
    public List<PokerPlayer> players = new ArrayList<PokerPlayer>();
    public List<Card> board = new ArrayList<Card>(4);
    public Deck deck;
    public int currentPhase;

    public Table(Player owner, String name, int id, Location location, UltimatePoker p)
    {
        this.p = p;

        this.owner = owner;
        this.name = name;
        this.id = id;

        open = false;

        elimination = p.getConfig().getBoolean("table.defaults.elimination");
        minBuy = p.getConfig().getDouble("table.defaults.minBuy");
        maxBuy = p.getConfig().getDouble("table.defaults.maxBuy");
        sb = p.getConfig().getDouble("table.defaults.sb");
        bb = p.getConfig().getDouble("table.defaults.bb");
        ante = p.getConfig().getDouble("table.defaults.ante");
        dynamicAnteFreq = p.getConfig().getDouble("table.defaults.dynamicAnteFreq");
        minRaise = p.getConfig().getDouble("table.defaults.minRaise");
        minRaiseIsAlwaysBB = p.getConfig().getBoolean("table.defaults.minRaiseIsAlwaysBB");

        originalSB = sb;
        originalBB = bb;
        originalAnte = ante;

        this.location = location;
        deck = new Deck(p);

        inProgress = false;
        turn = 0;
    }

    public void setElimination(Player player, String value)
    {
        if (value.equalsIgnoreCase("true"))
        {
            elimination = true;
            player.sendMessage(p.pluginTag + "Your table is now in elimination mode! Players are unable to re-buy.");
        } else
        if (value.equalsIgnoreCase("false"))
        {
            elimination = false;
            player.sendMessage(p.pluginTag + "Your table is now in normal (non-elimination) mode. Players are now able to re-buy.");
        } else player.sendMessage(p.pluginTag + ChatColor.RED + "'" + value + "' is an invalid value! Please specify 'true' or 'false' only.");
    }

    public void setNumberValue(Player player, String setting, String v)
    {
        if (p.methodsMisc.isDouble(v) == true)
        {
            double value = Double.parseDouble(v);
            
            if (setting.equalsIgnoreCase("minBuy")) { minBuy = value;
            player.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + ChatColor.GOLD + value + "!"); }
            
            if (setting.equalsIgnoreCase("maxBuy")) { maxBuy = value;
            player.sendMessage(p.pluginTag + "Maximum buy-in for table set to " + ChatColor.GOLD + value + "!"); }
            
            if (setting.equalsIgnoreCase("sb")) { sb = value; originalSB = sb;
            player.sendMessage(p.pluginTag + "Small blind for table set to " + ChatColor.GOLD + value + "!"); }
            
            if (setting.equalsIgnoreCase("bb")) { bb = value; originalSB = bb;
            player.sendMessage(p.pluginTag + "Big blind for table set to " + ChatColor.GOLD + value + "!"); }
            
            if (setting.equalsIgnoreCase("ante")) { ante = value; originalAnte = ante;
            player.sendMessage(p.pluginTag + "Ante for table set to " + ChatColor.GOLD + value + "!"); }
            
            if (setting.equalsIgnoreCase("minRaise") && p.getConfig().getBoolean("table.minRaiseIsAlwaysBB") == false) 
            { minRaise = value;
                player.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + ChatColor.GOLD + value + "!"); 
            } else
            player.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + ChatColor.GOLD + value + "!");
            
            if (setting.equalsIgnoreCase("setDynamicAnteFreq")) { dynamicAnteFreq = value;
            player.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + ChatColor.GOLD + value + "!"); }
        } else p.methodsError.notANumber(player, v);
    }
    
    public void displayBoard(Player who)
    {
        if (who == null) 
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Community Cards: ");
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + ChatColor.GOLD + "----------------------------------");
            for (Card card : board) p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + card.toString());
        } else 
        {
            who.sendMessage(p.pluginTag + "Community Cards: ");
            who.sendMessage(p.pluginTag + ChatColor.GOLD + "----------------------------------");
            for (Card card : board) who.sendMessage(p.pluginTag + card.toString());
        }
    }

    public void showdown()
    {
        currentPhase = 4;

    }
   
    public void river()
    {
        currentPhase = 3;
    }
    
    public void turn()
    {
        currentPhase = 2;
    }
    
    public void flop()
    {
        currentPhase = 1;
        Card[] cards = deck.generateCards(3);
        board.add(cards[0]);
        board.add(cards[1]);
        board.add(cards[2]);
        displayBoard(null);
        turn = 0;
        players.get(turn).takeAction();
    }
    
    public void preflop()
    {
        currentPhase = 0;
        players.get(turn).takeAction();
    }

    public void deal()
    {
        for (PokerPlayer player : players)
        {
            player.addCards(deck.generateCards(2));
        }
        preflop();
    }
    
    public void setInProgress(boolean state)
    {
        if (state = true)
        {
            inProgress = true;
            deal();
        } else inProgress = false;
    }

    public void nextPersonTurn()
    {
        if (turn < players.size() - 1)
        {
            turn++;
            players.get(turn).takeAction();
        }
        else nextPhase();
    }
    
    public void nextPhase()
    {
        if (currentPhase == 0) flop();
        if (currentPhase == 1) turn();
        if (currentPhase == 2) river();
        if (currentPhase == 3) showdown();
    }
}
