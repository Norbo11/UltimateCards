package com.github.norbo11.game.cards;

import java.util.ArrayList;

import org.bukkit.Location;

import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;
import com.github.norbo11.util.config.PluginConfig;


public abstract class CardsTableSettings {
    public class AllowRebuys extends TableSetting<Boolean> {
        public AllowRebuys(Boolean value) {
            super(value, "allowRebuys");
        }

        @Override
        public void setValueUsingInput(String value) {
            try { setValue(checkBoolean(value)); } catch (NumberFormatException e) { return; }
            if (getValue()) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has allowed rebuys!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has disallowed rebuys!");
            }
        }
        
        @Override
        public String toString() {
            return "Allow rebuys: &6" + getValue();
        }

        @Override
        public String getHelpString() {
            return "&6allowRebuys [true|false] - &fIf false, players can't re-buy.";
        }
    }
    
    public class DisplayTurnsPublicly extends TableSetting<Boolean> {
        public DisplayTurnsPublicly(Boolean value) {
            super(value, "displayTurnsPublicly");
        }

        @Override
        public void setValueUsingInput(String value) {
            try { setValue(checkBoolean(value)); } catch (NumberFormatException e) { return; }
            if (getValue()) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has made turn messages display publicly!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has made turn messages display privately!");
            }
        }
        
        @Override
        public String toString() {
            return "Display turns publicly: &6" + getValue();
        }

        @Override
        public String getHelpString() {
            return "&6displayTurnsPublicly [true|false] - &fDisplays player turns publicly.";
        }
    }
    
    public class AutoStart extends TableSetting<Integer> {
        public AutoStart(Integer value) {
            super(value, "autoStart");
        }

        @Override
        public void setValueUsingInput(String value) {
            if (checkInteger(value) == -99999) return;
            
            setValue(checkInteger(value));
            if (getValue() > 0) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set round auto-start to &6" + getValue()  + "&f seconds!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has turned off round auto-start.");
                getTable().cancelTimerTask();
            }
        }
        
        @Override
        public String toString() {
            return "Display turns publicly: &6" + getValue();
        }

        @Override
        public String getHelpString() {
            return "&6autoStart [number] - &fAutomatically start new rounds after [number] seconds. 0 = OFF";
        }
    }

    public class MaxBuy extends TableSetting<Double> {
        public MaxBuy(Double value) {
            super(value, "maxBuy");
        }

        @Override
        public void setValueUsingInput(String value) {
            if (checkDouble(value) == -99999) return;
            
            setValue(checkDouble(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Maximum Buy-In" + "&f to &6" + Formatter.formatMoney(getValue()));
        }
        
        @Override
        public String toString() {
            return "Maximum Buy-in: &6" + Formatter.formatMoney(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6maxBuy [number] - &fThe maximum (re)buy-in amount.";
        }
    }
    
    public class MinBuy extends TableSetting<Double> {
        public MinBuy(Double value) {
            super(value, "minBuy");
        }

        @Override
        public void setValueUsingInput(String value) {
            if (checkDouble(value) == -99999) return;
            
            setValue(checkDouble(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Minimum Buy-In" + "&f to &6" + Formatter.formatMoney(getValue()));
        }
        
        @Override
        public String toString() {
            return "Minimum Buy-in: &6" + Formatter.formatMoney(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6minBuy [number] - &fThe minimum (re)buy-in amount.";
        }
    }
    
    public class PublicChatRange extends TableSetting<Integer> {
        public PublicChatRange(Integer value) {
            super(value, "publicChatRange");
        }

        @Override
        public void setValueUsingInput(String value) {
            if (checkInteger(value) == -99999) return;
            
            setValue(checkInteger(value));
            if (getValue() > 0) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has enabled public display of &6" + value + "&f blocks.");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has turned off the public display.");
            }        
        }
        
        @Override
        public String toString() {
            return "Public chat range: &6" + getValue() + " blocks";
        }

        @Override
        public String getHelpString() {
            return "&6publicChatRange [number] - &fSpectator message display range. 0 = OFF";
        }
    }
    
    public class TurnSeconds extends TableSetting<Integer> {
        public TurnSeconds(Integer value) {
            super(value, "turnSeconds");
        }

        @Override
        public void setValueUsingInput(String value) {
            if (checkInteger(value) == -99999) return;
            
            setValue(checkInteger(value));
            if (getValue() > 0) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the turn time limit to &6" + getValue() + "&f seconds!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has allowed unlimited turn time.");
            }        
        }
        
        @Override
        public String toString() {
            return "Turn timer: &6" + getValue() + " seconds";
        }

        @Override
        public String getHelpString() {
            return "&6turnSeconds [number] - &fAllowed player action time. 0 = OFF";
        }
    }
    
    public class LeaveLocation extends TableSetting<Location> {
        public LeaveLocation() {
            super("leaveLocation");
        }
        
        @Override
        public String toString() {
            return "Leave location: " + Formatter.formatLocation(getValue());
        }

        @Override
        public void setValueUsingInput(String value) {
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the leave location to " + Formatter.formatLocation(getValue()));
        }

        @Override
        public String getHelpString() {
            return "&6leaveLocation - &fThe location players will be teleported to after leaving (do not specify a value).";
        }
    }
    
    public class StartLocation extends TableSetting<Location> {
        public StartLocation() {
            super("startLocation");
        }
        
        @Override
        public String toString() {
            return "Start location: " + Formatter.formatLocation(getValue());
        }

        @Override
        public void setValueUsingInput(String value) {
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the start location to " + Formatter.formatLocation(getValue()));
        }

        @Override
        public String getHelpString() {
            return "&6startLocation - &fThe location players will be teleported to after joining (do not specify a value).";
        }
    }
    
    public class AutoKickOnLeave extends TableSetting<Boolean> {
        public AutoKickOnLeave(boolean value) {
            super(value, "autoKickOnLeave");
        }
        
        @Override
        public void setValueUsingInput(String value) {
            try { setValue(checkBoolean(value)); } catch (NumberFormatException e) { return; }
            if (getValue()) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has turned on auto-kick upon leaving the table!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has turned off auto-kick upon leaving the table!");
            }
        }
        
        @Override
        public String toString() {
            return "Auto-kick on leave: &6" + getValue();
        }

        @Override
        public String getHelpString() {
            return "&6autoKickOnLeave [true|false] - &fAutomatically kicks players who leave the table.";
        }
    }
    
    //--------------------------------------------------------------------------------------------------
    
    public CardsTableSettings(CardsTable table) {
        this.parentTable = table;
    }

    public AllowRebuys allowRebuys = new AllowRebuys(PluginConfig.isAllowRebuys());
    public DisplayTurnsPublicly displayTurnsPublicly = new DisplayTurnsPublicly(PluginConfig.isDisplayTurnsPublicly());
    public MinBuy minBuy = new MinBuy(PluginConfig.getMinBuy());
    public MaxBuy maxBuy = new MaxBuy(PluginConfig.getMaxBuy());
    public AutoStart autoStart = new AutoStart(PluginConfig.getAutoStart());
    public PublicChatRange publicChatRange = new PublicChatRange(PluginConfig.getPublicChatRange());
    public TurnSeconds turnSeconds = new TurnSeconds(PluginConfig.getTurnSeconds());
    public StartLocation startLocation = new StartLocation();
    public LeaveLocation leaveLocation = new LeaveLocation();
    public AutoKickOnLeave autoKickOnLeave = new AutoKickOnLeave(PluginConfig.isAutoKickOnLeave());
    
    public TableSetting<?>[] allSettings = {
        allowRebuys, displayTurnsPublicly, minBuy, maxBuy, autoStart, publicChatRange, turnSeconds, startLocation, leaveLocation, autoKickOnLeave
    };
    
    private CardsTable parentTable;


    public CardsTable getTable() {
        return parentTable;
    }


    public static ArrayList<String> listSettings(TableSetting<?>[] settings) {
        ArrayList<String> list = new ArrayList<String>();

        for (TableSetting<?> setting : settings) {
            list.add(setting.toString());
        }

        return list;
    }
    
    public static boolean setSetting(String inputSetting, String inputValue, TableSetting<?>[] settings) {
        for (TableSetting<?> setting : settings)
        {
            if (setting.getName().equalsIgnoreCase(inputSetting)) {
                setting.setValueUsingInput(inputValue);
                return true;
            }
        }
        return false;
    }
    
    
    public ArrayList<String> listSettings() {
        ArrayList<String> messages = listSettings(allSettings);
        messages.addAll(listTableSpecificSettings());
        return messages;
    }
    

    public abstract ArrayList<String> listTableSpecificSettings();

    public void setSetting(String inputSetting, String inputValue) {
        if (!setSetting(inputSetting, inputValue, allSettings)) {
            setTableSpecificSetting(inputSetting, inputValue);
        }
    }

    public abstract void setTableSpecificSetting(String setting, String v);
    
    public boolean checkBoolean(String v) {
        boolean value;

        if (v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes")) {
            value = true;
        } else if (v.equalsIgnoreCase("false") || v.equalsIgnoreCase("no")) {
            value = false;
        } else {
            Messages.sendMessage(parentTable.getOwnerPlayer().getPlayer(), "&6" + v + "&c is an invalid value! Please specify " + "&6true &cor" + "&6 false&c.");
            throw new NumberFormatException();
        }
        return value;
    }

    public double checkDouble(String v)  {
        try {
            return NumberMethods.getDouble(v);
        } catch (NumberFormatException e) {
            ErrorMessages.invalidNumber(parentTable.getOwnerPlayer().getPlayer(), v);
            return -99999;
        }
    }

    public int checkInteger(String v) {
        try {
            return NumberMethods.getPositiveInteger(v);
        } catch (NumberFormatException e) {
            ErrorMessages.invalidNumber(parentTable.getOwnerPlayer().getPlayer(), v);
            return -99999;
        }
    }

    public double checkPercentage(String v) {
        try {
            double value = NumberMethods.getDouble(v);
            if (value >= 0 && value <= 1) return value;
        } catch (NumberFormatException e) {
            ErrorMessages.invalidPercentage(parentTable.getOwnerPlayer().getPlayer());
        }
        return -99999;
    }


    public void setStartLocation(Location location) {
        startLocation.setValue(location);
        startLocation.setValueUsingInput(null);
    }
    
    public void setLeaveLocation(Location location) {
        leaveLocation.setValue(location);
        leaveLocation.setValueUsingInput(null);
    }
}
