package com.github.norbo11.commands.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerReveal extends PluginCommand {
    public PokerReveal(Player player, String[] args) {
        super(player, args);
        getAlises().add("reveal");
        getAlises().add("show");
        getAlises().add("display");

        setDescription("Shows your hand to everyone around the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }
    
    public PokerReveal() {
        this(null, null);
    }

    PokerPlayer pokerPlayer;

    PokerTable pokerTable;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) {
                pokerTable = pokerPlayer.getPokerTable();
                if (!pokerPlayer.isEliminated()) {
                    if (pokerTable.getCurrentPhase() == PokerPhase.SHOWDOWN) // If it is showdown
                    {
                        if (pokerPlayer.isAction()) return true;
                        else {
                            ErrorMessages.notYourTurn(getPlayer());
                        }
                    } else {
                        ErrorMessages.cantReveal(getPlayer());
                    }
                } else {
                    ErrorMessages.playerIsEliminated(getPlayer());
                }
            } else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Publicly reveals the player's hand to everybody
    @Override
    public void perform() throws Exception {
        pokerPlayer.reveal();
    }
}
