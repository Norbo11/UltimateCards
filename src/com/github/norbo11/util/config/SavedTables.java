package com.github.norbo11.util.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.blackjack.BlackjackTable;
import com.github.norbo11.game.blackjack.BlackjackTableSettings;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.cards.CardsTableSettings;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.game.poker.PokerTableSettings;
import com.github.norbo11.util.NumberMethods;

public class SavedTables {
    private static FileConfiguration config;
    private static ArrayList<CardsTable> savedTables = new ArrayList<CardsTable>();

    public static FileConfiguration getConfig() {
        return config;
    }

    public static ArrayList<CardsTable> getSavedTables() {
        return savedTables;
    }

    public static void load() throws Exception {
        config = YamlConfiguration.loadConfiguration(UltimateCards.getFileSavedTables());
        loadTables();
    }

    public static void loadTables() throws Exception {
        // Poker Tables
        for (String table : config.getKeys(false)) {
            ConfigurationSection tableSection = config.getConfigurationSection(table);
            if (tableSection.getString("owner") == null || tableSection.getString("gameType") == null || tableSection.getStringList("location") == null) {
                System.out.println("Error while loading tables: Invalid config for table '" + table + "'");
                continue;
            }
            String owner = "";
            if (tableSection.getString("owner") != null) owner = tableSection.getString("owner");
            String type = tableSection.getString("gameType");
            String name = table;

            List<String> coords = tableSection.getStringList("location");
            Location location = new Location(Bukkit.getWorld(coords.get(0)), NumberMethods.getInteger(coords.get(1)), NumberMethods.getInteger(coords.get(2)), NumberMethods.getInteger(coords.get(3)));

            CardsTable cardsTable = null;
            CardsTableSettings tableSettings = null;
            ConfigurationSection settings = tableSection.getConfigurationSection("settings");

            if (CardsTable.isGameType(type)) {
                if (type.equalsIgnoreCase("poker")) {
                    cardsTable = new PokerTable(owner, name, CardsTable.getFreeTableID(), location);
                    PokerTableSettings pokerSettings = new PokerTableSettings((PokerTable) cardsTable);
                    pokerSettings.minRaiseAlwaysBB.setValue(settings.getBoolean("minRaiseIsAlwaysBB", PluginConfig.isMinRaiseAlwaysBB()));
                    pokerSettings.sb.setValue(settings.getDouble("sb", PluginConfig.getSb()));
                    pokerSettings.bb.setValue(settings.getDouble("bb", PluginConfig.getBb()));
                    pokerSettings.ante.setValue(settings.getDouble("ante", PluginConfig.getAnte()));
                    pokerSettings.dynamicFrequency.setValue(settings.getInt("dynamicFrequency", PluginConfig.getDynamicFrequency()));
                    pokerSettings.rake.setValue(settings.getDouble("rake", PluginConfig.getRake()));
                    pokerSettings.minRaise.setValue(settings.getDouble("minRaise", PluginConfig.getMinRaise()));
                    tableSettings = pokerSettings;
                }

                if (type.equalsIgnoreCase("blackjack") || type.equalsIgnoreCase("bj")) {
                    cardsTable = new BlackjackTable(owner, name, CardsTable.getFreeTableID(), location);
                    BlackjackTableSettings blackjackSettings = new BlackjackTableSettings((BlackjackTable) cardsTable);
                    blackjackSettings.allowDoubleDown.setValue(settings.getBoolean("allowDoubleDown", PluginConfig.isAllowDoubleDown()));
                    blackjackSettings.minBet.setValue(settings.getDouble("minBet", PluginConfig.getMinBet()));
                    blackjackSettings.amountOfDecks.setValue(settings.getInt("amountOfDecks", PluginConfig.getAmountOfDecks()));
                    tableSettings = blackjackSettings;
                }
            } else {
                System.out.println("Error while loading tables: Invalid gameType for table '" + table + "'. Use 'poker' or 'blackjack'");
                continue;
            }

            tableSettings.allowRebuys.setValue(settings.getBoolean("allowRebuys", PluginConfig.isAllowRebuys()));
            tableSettings.displayTurnsPublicly.setValue(settings.getBoolean("displayTurnsPublicly", PluginConfig.isDisplayTurnsPublicly()));
            tableSettings.autoStart.setValue(settings.getInt("autoStart", PluginConfig.getAutoStart()));
            tableSettings.turnSeconds.setValue(settings.getInt("turnSeconds", PluginConfig.getTurnSeconds()));
            tableSettings.minBuy.setValue(settings.getDouble("minBuy", PluginConfig.getMinBuy()));
            tableSettings.maxBuy.setValue(settings.getDouble("maxBuy", PluginConfig.getMaxBuy()));
            tableSettings.publicChatRange.setValue(settings.getInt("publicChatRange", PluginConfig.getPublicChatRange()));
            tableSettings.autoKickOnLeave.setValue(settings.getBoolean("autoKickOnLeave", PluginConfig.isAutoKickOnLeave()));

            cardsTable.setCardsTableSettings(tableSettings);
            cardsTable.setOpen(true);
            CardsTable.getTables().add(cardsTable);
            savedTables.add(cardsTable);
        }
    }

