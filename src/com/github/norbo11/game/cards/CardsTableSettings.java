package com.github.norbo11.game.cards;

import java.util.ArrayList;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public abstract class CardsTableSettings
{
    public CardsTableSettings(CardsTable table)
    {
        this.table = table;
    }

    private boolean allowRebuys = UltimateCards.getPluginConfig().isAllowRebuys();
    private boolean displayTurnsPublicly = UltimateCards.getPluginConfig().isDisplayTurnsPublicly();
    private double minBuy = UltimateCards.getPluginConfig().getMinBuy();
    private double maxBuy = UltimateCards.getPluginConfig().getMaxBuy();

    private CardsTable table;

    public String checkBoolean(String v)
    {
        boolean value;

        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes"))
        {
            value = true;
        } else if (v.equalsIgnoreCase("false") || v.equalsIgnoreCase("no"))
        {
            value = false;
        } else
        {
            Messages.sendMessage(table.getOwner().getPlayer(), "&6" + v + "&c is an invalid value! Please specify " + "&6true|yes &cor" + "&6 false|no &conly.");
            return "";
        }
        return Boolean.toString(value);
    }

    public double checkDouble(String v)
    {
        double value = NumberMethods.getDouble(v);

        if (value != -99999) return value;
        else
        {
            ErrorMessages.invalidNumber(table.getOwner().getPlayer(), v);
        }
        return -99999;
    }

    public int checkInteger(String v)
    {
        int integer = NumberMethods.getInteger(v);
        if (integer != -99999) return integer;
        else
        {
            ErrorMessages.invalidNumber(table.getOwner().getPlayer(), v);
        }
        return -99999;
    }

    public double checkPercentage(String v)
    {
        double value = NumberMethods.getDouble(v);

        if (value >= 0 && value <= 1) return value;
        else
        {
            ErrorMessages.invalidPercentage(table.getOwner().getPlayer());
        }
        return -99999;
    }

    public double getMaxBuy()
    {
        return maxBuy;
    }

    public double getMinBuy()
    {
        return minBuy;
    }

    public CardsTable getTable()
    {
        return table;
    }

    public boolean isAllowRebuys()
    {
        return allowRebuys;
    }

    public boolean isDisplayTurnsPublicly()
    {
        return displayTurnsPublicly;
    }

    public ArrayList<String> listSettings()
    {
        ArrayList<String> messages = new ArrayList<String>();

        messages.add("Minimum Buy-in: &6" + Formatter.formatMoney(minBuy));
        messages.add("Maximum Buy-in: &6" + Formatter.formatMoney(maxBuy));
        messages.add("Display turns publicly: &6" + displayTurnsPublicly);
        messages.add("Allow rebuys: &6" + allowRebuys);
        messages.addAll(listTableSpecificSettings());

        return messages;
    }

    public abstract ArrayList<String> listTableSpecificSettings();

    public void setAllowRebuys(boolean value)
    {
        allowRebuys = value;
        if (value == true)
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has allowed rebuys!");
        } else
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has disallowed rebuys!");
        }
    }

    public void setDisplayTurnsPublicly(boolean value)
    {
        displayTurnsPublicly = value;
        if (value)
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has made turn messages display publicly!");
        } else
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has made turn messages display privately!");
        }
    }

    public void setMaxBuy(double value)
    {
        maxBuy = value;
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Maximum Buy-In" + "&f to &6" + Formatter.formatMoney(value));
    }

    public void setMinBuy(double value)
    {
        minBuy = value;
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Minimum Buy-In" + "&f to &6" + Formatter.formatMoney(value));
    }

    public void setSetting(String setting, String v)
    {
        if (setting.equalsIgnoreCase("displayTurnsPublicly"))
        {
            String value = checkBoolean(v);
            if (!value.equals(""))
            {
                setDisplayTurnsPublicly(Boolean.parseBoolean(value));
            }
        } else if (setting.equalsIgnoreCase("minBuy"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setMinBuy(value);
            }
        } else if (setting.equalsIgnoreCase("maxBuy"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setMaxBuy(value);
            }
        } else if (setting.equalsIgnoreCase("allowRebuys"))
        {
            String value = checkBoolean(v);
            if (!value.equals(""))
            {
                setAllowRebuys(Boolean.parseBoolean(value));
            }
        } else
        {
            setTableSpecificSetting(setting, v);
        }
    }

    public abstract void setTableSpecificSetting(String setting, String v);
}
