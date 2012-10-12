package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Messages;

public class PokerPot extends PluginCommand
{

    PokerPlayer pokerPlayer;

    public PokerPot()
    {
        getAlises().add("pot");
        getAlises().add("pots");

        setDescription("Displays all pots at the table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    @Override
    public boolean conditions()
    {
        if (getArgs().length == 1)
        {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null)
            {
            	return true;
            } else
            {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else
        {
            showUsage();
        }
        return false;
    }

    // Displays the table's pots to the player
    @Override
    public void perform() throws Exception
    {
    	for (PokerPlayer p : pokerPlayer.getPokerTable().getNonFoldedPlayers()) {
    		Messages.sendMessage(getPlayer(), p.getPlayerName() + " - " + Double.toString(p.getTotalPot()));
    	}
//        for (String pot : pokerPlayer.getPokerTable().listPots())
//        {
//            Messages.sendMessage(getPlayer(), pot);
//        }
    }
}
