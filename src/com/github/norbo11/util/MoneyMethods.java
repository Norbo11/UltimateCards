package com.github.norbo11.util;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.cards.CardsPlayer;
import com.github.norbo11.game.cards.CardsTable;

public class MoneyMethods {
    public static void depositMoney(String user, double amount) {
        UltimateCards.getEconomy().depositPlayer(user, amount);
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + amount + " to " + user);
    }

    public static double getMoney(String owner) {
        return UltimateCards.getEconomy().getBalance(owner);
    }

    // Returns money to EVERYONE that is currently at a table. Should be called
    // when a reload happens
    public static void returnMoney() {
        for (CardsTable cardsTable : CardsTable.getTables()) {
            // Go through every online player, teleport them to their starting
            // location, display them messages to them, give them their money
            // back and log the event.
            for (CardsPlayer player : cardsTable.getPlayers()) {
                if (player.isOnline()) {
                    player.getPlayer().teleport(player.getStartLocation());
                    Messages.sendMessage(player.getPlayer(), "&cSomething (an error, plugin reload, etc) has caused all tables to be deleted!");
                    Messages.sendMessage(player.getPlayer(), "You have been paid your remaining stack of &6" + Formatter.formatMoney(player.getMoney()));
                }
                UltimateCards.getEconomy().depositPlayer(player.getPlayerName(), player.getMoney());
                Log.addToLog(DateMethods.getDate() + " [ECONOMY] Depositing " + player.getMoney() + " to " + player.getPlayerName());
            }
        }
        CardsTable.getTables().clear();
    }

    // Returns money to all the players in the specified table. Should be called
    // when a table is deleted, etc.
    public static void returnMoney(CardsTable cardsTable) {
        // Go through every online player, teleport them to their starting
        // location, display them messages to them, give them their money back
        // and log the event.
        for (CardsPlayer player : cardsTable.getPlayers()) {
            player.getTable().returnMoney(player);
        }
    }

    public static void withdrawMoney(String user, double amount) {
        UltimateCards.getEconomy().withdrawPlayer(user, amount);
        Log.addToLog(DateMethods.getDate() + " [ECONOMY] Withdrawing " + amount + " from " + user);
    }
}
