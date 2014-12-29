package com.github.norbo11.commands.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerFold extends PluginCommand {

    public PokerFold() {
        this(null, null);
    }

    public PokerFold(Player player, String[] args) {
        super(player, args);

        getAlises().add("fold");
        getAlises().add("muck");
        getAlises().add("f");

        setDescription("Folds your hand.");

        setArgumentString("");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "poker." + getAlises().get(0));
    }

    PokerPlayer pokerPlayer;
    PokerTable pokerTable;

    @Override
    public boolean conditions() {
        if (getArgs().length == 1) {
            pokerPlayer = PokerPlayer.getPokerPlayer(getPlayer().getName());
            if (pokerPlayer != null) {
                if (!pokerPlayer.isEliminated()) {
                    pokerTable = pokerPlayer.getPokerTable();
                    if (pokerTable.isInProgress()) {
                        if (pokerPlayer.isAction()) {
                            if (!pokerPlayer.isFolded()) {
                                if (!pokerPlayer.isAllIn() || pokerTable.getCurrentPhase() == PokerPhase.SHOWDOWN) return true;
                                else {
                                    ErrorMessages.playerIsAllIn(getPlayer());
                                }
                            } else {
                                ErrorMessages.playerIsFolded(getPlayer());
                            }
                        } else {
                            ErrorMessages.notYourTurn(getPlayer());
                        }
                    } else {
                        ErrorMessages.tableNotInProgress(getPlayer());
                    }
                } else {
                    ErrorMessages.playerIsEliminated(getPlayer());
                }
            } else {
                ErrorMessages.notSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    // Folds the hand of the specified player
    // Clears the player's hand, sets their folded flag to true and
    // displays a message. Then goes to the turn of the next player
    @Override
    public void perform() throws Exception {
        pokerPlayer.fold();
    }
}
