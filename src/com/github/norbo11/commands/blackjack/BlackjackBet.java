package com.github.norbo11.commands.blackjack;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackPlayer;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public class BlackjackBet extends PluginCommand
{

    BlackjackPlayer blackjackPlayer;
    BlackjackTable blackjackTable;
    double amountToBet;

    public BlackjackBet()
    {
        getAlises().add("bet");
        getAlises().add("b");

        setDescription("Bets an amount of money and puts you in the next hand.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "blackjack." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 2)
        {
            blackjackPlayer = BlackjackPlayer.getBlackjackPlayer(getPlayer().getName());
            if (blackjackPlayer != null)
            {
                if (blackjackPlayer.getTable().getOwner() != blackjackPlayer || blackjackPlayer.getBlackjackTable().getSettings().isServerDealer())
                {
                    blackjackTable = blackjackPlayer.getBlackjackTable();
                    amountToBet = NumberMethods.getDouble(getArgs()[1]);
                    if (amountToBet != -99999)
                    {
                        if (blackjackPlayer.getMoney() >= amountToBet)
                        {
                            blackjackTable = blackjackPlayer.getBlackjackTable();
                            if (amountToBet >= blackjackTable.getSettings().getMinBet())
                            {
                                if (!blackjackTable.isInProgress()) return true;
                                else
                                {
                                    ErrorMessages.tableInProgress(getPlayer());
                                }
                            } else
                            {
                                ErrorMessages.tooSmallBet(getPlayer(), blackjackTable.getSettings().getMinBet());
                            }
                        } else
                        {
                            ErrorMessages.notEnoughMoney(getPlayer(), amountToBet, blackjackPlayer.getMoney());
                        }
                    } else
                    {
                        ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                    }
                } else
                {
                    ErrorMessages.playerIsBlackjackDealer(getPlayer());
                }
            } else
            {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else
        {
            showUsage();
        }
        return false;
    }

    @Override
    public void perform() throws Exception
    {
        blackjackPlayer.setMoney(blackjackPlayer.getMoney() - amountToBet);
        blackjackPlayer.getHands().get(0).setAmountBet(blackjackPlayer.getHands().get(0).getAmountBet() + amountToBet);
        Messages.sendToAllWithinRange(blackjackTable.getLocation(), "&6" + blackjackPlayer.getPlayerName() + "&f bets &6" + Formatter.formatMoney(blackjackPlayer.getHands().get(0).getAmountBet()));

    }
}
