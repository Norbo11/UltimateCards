/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: UltimatePoker.java
 * -Main class of the plugin
 * -On enable, hooks into Vault and an ECONOMY system, sets command executors and creates the config
 * and log file.
 * -Contains a few useful variables such as commonly used colors, the list of tables, VERSION, 
 * directory, log file.
 * -Contains a useful method getDate()
 * ===================================================================================================
 */

package com.github.norbo11;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.listeners.MapListener;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.PluginConfig;
import com.github.norbo11.util.ResourceManager;
import com.github.norbo11.util.ReturnMoney;
import com.github.norbo11.util.Sound;

public class UltimateCards extends JavaPlugin
{

    // Listeners
    private static PluginExecutor pluginExecutor;

    // Classes
    private static PluginConfig pluginConfig;
    public static MapMethods mapMethods;
    public static Messages messages;

    // Files
    private static File filePluginDir, filePluginConfig, fileLog;

    // Misc
    private static ResourceManager resourceManager;
    private static Logger log;
    private static String version;
    private static Economy economy;
    private static Vault vault;
    private static boolean tagApiEnabled;

    // Constants
    private static final String PLUGIN_TAG = "[UC]&f";

    private static final String LINE_STRING = "---------------------------------------";

    public static Economy getEconomy()
    {
        return economy;
    }

    public static File getFileLog()
    {
        return fileLog;
    }

    public static File getFilePluginConfig()
    {
        return filePluginConfig;
    }

    public static File getFilePluginDir()
    {
        return filePluginDir;
    }

    public static String getLineString()
    {
        return LINE_STRING;
    }

    public static Logger getLog()
    {
        return log;
    }

    public static PluginConfig getPluginConfig()
    {
        return pluginConfig;
    }

    public static String getPluginTag()
    {
        return PLUGIN_TAG;
    }

    public static ResourceManager getResourceManager()
    {
        return resourceManager;
    }

    public static String getVersion()
    {
        return version;
    }

    public static boolean isTagApiEnabled()
    {
        return tagApiEnabled;
    }

    public void addPermissions()
    {
        for (int i = 0; i < PluginExecutor.commands.length; i++)
        {
            //Cards
            if (i == 0)
            {
                for (PluginCommand cmd : PluginExecutor.commands[i])
                {
                    getServer().getPluginManager().addPermission(new Permission(cmd.getPermissionNodes().get(1), PermissionDefault.OP));
                }
            }

            //Table
            if (i == 1)
            {
                for (PluginCommand cmd : PluginExecutor.commands[i])
                {
                    getServer().getPluginManager().addPermission(new Permission(cmd.getPermissionNodes().get(1), PermissionDefault.OP));
                }
            }

            //Poker
            if (i == 2)
            {
                for (PluginCommand cmd : PluginExecutor.commands[i])
                {
                    getServer().getPluginManager().addPermission(new Permission(cmd.getPermissionNodes().get(1), PermissionDefault.OP));
                }
            }

            //Blackjack
            if (i == 3)
            {
                for (PluginCommand cmd : PluginExecutor.commands[i])
                {
                    getServer().getPluginManager().addPermission(new Permission(cmd.getPermissionNodes().get(1), PermissionDefault.OP));
                }
            }
        }
    }

    public boolean createFiles()
    {
        try
        {
            // Attempt to create the create a config file if one doesn't exist
            if (filePluginConfig.exists() == false)
            {
                log.info("Created config file");
                getConfig().options().copyDefaults(true); // Copies the config in the actual plugin
                saveDefaultConfig(); // Saves the new config
            }
        } catch (Exception e)
        {
            terminate("Something went wrong when trying to create the config file!", e);
            return false;
        }

        // Attempt to create a log.txt file if it doesnt exist
        try
        {
            if (fileLog.exists() == false)
            {
                fileLog.createNewFile();
            }
        } catch (Exception e)
        {
            terminate("Something went wrong when trying to create the log file!", e);
            return false;
        }

        return true;
    }

    @Override
    public void onDisable()
    {
        ReturnMoney.returnMoney();
        mapMethods.restoreAllMaps();
        log.info("UltimateCards v" + version + " plugin disabled!");
    }

    @Override
    public void onEnable()
    {
        log = getLogger();
        version = getDescription().getVersion();

        // Set file variables
        filePluginDir = getDataFolder();
        filePluginConfig = new File(filePluginDir, "config.yml");
        fileLog = new File(filePluginDir, "log.txt");

        // Create/load config
        pluginConfig = new PluginConfig(this);
        pluginConfig.load();

        // Update
        /*
         * Updater updater; if (pluginConfig.isAutoUpdate()) { updater = new Updater(this, "ultimatepoker", this.getFile(), Updater.UpdateType.DEFAULT, true); if (updater.getResult() == Updater.UpdateResult.SUCCESS) log.info("To apply the update, reload/restart your server."); }
         */

        // Set all listeners and create classes
        pluginExecutor = new PluginExecutor();
        resourceManager = new ResourceManager(this);
        mapMethods = new MapMethods(this);
        messages = new Messages(this);
        Sound.p = this;
        getServer().getPluginManager().registerEvents(new MapListener(), this);

        // Set all commands to the command executor
        getCommand("cards").setExecutor(pluginExecutor);
        getCommand("c").setExecutor(pluginExecutor);
        getCommand("table").setExecutor(pluginExecutor);
        getCommand("t").setExecutor(pluginExecutor);
        getCommand("poker").setExecutor(pluginExecutor);
        getCommand("p").setExecutor(pluginExecutor);
        getCommand("blackjack").setExecutor(pluginExecutor);
        getCommand("bj").setExecutor(pluginExecutor);

        // Creates all files
        if (!createFiles()) return;

        // Hook into vault, economy and database
        if (!setupVault()) return;
        if (!setupEconomy()) return;
        addPermissions();

        if (!(getConfig().getDouble("table.fixRake") <= 1 && getConfig().getDouble("table.fixRake") >= -1))
        {
            terminate("Check your config file! The field fixRake must be either -1 or 0-1!", null);
            return;
        }

        log.info("UltimateCards v" + version + " plugin enabled!");
    }

    public boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            terminate("ECONOMY plugin not detected! You need an ECONOMY plugin such as iConomy to run this plugin! iConomy DL at: http://dev.bukkit.org/server-mods/iconomy/", null);
            return false;
        }
        economy = rsp.getProvider();
        log.info("Hooked into " + economy.getName());
        return true;
    }

    public boolean setupVault()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        vault = (Vault) plugin;
        if (vault == null)
        {
            terminate("Vault plugin not detected! You need Vault to run this plugin! DL at: http://dev.bukkit.org/server-mods/vault/", null);
            return false;
        }
        log.info("Hooked into Vault");
        return true;
    }

    // Stops the plugin with the specified message and prints a stack trace of the error
    public void terminate(String message, Exception e)
    {
        log.severe(message);
        if (e != null)
        {
            e.printStackTrace();
        }
        getServer().getPluginManager().disablePlugin(this);
    }
}
