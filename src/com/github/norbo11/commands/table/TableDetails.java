package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class TableDetails extends PluginCommand {
    public TableDetails() {
        getAlises().add("details");
        getAlises().add("info");
        getAlises().add("d");

        setDescription("Gives specific details about a table, or the one you're sitting at.");

        setArgumentString("(table ID)");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsTable cardsTable;

    // Lists the specified details type of the specified table. If no table is specified, lists details of the table that the player is sitting on.
    @Override
    public boolean conditions() {
        // cards details 5
        if (getArgs().length == 2) {
            int tableID = NumberMethods.getPositiveInteger(getArgs()[1]);
            if (tableID != -99999) {
                cardsTable = CardsTable.getTable(tableID);
                if (cardsTable != null) return true;
                else {
                    ErrorMessages.notTable(getPlayer(), getArgs()[1]);
                }
            } else {
                ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            }
        }

        // cards details
        else if (getArgs().length == 1) {
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                return true;
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
        cardsTable.displayDetails(getPlayer());
    }
}
