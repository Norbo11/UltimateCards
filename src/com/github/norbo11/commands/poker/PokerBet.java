package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.game.poker.Pot;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public class PokerBet extends PluginCommand
{

    PokerPlayer pokerPlayer;

    PokerTable pokerTable;
    double amountToBet;

    public PokerBet()
    {
        getAlises().add("bet");
        getAlises().add("b");

        setDescription("Bets or raises to the specified amount.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 2)
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
                                        amountToBet = NumberMethods.getDouble(getArgs()[1]);
                                        if (amountToBet != -99999)
                                        {
                                            if (pokerPlayer.hasMoney(amountToBet - pokerPlayer.getCurrentBet()))
                                            {
                                                if (pokerPlayer.getPokerTable().getCallablePlayers(amountToBet, pokerPlayer).size() >= 1)
                                                {
                                                    if (amountToBet >= pokerTable.getCurrentBet() + pokerTable.getSettings().getMinRaise()) return true;
                                                    else
                                                    {
                                                        ErrorMessages.cantRaise(getPlayer(), pokerTable.getSettings().getMinRaise(), pokerTable.getCurrentBet());
                                                    }
                                                } else
                                                {
                                                    ErrorMessages.tableHasNoCallers(getPlayer(), getArgs()[1], pokerTable.getHighestCallingAmount(pokerPlayer));
                                                }
                                            } else
                                            {
                                                ErrorMessages.notEnoughMoney(getPlayer(), amountToBet, pokerPlayer.getMoney());
                                            }
                                        } else
                                        {
                                            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
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

    // Bets the specified amountToBet in the name of the specified player.
    // This method is only called when raising or betting for the first time in
    // the phase. You cannot bet less than the current bet of the table
    @Override
    public void perform() throws Exception
    {
        double tableCurrentBet = pokerTable.getCurrentBet(); // The table
                                                             // current bet
                                                             // prior to the
                                                             // raise
        double alreadyContributed = pokerPlayer.getCurrentBet(); // The
                                                                 // amountToBet
                                                                 // that the
                                                                 // player
                                                                 // has already
                                                                 // put in the
                                                                 // pop prior to
                                                                 // the raise
        double raised = amountToBet - alreadyContributed; // The amountToBet
                                                          // that is actually
                                                          // being raised (so,
                                                          // the amountToBet
                                                          // over the current
                                                          // bet of the
                                                          // player that is
                                                          // raising)

        // If the player has bet all of their money, go all in instead of
        // going through this method
        if (pokerPlayer.getMoney() - raised == 0)
        {
            pokerPlayer.allIn();
            return;
        }

        pokerPlayer.setActed(true);

        // Set the table current bet to the new amountToBet, add the raised
        // amountToBet to the player's total, and set the player's
        // current bet to the amountToBet of the raise/bet
        pokerPlayer.setCurrentBet(amountToBet);
        pokerPlayer.setTotalBet(pokerPlayer.getTotalBet() + raised);
        pokerTable.setCurrentBet(amountToBet);

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
            pokerTable.getLatestPot().contribute(pokerPlayer, raised - allInCover, false);
            // if (allInCover == 0)
            // pokerTable.getPots().get(pokerTable.getPots().indexOf(pot) -
            // 1).even(this);
            pokerTable.getLatestPot().adjustPot();
        } else
        {
            pokerTable.getLatestPot().contribute(pokerPlayer, raised, false);
            pokerTable.getLatestPot().adjustPot();
        }

        pokerTable.adjustPots();
        // If the original current bet of the table was 0, say that the
        // player has "bet" (first time that someone adds money to the pot
        // during the phase)
        if (tableCurrentBet == 0)
        {
            Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + pokerPlayer.getPlayerName() + "&f bets " + "&6" + Formatter.formatMoney(amountToBet) + "&f (Total: " + "&6" + Formatter.formatMoney(pokerPlayer.getTotalBet()) + "&f)");
        } else
        {
            Messages.sendToAllWithinRange(pokerTable.getLocation(), "&6" + pokerPlayer.getPlayerName() + "&f raises to " + "&6" + Formatter.formatMoney(amountToBet) + "&f (Total: " + "&6" + Formatter.formatMoney(pokerPlayer.getTotalBet()) + "&f)");
        }
        // Deduct the raised amountToBet from the player's stack
        pokerPlayer.setMoney(pokerPlayer.getMoney() - raised);
        pokerTable.nextPersonTurn(pokerPlayer);

    }

}
