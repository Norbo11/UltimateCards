package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class TableRelocate extends PluginCommand {
    public TableRelocate() {
        getAlises().add("relocate");

        setDescription("Changes a table's location to wherever you are standing.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    CardsTable cardsTable;
    CardsPlayer cardsPlayer;

    // table create name buyin poker|blackjack
    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.isOwner(cardsPlayer.getPlayerName())) return true;
                else {
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

    @Override
    public void perform() throws Exception {
        cardsTable.setLocation(getPlayer().getLocation());
        Messages.sendMessage(getPlayer(), "&fTable relocated to " + Formatter.formatLocation(cardsTable.getLocation()));
    }
}
