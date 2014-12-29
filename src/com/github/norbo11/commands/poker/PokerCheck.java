package com.github.norbo11.commands.poker;

import org.bukkit.entity.Player;

import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.game.poker.PokerPlayer;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;

public class PokerCheck extends PluginCommand {

    public PokerCheck() {
        this(null, null);
    }

    public PokerCheck(Player player, String[] args) {
        super(player, args);

        getAlises().add("check");
        getAlises().add("ch");

        setDescription("Checks your hand.");

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
                        if (pokerTable.getCurrentPhase() != PokerPhase.SHOWDOWN) {
                            if (pokerPlayer.isAction()) {
                                if (!pokerPlayer.isFolded()) {
                                    if (!pokerPlayer.isAllIn()) {
                                        if (pokerPlayer.getCurrentBet() == pokerTable.getCurrentBet()) return true;
                                        else {
                                            ErrorMessages.cantCheck(pokerPlayer);
                                        }
                                    } else {
                                        ErrorMessages.playerIsAllIn(getPlayer());
                                    }
                                } else {
                                    ErrorMessages.playerIsFolded(getPlayer());
                                }
                            } else {
                                ErrorMessages.notYourTurn(getPlayer());
                            }
                        } else {
                            ErrorMessages.tableAtShowdown(getPlayer());
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

    // Checks the turn of the specified player()
    @Override
    public void perform() throws Exception {
        pokerPlayer.check();
    }
}
