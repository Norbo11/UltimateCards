package com.github.norbo11;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import net.gravitydevelopment.updater.Updater;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.listeners.MapListener;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.ResourceManager;
import com.github.norbo11.util.Sound;
import com.github.norbo11.util.Timers;
import com.github.norbo11.util.config.PluginConfig;
import com.github.norbo11.util.config.SavedTables;

public class UltimateCards extends JavaPlugin {

    // Listeners
    private static PluginExecutor pluginExecutor;

    // Classes
    private static PluginConfig pluginConfig;
    private static SavedTables savedTables;

    // Files
    private static File filePluginDir, filePluginConfig, fileLog, fileSavedTables;

    // Misc
    private static Logger log;
    private static String version;
    private static Economy economy;
    private static Vault vault;
    private static boolean tagApiEnabled;

    // Constants
    private static final String PLUGIN_TAG = "[UC]&f";
    private static final String LINE_STRING = "---------------------------------------";

    public static Economy getEconomy() {
        return economy;
    }

    public static File getFileLog() {
        return fileLog;
    }

    public static File getFilePluginConfig() {
        return filePluginConfig;
    }

    public static File getFilePluginDir() {
        return filePluginDir;
    }

    public static File getFileSavedTables() {
        return fileSavedTables;
    }

    public static String getLineString() {
        return LINE_STRING;
    }

    public static Logger getLog() {
        return log;
    }

    public static PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public static PluginExecutor getPluginExecutor() {
        return pluginExecutor;
    }

    public static String getPluginTag() {
        return PLUGIN_TAG;
    }

    public static SavedTables getSavedTables() {
        return savedTables;
    }

    public static Vault getVault() {
        return vault;
    }

    public static String getVersion() {
        return version;
    }

    public static boolean isTagApiEnabled() {
        return tagApiEnabled;
    }

    public void addPermissions() {
        for (ArrayList<PluginCommand> commandGroup : PluginExecutor.commands) {
            for (PluginCommand cmd : commandGroup) {
                getServer().getPluginManager().addPermission(new Permission(cmd.getPermissionNodes().get(1), PermissionDefault.OP));
            }
        }
    }

    public boolean createFiles() {
        try {
            // Attempt to create the create a config file if one doesn't exist
            if (filePluginConfig.exists() == false) {
                getConfig().options().copyDefaults(true); // Copies the config in the actual plugin
                saveDefaultConfig(); // Saves the new config
                log.info("Created config file");
            }
            if (fileSavedTables.exists() == false) {
                saveResource("tables.yml", false);
                log.info("Created tables file");
            }
        } catch (Exception e) {
            terminate("Something went wrong when trying to create the config file(s)!", e);
            return false;
        }

        // Attempt to create a log.txt file if it doesnt exist
        try {
            if (fileLog.exists() == false) {
                fileLog.createNewFile();
            }
        } catch (Exception e) {
            terminate("Something went wrong when trying to create the log file!", e);
            return false;
        }

        return true;
    }

    @Override
    public void onDisable() {
        if (pluginConfig.isCleanupOnDisable()) {
            MoneyMethods.returnMoney();
            MapMethods.restoreAllMaps();
        }
        log.info("UltimateCards v" + version + " plugin disabled!");
    }

    @Override
    public void onEnable() {
        log = getLogger();
        version = getDescription().getVersion();

        // Set file variables
        filePluginDir = getDataFolder();
        filePluginConfig = new File(filePluginDir, "config.yml");
        fileLog = new File(filePluginDir, "log.txt");
        fileSavedTables = new File(filePluginDir, "tables.yml");

        // Set all listeners and create classes
        pluginExecutor = new PluginExecutor();
        ResourceManager.p = this;
        MapMethods.p = this;
        Timers.p = this;
        Sound.p = this;
        getServer().getPluginManager().registerEvents(new MapListener(), this);
        addPermissions();

        // Creates all files
        if (!createFiles()) return;

        // Hook into vault, economy and database
        if (!setupVault()) return;
        if (!setupEconomy()) return;

        // Create/load configs
        try {
            pluginConfig = new PluginConfig(this);
            pluginConfig.load();
            SavedTables.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Metrics
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            System.out.println("Couldn't submit Metrics data!");
        }

        // Update
        Updater updater;
        if (pluginConfig.isAutoUpdate()) {
            updater = new Updater(this, 39468, getFile(), Updater.UpdateType.DEFAULT, true);
            if (updater.getResult() == Updater.UpdateResult.SUCCESS) {
                log.info("To apply the update, reload/restart your server.");
            }
        }

        // Set all commands to the command executor
        getCommand("cards").setExecutor(pluginExecutor);
        getCommand("table").setExecutor(pluginExecutor);
        getCommand("poker").setExecutor(pluginExecutor);
        getCommand("blackjack").setExecutor(pluginExecutor);
        getCommand("bj").setExecutor(pluginExecutor);

        if (!(getConfig().getDouble("table.fixRake") <= 1 && getConfig().getDouble("table.fixRake") >= -1)) {
            terminate("Check your config file! The field fixRake must be either -1 or 0-1!", null);
            return;
        }

        log.info("UltimateCards v" + version + " plugin enabled!");
    }

    public boolean setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            terminate("Economy plugin not detected! You need an ECONOMY plugin such as iConomy to run this plugin! iConomy DL at: http://dev.bukkit.org/server-mods/iconomy/", null);
            return false;
        }
        economy = rsp.getProvider();
        log.info("Hooked into " + economy.getName());
        return true;
    }

    public boolean setupVault() {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        vault = (Vault) plugin;
        if (vault == null) {
            terminate("Vault plugin not detected! You need Vault to run this plugin! DL at: http://dev.bukkit.org/server-mods/vault/", null);
            return false;
        }
        log.info("Hooked into Vault");
        return true;
    }

    // Stops the plugin with the specified message and prints a stack trace of the error
    public void terminate(String message, Exception e) {
        log.severe(message);
        if (e != null) {
            e.printStackTrace();
        }
        getServer().getPluginManager().disablePlugin(this);
    }
}
