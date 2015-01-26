package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class PokerPot extends PluginCommand {

    public PokerPot() {
        getAlises().add("pot");
        getAlises().add("pots");

        setDescription("Displays all pots at the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    PokerPlayer pokerPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) return true;
            else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Displays the table's pots to the player
    @Override
    public void perform() throws Exception {
        for (PokerPlayer p : pokerPlayer.getPokerTable().getNonFoldedPlayers()) {
            double pot = p.getTotalPot() > 0 ? p.getTotalPot() : 0;
            Messages.sendMessage(getPlayer(), "If &6" + p.getPlayerName() + "&f wins, he will win &6" + Formatter.formatMoney(pot) + "&f.");
        }
        Messages.sendMessage(getPlayer(), "Total amount to win: " + Formatter.formatMoney(pokerPlayer.getPokerTable().getHighestPot()));
    }
}
