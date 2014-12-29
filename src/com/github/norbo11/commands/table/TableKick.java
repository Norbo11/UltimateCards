package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.NumberMethods;

public class TableKick extends PluginCommand {
    public TableKick() {
        getAlises().add("kick");
        getAlises().add("boot");
        getAlises().add("k");

        setDescription("Kicks the specified player from your table.");

        setArgumentString("[player ID]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;
    CardsTable cardsTable;
    CardsPlayer toKick;

    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.isOwner(cardsPlayer.getPlayerName())) {
                    int IDtoKick = NumberMethods.getPositiveInteger(getArgs()[1]);
                    if (IDtoKick != -99999) {
                        toKick = CardsPlayer.getCardsPlayer(IDtoKick, cardsTable);
                        if (toKick != null) // Check if the ID specified is a real poker player.
                        {
                            return true;
                        } else {
                            ErrorMessages.notPlayerID(getPlayer(), IDtoKick);
                        }
                    } else {
                        ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
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

    // Kicks the specified player from the owner's table
    @Override
    public void perform() {
        toKick.getTable().kick(toKick);
    }
}
