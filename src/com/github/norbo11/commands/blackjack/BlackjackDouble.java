package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;

public class BlackjackDouble extends PluginCommand {

    public BlackjackDouble() {
        getAlises().add("doubledown");
        getAlises().add("double");
        getAlises().add("dd");

        setDescription("Doubles your bet, gives you one more card and stands.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    BlackjackPlayer blackjackPlayer;

    BlackjackTable blackjackTable;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(getPlayer().getName());
            if (blackjackPlayer != null) {
                blackjackTable = blackjackPlayer.getTable();
                if (blackjackTable.isInProgress()) {
                    if (blackjackPlayer.isAction()) {
                        if (!blackjackPlayer.isDoubled()) {
                            if (!blackjackPlayer.isSplit()) {
                                if (!blackjackPlayer.isHitted()) {
                                    if (blackjackPlayer.hasMoney(blackjackPlayer.getTotalAmountBet())) {
                                        blackjackTable = blackjackPlayer.getTable();
                                        return true;
                                    } else {
                                        ErrorMessages.notEnoughMoney(getPlayer(), blackjackPlayer.getTotalAmountBet(), blackjackPlayer.getMoney());
                                    }
                                } else {
                                    ErrorMessages.playerAlreadyHit(getPlayer());
                                }
                            } else {
                                ErrorMessages.playerIsSplit(getPlayer());
                            }
                        } else {
                            ErrorMessages.playerAlreadyDoubled(getPlayer());
                        }
                    } else {
                        ErrorMessages.notYourTurn(getPlayer());
                    }
                } else {
                    ErrorMessages.tableNotInProgress(getPlayer());
                }
            } else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    @Override
    public void perform() throws Exception {
        blackjackPlayer.doubleDown();
    }
}
