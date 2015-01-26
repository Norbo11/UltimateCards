package com.github.norbo11.util;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerControlled {
    private UUID uuid; // This holds the player's name. Should never be changed

    public PlayerControlled(Player player) {
        uuid = player.getUniqueId();
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
    
    public UUID getUuid() {
        return uuid;
    }

    public void sendMessage(String message) {
        Messages.sendMessage(getPlayer(), message);
    }
    
    public String getPlayerName() {
        if (getPlayer() != null) {
            return getPlayer().getName();
        } else return "";
    }
    
    @Override
    public String toString() {
        return getPlayerName();
    }
}
