package com.github.norbo11.game.poker;

import java.util.ArrayList;

import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsTableSettings;
import com.github.norbo11.game.cards.TableSetting;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.config.PluginConfig;

public class PokerTableSettings extends CardsTableSettings {
    public PokerTableSettings(PokerTable table) {
        super(table);

        // If the min raise is always BB, set it to the BB. If not, make it whatever is in the settings
        if (minRaiseAlwaysBB.getValue()) {
            minRaise.setValue(bb.getValue());
        }

        // Set originals, required for dynamic antes/blinds
        originalSB = sb.getValue();
        originalBB = bb.getValue();
        originalAnte = ante.getValue();

        // Negative number allows players to set their rake. 0 or positive number fixes the rake to that amount.
        if (PluginConfig.getFixRake() > -1) {
            rake.setValue(PluginConfig.getFixRake());
            rakeFixed = true;
        } else {
            rake.setValue(PluginConfig.getRake());
            rakeFixed = false;
        }
    }

    private double originalSB;
    private double originalBB;
    private double originalAnte;
    
    public SB sb = new SB(PluginConfig.getSb());
    public BB bb = new BB(PluginConfig.getBb());
    public Ante ante = new Ante(PluginConfig.getAnte());
    public Rake rake = new Rake(PluginConfig.getRake()); // A number from 0-1 which represents the rake that the owner of the table gets after paying a pot.
    public MinRaise minRaise = new MinRaise(PluginConfig.getMinRaise());
    public DynamicFrequency dynamicFrequency = new DynamicFrequency(PluginConfig.getDynamicFrequency());
    public MinRaiseAlwaysBB minRaiseAlwaysBB = new MinRaiseAlwaysBB(PluginConfig.isMinRaiseAlwaysBB());
   
    private boolean rakeFixed = false;

    public TableSetting<?>[] allSettings = {
        sb, bb, ante, rake, minRaise, dynamicFrequency, minRaiseAlwaysBB
    };
    
    public double getOriginalAnte() {
        return originalAnte;
    }

    public double getOriginalBB() {
        return originalBB;
    }

    public double getOriginalSB() {
        return originalSB;
    }

    @Override
    public ArrayList<String> listTableSpecificSettings() {
        return listSettings(allSettings);
    }

    public void raiseBlinds() {
        ante.setValue(ante.getValue() + getOriginalAnte());
        bb.setValue(bb.getValue() + getOriginalBB());
        sb.setValue(sb.getValue() + getOriginalSB());
    }

    public class BB extends TableSetting<Double> {
        public BB(Double value) {
            super(value, "bb");
        }

