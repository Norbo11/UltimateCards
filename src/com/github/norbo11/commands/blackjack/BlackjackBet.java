package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class BlackjackBet extends PluginCommand {

    public BlackjackBet() {
        getAlises().add("bet");
        getAlises().add("b");

        setDescription("Bets an amount of money and puts you in the next hand.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    BlackjackPlayer blackjackPlayer;
    BlackjackTable blackjackTable;

    double amountToBet;

    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(getPlayer().getName());
            if (blackjackPlayer != null) {
                if (blackjackPlayer.getTable().getOwnerPlayer() != blackjackPlayer) {
                    blackjackTable = blackjackPlayer.getTable();
                    amountToBet = NumberMethods.getDouble(getArgs()[1]);
                    if (amountToBet != -99999) {
                        if (blackjackPlayer.getMoney() >= amountToBet) {
                            blackjackTable = blackjackPlayer.getTable();
                            if (blackjackTable.getDealer().hasEnoughMoney(amountToBet)) {
                                if (amountToBet >= blackjackTable.getSettings().minBet.getValue()) {
                                    if (!blackjackTable.isInProgress()) return true;
                                    else {
                                        ErrorMessages.tableInProgress(getPlayer());
                                    }
                                } else {
                                    ErrorMessages.tooSmallBet(getPlayer(), blackjackTable.getSettings().minBet.getValue());
                                }
                            } else {
                                ErrorMessages.dealerHasNotEnoughMoney(getPlayer(), blackjackTable.getOwnerPlayer().getMoney() / ((blackjackTable.getPlayers().size() - 1) * 2));
                            }
                        } else {
                            ErrorMessages.notEnoughMoney(getPlayer(), amountToBet, blackjackPlayer.getMoney());
                        }
                    } else {
                        ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                    }
                } else {
                    ErrorMessages.playerIsBlackjackDealer(getPlayer());
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
        blackjackPlayer.bet(amountToBet);
    }
}
