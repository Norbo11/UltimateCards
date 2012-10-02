package com.github.norbo11.commands.cards;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.util.DateMethods;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public class CardsWithdraw extends PluginCommand
{

    CardsPlayer cardsPlayer;

    double amountToWithdraw;

    public CardsWithdraw()
    {
        getAlises().add("withdraw");
        getAlises().add("cashin");
        getAlises().add("w");

        setDescription("Withdraws the specified amount from your stack.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    // cards withdraw <amount>
    @Override
    public boolean conditions()
    {
        if (getArgs().length == 2)
        {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null)
            {
                if (cardsPlayer.getTable().getSettings().isAllowRebuys())
                {
                    if (!cardsPlayer.getTable().isInProgress())
                    {
                        amountToWithdraw = NumberMethods.getDouble(getArgs()[1]);
                        if (amountToWithdraw != -99999)
                        {
                            if (amountToWithdraw <= cardsPlayer.getMoney()) return true;
                            else
                            {
                                ErrorMessages.notEnoughMoney(getPlayer(), cardsPlayer.getMoney(), amountToWithdraw);
                            }
                        } else
                        {
                            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                        }
                    } else
                    {
                        ErrorMessages.tableInProgress(getPlayer());
                    }
                } else
                {
                    ErrorMessages.tableDoesntAllowRebuys(getPlayer());
                }
            } else
            {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        }
        return false;
    }

    @Override
    public void perform() throws Exception
    {
        cardsPlayer.setMoney(cardsPlayer.getMoney() - amountToWithdraw);
        UltimateCards.getEconomy().depositPlayer(getPlayer().getName(), amountToWithdraw);
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + amountToWithdraw + " to " + getPlayer().getName());
        Messages.sendToAllWithinRange(cardsPlayer.getTable().getLocation(), "&6" + getPlayer().getName() + "&f withdraws " + "&6" + Formatter.formatMoney(amountToWithdraw) + "&f New balance: " + "&6" + Formatter.formatMoney(cardsPlayer.getMoney()));
    }
}