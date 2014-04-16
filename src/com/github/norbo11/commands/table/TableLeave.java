package com.github.norbo11.commands.table;

import org.bukkit.Bukkit;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;

public class TableLeave extends PluginCommand {
    public TableLeave() {
        getAlises().add("leave");
        getAlises().add("getup");
        getAlises().add("stand");
        getAlises().add("standup");

        setDescription("Leaves the table that you are currently sitting at.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsTable cardsTable;
    CardsPlayer cardsPlayer;

    double money;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null) {
                cardsTable = cardsPlayer.getTable();
                money = cardsPlayer.getMoney();
                return true;
            } else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Deletes the specified player from the table, if they are currently sitting at one. Doesnt allow the owner to leave
    @Override
    public void perform() throws Exception {
        cardsTable.playerLeave(cardsPlayer);

        MoneyMethods.depositMoney(getPlayer().getName(), money);

        // Message
        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f has left the table with " + "&6" + Formatter.formatMoney(money));

        // Teleport
        Bukkit.getPlayer(getPlayer().getName()).teleport(cardsPlayer.getStartLocation());

        // Remove player
        cardsTable.removePlayer(cardsPlayer);
    }
}
