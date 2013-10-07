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
            String owner = tableSection.getString("owner");
            String type = tableSection.getString("gameType");
            String name = table;

            List<String> coords = tableSection.getStringList("location");
            Location location = new Location(Bukkit.getWorld(coords.get(0)), NumberMethods.getDouble(coords.get(1)), NumberMethods.getDouble(coords.get(2)), NumberMethods.getDouble(coords.get(3)));

            CardsTable cardsTable = null;
            CardsTableSettings tableSettings = null;
            ConfigurationSection settings = tableSection.getConfigurationSection("settings");

            if (CardsTable.isGameType(type)) {
                if (type.equalsIgnoreCase("poker")) {
                    cardsTable = new PokerTable(owner, name, CardsTable.getFreeTableID(), location);
                    PokerTableSettings pokerSettings = new PokerTableSettings((PokerTable) cardsTable);
                    pokerSettings.setMinRaiseAlwaysBBNoMsg(settings.getBoolean("minRaiseIsAlwaysBB", UltimateCards.getPluginConfig().isMinRaiseAlwaysBB()));
                    pokerSettings.setMinBuyNoMsg(settings.getDouble("minBuy", UltimateCards.getPluginConfig().getMinBuy()));
                    pokerSettings.setMaxBuyNoMsg(settings.getDouble("maxBuy", UltimateCards.getPluginConfig().getMaxBuy()));
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
        location.add(table.getLocation().getWorld().getName());
        location.add(Math.round(table.getLocation().getX()) + "");
        location.add(Math.round(table.getLocation().getY()) + "");
        location.add(Math.round(table.getLocation().getZ()) + "");
        section.set("location", location);

        ConfigurationSection settings = section.createSection("settings");
        settings.set("allowRebuys", table.getSettings().isAllowRebuys());
        settings.set("displayTurnsPublicly", table.getSettings().isDisplayTurnsPublicly());
        settings.set("autoStart", table.getSettings().getAutoStart());

        if (table instanceof PokerTable) {
            section.set("gameType", "poker");
            PokerTable pokerTable = (PokerTable) table;
            settings.set("minRaiseIsAlwaysBB", pokerTable.getSettings().isMinRaiseAlwaysBB());
            settings.set("minBuy", pokerTable.getSettings().getMinBuy());
            settings.set("maxBuy", pokerTable.getSettings().getMaxBuy());
            settings.set("sb", pokerTable.getSettings().getSb());
            settings.set("bb", pokerTable.getSettings().getBb());
            settings.set("ante", pokerTable.getSettings().getAnte());
            settings.set("dynamicFrequency", pokerTable.getSettings().getDynamicFrequency());
            settings.set("rake", pokerTable.getSettings().getRake());
            settings.set("minRaise", pokerTable.getSettings().getMinRaise());
        }

        if (table instanceof BlackjackTable) {
            section.set("gameType", "blackjack");
            BlackjackTable blackjackTable = (BlackjackTable) table;
            settings.set("allowDoubleDown", blackjackTable.getSettings().isAllowDoubleDown());
            settings.set("minBet", blackjackTable.getSettings().getMinBet());
            settings.set("amountOfDecks", blackjackTable.getSettings().getAmountOfDecks());
        }

        save();
    }

    public static void unsaveTable(CardsTable table) throws IOException {
        config.set(table.getName(), null);
        savedTables.remove(table);
        save();
    }
}
