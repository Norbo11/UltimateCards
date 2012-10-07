package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerAllin extends PluginCommand
{

    PokerPlayer pokerPlayer;

    PokerTable pokerTable;

    public PokerAllin()
    {
        getAlises().add("allin");
        getAlises().add("shove");
        getAlises().add("a");

        setDescription("Bets the rest of your stack and puts you in all in mode.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
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
                    if (pokerTable.isInProgress()) // Check if the table is
                                                   // in progress
                    {
                        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN)
                        {
                            if (pokerPlayer.isAction())
                            {
                                if (!pokerPlayer.isFolded())
                                {
                                    if (pokerPlayer.getAllIn() == 0) // Check if the player is not already all in
                                    {
                                        // Check if the player has enough money to call. This is done by taking their current bet away from the table current bet,
                                        // effectively getting the amount that they need to call. Then add the minimum raise to it.
                                        if (pokerTable.getCurrentBet() - pokerPlayer.getCurrentBet() + pokerTable.getSettings().getMinRaise() > pokerPlayer.getMoney()) return true;
                                        else
                                        {
                                            ErrorMessages.cantAllIn(getPlayer());
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

    // Declares the specified player all in
    @Override
    public void perform() throws Exception
    {
        pokerPlayer.allIn();
    }
}
