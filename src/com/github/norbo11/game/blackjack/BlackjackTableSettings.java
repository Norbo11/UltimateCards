package com.github.norbo11.game.blackjack;

import java.util.ArrayList;

import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsTableSettings;
import com.github.norbo11.game.cards.TableSetting;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.config.PluginConfig;

public class BlackjackTableSettings extends CardsTableSettings {
    public BlackjackTableSettings(BlackjackTable table) {
        super(table);
    }

    public AllowDoubleDown allowDoubleDown = new AllowDoubleDown(PluginConfig.isAllowDoubleDown());
    public MinBet minBet = new MinBet(PluginConfig.getMinBet());
    public AmountOfDecks amountOfDecks = new AmountOfDecks(PluginConfig.getAmountOfDecks());

    public TableSetting<?>[] allSettings = {
        allowDoubleDown, minBet, amountOfDecks
    };
    
    // Lists the settings of the table, returning a string array
    @Override
    public ArrayList<String> listTableSpecificSettings() {
        return listSettings(allSettings);
    }
    
    public class AllowDoubleDown extends TableSetting<Boolean> {
        public AllowDoubleDown(Boolean value) {
            super(value, "allowDoubleDown");
        }

        @Override
        public void setValueUsingInput(String value) {            
            try { setValue(checkBoolean(value)); } catch (NumberFormatException e) { return; }
            if (getValue()) {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has allowed " + "&6Double Down&f!");
            } else {
                getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has disallowed " + "&6Double Down&f!");
            }
        }

        @Override
        public String toString() {
            return "Allow double down: &6" + getValue();
        }

        @Override
        public String getHelpString() {
            return "&6allowDoubleDown [true|false] - &fIf true, players can double down.";
        }
    }

    public class AmountOfDecks extends TableSetting<Integer> {
        public AmountOfDecks(Integer value) {
            super(value, "amountOfDecks");
        }

        @Override
        public void setValueUsingInput(String value) {        
            if (checkInteger(value) == -99999) return;
            
            setValue(checkInteger(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&Amount of Decks" + "&f to &6" + amountOfDecks);
        }

        @Override
        public void setValue(Integer value) {
            super.setValue(value);
            if (amountOfDecks.getValue() > 15) {
                amountOfDecks.setValue(15);
            }
            getTable().getDeck().setAmountOfDecks(value);
        }
        
        @Override
        public String toString() {
            return "Amount of decks: &6" + getValue();
        }

        @Override
        public String getHelpString() {
            return "&6amountOfDecks [number] - &fThe amount of decks used for the game.";
        }
    }
    
    public class MinBet extends TableSetting<Double> {
        public MinBet(Double value) {
            super(value, "minBet");
        }

        @Override
        public void setValueUsingInput(String value) {  
            if (checkDouble(value) == -99999) return;
            
            setValue(checkDouble(value));
            getTable().sendTableMessage("&6" + getTable().getOwner() + "&f has set the " + "&6Minimum Bet" + "&f to &6" + Formatter.formatMoney(getValue()));
        }
        
        @Override
        public String toString() {
            return "Minimum Bet: &6" + Formatter.formatMoney(getValue());
        }

        @Override
        public String getHelpString() {
            return "&6minBet [number] - &fThe minimum bet allowed at the table.";
        }
    }


    @Override
    public void setTableSpecificSetting(String inputSetting, String inputValue) {
        if (!setSetting(inputSetting, inputValue, allSettings))
            Messages.sendMessage(getTable().getOwnerPlayer().getPlayer(), "&cInvalid setting. Check available settings with " + PluginExecutor.tableListSettings.getCommandString() + ".");
    }

}
