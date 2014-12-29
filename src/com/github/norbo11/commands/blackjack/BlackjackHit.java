package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class BlackjackHit extends PluginCommand {

    public BlackjackHit() {
        getAlises().add("hit");
        getAlises().add("hitme");
        getAlises().add("h");

        setDescription("Gives you an additional card.");

        setArgumentString("(hand ID)");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    BlackjackPlayer blackjackPlayer;
    BlackjackTable blackjackTable;

    int hand = 0;

    @Override
    public boolean conditions() {
        hand = 0;
        if (getArgs().length == 1 || getArgs().length == 2) {
            blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(getPlayer().getName());
            if (blackjackPlayer != null) {
                blackjackTable = blackjackPlayer.getTable();

                if (blackjackTable.isInProgress()) {
                    if (blackjackPlayer.isAction()) {
                        if (getArgs().length == 2) {
                            if (blackjackPlayer.isSplit()) {
                                hand = NumberMethods.getPositiveInteger(getArgs()[1]);
                                if (hand != 0 && hand != 1) {
                                    ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                                    return false;
                                }
                            } else {
                                ErrorMessages.cannotSpecifyHand(getPlayer());
                                return false;
                            }
                        } else {
                            if (blackjackPlayer.isSplit()) {
                                ErrorMessages.needToSpecifyHand(getPlayer());
                                return false;
                            }
                        }

                        if (!blackjackPlayer.getHands().get(hand).isBust()) {
                            if (!blackjackPlayer.getHands().get(hand).isStayed()) return true;
                            else {
                                ErrorMessages.playerIsStayed(getPlayer());
                            }
                        } else {
                            ErrorMessages.playerIsBust(getPlayer());
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
        blackjackPlayer.hit(hand);
    }
}
