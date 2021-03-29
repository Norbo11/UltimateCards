package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class TableMoney extends PluginCommand {
    public TableMoney() {
        getAlises().add("money");
        getAlises().add("balance");
        getAlises().add("m");

        setDescription("Shows you your remaining stack at the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());

            if (cardsPlayer != null) return true;
            else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }

        return false;
    }

    // Displays the player's money just to the player
    @Override
    public void perform() throws Exception {

        Messages.sendMessage(getPlayer().getName(), "You have &6" + Formatter.formatMoney(cardsPlayer.getMoney()) + "&f left on this table.");
        Messages.sendMessage(getPlayer().getName(), "Average stack size: &6" + Formatter.formatMoney(cardsPlayer.getTable().getAverageStack()));
    }

}
