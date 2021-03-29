package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableBan extends PluginCommand {
    public TableBan() {
        getAlises().add("ban");
        getAlises().add("b");

        setDescription("Bans the player from the table.");

        setArgumentString("[player name]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    String toBan;
    CardsPlayer cardsPlayer;
    CardsTable cardsTable;

    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            toBan = getArgs()[1];
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.isOwner(cardsPlayer.getPlayerName())) {
                    if (!cardsTable.getBannedList().contains(toBan)) {
                        return true;
                    } else {
                        ErrorMessages.playerAlreadyBanned(getPlayer(), toBan);
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

    // Bans the specified player
    @Override
    public void perform() throws Exception {
        cardsTable.getBannedList().add(toBan);
        cardsTable.sendTableMessage("&6" + cardsPlayer.getPlayerName() + "&f has banned &6" + toBan + "&f from the table!");

    }
}