        @Override
        public void setValueUsingInput(String value) {   
            if (checkDouble(value) == -99999) return;
            setValue(checkDouble(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Big Blind" + "&f to &6" + Formatter.formatMoney(getValue()));
        }
        
        @Override
        public void setValue(Double value) {
            super.setValue(value);
            originalBB = getValue();
        }
        
        @Override
        public String toString() {
            return "Big Blind: &6" + Formatter.formatMoney(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6bb [number] - &fThe big blind";
        }
    }
    
    public class Ante extends TableSetting<Double> {
        public Ante(Double value) {
            super(value, "ante");
        }

        @Override
        public void setValueUsingInput(String value) {    
            if (checkDouble(value) == -99999) return;
            setValue(checkDouble(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Ante" + "&f to &6" + Formatter.formatMoney(getValue()));
        }
        
        @Override
        public void setValue(Double value) {
            super.setValue(value);
            originalAnte = getValue();
        }
        
        @Override
        public String toString() {
            return "Ante: &6" + Formatter.formatMoney(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6ante [number] - &fThe ante.";
        }
    }
    
    public class DynamicFrequency extends TableSetting<Integer> {
        public DynamicFrequency(Integer value) {
            super(value, "dynamicFrequency");
        }

        @Override
        public void setValueUsingInput(String value) {
            if (checkInteger(value) == -99999) return;
            
            // Only allow the player to set the dynamic frequency if the blinds increased on the current hand, or the table is not currently in progress
            if (!getTable().isInProgress() || getTable().getHandNumber() % getValue() == 0) {
                setValue(checkInteger(value));
                if (getValue() > 0) {
                    getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Dynamic Frequency " + "&fto " + "&6'Every " + value + " hands'");
                } else {
                    getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has truned the " + "&6Dynamic Frequency " + "&f&6off.");
                }
            } else {
                Messages.sendMessage(getTable().getOwnerPlayer().getPlayer(), "&cYou may only set the dynamic frequency during a hand where the blinds increased, or if the table is not in progress.");
            }
        }
        
        @Override
        public void setValue(Integer value) {
            if (!getTable().isInProgress() || getTable().getHandNumber() % getValue() == 0) {
                super.setValue(value);
            }
        }
        
        @Override
        public String toString() {
            if (getValue() > 0) {
                return "Dynamic Frequency: &6" + "Every " + getValue() + " hands";
            } else {
                return "Dynamic Frequency: &6" + "OFF";
            }
        }

        @Override
        public String getHelpString() {
            return "&6dynamicFrequency [number] - &fEvery [number] hands, the ante + blinds will increase by their original setting. 0 = OFF.";
        }
    }

    public class MinRaise extends TableSetting<Double> {
        public MinRaise(Double value) {
            super(value, "minRaise");
        }

        @Override
        public void setValueUsingInput(String value) {  
            if (checkDouble(value) == -99999) return;
            
            if (!minRaiseAlwaysBB.getValue()) {
                setValue(checkDouble(value));
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Minimum Raise" + "&f to &6" + Formatter.formatMoney(getValue()));
            } else {
                Messages.sendMessage(getTable().getOwnerPlayer().getPlayer(), "&cThis table's minimum raise is currently set to always be equal to the big blind! Change this with " + PluginExecutor.tableSet.getCommandString() + " minRaiseAlwaySBB false.");
            }
        }
        
        @Override
        public void setValue(Double value) {
            if (!minRaiseAlwaysBB.getValue()) {
                super.setValue(value);
            }
        }
        
        @Override
        public String toString() {
            if (minRaiseAlwaysBB.getValue()) {
                return "Minimum Raise: &6equal to the Big Blind";
            } else {
                return "Minimum Raise: &6" + Formatter.formatMoney(getValue());
            }
        }

        @Override
        public String getHelpString() {
            return "&6minRaise [number] - &fThe minimum raise at the table.";
        }
    }
    
    public class MinRaiseAlwaysBB extends TableSetting<Boolean> {
        public MinRaiseAlwaysBB(Boolean value) {
            super(value, "minRaiseAlwaysBB");
        }

        @Override
        public void setValueUsingInput(String value) {  
            try { setValue(checkBoolean(value)); } catch (NumberFormatException e) { return; }
            if (getValue()) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has made the " + "&6Minimum Raise" + "&f be always equal to the Big Blind!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has made the " + "&6Minimum Raise" + "&f no longer be equal to the Big Blind!");
            }
        }
        
        @Override
        public String toString() {
            return ""; //Handled by MinRaise
        }

        @Override
        public String getHelpString() {
            return "&6minRaiseAlwaysBB [true|false] - &fIf true, the minimum raise will always be equal big blind.";
        }
    }
   
    public class Rake extends TableSetting<Double> {
        public Rake(Double value) {
            super(value, "rake");
        }

        @Override
        public void setValueUsingInput(String value) {    
            if (checkPercentage(value) == -99999) return;
            
            if (!rakeFixed) {
                setValue(checkPercentage(value));
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Rake" + "&f to &6" + Formatter.convertToPercentage(getValue()));
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f will now receive &6" + Formatter.convertToPercentage(getValue()) + "&f of each pot to their own pocket!");
            } else {
                Messages.sendMessage(getTable().getOwnerPlayer().getPlayer(), "&cThe configuration of the plugin has fixed the rake to &6" + Formatter.convertToPercentage(getValue()) + "&c. Sorry!");
            }
        }
        
        @Override
        public void setValue(Double value) {
            if (!rakeFixed) {
                super.setValue(value);
            }
        }
        
        @Override
        public String toString() {
            return "Rake: &6" + Formatter.convertToPercentage(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6rake [number] - &fPercentage of the pot earned every hand as rake. Example: 0.05 = 5% rake.";
        }
    }

    public class SB extends TableSetting<Double> {
        public SB(Double value) {
            super(value, "sb");
        }

        @Override
        public void setValueUsingInput(String value) { 
            if (checkDouble(value) == -99999) return;

            setValue(checkDouble(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Small Blind" + "&f to &6" + Formatter.formatMoney(getValue()));
        }
        
        @Override
        public void setValue(Double value) {
            super.setValue(value);
            originalSB = getValue();
        }
        
        @Override
        public String toString() {
            return "Small Blind: &6" + Formatter.formatMoney(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6sb [number] - &fThe small blind.";
        }
    }

    public void updateMinRaise() {
        minRaise.setValue(bb.getValue());
    }
    
    @Override
    public void setTableSpecificSetting(String inputSetting, String inputValue) {
        if (!setSetting(inputSetting, inputValue, allSettings))        
            Messages.sendMessage(getTable().getOwnerPlayer().getPlayer(), "&cInvalid setting. Check available settings with " + PluginExecutor.tableListSettings.getCommandString() + ".");
    }
}
