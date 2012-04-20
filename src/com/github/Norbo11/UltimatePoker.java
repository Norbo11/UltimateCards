package com.github.Norbo11;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.Norbo11.methods.ErrorDisplay;
import com.github.Norbo11.methods.MethodsMisc;
import com.github.Norbo11.table.TableDisplay;
import com.github.Norbo11.table.TableMethods;


public class UltimatePoker extends JavaPlugin {

    //Listeners
    public ListenerCommandExecutor CommandExecutor;

    //Classes
    public TableMethods tableMethods;
    public TableDisplay tableDisplay;
    public ErrorDisplay errorDisplay;
    public MethodsMisc methodsMisc;

    //Variables
    public Logger log;
    public String version = "0.1";
    public String pluginTag = ChatColor.BLUE + "[UPoker]" + ChatColor.WHITE + " ";
    public String PLUGIN_DIRECTORY = "plugins/UltimatePoker/";


    public void onEnable()
    {

        log = this.getLogger();

        CommandExecutor = new ListenerCommandExecutor(this);
        tableMethods = new TableMethods(this);
        tableDisplay = new TableDisplay(this);
        errorDisplay = new ErrorDisplay(this);
        methodsMisc = new MethodsMisc(this);

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
        getCommand("pk").setExecutor(CommandExecutor);

        log.info("UltimatePoker v" + version + " plugin enabled!");
    }

    public void onDisable()
    {
        log.info("UltimatePoker v" + version + " plugin disabled!");
    }
}
