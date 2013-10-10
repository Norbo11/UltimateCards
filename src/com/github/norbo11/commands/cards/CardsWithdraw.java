package com.github.norbo11.commands.cards;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class CardsWithdraw extends PluginCommand {

    public CardsWithdraw() {
        getAlises().add("withdraw");
        getAlises().add("cashin");
        getAlises().add("w");

        setDescription("Withdraws the specified amount from your stack.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;
    CardsTable cardsTable;

    double amountToWithdraw;

    // cards withdraw <amount>
    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.getSettings().isAllowRebuys()) {
                    if (!cardsPlayer.getTable().isInProgress()) {
                        amountToWithdraw = NumberMethods.getDouble(getArgs()[1]);
                        if (amountToWithdraw != -99999) {
                            if (amountToWithdraw <= cardsPlayer.getMoney()) return true;
                            else {
                                ErrorMessages.notEnoughMoney(getPlayer(), cardsPlayer.getMoney(), amountToWithdraw);
                            }
                        } else {
                            ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                        }
                    } else {
                        ErrorMessages.tableInProgress(getPlayer());
                    }
                } else {
                    ErrorMessages.tableDoesntAllowRebuys(getPlayer());
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
        cardsPlayer.setMoney(cardsPlayer.getMoney() - amountToWithdraw);
        MoneyMethods.depositMoney(getPlayer().getName(), amountToWithdraw);
        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f withdraws " + "&6" + Formatter.formatMoney(amountToWithdraw) + "&f New balance: " + "&6" + Formatter.formatMoney(cardsPlayer.getMoney()));
    }
}
