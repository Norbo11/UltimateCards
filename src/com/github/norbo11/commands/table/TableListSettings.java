package com.github.norbo11.commands.table;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class TableListSettings extends PluginCommand
{
    public TableListSettings()
    {
        getAlises().add("listsettings");
        getAlises().add("ls");

        setDescription("Lists all available settings for this table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "table." + getAlises().get(0));
    }

    CardsPlayer cardsPlayer;

    CardsTable cardsTable;

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer != null)
            {
                cardsTable = cardsPlayer.getTable();
                return true;
            } else
            {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else
        {
            showUsage();
        }
        return false;
    }

    // Lists all valid setting types to the player.
    @Override
    public void perform() throws Exception
    {
        if (cardsTable instanceof PokerTable)
        {
            Messages.sendMessage(getPlayer(), "&fAvailable poker settings:");
            Messages.sendMessage(getPlayer(), "&6" + UltimateCards.getLineString());
            Messages.sendMessage(getPlayer(), "&6sb [number] - &fThe small blind.");
            Messages.sendMessage(getPlayer(), "&6bb [number] - &fThe big blind");
            Messages.sendMessage(getPlayer(), "&6ante [number] - &fThe ante.");
            Messages.sendMessage(getPlayer(), "&6dynamicFrequency [number] - &fEvery [number] hands, the ante + blinds will increase by their original setting. 0 = OFF.");
            Messages.sendMessage(getPlayer(), "&6rake [number] - &fHow much of the pot you will get every hand, in percentages. Example: 0.05 = 5% rake.");
            Messages.sendMessage(getPlayer(), "&6minRaise [number] - &fThe minimum raise at the table.");
            Messages.sendMessage(getPlayer(), "&6minRaiseAlwaysBB [true|false] - &fIf true, the minimum raise will always be equal big blind.");
        } else if (cardsTable instanceof BlackjackTable)
        {
            Messages.sendMessage(getPlayer(), "&fAvailable blackjack settings:");
            Messages.sendMessage(getPlayer(), "&6" + UltimateCards.getLineString());
            Messages.sendMessage(getPlayer(), "&6allowDoubleDown [true|false] - &fIf true, players can double down.");
            Messages.sendMessage(getPlayer(), "&6serverDealer [true|false] - &fDecides if the profits should go to the table creator or the server.");
            Messages.sendMessage(getPlayer(), "&6amountOfDecks [number] - &fThe amount of decks used for the game.");
        }
        Messages.sendMessage(getPlayer(), "&fAvailable general settings:");
        Messages.sendMessage(getPlayer(), "&6" + UltimateCards.getLineString());

        Messages.sendMessage(getPlayer(), "&6allowRebuys [true|false] - &fIf true, players can't re-buy.");
        Messages.sendMessage(getPlayer(), "&6minBuy [number] - &fThe minimum (re)buy-in amount.");
        Messages.sendMessage(getPlayer(), "&6maxBuy [number] - &fThe maximum (re)buy-in amount.");
        Messages.sendMessage(getPlayer(), "&6displayTurnsPublicly [true|false] - &fIf true, the player turn announcments will be displayed publicly.");

        Messages.sendMessage(getPlayer(), "&cUsage: " + PluginExecutor.tableSet.getCommandString() + " [setting] [value]");
    }
}
