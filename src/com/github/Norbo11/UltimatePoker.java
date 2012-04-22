package com.github.Norbo11;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.Norbo11.methods.MethodsError;
import com.github.Norbo11.methods.MethodsHand;
import com.github.Norbo11.methods.MethodsMisc;
import com.github.Norbo11.table.TableDisplay;
import com.github.Norbo11.table.TableMethods;


public class UltimatePoker extends JavaPlugin {

    //Listeners
    public ListenerCommandExecutor CommandExecutor;

    //Classes
    public TableMethods tableMethods;
    public TableDisplay tableDisplay;
    public MethodsError methodsError;
    public MethodsMisc methodsMisc;
    public MethodsHand methodsHand;

    //Variables
    public Logger log;
    public String version = "0.1";
    public String pluginTag = ChatColor.BLUE + "[UPoker]" + ChatColor.WHITE + " ";
    public String PLUGIN_DIRECTORY = "plugins/UltimatePoker/";
    public Economy economy;
    public Vault vault;

    private boolean setupVault()
    {
        Plugin plugin = getServer().getPluginManager().getPlugin("Vault");
        this.vault = (Vault) plugin;
        if (vault == null) {
            return false;
        } else return true;
    }
    
    public boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
    
    public void onEnable()
    {

        log = this.getLogger();

        CommandExecutor = new ListenerCommandExecutor(this);
        tableMethods = new TableMethods(this);
        tableDisplay = new TableDisplay(this);
        methodsError = new MethodsError(this);
        methodsMisc = new MethodsMisc(this);
        methodsHand = new MethodsHand(this);

        File pluginConfig = new File(PLUGIN_DIRECTORY + "config.yml");
        try
        {
            if (pluginConfig.exists() == false)
            {
                log.info("Plugin config file not found! Creating one now...");
                getConfig().options().copyDefaults();
                saveDefaultConfig();
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        getCommand("table").setExecutor(CommandExecutor);
        getCommand("tables").setExecutor(CommandExecutor);
        getCommand("hand").setExecutor(CommandExecutor);
        
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
        
        log.info("UltimatePoker v" + version + " plugin enabled!");
    }

    public void onDisable()
    {
        log.info("UltimatePoker v" + version + " plugin disabled!");
    }
}
