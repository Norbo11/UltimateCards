package com.github.norbo11.commands.table;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public class TableTeleport extends PluginCommand {

    public TableTeleport() {
        getAlises().add("teleport");
        getAlises().add("tp");

        setDescription("Teleports you to the specified table.");

        setArgumentString("[table ID]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsTable cardsTable;

    // cards teleport <id>
    @Override
    public boolean conditions() {
        if (getArgs().length == 2) {
            int id = NumberMethods.getPositiveInteger(getArgs()[1]);
            if (id != -99999) {
                cardsTable = CardsTable.getTable(id);
                if (cardsTable != null) return true;
                else {
                    ErrorMessages.notTable(getPlayer(), getArgs()[1]);
                }
            } else {
                ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
            }
        } else {
            showUsage();
        }
        return false;
    }

    @Override
    public void perform() throws Exception {
        getPlayer().teleport(cardsTable.getSettings().startLocation.getValue());
        Messages.sendMessage(getPlayer(), "You have teleported to table " + "&6" + cardsTable.getName() + "&f, ID #" + "&6" + cardsTable.getId() + "&f. Sit down with " + PluginExecutor.tableSit.getCommandString() + " [ID]");
    }
}
