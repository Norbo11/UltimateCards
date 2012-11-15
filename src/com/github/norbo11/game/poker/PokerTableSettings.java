package com.github.norbo11.game.poker;

import java.util.ArrayList;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsTableSettings;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;

public class PokerTableSettings extends CardsTableSettings
{
    public PokerTableSettings(PokerTable table)
    {
        super(table);

        // If the min raise is always BB, set it to the BB. If not, make it
        // whatever is in the settings
        if (minRaiseAlwaysBB)
        {
            minRaise = bb;
        }

        // Set originals, required for dynamic antes/blinds
        originalSB = sb;
        originalBB = bb;
        originalAnte = ante;

        // Negative number allows players to set their rake. 0 or positive
        // number fixes the rake to that amount.
        if (UltimateCards.getPluginConfig().getFixRake() > -1)
        {
            rake = UltimateCards.getPluginConfig().getFixRake();
            rakeFixed = true;
        } else
        {
            rake = UltimateCards.getPluginConfig().getRake();
            rakeFixed = false;
        }
    }

    private double originalSB;
    private double originalBB;
    private double originalAnte;
    private double sb = UltimateCards.getPluginConfig().getSb();
    private double bb = UltimateCards.getPluginConfig().getBb();
    private double ante = UltimateCards.getPluginConfig().getAnte();
    private double rake = UltimateCards.getPluginConfig().getRake(); // A number from 0-1 which represents the rake that the owner of the table gets after paying a pot.

    private double minRaise = UltimateCards.getPluginConfig().getMinRaise();
    private boolean rakeFixed = false;
    private int dynamicFrequency = UltimateCards.getPluginConfig().getDynamicFrequency();

    private boolean minRaiseAlwaysBB = UltimateCards.getPluginConfig().isMinRaiseAlwaysBB();

    public double getAnte()
    {
        return ante;
    }

    public double getBb()
    {
        return bb;
    }

    public int getDynamicFrequency()
    {
        return dynamicFrequency;
    }

    public double getMinRaise()
    {
        return minRaise;
    }

    public double getOriginalAnte()
    {
        return originalAnte;
    }

    public double getOriginalBB()
    {
        return originalBB;
    }

    public double getOriginalSB()
    {
        return originalSB;
    }

    public double getRake()
    {
        return rake;
    }

    public double getSb()
    {
        return sb;
    }

    public boolean isMinRaiseAlwaysBB()
    {
        return minRaiseAlwaysBB;
    }

    public boolean isRakeFixed()
    {
        return rakeFixed;
    }

    // Lists the settings of the table, returning a string array
    @Override
    public ArrayList<String> listTableSpecificSettings()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Ante: &6" + Formatter.formatMoney(ante));
        list.add("Small Blind: &6" + Formatter.formatMoney(sb));
        list.add("Big Blind: &6" + Formatter.formatMoney(bb));
        list.add("Rake: &6" + Formatter.convertToPercentage(rake));

        if (minRaiseAlwaysBB)
        {
            list.add("Minimum Raise: &6equal to the Big Blind");
        } else
        {
            list.add("Minimum Raise: &6" + Formatter.formatMoney(minRaise));
        }

        if (dynamicFrequency > 0)
        {
            list.add("Dynamic Frequency: &6" + "Every " + dynamicFrequency + " hands");
        } else
        {
            list.add("Dynamic Frequency: &6" + "OFF");
        }

        return list;
    }

    public void raiseBlinds()
    {
        ante += getOriginalAnte();
        bb += getOriginalBB();
        sb += getOriginalSB();
    }

    public void setAnte(double value)
    {
        ante = value;
        originalAnte = ante;
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Ante" + "&f to &6" + Formatter.formatMoney(value));
    }

    public void setBB(double value)
    {
        bb = value;
        originalSB = bb;
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Big Blind" + "&f to &6" + Formatter.formatMoney(value));
    }

    public void setDynamicFrequency(int value)
    {
        // Only allow the player to set the dynamic frequency if the blinds
        // increased on the current hand, or the table is not currently in
        // progress
        if (!getTable().isInProgress() || getTable().getHandNumber() % dynamicFrequency == 0)
        {
            dynamicFrequency = value;
            if (dynamicFrequency > 0)
            {
                Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Dynamic Frequency " + "&fto " + "&6'Every " + value + " hands'");
            } else
            {
                Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has truned the " + "&6Dynamic Frequency " + "&f&6off.");
            }
        } else
        {
            Messages.sendMessage(getTable().getOwner().getPlayer(), "&cYou may only set the dynamic frequency during a hand where the blinds increased, or if the table is not in progress.");
        }
    }

    public void setMinRaise(double value)
    {
        if (!minRaiseAlwaysBB)
        {
            minRaise = value;
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Minimum Raise" + "&f to &6" + Formatter.formatMoney(value));
        } else
        {
            Messages.sendMessage(getTable().getOwner().getPlayer(), "&cThis table's minimum raise is currently set to always be equal to the big blind! Change this with " + PluginExecutor.tableSet.getCommandString() + " minRaiseAlwaySBB false.");
        }
    }

    public void setMinRaiseAlwaysBB(boolean value)
    {
        minRaiseAlwaysBB = value;
        if (value == true)
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has made the " + "&6Minimum Raise" + "&f be always equal to the Big Blind!");
        } else
        {
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has made the " + "&6Minimum Raise" + "&f no longer be equal to the Big Blind!");
        }
        return;
    }

    public void setRake(double value)
    {
        if (!rakeFixed)
        {
            rake = value;
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Rake" + "&f to &6" + Formatter.convertToPercentage(value));
            Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f will now receive &6" + Formatter.convertToPercentage(value) + "&f of each pot to their own pocket!");
        } else
        {
            Messages.sendMessage(getTable().getOwner().getPlayer(), "&cThe configuration of the plugin has fixed the rake to &6" + Formatter.convertToPercentage(rake) + "&c. Sorry!");
        }
    }

    public void setSB(double value)
    {
        sb = value;
        originalSB = sb;
        Messages.sendToAllWithinRange(getTable().getLocation(), "&6" + getTable().getOwner().getPlayerName() + "&f has set the " + "&6Small Blind" + "&f to &6" + Formatter.formatMoney(value));
    }

    @Override
    public void setTableSpecificSetting(String setting, String v)
    {
        if (setting.equalsIgnoreCase("sb"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setSB(value);
            }
        } else if (setting.equalsIgnoreCase("bb"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setBB(value);
            }
        } else if (setting.equalsIgnoreCase("ante"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setAnte(value);
            }
        } else if (setting.equalsIgnoreCase("minRaise"))
        {
            double value = checkDouble(v);
            if (value != -99999)
            {
                setMinRaise(value);
            }
        } else if (setting.equalsIgnoreCase("minRaiseAlwaysBB"))
        {
            String value = checkBoolean(v);
            if (!value.equals(""))
            {
                setMinRaiseAlwaysBB(Boolean.parseBoolean(value));
            }
        } else if (setting.equalsIgnoreCase("dynamicFrequency"))
        {
            int value = checkInteger(v);
            if (value != -99999)
            {
                setDynamicFrequency(value);
            }
        } else if (setting.equalsIgnoreCase("rake"))
        {
            double value = checkPercentage(v);
            if (value != -99999)
            {
                setRake(value);
            }
        } else
        {
            Messages.sendMessage(getTable().getOwner().getPlayer(), "&cInvalid setting. Check available settings with " + PluginExecutor.tableListSettings.getCommandString() + ".");
        }
    }

    public void updateMinRaise()
    {
        minRaise = getBb();
    }
}
