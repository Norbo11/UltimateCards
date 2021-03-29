package com.github.norbo11.commands.table;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class TableRebuy extends PluginCommand {
    public TableRebuy() {
        getAlises().add("rebuy");
        getAlises().add("addmoney");
        getAlises().add("addstack");
        getAlises().add("r");

        setDescription("Adds more money to your stack.");

        setArgumentString("[amount]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;
    CardsTable cardsTable;

    double amount;

    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.getSettings().allowRebuys.getValue()) {
                    if (!cardsTable.isInProgress()) {
                        amount = NumberMethods.getDouble(getArgs()[1]);
                        if (amount != -99999) {
                            if (UltimateCards.getEconomy().has(getPlayer().getName(), amount)) return true;
                            else {
                                ErrorMessages.notEnoughMoney(getPlayer(), amount, UltimateCards.getEconomy().getBalance(getPlayer().getName()));
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

    // Adds money to the specified player
    @Override
    public void perform() throws Exception {
        // Withdraw the desired amount from the ECONOMY, add it to their stack, then display the message
        MoneyMethods.withdrawMoney(getPlayer().getName(), amount);
        cardsPlayer.setMoney(cardsPlayer.getMoney() + amount);

        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f has added &6" + Formatter.formatMoney(amount) + "&f to his stack. New balance: &6" + Formatter.formatMoney(cardsPlayer.getMoney()));
        if (cardsTable instanceof PokerTable) {
            PokerTable pokerTable = (PokerTable) cardsTable;
            pokerTable.autoStart();
        }
    }
}
