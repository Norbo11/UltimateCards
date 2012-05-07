/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: ListenerGeneral.java
 * -Contains all listeners used in the plugin
 * -Currently only contains join/quit events which display messages to the table if a player quits.
 * -If a player joins back, the table is automatically resumed.
 * ===================================================================================================
 */

package com.github.norbo11;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.norbo11.classes.PokerPlayer;

public class ListenerGeneral implements Listener
{
    ListenerGeneral(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(event.getPlayer());
        if (pokerPlayer != null)
        {
            pokerPlayer.online = true;
            if (pokerPlayer.table.getOnlinePlayers().size() == pokerPlayer.table.players.size())
            {
                p.methodsMisc.sendToAllWithinRange(pokerPlayer.table.location, p.pluginTag + "All players online again, resuming the table!");
                pokerPlayer.table.stopped = false;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        PokerPlayer pokerPlayer = p.methodsCheck.isAPokerPlayer(event.getPlayer());
        if (pokerPlayer != null)
        {
            pokerPlayer.online = false;
            p.methodsMisc.sendToAllWithinRange(pokerPlayer.table.location, p.pluginTag + p.gold + pokerPlayer.player.getName() + p.red + " has left the game, pausing the table!");
            p.methodsMisc.sendToAllWithinRange(pokerPlayer.table.location, p.pluginTag + p.red + "Table owner: please " + p.gold + "/table kick [player]" + p.red + " the missing players or wait for them to join back.");
            pokerPlayer.table.stopped = true;
        }
    }
}
