package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.game.poker.Pot;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class PokerPay extends PluginCommand
{

    PokerPlayer owner;

    PokerPlayer playerToPay;
    PokerTable pokerTable;
    Pot potToPay;

    public PokerPay()
    {
        getAlises().add("pay");
        getAlises().add("p");

        setDescription("Pays the specified pot to the specified player. If only 1 pot exists, pot ID is optional.");

        setArgumentString("(pot ID) [player ID]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 3)
        {
            owner = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(owner))
            {
                pokerTable = owner.getPokerTable();
                int potID = NumberMethods.getInteger(getArgs()[1]);
                if (potID != -99999)
                {
                    int playerID = NumberMethods.getInteger(getArgs()[2]);
                    if (playerID != -99999)
                    {
                        if (!pokerTable.isInProgress())
                        {
                            Pot pot = pokerTable.getPot(potID);
                            if (pot != null)
                            {
                                if (pot.getPot() > 0)
                                {
                                    playerToPay = PokerPlayer.getPokerPlayer(playerID, pokerTable);
                                    if (playerToPay != null) return true;
                                    else
                                    {
                                        ErrorMessages.notPlayerID(getPlayer(), playerID);
                                    }
                                } else
                                {
                                    ErrorMessages.potEmpty(getPlayer(), pot);
                                }
                            } else
                            {
                                ErrorMessages.notPotID(getPlayer(), getArgs()[1]);
                            }
                        } else
                        {
                            ErrorMessages.tableInProgress(getPlayer());
                        }
                    } else
                    {
                        ErrorMessages.invalidNumber(getPlayer(), getArgs()[2]);
                    }
                } else
                {
                    ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                }
            } else
            {
                ErrorMessages.notOwnerOfAnyTable(getPlayer());
            }
        } else if (getArgs().length == 2)
        {
            owner = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(owner))
            {
                pokerTable = owner.getPokerTable();
                if (pokerTable.getPots().size() == 1) // If there is only 1 pot (the main one)
                {
                    if (pokerTable.getPots().get(0).getPot() > 0) // If the main pot is not 0
                    {
                        potToPay = pokerTable.getPots().get(0);
                        int playerID = NumberMethods.getInteger(getArgs()[1]);
                        if (playerID != -99999)
                        {
                            playerToPay = PokerPlayer.getPokerPlayer(playerID, pokerTable);
                            if (playerToPay != null) return true;
                            else
                            {
                                ErrorMessages.notPlayerID(getPlayer(), playerID);
                            }
                        } else
                        {
                            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                        }
                    } else
                    {
                        ErrorMessages.potEmpty(getPlayer(), pokerTable.getPots().get(0));
                    }
                } else
                {
                    ErrorMessages.tableHasMultiplePots(getPlayer());
                }
            } else
            {
                ErrorMessages.notOwnerOfAnyTable(getPlayer());
            }
        } else
        {
            showUsage();
        }
        return false;
    }

    // Pays the specified pot ID to the specified player ID. The first player argument is the owner typing the /table pay command.
    @Override
    public void perform() throws Exception
    {
        potToPay.payPot(playerToPay);
    }
}
