package com.github.Norbo11.table;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.cards.Deck;
import com.github.Norbo11.cards.PokerPlayer;

public class Table {

    UltimatePoker p;

    public Player owner;
    public String name;
    public int id;

    public boolean open = false;

    public boolean elimination;
    public int minBuy;
    public int maxBuy;
    public int originalSB;
    public int originalBB;
    public int originalAnte;
    public int sb;
    public int bb;
    public int ante;
    public int dynamicAnteFreq;

    public Map<Player, PokerPlayer> players = new HashMap<Player, PokerPlayer>();

    public Location location;
    public Deck deck;

    public boolean inProgress;

    public Table(Player owner, String name, int id, Location location, UltimatePoker p)
    {
        this.p = p;

        this.owner = owner;
        this.name = name;
        this.id = id;

        open = false;

        elimination = p.getConfig().getBoolean("tableDefaults.elimination");
        minBuy = p.getConfig().getInt("tableDefaults.minBuy");
        maxBuy = p.getConfig().getInt("tableDefaults.maxBuy");
        sb = p.getConfig().getInt("tableDefaults.sb");
        bb = p.getConfig().getInt("tableDefaults.bb");
        ante = p.getConfig().getInt("tableDefaults.ante");
        dynamicAnteFreq = p.getConfig().getInt("tableDefaults.dynamicAnteFreq");

        originalSB = sb;
        originalBB = bb;
        originalAnte = ante;

        this.location = location;
        deck = new Deck(p);

        inProgress = false;
    }

    public void setElimination(CommandSender sender, String value)
    {
        if (value.equalsIgnoreCase("true"))
        {
            elimination = true;
            sender.sendMessage(p.pluginTag + "Your table is now in elimination mode! Players are unable to re-buy.");
        } else
        if (value.equalsIgnoreCase("false"))
        {
            elimination = false;
            sender.sendMessage(p.pluginTag + "Your table is now in normal (non-elimination) mode. Players are now able to re-buy.");
        } else sender.sendMessage(p.pluginTag + ChatColor.RED + "'" + value + "' is an invalid value! Please specify 'true' or 'false' only.");
    }

    public void setMinBuy(CommandSender sender, String value)
    {
        if (p.methodsMisc.isInteger(value) == true)
        {
            minBuy = Integer.parseInt(value);
            sender.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + ChatColor.GOLD + value + "!");
        } else p.errorDisplay.notANumber(sender, value);
    }

    public void setMaxBuy(CommandSender sender, String value)
    {
        if (p.methodsMisc.isInteger(value) == true)
        {
            maxBuy = Integer.parseInt(value);
            sender.sendMessage(p.pluginTag + "Maximum buy-in for table set to " + ChatColor.GOLD + value + "!");
        } else p.errorDisplay.notANumber(sender, value);
    }

    public void setSB(CommandSender sender, String value)
    {
        if (p.methodsMisc.isInteger(value) == true)
        {
            sb = Integer.parseInt(value);
            sender.sendMessage(p.pluginTag + "Small blind for table set to " + ChatColor.GOLD + value + "!");
        } else p.errorDisplay.notANumber(sender, value);
    }

    public void setBB(CommandSender sender, String value)
    {
        if (p.methodsMisc.isInteger(value) == true)
        {
            bb = Integer.parseInt(value);
            sender.sendMessage(p.pluginTag + "Big blind for table set to " + ChatColor.GOLD + value + "!");
        } else p.errorDisplay.notANumber(sender, value);
    }

    public void setAnte(CommandSender sender, String value)
    {
        if (p.methodsMisc.isInteger(value) == true)
        {
            ante = Integer.parseInt(value);
            sender.sendMessage(p.pluginTag + "Ante for table set to " + ChatColor.GOLD + value + "!");
        } else p.errorDisplay.notANumber(sender, value);
    }

    public void setDynamicAnteFreq(CommandSender sender, String value)
    {
        if (p.methodsMisc.isInteger(value) == true)
        {
            dynamicAnteFreq = Integer.parseInt(value);
            if (dynamicAnteFreq > 0)
            {
                sender.sendMessage(p.pluginTag + "Your table ante + blinds will now increase by themselves every " + ChatColor.GOLD + value + ChatColor.WHITE + " hands.");
            } else sender.sendMessage(p.pluginTag + "Your table ante + blidns will now never increase on its own.");
        } else p.errorDisplay.notANumber(sender, value);
    }

    public void setInProgress()
    {

    }
}
