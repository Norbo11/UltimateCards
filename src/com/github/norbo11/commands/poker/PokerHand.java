package com.github.norbo11.commands.poker;

import java.util.ArrayList;
import java.util.Arrays;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class PokerHand extends PluginCommand {
    public PokerHand() {
        getAlises().add("cards");
        getAlises().add("hand");

        setDescription("Displays your hand.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    PokerPlayer pokerPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) if (pokerPlayer.getHand().getCards().size() > 0) return true;
            else {
                ErrorMessages.playerHasNoHand(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Displays the player's hand (to himself only)
    @Override
    public void perform() throws Exception {
        Messages.sendMessage(getPlayer(), "Your hand:");
        Messages.sendMessage(getPlayer(), "&6" + UltimateCards.getLineString());
        Messages.sendMessage(getPlayer(), new ArrayList<String>(Arrays.asList(pokerPlayer.getHand().getHand())));
    }
}
