package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class TableOpen extends PluginCommand
{
    CardsTable cardsTable;

    public TableOpen()
    {
        getAlises().add("open");
        getAlises().add("unlock");
        getAlises().add("o");

        setDescription("Opens your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(cardsPlayer))
            {
                cardsTable = cardsPlayer.getTable();
                if (!cardsTable.isOpen()) return true;
                else
                {
                    ErrorMessages.tableAlreadyOpen(getPlayer());
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

    // Opens the specified player's table
    @Override
    public void perform()
    {
        cardsTable.setOpen(true);
        Messages.sendToAllWithinRange(cardsTable.getLocation(), "Table named &6" + cardsTable.getName() + "&f, ID #&6" + cardsTable.getID() + "&f is now open! Players can now join!");

    }
}
