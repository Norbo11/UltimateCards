package com.github.norbo11.commands.table;

import org.bukkit.Bukkit;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class TableBan extends PluginCommand
{
    String toBan;

    CardsPlayer owner;
    CardsTable table;

    public TableBan()
    {
        getAlises().add("ban");
        getAlises().add("b");

        setDescription("Bans the player from the table.");

        setArgumentString("[player name]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 2)
        {
            toBan = getArgs()[1];
            owner = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (CardsTable.isOwnerOfTable(owner))
            {
                table = owner.getTable();
                if (!table.getBannedList().contains(toBan))
                {
                    if (Bukkit.getPlayer(toBan) != null) return true;
                    else
                    {
                        ErrorMessages.playerNotFound(getPlayer(), toBan);
                    }
                } else
                {
                    ErrorMessages.playerAlreadyBanned(getPlayer(), toBan);
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

    // Bans the specified player
    @Override
    public void perform() throws Exception
    {
        table.getBannedList().add(toBan);
        Messages.sendToAllWithinRange(table.getLocation(), "&6" + owner.getPlayerName() + "&f has banned &6" + toBan + "&f from the table!");

    }
}
