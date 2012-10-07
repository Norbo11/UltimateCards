package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class TableClose extends PluginCommand
{
    CardsPlayer owner;

    CardsTable table;

    public TableClose()
    {
        getAlises().add("close");
        getAlises().add("lock");
        getAlises().add("c");

        setDescription("Closes your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            owner = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(owner))
            {
                table = owner.getTable();
                if (table.isOpen()) return true;
                else
                {
                    ErrorMessages.tableAlreadyClosed(getPlayer());
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

    // Closes the table and doesn't allow any more people to sit
    @Override
    public void perform() throws Exception
    {
        table.setOpen(false);
        Messages.sendToAllWithinRange(table.getLocation(), "Table named &6" + table.getName() + "&f, ID #&6" + table.getID() + "&f is now closed! Players now can't join!");
    }
}
