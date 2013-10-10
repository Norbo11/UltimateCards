package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsTableSettings;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class BlackjackTableSettings extends CardsTableSettings {
    public BlackjackTableSettings(BlackjackTable table) {
        super(table);
    }

    private boolean allowDoubleDown = UltimateCards.getPluginConfig().isAllowDoubleDown();
    private double minBet = UltimateCards.getPluginConfig().getMinBet();

    private int amountOfDecks = UltimateCards.getPluginConfig().getAmountOfDecks();

    public int getAmountOfDecks() {
        return amountOfDecks;
    }

    public double getMinBet() {
        return minBet;
    }

    public boolean isAllowDoubleDown() {
        return allowDoubleDown;
    }

    // Lists the settings of the table, returning a string array
    @Override
    public ArrayList<String> listTableSpecificSettings() {
        ArrayList<String> list = new ArrayList<String>();

        list.add("Minimum Bet: &6" + Formatter.formatMoney(minBet));
        list.add("Allow double down: &6" + allowDoubleDown);
        list.add("Amount of decks: &6" + amountOfDecks);

        return list;
    }

    public void setAllowDoubleDown(boolean value) {
        allowDoubleDown = value;
        if (value) {
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has allowed " + "&6Double Down&f!");
        } else {
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has disallowed " + "&6Double Down&f!");
        }
    }

    public void setAllowDoubleDownNoMsg(boolean value) {
        allowDoubleDown = value;
    }

    public void setAmountOfDecks(int value) {
        amountOfDecks = value;
        if (amountOfDecks > 15) {
            amountOfDecks = 15;
        }
        getTable().getDeck().setAmountOfDecks(value);
        getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&Amount of Decks" + "&f to &6" + amountOfDecks);
    }

    public void setAmountOfDecksNoMsg(int value) {
        amountOfDecks = value;
        if (amountOfDecks > 15) {
            amountOfDecks = 15;
        }
        getTable().getDeck().setAmountOfDecks(value);
    }

    public void setMinBet(double value) {
        minBet = value;
        getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Minimum Bet" + "&f to &6" + Formatter.formatMoney(value));
    }

    public void setMinBetNoMsg(double value) {
        minBet = value;
    }

    @Override
    public void setTableSpecificSetting(String setting, String v) {
        if (setting.equalsIgnoreCase("minBet")) {
            double value = checkDouble(v);
            if (value != -99999) {
                setMinBet(value);
            }
        } else if (setting.equalsIgnoreCase("allowDoubleDown")) {
            String value = checkBoolean(v);
            if (!value.equalsIgnoreCase("")) {
                setAllowDoubleDown(Boolean.parseBoolean(checkBoolean(v)));
            }
        } else if (setting.equalsIgnoreCase("amountOfDecks")) {
            int value = checkInteger(v);
            if (value != -99999) {
                setAmountOfDecks(value);
            }
        } else {
            Messages.sendMessage(getTable().getOwnerPlayer().getPlayer(), "&cInvalid setting. Check available settings with " + PluginExecutor.tableListSettings.getCommandString() + ".");
        }
    }

}
