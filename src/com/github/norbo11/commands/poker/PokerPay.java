package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class PokerPay extends PluginCommand
{
    PokerPlayer playerToPay;

    public PokerPay()
    {
        getAlises().add("pay");

        setDescription("Pays the specified pot to the specified player. If only 1 pot exists, pot ID is optional.");

        setArgumentString("(pot ID) [player ID]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length != 2)
        {
            showUsage();
            return false;
        }

        PokerPlayer owner = PokerPlayer.getPokerPlayer(getPlayer().getName());
        if (!CardsTable.isOwnerOfTable(owner))
        {
            ErrorMessages.notOwnerOfAnyTable(getPlayer());
            return false;
        }

        int playerID = NumberMethods.getInteger(getArgs()[1]);
        if (playerID == -99999)
        {
            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            return false;
        }
        playerToPay = PokerPlayer.getPokerPlayer(playerID, owner.getPokerTable());
        if (playerToPay == null)
        {
            ErrorMessages.notPlayerID(getPlayer(), playerID);
            return false;
        }

        if (playerToPay.getPot() <= 0)
        {
            ErrorMessages.cantPay(getPlayer());
            return false;
        }

        return true;
    }

    // Pays the specified pot ID to the specified player ID. The first player argument is the owner typing the /table pay command.
    @Override
    public void perform() throws Exception
    {
        playerToPay.payPot();
    }
}
