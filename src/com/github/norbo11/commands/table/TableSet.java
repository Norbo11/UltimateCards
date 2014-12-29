package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableSet extends PluginCommand {
    public TableSet() {
        getAlises().add("set");

        setDescription("Sets the [setting] to the [value]");

        setArgumentString("[setting] [value]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;
    CardsTable cardsTable;

    @Override
    // table set <Setting> <value>
    public boolean conditions() {
        
        //TODO: rework this and make it work with no arguments (for locations)
        if (getArgs().length == 3) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.isOwner(cardsPlayer.getPlayerName())) {
                    if (!cardsTable.isInProgress()) return true;
                    else {
                        ErrorMessages.tableInProgress(getPlayer());
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

    // Sets the specified setting on the player's table, to the specified
    // value.
    @Override
    public void perform() throws Exception {        
        cardsTable.getSettings().setSetting(getArgs()[1], getArgs()[2]);
    }
}
