package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class TableUnban extends PluginCommand
{
    CardsPlayer cardsPlayer;

    CardsTable cardsTable;
    String toUnBan;

    public TableUnban()
    {
        getAlises().add("unban");
        getAlises().add("pardon");
        getAlises().add("forgive");
        getAlises().add("u");

        setDescription("Unbans the player from the table.");

        setArgumentString("[player name]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 2)
        {
            toUnBan = getArgs()[1];
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(cardsPlayer))
            {
                cardsTable = cardsPlayer.getTable();
                if (cardsTable.getBannedList().contains(toUnBan)) return true;
                else
                {
                    ErrorMessages.playerNotBanned(getPlayer(), toUnBan);
                }
            } else
            {
                ErrorMessages.notOwnerOfAnyTable(getPlayer());
            }
        } else
        {
            showUsage();
        }
        return false;
    }

    // Unbans the specified player from the player's table specified
    // in the first argument
    @Override
    public void perform()
    {
        cardsTable.getBannedList().remove(toUnBan);
        Messages.sendToAllWithinRange(cardsTable.getLocation(), "&6" + cardsPlayer.getPlayerName() + " &fhas unbanned &6" + toUnBan + " &ffrom the table!");

    }
}
