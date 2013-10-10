package com.github.norbo11.commands.cards;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;
import com.github.norbo11.game.poker.PokerTable;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;

public class CardsSit extends PluginCommand {
    public CardsSit() {
        getAlises().add("sit");
        getAlises().add("join");
        getAlises().add("s");

        setDescription("Sits down at the specified table with the specified buy-in.");

        setArgumentString("[table ID] [buyin]");

        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards");
        getPermissionNodes().add(PERMISSIONS_BASE_NODE + "cards." + getAlises().get(0));
    }

    CardsTable cardsTable;

    double buyin;

    @Override
    // cards sit <id> <buyin>
    public boolean conditions() {
        if (getArgs().length == 3) {
            // Firstly check if the player is already sitting at a table or
            // not
            CardsPlayer cardsPlayer = CardsPlayer.getCardsPlayer(getPlayer().getName());
            if (cardsPlayer == null) {
                int id = NumberMethods.getPositiveInteger(getArgs()[1]);
                // Check if the ID and buyIn are numbers
                if (id != -99999) {
                    buyin = NumberMethods.getDouble(getArgs()[2]);
                    if (buyin != -99999) {
                        // Then check if the table exists
                        cardsTable = CardsTable.getTable(id);
                        if (cardsTable != null) {
                            // Check if the player is banned
                            if (!cardsTable.getBannedList().contains(getPlayer().getName())) {
                                // Check if the table is open
                                if (cardsTable.isOpen()) {
                                    // Check if the table is in progress
                                    if (!cardsTable.isInProgress()) {
                                        // Check if the buy in is within the
                                        // bounds of the table
                                        if (buyin >= cardsTable.getSettings().getMinBuy() && buyin <= cardsTable.getSettings().getMaxBuy()) {
                                            // Check if the player even has
                                            // that amount
                                            if (UltimateCards.getEconomy().has(getPlayer().getName(), buyin)) return true;
                                            else {
                                                ErrorMessages.notEnoughMoney(getPlayer(), buyin, UltimateCards.getEconomy().getBalance(getPlayer().getName()));
                                            }
                                        } else {
                                            ErrorMessages.notWithinBuyinBounds(getPlayer(), buyin, cardsTable.getSettings().getMinBuy(), cardsTable.getSettings().getMaxBuy());
                                        }
                                    } else {
                                        ErrorMessages.tableInProgress(getPlayer());
                                    }
                                } else {
                                    ErrorMessages.tableNotOpen(getPlayer(), getArgs()[1]);
                                }
                            } else {
                                ErrorMessages.playerIsBanned(getPlayer());
                            }
                        } else {
                            ErrorMessages.notTable(getPlayer(), getArgs()[1]);
                        }
                    } else {
                        ErrorMessages.invalidNumber(getPlayer(), getArgs()[2]);
                    }
                } else {
                    ErrorMessages.invalidNumber(getPlayer(), getArgs()[1]);
                }
            } else {
                ErrorMessages.playerSittingAtTable(getPlayer());
            }
        } else {
            showUsage();
        }
        return false;
    }

    @Override
    // Sits the player at the specified table with the specified buy-in
    public void perform() throws Exception {
        if (PluginExecutor.cardsTeleport.hasPermission(getPlayer())) {
            getPlayer().teleport(cardsTable.getLocation());
        }

        MoneyMethods.withdrawMoney(getPlayer().getName(), buyin);

        boolean isOwner = cardsTable.getOwner().equalsIgnoreCase(getPlayer().getName());

        cardsTable.sendTableMessage("&6" + getPlayer().getName() + "&f" + (isOwner ? " (Owner)" : "") + " has sat down at the table with &6" + Formatter.formatMoney(buyin));

        CardsPlayer cardsPlayer = cardsTable.playerSit(getPlayer(), buyin);
        if (isOwner) {
            cardsTable.setOwnerPlayer(cardsPlayer);
        }

        if (cardsTable instanceof PokerTable) {
            PokerTable pokerTable = (PokerTable) cardsTable;
            pokerTable.autoStart();
        }
    }
}
