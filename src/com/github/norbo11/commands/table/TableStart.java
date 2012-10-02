package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;

public class TableStart extends PluginCommand
{

    CardsTable cardsTable;

    public TableStart()
    {
        getAlises().add("start");
        getAlises().add("go");
        getAlises().add("s");

        setDescription("Starts the game at your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    // Starts the player's table if they are the owner.
    // table start
    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(cardsPlayer))
            {
                cardsTable = cardsPlayer.getTable();
                if (!cardsTable.isInProgress())
                {
                    if (cardsTable.getPlayers().size() >= cardsTable.getMinPlayers()) return true;
                    else
                    {
                        ErrorMessages.notEnoughPlayers(getPlayer());
                    }
                } else
                {
                    ErrorMessages.tableInProgress(getPlayer());
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

    @Override
    public void perform() throws Exception
    {
        cardsTable.deal();
    }
}
