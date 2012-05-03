package com.github.Norbo11;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.Norbo11.classes.PokerPlayer;

public class ListenerGeneral implements Listener
{
    UltimatePoker p;
    ListenerGeneral(UltimatePoker p)
    {
        this.p = p;
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
            pokerPlayer.table.stopTable();
        }
    }
    
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
                pokerPlayer.table.resumeTable();
            }
        }
    }
}
