package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerAllin extends PluginCommand {

    public PokerAllin() {
        getAlises().add("allin");
        getAlises().add("shove");
        getAlises().add("a");

        setDescription("Bets the rest of your stack and puts you in all in mode.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    PokerPlayer pokerPlayer;

    PokerTable pokerTable;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) {
                if (!pokerPlayer.isEliminated()) {
                    pokerTable = pokerPlayer.getPokerTable();
                    if (pokerTable.isInProgress()) // Check if the table is
                                                   // in progress
                    {
                        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN) {
                            if (pokerPlayer.isAction()) {
                                if (!pokerPlayer.isFolded()) {
                                    if (!pokerPlayer.isAllIn()) return true;
                                    else {
                                        ErrorMessages.playerIsAllIn(getPlayer());
                                    }
                                } else {
                                    ErrorMessages.playerIsFolded(getPlayer());
                                }
                            } else {
                                ErrorMessages.notYourTurn(getPlayer());
                            }
                        } else {
                            ErrorMessages.tableAtShowdown(getPlayer());
                        }
                    } else {
                        ErrorMessages.tableNotInProgress(getPlayer());
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

    // Declares the specified player all in
    @Override
    public void perform() throws Exception {
        double betAmount = pokerPlayer.getMoney() + pokerPlayer.getCurrentBet();
        pokerPlayer.bet(betAmount, null);
    }
}