    public static void save() throws IOException {
        config.save(UltimateCards.getFileSavedTables());
    }

    public static void saveTable(CardsTable table) throws IOException {
        ConfigurationSection section = config.createSection(table.getName());

        section.set("owner", table.getOwner());
        ArrayList<String> location = new ArrayList<String>();
        CardsTableSettings tableSettings = table.getSettings();
        Location tableLocation = table.getSettings().startLocation.getValue();
        location.add(tableLocation.getWorld().getName());
        location.add(Math.round(tableLocation.getX()) + "");
        location.add(Math.round(tableLocation.getY()) + "");
        location.add(Math.round(tableLocation.getZ()) + "");
        section.set("location", location);

        ConfigurationSection settings = section.createSection("settings");

        if (table instanceof PokerTable) {
            section.set("gameType", "poker");
            PokerTableSettings pokerTableSettings = (PokerTableSettings) tableSettings;
            settings.set("minRaiseIsAlwaysBB", pokerTableSettings.minRaiseAlwaysBB.getValue());
            settings.set("minBuy", pokerTableSettings.minBuy.getValue());
            settings.set("maxBuy", pokerTableSettings.maxBuy.getValue());
            settings.set("sb", pokerTableSettings.sb.getValue());
            settings.set("bb", pokerTableSettings.bb.getValue());
            settings.set("ante", pokerTableSettings.ante.getValue());
            settings.set("dynamicFrequency", pokerTableSettings.dynamicFrequency.getValue());
            settings.set("rake", pokerTableSettings.rake.getValue());
            settings.set("minRaise", pokerTableSettings.minRaise.getValue());

        }

        if (table instanceof BlackjackTable) {
            section.set("gameType", "blackjack");
            BlackjackTableSettings blackjackTableSettings = (BlackjackTableSettings) tableSettings;
            settings.set("allowDoubleDown", blackjackTableSettings.allowDoubleDown.getValue());
            settings.set("minBet", blackjackTableSettings.minBet.getValue());
            settings.set("amountOfDecks", blackjackTableSettings.amountOfDecks.getValue());
        }

        settings.set("allowRebuys", tableSettings.allowRebuys.getValue());
        settings.set("displayTurnsPublicly", tableSettings.displayTurnsPublicly.getValue());
        settings.set("autoStart", tableSettings.autoStart.getValue());
        settings.set("turnSeconds", tableSettings.turnSeconds.getValue());
        settings.set("minBuy", tableSettings.minBuy.getValue());
        settings.set("maxBuy", tableSettings.maxBuy.getValue());
        settings.set("publicChatRange", tableSettings.publicChatRange.getValue());
        settings.set("autoKickOnLeave", tableSettings.autoKickOnLeave.getValue());

        save();
    }

    public static void unsaveTable(CardsTable table) throws IOException {
        config.set(table.getName(), null);
        savedTables.remove(table);
        save();
    }
}
