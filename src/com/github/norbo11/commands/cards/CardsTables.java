package com.github.norbo11.commands.cards;

import org.bukkit.ChatColor;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class CardsTables extends PluginCommand
{
    public CardsTables()
    {
        getAlises().add("tables");
        getAlises().add("list");
        getAlises().add("l");

        setDescription("Lists all created tables.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards.*");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            Messages.sendMessage(getPlayer(), "List of currently created poker tables:");
            if (CardsTable.getTables().size() > 0) return true;
            else
            {
                ErrorMessages.noTablesAvailable(getPlayer()); // If no tables were found simply display an error message.
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
        for (CardsTable table : CardsTable.getTables()) // Goes through all tables and lists them. Displays the name in red if the table is closed, or green if its open.
        {
            String color = "";
            if (table.isOpen())
            {
                color = "&4";
            } else
            {
                color = "&2";
            }

            String type = "";
            if (table instanceof PokerTable)
            {
                type = "Poker";
            } else if (table instanceof BlackjackTable)
            {
                type = "Blackjack";
            }

            Messages.sendMessage(getPlayer(), color + "[" + table.getID() + "] " + table.getName() + " - " + type);
        }
        Messages.sendMessage(getPlayer(), ChatColor.GREEN + "GREEN = Open. &cRED = Closed.");
        Messages.sendMessage(getPlayer(), "Use " + "&6/poker sit [table ID] [buy-in] " + "&fto join a table.");
    }
}
