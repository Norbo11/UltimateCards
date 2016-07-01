package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerCall extends PluginCommand {

    public PokerCall() {
        getAlises().add("call");
        getAlises().add("match");
        getAlises().add("ca");

        setDescription("Matches your total bet with the rest of the table.");

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
                    if (pokerTable.isInProgress()) {
                        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN) {
                            if (pokerPlayer.isAction()) {
                                if (!pokerPlayer.isFolded()) {
                                    if (!pokerPlayer.isAllIn()) {
                                        if (pokerPlayer.getCurrentBet() < pokerTable.getCurrentBet()) // Check if the player hasn't already called
                                        {
                                            if (pokerPlayer.hasMoney(pokerTable.getCurrentBet() - pokerPlayer.getCurrentBet())) return true;
                                            else {
                                                ErrorMessages.notEnoughMoney(getPlayer(), pokerTable.getCurrentBet(), pokerPlayer.getMoney());
                                            }
                                        } else {
                                            ErrorMessages.cantCall(getPlayer());
                                        }
                                    } else {
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

    // Calls the latest bet in the name of the player.
    @Override
    public void perform() throws Exception {
        double amountCalled = pokerTable.getCurrentBet();
        pokerPlayer.bet(amountCalled, null);
    }
}
