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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.norbo11.classes.Table;
import com.github.norbo11.database.MethodsDatabase;
import com.github.norbo11.methods.MethodsCheck;
import com.github.norbo11.methods.MethodsError;
import com.github.norbo11.methods.MethodsHand;
import com.github.norbo11.methods.MethodsMisc;
import com.github.norbo11.methods.MethodsPoker;
import com.github.norbo11.methods.MethodsSound;
import com.github.norbo11.methods.MethodsTable;
import com.github.norbo11.stats.MethodsStats;

public class UltimatePoker extends JavaPlugin
{

    // Listeners
    public ListenerCommandExecutor CommandExecutor;

    // Classes
    public MethodsTable methodsTable;
    public MethodsError methodsError;
    public MethodsCheck methodsCheck;
    public MethodsHand methodsHand;
    public MethodsMisc methodsMisc;
    public MethodsPoker methodsPoker;
    public MethodsDatabase methodsDatabase;
    public MethodsStats methodsStats;
    public MethodsSound methodsSound;

    // Colors
    public ChatColor red = ChatColor.RED;
    public ChatColor gold = ChatColor.GOLD;
    public ChatColor white = ChatColor.WHITE;
    public ChatColor gray = ChatColor.DARK_GRAY;
    public ChatColor darkRed = ChatColor.DARK_RED;

    // Variables
    public Logger log;
    public String VERSION;
    public String PLUGIN_TAG = ChatColor.BLUE + "[UP]" + white + " ";

    public String LINE_STRING = "---------------------------------------";
    public List<Table> tables = new ArrayList<Table>();
    public Economy ECONOMY;
    public Vault VAULT;

    // Files
    public File FILE_PLUGIN_DIR;
    public File FILE_PLUGIN_CONFIG;
    public File FILE_LOG;
    
    
    // Database
    public Connection database;
    public String DATABASE_TABLE_NAME = "UltimatePokerStats";
    
    // Misc
    DateFormat dateFormat;

    public boolean createFiles()
    {
        try
        {
            // Attempt to create the create a config file if one doesn't exist
            if (FILE_PLUGIN_CONFIG.exists() == false)
            {
                log.info("Created config file");
                getConfig().options().copyDefaults(); // Copies the config in the actual plugin
                saveDefaultConfig();                  // Saves the new config
            }
        } catch (Exception e)
        {
            terminate("Something went wrong when trying to create the config file!", e);
            return false;
        }

        // Attempt to create a log.txt file if it doesnt exist
        try
        {
            if (FILE_LOG.exists() == false) FILE_LOG.createNewFile();
        } catch (Exception e)
        {
            terminate("Something went wrong when trying to create the log file!", e);
            return false;
        }

        return true;
    }

    public String getDate()
    {
        Date currentDate = new Date();
        return "[" + dateFormat.format(currentDate) + "]";
    }

    public void onDisable()
    {
        methodsMisc.returnMoney();
        try
        {
            database.close();
        } catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        log.info("UltimatePoker v" + VERSION + " plugin disabled!");
    }

    public void onEnable()
    {
        // log.info(message) sends a message to the console
        log = getLogger();
        VERSION = getDescription().getVersion();

        // Sets the date format to whatever is specified in the config
        dateFormat = new SimpleDateFormat(getConfig().getString("log.dateFormat"));

        // Set file variables
        FILE_PLUGIN_DIR = getDataFolder();
        FILE_PLUGIN_CONFIG = new File(FILE_PLUGIN_DIR, "config.yml");
        FILE_LOG = new File(FILE_PLUGIN_DIR, "log.txt");

        // Set all listeners and create classes
        CommandExecutor = new ListenerCommandExecutor(this);
        methodsTable = new MethodsTable(this);
        methodsError = new MethodsError(this);
        methodsCheck = new MethodsCheck(this);
        methodsHand = new MethodsHand(this);
        methodsMisc = new MethodsMisc(this);
        methodsPoker = new MethodsPoker(this);
        methodsDatabase = new MethodsDatabase(this);
        methodsStats = new MethodsStats(this);
        methodsSound = new MethodsSound(this);
        getServer().getPluginManager().registerEvents(new ListenerGeneral(this), this);

        // Set all commands to the command executor
        getCommand("tables").setExecutor(CommandExecutor);
        getCommand("table").setExecutor(CommandExecutor);
        getCommand("tbl").setExecutor(CommandExecutor);
        getCommand("hand").setExecutor(CommandExecutor);
        getCommand("poker").setExecutor(CommandExecutor);
        getCommand("pkr").setExecutor(CommandExecutor);

        // Creates all files
        if (!createFiles()) return;

        // Hook into vault, ECONOMY and database
        if (!setupVault()) return;
        if (!setupECONOMY()) return;
        if (!setupDatabase()) return;

        if (!(getConfig().getDouble("table.fixRake") <= 1 && getConfig().getDouble("table.fixRake") >= -1))
        {
            terminate("Check your config file! The field fixRake must be either -1 or 0-1!", null);
            return;
        }

        log.info("UltimatePoker v" + VERSION + " plugin enabled!");
    }

    public boolean setupDatabase()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            database = DriverManager.getConnection("jdbc:sqlite:" + FILE_PLUGIN_DIR.getPath() + File.separatorChar + "UltimatePoker.db");
            methodsDatabase.createTable(DATABASE_TABLE_NAME, methodsStats.getStatHeaders());
        } catch (Exception e)
        {
            terminate("Something went wrong when trying to connect to the database!", e);
            return false;
        }
        log.info("Hooked into SQLite");
        return true;
    }

    // Tries to connect to an economy plugin (vault needs to be found first)
    public boolean setupECONOMY()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            terminate("ECONOMY plugin not detected! You need an ECONOMY plugin such as iConomy to run this plugin! iConomy DL at: http://dev.bukkit.org/server-mods/iconomy/", null);
            return false;
        }
        ECONOMY = rsp.getProvider();
        log.info("Hooked into " + ECONOMY.getName());
        return true;
    }

    // Tries to connect to vault
    public boolean setupVault()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        VAULT = (Vault) plugin;
        if (VAULT == null)
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
        if (e != null) e.printStackTrace();
        getServer().getPluginManager().disablePlugin(this);
    }

}
