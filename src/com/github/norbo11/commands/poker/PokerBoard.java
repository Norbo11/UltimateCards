package com.github.norbo11.commands.poker;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.util.ErrorMessages;

public class PokerBoard extends PluginCommand {

    public PokerBoard() {
        getAlises().add("board");
        getAlises().add("community");

        setDescription("Shows the community cards at your table.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    PokerPlayer pokerPlayer;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) return true;
            else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Displays the board to the specified player
    @Override
    public void perform() throws Exception {
        pokerPlayer.getPokerTable().displayBoard(getPlayer(), pokerPlayer.getPokerTable().getBoard().getCards());
    }
}
