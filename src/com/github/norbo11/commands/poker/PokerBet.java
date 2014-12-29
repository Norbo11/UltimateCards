package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class PokerBet extends PluginCommand {

    public PokerBet() {
        getAlises().add("raiseto");
        getAlises().add("bet");
        getAlises().add("raise");
        getAlises().add("b");

        setDescription("Bets or raises to the specified amount.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    PokerPlayer pokerPlayer;
    PokerTable pokerTable;

    double amountToBet;

    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) {
                if (!pokerPlayer.isEliminated()) {
                    pokerTable = pokerPlayer.getPokerTable();
                    if (pokerTable.isInProgress()) {
                        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN) {
                            if (pokerPlayer.isAction()) {
                                if (!pokerPlayer.isFolded()) {
                                    if (!pokerPlayer.isAllIn()) {
                                        amountToBet = NumberMethods.getDouble(getArgs()[1]);
                                        if (amountToBet != -99999) {
                                            if (pokerPlayer.hasMoney(amountToBet - pokerPlayer.getCurrentBet())) {
                                                // Raise
                                                if (amountToBet > pokerTable.getCurrentBet()) {
                                                    if (amountToBet - pokerTable.getCurrentBet() >= pokerTable.getSettings().minRaise.getValue()) return true;
                                                    else {
                                                        ErrorMessages.betBelowMinRaise(getPlayer(), pokerTable.getSettings().minRaise.getValue(), pokerTable.getCurrentBet());
                                                    }
                                                    // Call
                                                } else if (amountToBet == pokerTable.getCurrentBet()) return true;
                                                else {
                                                    ErrorMessages.betBelowCurrentBet(getPlayer());
                                                }
                                            } else {
                                                ErrorMessages.notEnoughMoney(getPlayer(), amountToBet, pokerPlayer.getMoney());
                                            }
                                        } else {
                                            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
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

    // Bets the specified amountToBet in the name of the specified player.
    // This method is only called when raising or betting for the first time in
    // the phase. You cannot bet less than the current bet of the table
    @Override
    public void perform() throws Exception {
        pokerPlayer.bet(amountToBet, null);
    }

}
