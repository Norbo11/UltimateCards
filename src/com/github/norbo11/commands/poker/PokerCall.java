package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.game.poker.Pot;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class PokerCall extends PluginCommand
{

    PokerPlayer pokerPlayer;

    PokerTable pokerTable;

    public PokerCall()
    {
        getAlises().add("call");
        getAlises().add("match");
        getAlises().add("ca");

        setDescription("Matches your total bet with the rest of the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null)
            {
                if (!pokerPlayer.isEliminated())
                {
                    pokerTable = pokerPlayer.getPokerTable();
                    if (pokerTable.isInProgress())
                    {
                        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN)
                        {
                            if (pokerPlayer.isAction())
                            {
                                if (!pokerPlayer.isFolded())
                                {
                                    if (pokerPlayer.getAllIn() == 0)
                                    {
                                        if (pokerPlayer.getCurrentBet() < pokerTable.getCurrentBet()) // Check if the player hasn't already called
                                        {
                                            if (pokerPlayer.hasMoney(pokerTable.getCurrentBet() - pokerPlayer.getCurrentBet())) return true;
                                            else
                                            {
                                                ErrorMessages.notEnoughMoney(getPlayer(), pokerTable.getCurrentBet(), pokerPlayer.getMoney());
                                            }
                                        } else
                                        {
                                            ErrorMessages.cantCall(getPlayer());
                                        }
                                    } else
                                    {
                                        ErrorMessages.playerIsAllIn(getPlayer());
                                    }
                                } else
                                {
                                    ErrorMessages.playerIsFolded(getPlayer());
                                }
                            } else
                            {
                                ErrorMessages.notYourTurn(getPlayer());
                            }
                        } else
                        {
                            ErrorMessages.tableAtShowdown(getPlayer());
                        }
                    } else
                    {
                        ErrorMessages.tableNotInProgress(getPlayer());
                    }
                } else
                {
                    ErrorMessages.playerIsEliminated(getPlayer());
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

    // Calls the latest bet in the name of the player.
    @Override
    public void perform() throws Exception
    {

        double called = pokerTable.getCurrentBet() - pokerPlayer.getCurrentBet(); // Amount
                                                                                  // of
                                                                                  // money
                                                                                  // that
                                                                                  // is
                                                                                  // being
                                                                                  // called
        // double allInCover = called - pokerTable.getLatestPot().pot; // The
        // cover for the latest all in.

        if (pokerPlayer.getMoney() - called == 0)
        {
            pokerPlayer.allIn();
            return;
        }

        pokerPlayer.setActed(true);

        // Call, by simply adding the amount of money being called to the
        // player's current and total bet
        pokerPlayer.setCurrentBet(pokerPlayer.getCurrentBet() + called);
        pokerPlayer.setTotalBet(pokerPlayer.getTotalBet() + called);

        // If there is more than 1 pot (effectively, if there is a side pot) and
        // that pot was created during the current phase
        if (pokerTable.getPots().size() > 1 && pokerTable.getLatestPot().getPokerPhase() == pokerTable.getCurrentPhase())
        {
            double allInCover = 0;
            for (Pot pot : pokerTable.getPots())
                if (!pot.isMain() && pot.getPlayerAllIn() != pokerPlayer) if (pot.getPlayerAllIn().getAllIn() > pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).getContribution(pokerPlayer))
                {
                    allInCover = allInCover + (pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).getContribution(pot.getPlayerAllIn()) - pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).getContribution(pokerPlayer));
                    pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).contribute(pokerPlayer, pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).getContribution(pot.getPlayerAllIn()) - pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).getContribution(pokerPlayer), false);
                    pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) - 1).adjustPot();
                }
            pokerTable.getLatestPot().contribute(pokerPlayer, called - allInCover, false);
            // if (allInCover == 0)
            // pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) -
            // 1).even(this);
            pokerTable.getLatestPot().adjustPot();
        } else
        {
            pokerTable.getLatestPot().contribute(pokerPlayer, called, false);
            pokerTable.getLatestPot().adjustPot();
        }

        pokerTable.adjustPots();
        pokerPlayer.setMoney(pokerPlayer.getMoney() - called);
        Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + pokerPlayer.getPlayerName() + "&f calls " + "&6" + Formatter.formatMoney(called) + "&f (Total: " + "&6" + Formatter.formatMoney(pokerPlayer.getTotalBet()) + "&f)");
        pokerTable.nextPersonTurn(pokerPlayer);

    }
}
