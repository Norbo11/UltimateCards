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
        if (getArgs().length == 2 || getArgs().length == 3) {
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
        String setting = getArgs()[1];

        if (setting.equalsIgnoreCase("startLocation")) {
            cardsTable.getSettings().setStartLocation(getPlayer().getLocation());
        } else if (setting.equalsIgnoreCase("leaveLocation")) {
            cardsTable.getSettings().setLeaveLocation(getPlayer().getLocation());
        } else if (getArgs().length == 3) {
            String value = getArgs()[2];
            cardsTable.getSettings().setSetting(setting, value);
        }
        
    }
}
