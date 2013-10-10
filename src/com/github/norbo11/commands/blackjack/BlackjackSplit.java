package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackHand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.cards.Card;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;

public class BlackjackSplit extends PluginCommand {

    public BlackjackSplit() {
        getAlises().add("split");
        getAlises().add("sp");

        setDescription("Splits your cards and puts each card in a seperate hand.");

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
                blackjackTable = blackjackPlayer.getBlackjackTable();

                if (blackjackTable.isInProgress()) {
                    if (blackjackPlayer.isAction()) {
                        if (!blackjackPlayer.isSplit()) {
                            if (!blackjackPlayer.isHitted()) {
                                if (blackjackPlayer.sameHoleCards()) {
                                    if (blackjackPlayer.hasMoney(blackjackPlayer.getTotalAmountBet())) {
                                        blackjackTable = blackjackPlayer.getBlackjackTable();
                                        return true;
                                    } else {
                                        ErrorMessages.notEnoughMoney(getPlayer(), blackjackPlayer.getTotalAmountBet(), blackjackPlayer.getMoney());
                                    }
                                } else {
                                    ErrorMessages.holeCardsNotMatching(getPlayer());
                                }
                            } else {
                                ErrorMessages.playerIsSplit(getPlayer());
                            }
                        } else {
                            ErrorMessages.playerIsStayed(getPlayer());
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
        blackjackPlayer.removeMoney(blackjackPlayer.getHands().get(0).getAmountBet());
        blackjackPlayer.getBlackjackTable().getDealer().addMoney(blackjackPlayer.getHands().get(0).getAmountBet());
        blackjackPlayer.getHands().add(new BlackjackHand(blackjackPlayer, blackjackPlayer.getHands().get(0).getAmountBet()));

        blackjackTable.sendTableMessage("&6" + blackjackPlayer.getPlayerName() + "&f splits! New bet: &6" + Formatter.formatMoney(blackjackPlayer.getTotalAmountBet()));

        Card card = blackjackPlayer.getHands().get(0).getHand().getCards().get(0);
        blackjackPlayer.getHands().get(0).getHand().getCards().remove(card);
        blackjackPlayer.getHands().get(1).getHand().getCards().add(card);

        // Hit both hands
        blackjackPlayer.getHands().get(0).recalculateScore();
        blackjackPlayer.getHands().get(0).addCards(blackjackTable.getDeck().generateCards(1));
        blackjackPlayer.getHands().get(1).recalculateScore();
        blackjackPlayer.getHands().get(1).addCards(blackjackTable.getDeck().generateCards(1));
        blackjackPlayer.displayScore();
        blackjackPlayer.setHitted(true);

        blackjackTable.nextPersonTurn(blackjackPlayer);
    }
}
