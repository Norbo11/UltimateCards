package com.github.norbo11.util.config;

import org.bukkit.configuration.file.FileConfiguration;

import com.github.norbo11.UltimateCards;

public class PluginConfig {
    // Options --------------------------------------------------
    private static FileConfiguration config;

    // General
    private static boolean autoUpdate, cleanupOnDisable, disableCommandsWhilePlaying, preventMovementOutsideChatRange;

    // Log
    private static boolean enableLog;
    private static String dateFormat;

    // Chat
    private static boolean displayTag;
    private static String colorTag, colorNormalMessage, colorErrorMessage, colorHighlight;

    // General
    private static boolean displayTurnsPublicly, allowRebuys, autoKickOnLeave;
    private static int autoStart, turnSeconds, publicChatRange;

    // Poker
    private static int dynamicFrequency;
    private static double maxBuy, sb, bb, ante, minRaise, fixRake, minBuy, rake;
    private static boolean minRaiseAlwaysBB;
    
    // Blackjack
    private static boolean allowDoubleDown;
    private static double minBet;

    private static int amountOfDecks;
    
    public static boolean isAutoKickOnLeave() {
        return autoKickOnLeave;
    }

    public static boolean isPreventMovementOutsideChatRange() {
        return preventMovementOutsideChatRange;
    }

    public static boolean isDisableCommandsWhilePlaying() {
        return disableCommandsWhilePlaying;
    }

    public static int getAmountOfDecks() {
        return amountOfDecks;
    }

    public static double getAnte() {
        return ante;
    }

    public static int getAutoStart() {
        return autoStart;
    }

    public static double getBb() {
        return bb;
    }

    public static String getColorErrorMessage() {
        return colorErrorMessage;
    }

    public static String getColorHighlight() {
        return colorHighlight;
    }

    public static String getColorNormalMessage() {
        return colorNormalMessage;
    }

    public static String getColorTag() {
        return colorTag;
    }

