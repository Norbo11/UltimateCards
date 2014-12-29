package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableClose extends PluginCommand {
    public TableClose() {
        getAlises().add("close");
        getAlises().add("lock");
        getAlises().add("c");

        setDescription("Closes your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;
    CardsTable cardsTable;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.isOwner(cardsPlayer.getPlayerName())) {
                    if (cardsTable.isOpen()) return true;
                    else {
                        ErrorMessages.tableAlreadyClosed(getPlayer());
                    }
                } else {
                    ErrorMessages.playerNotOwner(getPlayer());
                }
            } else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Closes the table and doesn't allow any more people to sit
    @Override
    public void perform() throws Exception {
        cardsTable.setOpen(false);
        cardsTable.sendTableMessage("Table named &6" + cardsTable.getName() + "&f, ID #&6" + cardsTable.getId() + "&f is now closed! Players now can't join!");
    }
}
