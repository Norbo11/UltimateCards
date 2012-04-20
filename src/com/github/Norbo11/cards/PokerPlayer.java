package com.github.Norbo11.cards;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class PokerPlayer {

    UltimatePoker p;

    public String name;
    public Location startLocation;

    public PokerPlayer(Player player)
    {
        p = new UltimatePoker();

        name = player.getName();
        startLocation = player.getLocation();
    }
}