    public static FileConfiguration getConfig() {
        return config;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static int getDynamicFrequency() {
        return dynamicFrequency;
    }

    public static double getFixRake() {
        return fixRake;
    }

    public static double getMaxBuy() {
        return maxBuy;
    }

    public static double getMinBet() {
        return minBet;
    }

    public static double getMinBuy() {
        return minBuy;
    }

    public static double getMinRaise() {
        return minRaise;
    }

    public static int getPublicChatRange() {
        return publicChatRange;
    }

    public static double getRake() {
        return rake;
    }

    public static double getSb() {
        return sb;
    }

    public static int getTurnSeconds() {
        return turnSeconds;
    }

    public static boolean isAllowDoubleDown() {
        return allowDoubleDown;
    }

    public static boolean isAllowRebuys() {
        return allowRebuys;
    }

    public static boolean isAutoUpdate() {
        return autoUpdate;
    }

    public static boolean isCleanupOnDisable() {
        return cleanupOnDisable;
    }

    public static boolean isDisplayTag() {
        return displayTag;
    }

    public static boolean isDisplayTurnsPublicly() {
        return displayTurnsPublicly;
    }

    public static boolean isEnableLog() {
        return enableLog;
    }

    public static boolean isMinRaiseAlwaysBB() {
        return minRaiseAlwaysBB;
    }

    public static void load() {
        config = UltimateCards.getPluginInstance().getConfig();
        try {
            config.load(UltimateCards.getFilePluginConfig());
        } catch (Exception e) {
            e.printStackTrace();
        }
        setValues();
    }

    private static void setValues() {
        // General
        cleanupOnDisable = config.getBoolean("options.general.cleanupOnDisable", config.getDefaults().getBoolean("options.general.cleanupOnDisable"));
        autoUpdate = config.getBoolean("options.general.autoUpdate", config.getDefaults().getBoolean("options.general.autoUpdate"));
        disableCommandsWhilePlaying = config.getBoolean("options.general.disableCommandsWhilePlaying", config.getDefaults().getBoolean("options.general.disableCommandsWhilePlaying"));
        preventMovementOutsideChatRange = config.getBoolean("options.general.preventMovementOutsideChatRange", config.getDefaults().getBoolean("options.general.preventMovementOutsideChatRange"));
        autoKickOnLeave = config.getBoolean("options.general.autoKickOnLeave", config.getDefaults().getBoolean("options.general.autoKickOnLeave"));
        
        // Log
        enableLog = config.getBoolean("options.log.enableLog", config.getDefaults().getBoolean("options.log.enableLog"));
        dateFormat = config.getString("options.log.dateFormat", config.getDefaults().getString("options.log.dataFormat"));

        // Chat
        displayTag = config.getBoolean("options.chat.displayTag", config.getDefaults().getBoolean("options.chat.displayTag"));
        colorTag = config.getString("options.chat.colorTag", config.getDefaults().getString("options.chat.colorTag"));
        colorNormalMessage = config.getString("options.chat.colorNormalMessage", config.getDefaults().getString("options.chat.colorNormalMessage"));
        colorErrorMessage = config.getString("options.chat.colorErrorMessage", config.getDefaults().getString("options.chat.colorErrorMessage"));
        colorHighlight = config.getString("options.chat.colorHighlight", config.getDefaults().getString("options.chat.colorHighlight"));

        // General
        allowRebuys = config.getBoolean("options.generalDefaults.allowRebuys", config.getDefaults().getBoolean("options.generalDefaults.allowRebuys"));
        displayTurnsPublicly = config.getBoolean("options.generalDefaults.displayTurnsPublicly", config.getDefaults().getBoolean("options.generalDefaults.displayTurnsPublicly"));
        autoStart = config.getInt("options.generalDefaults.autoStart", config.getDefaults().getInt("options.generalDefaults.autoStart"));
        turnSeconds = config.getInt("options.generalDefaults.turnSeconds", config.getDefaults().getInt("options.generalDefaults.turnSeconds"));
        minBuy = config.getDouble("options.generalDefaults.minBuy", config.getDefaults().getDouble("options.generalDefaults.minBuy"));
        maxBuy = config.getDouble("options.generalDefaults.maxBuy", config.getDefaults().getDouble("options.generalDefaults.maxBuy"));
        publicChatRange = config.getInt("options.generalDefaults.publicChatRange", config.getDefaults().getInt("options.generalDefaults.publicChatRange"));

        // Poker
        fixRake = config.getDouble("options.poker.fixRake", config.getDefaults().getDouble("options.poker.fixRake"));
        minRaiseAlwaysBB = config.getBoolean("options.poker.defaults.minRaiseIsAlwaysbb", config.getDefaults().getBoolean("options.poker.defaults.minRaiseIsAlwaysbb"));
        sb = config.getDouble("options.poker.defaults.sb", config.getDefaults().getDouble("options.poker.defaults.sb"));
        bb = config.getDouble("options.poker.defaults.bb", config.getDefaults().getDouble("options.poker.defaults.bb"));
        ante = config.getDouble("options.poker.defaults.ante", config.getDefaults().getDouble("options.poker.defaults.ante"));
        dynamicFrequency = config.getInt("options.poker.defaults.dynamicFrequency", config.getDefaults().getInt("options.poker.defaults.dynamicFrequency"));
        rake = config.getDouble("options.poker.defaults.rake", config.getDefaults().getDouble("options.poker.defaults.rake"));
        minRaise = config.getDouble("options.poker.defaults.minRaise", config.getDefaults().getDouble("options.poker.defaults.minRaise"));

        // Blackjack
        allowDoubleDown = config.getBoolean("options.blackjack.defaults.allowDoubleDown", config.getDefaults().getBoolean("options.blackjack.defaults.allowDoubleDown"));
        minBet = config.getDouble("options.blackjack.defaults.minBet", config.getDefaults().getDouble("options.blackjack.defaults.minBet"));
        amountOfDecks = config.getInt("options.blackjack.defaults.amountOfDecks", config.getDefaults().getInt("options.blackjack.defaults.amountOfDecks"));
    }
}
