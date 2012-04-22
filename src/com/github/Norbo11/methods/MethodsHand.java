package com.github.Norbo11.methods;

import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.cards.PokerPlayer;

public class MethodsHand
{
    UltimatePoker p;
    public MethodsHand(UltimatePoker p) {
        this.p = p;
    }
    
    public void displayHand(Player player)
    {
        PokerPlayer pokerPlayer = p.methodsMisc.isAPokerPlayer(player);
        if (pokerPlayer != null)
        {
            pokerPlayer.displayHand();
        } else p.methodsError.notPokerPlayer(player);
    }
}
