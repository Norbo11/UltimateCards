/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: UltimatePoker.java
 * -Main class of the plugin
 * -On enable, hooks into Vault and an economy system, sets command executors and creates the config
 * and log file.
 * -Contains a few useful variables such as commonly used colors, the list of tables, version, 
 * directory, log file.
 * -Contains a useful method getDate()
 * ===================================================================================================
 */

package com.github.norbo11;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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
import com.github.norbo11.methods.MethodsCheck;
import com.github.norbo11.methods.MethodsError;
import com.github.norbo11.methods.MethodsHand;
import com.github.norbo11.methods.MethodsMisc;
import com.github.norbo11.methods.MethodsPoker;
import com.github.norbo11.methods.MethodsTable;

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

    // Colors
    public ChatColor red = ChatColor.RED;
    public ChatColor gold = ChatColor.GOLD;
    public ChatColor white = ChatColor.WHITE;
    public ChatColor gray = ChatColor.DARK_GRAY;
    public ChatColor darkRed = ChatColor.DARK_RED;

    // Variables
    public Logger log;
    public String version = "1.0";
    public String pluginTag = ChatColor.BLUE + "[UP]" + white + " ";

    public String lineString = "---------------------------------------";
    public List<Table> tables = new ArrayList<Table>();
    public Economy economy;
    public Vault vault;
    
    //Files
    public File filePluginDir;
    public File fileLog;
    public File filePluginConfig;
    public String fileDatabase = "UltimatePoker.db";

    // Misc
    DateFormat dateFormat;
    public Connection database;
    public String databaseTableName = "UltimatePokerStats";
    public String databaseRowName = "playerName";

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
        log.info("UltimatePoker v" + version + " plugin disabled!");
    }

    public void onEnable()
    {
        //log.info(message) sends a message to the console
        log = getLogger();
        
        //Sets the date format to whatever is specified in the config
        dateFormat = new SimpleDateFormat(getConfig().getString("log.dateFormat")); 
        
        //Set file variables
        filePluginDir = getDataFolder();
        filePluginConfig = new File(filePluginDir, "config.yml");
        fileLog = new File(filePluginDir, "log.txt");
        
        //Set all listeners and create classes
        CommandExecutor = new ListenerCommandExecutor(this);
        methodsTable = new MethodsTable(this);
        methodsError = new MethodsError(this);
        methodsCheck = new MethodsCheck(this);
        methodsHand = new MethodsHand(this);
        methodsMisc = new MethodsMisc(this);
        methodsPoker = new MethodsPoker(this);
        getServer().getPluginManager().registerEvents(new ListenerGeneral(this), this);

        //Set all commands to the command executor
        getCommand("tables").setExecutor(CommandExecutor);
        getCommand("table").setExecutor(CommandExecutor);
        getCommand("tbl").setExecutor(CommandExecutor);
        getCommand("hand").setExecutor(CommandExecutor);
        getCommand("poker").setExecutor(CommandExecutor);
        getCommand("pkr").setExecutor(CommandExecutor);

        //Hook into vault, economy and database
        hook();
        
        //Creates all files
        createFiles();

        log.info("UltimatePoker v" + version + " plugin enabled!");
    }
    
    public void createFiles()
    {        
        //Attempt to create the create a config file if one doesn't exist
        if (filePluginConfig.exists() == false) try
        {
            log.info("Plugin config file not found! Creating one now...");
            getConfig().options().copyDefaults(); //Copies the config in the actual plugin
            saveDefaultConfig(); //Saves the new config
        } catch (Exception e) { e.printStackTrace(); }

        //Attempt to create a log.txt file if it doesnt exist
        if (fileLog.exists() == false) try
        {
            fileLog.createNewFile();
        } catch (IOException e)
        {
            log.info("Something went wrong when trying to create the log file!");
            e.printStackTrace();
        }
    }
    
    public void hook()
    {
        if (setupVault() == true)
        {
            log.info("Hooked into Vault");
            if (setupEconomy() == true)
            {
                log.info("Hooked into " + economy.getName());
            } else
            {
                log.info("Economy plugin not detected! You need an economy plugin such as iConomy to run this plugin! iConomy DL at: http://dev.bukkit.org/server-mods/iconomy/");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } else
        {
            log.info("Vault plugin not detected! You need Vault to run this plugin! DL at: http://dev.bukkit.org/server-mods/vault/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }
   

    //Tries to connect to an economy (vault needs to be found first)
    public boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;
        economy = rsp.getProvider();
        return true;
    }

    //Tries to connect to vault
    private boolean setupVault()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        vault = (Vault) plugin;
        if (vault == null)
            return false;
        else return true;
    }
}
