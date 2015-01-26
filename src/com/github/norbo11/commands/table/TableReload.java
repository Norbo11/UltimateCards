package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.config.PluginConfig;

public class TableReload extends PluginCommand {
    public TableReload() {
        getAlises().add("reload");

        setDescription("Reloads the plugin configuration.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    // Lists the specified details type of the specified table. If no table is specified, lists details of the table that the player is sitting on. If a type is not specified, lists all details.
    @Override
    public boolean conditions() {
        if (getArgs().length == 1) return true;
        else {
            showUsage();
        }

        return false;
    }

    @Override
    public void perform() throws Exception {
        PluginConfig.load();
        Messages.sendMessage(getPlayer(), "Plugin configuration sucessfully reloaded.");
    }
}
