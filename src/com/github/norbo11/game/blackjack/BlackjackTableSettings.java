package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsTableSettings;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class BlackjackTableSettings extends CardsTableSettings
{
    private boolean allowDoubleDown = UltimateCards.getPluginConfig().isAllowDoubleDown();
    private boolean serverDealer = UltimateCards.getPluginConfig().isServerDealer();
    private double minBet = UltimateCards.getPluginConfig().getMinBet();
    private int amountOfDecks = UltimateCards.getPluginConfig().getAmountOfDecks();

    public BlackjackTableSettings(BlackjackTable table)
    {
        super(table);

        if (UltimateCards.getPluginConfig().isServerNeverDealer())
        {
            serverDealer = false;
        }
    }

    public int getAmountOfDecks()
    {
        return amountOfDecks;
    }

    public double getMinBet()
    {
        return minBet;
    }

    public boolean isAllowDoubleDown()
    {
        return allowDoubleDown;
    }

    public boolean isServerDealer()
    {
        return serverDealer;
    }

    // Lists the settings of the table, returning a string array
    @Override
    public ArrayList<String> listTableSpecificSettings()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("Minimum Bet: &6" + Formatter.formatMoney(minBet));
        list.add("Allow double down: &6" + allowDoubleDown);
        list.add("Server is the dealer: &6" + serverDealer);
        list.add("Amount of decks: &6" + amountOfDecks);

        return list;
    }

    public void setAllowDoubleDown(boolean value)
    {
        allowDoubleDown = value;
        if (value)
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has allowed " + "&6Double Down&f!");
        } else
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has disallowed " + "&6Double Down&f!");
        }
    }

    public void setAmountOfDecks(int value)
    {
        amountOfDecks = value;
        if (amountOfDecks > 15)
        {
            amountOfDecks = 15;
        }
        getTable().getDeck().setAmountOfDecks(value);
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&Amount of Decks" + "&f to &6" + amountOfDecks);
    }

    public void setMinBet(double value)
    {
        minBet = value;
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Minimum Bet" + "&f to &6" + Formatter.formatMoney(value));
    }

    public void setServerDealer(boolean value)
    {
        if (!UltimateCards.getPluginConfig().isServerNeverDealer())
        {
            BlackjackPlayer owner = (BlackjackPlayer) getTable().getOwner();
            if (!owner.playingThisHand())
            {
                serverDealer = value;
                if (value)
                {
                    Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the server to be the table dealer!");
                } else
                {
                    Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set himself (not the server) to be the table dealer!");
                }
            } else
            {
                ErrorMessages.playerAlreadyPlaying(owner.getPlayer());
            }
        } else
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "Sorry, the server cannot be the dealer!");
        }
    }

    @Override
    public void setTableSpecificSetting(String setting, String v)
    {
        if (setting.equalsIgnoreCase("minBet"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setMinBet(value);
            }
        } else if (setting.equalsIgnoreCase("allowDoubleDown"))
        {
            String value = checkBoolean(v);
            if (!value.equalsIgnoreCase(""))
            {
                setAllowDoubleDown(Boolean.parseBoolean(checkBoolean(v)));
            }
        } else if (setting.equalsIgnoreCase("serverDealer"))
        {
            String value = checkBoolean(v);
            if (!value.equalsIgnoreCase(""))
            {
                setServerDealer(Boolean.parseBoolean(checkBoolean(v)));
            }
        } else if (setting.equalsIgnoreCase("amountOfDecks"))
        {
            int value = checkInteger(v);
            if (value != -99999)
            {
                setAmountOfDecks(value);
            }
        } else
        {
            Messages.sendMessage(getTable().getOwner().getPlayer(), "&cInvalid setting. Check available settings with " + PluginExecutor.tableListSettings.getCommandString() + ".");
        }
    }

}
