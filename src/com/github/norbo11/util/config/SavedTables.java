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
                    pokerSettings.setMinRaiseAlwaysBBNoMsg(settings.getBoolean("minRaiseIsAlwaysBB", UltimateCards.getPluginConfig().isMinRaiseAlwaysBB()));
                    pokerSettings.setSBNoMsg(settings.getDouble("sb", UltimateCards.getPluginConfig().getSb()));
                    pokerSettings.setBBNoMsg(settings.getDouble("bb", UltimateCards.getPluginConfig().getBb()));
                    pokerSettings.setAnteNoMsg(settings.getDouble("ante", UltimateCards.getPluginConfig().getAnte()));
                    pokerSettings.setDynamicFrequencyNoMsg(settings.getInt("dynamicFrequency", UltimateCards.getPluginConfig().getDynamicFrequency()));
                    pokerSettings.setRakeNoMsg(settings.getDouble("rake", UltimateCards.getPluginConfig().getRake()));
                    pokerSettings.setMinRaiseNoMsg(settings.getDouble("minRaise", UltimateCards.getPluginConfig().getMinRaise()));
                    tableSettings = pokerSettings;
                }

                if (type.equalsIgnoreCase("blackjack") || type.equalsIgnoreCase("bj")) {
                    cardsTable = new BlackjackTable(owner, name, CardsTable.getFreeTableID(), location);
                    BlackjackTableSettings blackjackSettings = new BlackjackTableSettings((BlackjackTable) cardsTable);
                    blackjackSettings.setAllowDoubleDownNoMsg(settings.getBoolean("allowDoubleDown", UltimateCards.getPluginConfig().isAllowDoubleDown()));
                    blackjackSettings.setMinBetNoMsg(settings.getDouble("minBet", UltimateCards.getPluginConfig().getMinBet()));
                    blackjackSettings.setAmountOfDecksNoMsg(settings.getInt("amountOfDecks", UltimateCards.getPluginConfig().getAmountOfDecks()));
                    tableSettings = blackjackSettings;
                }
            } else {
                System.out.println("Error while loading tables: Invalid gameType for table '" + table + "'. Use 'poker' or 'blackjack'");
                continue;
            }

            tableSettings.setAllowRebuysNoMsg(settings.getBoolean("allowRebuys", UltimateCards.getPluginConfig().isAllowRebuys()));
            tableSettings.setDisplayTurnsPubliclyNoMsg(settings.getBoolean("displayTurnsPublicly", UltimateCards.getPluginConfig().isDisplayTurnsPublicly()));
            tableSettings.setAutoStartNoMsg(settings.getInt("autoStart", UltimateCards.getPluginConfig().getAutoStart()));
            tableSettings.setTurnSecondsNoMsg(settings.getInt("turnSeconds", UltimateCards.getPluginConfig().getTurnSeconds()));
            tableSettings.setMinBuyNoMsg(settings.getDouble("minBuy", UltimateCards.getPluginConfig().getMinBuy()));
            tableSettings.setMaxBuyNoMsg(settings.getDouble("maxBuy", UltimateCards.getPluginConfig().getMaxBuy()));
            tableSettings.setPublicChatRangeNoMsg(settings.getInt("publicChatRange", UltimateCards.getPluginConfig().getPublicChatRange()));

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
        location.add(table.getLocation().getWorld().getName());
        location.add(Math.round(table.getLocation().getX()) + "");
        location.add(Math.round(table.getLocation().getY()) + "");
        location.add(Math.round(table.getLocation().getZ()) + "");
        section.set("location", location);

        ConfigurationSection settings = section.createSection("settings");

        if (table instanceof PokerTable) {
            section.set("gameType", "poker");
            PokerTableSettings pokerTableSettings = (PokerTableSettings) tableSettings;
            settings.set("minRaiseIsAlwaysBB", pokerTableSettings.isMinRaiseAlwaysBB());
            settings.set("minBuy", pokerTableSettings.getMinBuy());
            settings.set("maxBuy", pokerTableSettings.getMaxBuy());
            settings.set("sb", pokerTableSettings.getSb());
            settings.set("bb", pokerTableSettings.getBb());
            settings.set("ante", pokerTableSettings.getAnte());
            settings.set("dynamicFrequency", pokerTableSettings.getDynamicFrequency());
            settings.set("rake", pokerTableSettings.getRake());
            settings.set("minRaise", pokerTableSettings.getMinRaise());
        }

        if (table instanceof BlackjackTable) {
            section.set("gameType", "blackjack");
            BlackjackTableSettings blackjackTableSettings = (BlackjackTableSettings) tableSettings;
            settings.set("allowDoubleDown", blackjackTableSettings.isAllowDoubleDown());
            settings.set("minBet", blackjackTableSettings.getMinBet());
            settings.set("amountOfDecks", blackjackTableSettings.getAmountOfDecks());
        }

        settings.set("allowRebuys", tableSettings.isAllowRebuys());
        settings.set("displayTurnsPublicly", tableSettings.isDisplayTurnsPublicly());
        settings.set("autoStart", tableSettings.getAutoStart());
        settings.set("turnSeconds", tableSettings.getTurnSeconds());
        settings.set("minBuy", tableSettings.getMinBuy());
        settings.set("maxBuy", tableSettings.getMaxBuy());
        settings.set("publicChatRange", tableSettings.getPublicChatRange());

        save();
    }

    public static void unsaveTable(CardsTable table) throws IOException {
        config.set(table.getName(), null);
        savedTables.remove(table);
        save();
    }
}
