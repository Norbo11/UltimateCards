package com.github.norbo11.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerControlled
{
    private String playerName; // This holds the player's name. Should never be
                               // changed

    public Player getPlayer()
    {
        return Bukkit.getPlayer(playerName);
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setName(String playerName)
    {
        this.playerName = playerName;
    }

}
