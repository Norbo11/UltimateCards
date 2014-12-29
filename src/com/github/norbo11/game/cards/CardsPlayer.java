package com.github.norbo11.game.cards;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import com.github.norbo11.util.Messages;
import com.github.norbo11.util.PlayerControlled;
import com.github.norbo11.util.Sound;

public abstract class CardsPlayer extends PlayerControlled {
    private Location startLocation; // Used to teleport the player back to the location where he/she joined
    private CardsTable table;       // This holds the table that the player is sitting at
    protected double money;         // This is the player's stack
    private int ID;                 // ID of the player every single statistic associated with this player
    private BukkitTask turnTimer;

    public static CardsPlayer getCardsPlayer(int id, CardsTable table) {
        if (table != null) {
            for (CardsPlayer cardsPlayer : table.getPlayers())
                if (cardsPlayer.getID() == id) return cardsPlayer;
        }
        return null;
    }

    public static CardsPlayer getCardsPlayer(String toCheck) {
        for (CardsTable cardsTable : CardsTable.getTables()) {
            for (CardsPlayer cardsPlayer : cardsTable.getPlayers())
                if (cardsPlayer.getPlayerName().equalsIgnoreCase(toCheck)) return cardsPlayer;
        }
        return null;
    }

    public static CardsPlayer getCardsPlayer(String toCheck, CardsTable table) {
        if (table != null) {
            for (CardsPlayer cardsPlayer : table.getPlayers())
                // If the player list contains the player we are
                // looking for
                if (cardsPlayer.getPlayerName().equalsIgnoreCase(toCheck)) return cardsPlayer;
        }
        return null; // If no match is found, or table doesnt exist, return null
    }

    public void cancelTurnTimer() {
        if (turnTimer != null) {
            turnTimer.cancel();
            turnTimer = null;
        }
    }

    public abstract boolean canPlay();

    public int getID() {
        return ID;
    }

    public double getMoney() {
        return money;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public CardsTable getTable() {
        return table;
    }

    public BukkitTask getTurnTimer() {
        return turnTimer;
    }

    public void giveMoney(double amount) {
        money += amount;
    }

    public boolean hasMoney(double amount) {
        return getMoney() >= amount;
    }

    public boolean isAction() {
        return getTable().getActionPlayer() == this;
    }

    public boolean isButton() {
        return getTable().getButtonPlayer() == this;
    }

    public boolean isEliminated() {
        return !getTable().getPlayersThisHand().contains(this);
    }

    public boolean isOnline() {
        return getPlayer() != null;
    }

    public void removeMoney(double amount) {
        money -= amount;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public void setTable(CardsTable table) {
        this.table = table;
    }

    public void setTurnTimer(BukkitTask turnTimer) {
        this.turnTimer = turnTimer;
    }

    public abstract void startTurnTimer();

    // Sets the player's action flag to true and gives them an eye-catching message
    public void takeAction() {
        if (getTable().getCardsTableSettings().displayTurnsPublicly.getValue()) {
            getTable().sendTableMessage(ChatColor.DARK_PURPLE + getPlayerName() + " has &6" + table.getSettings().turnSeconds.getValue() + " seconds" + ChatColor.DARK_PURPLE + " to act!");
        } else {
            Messages.sendMessage(getPlayer(), ChatColor.DARK_PURPLE + "You have &6" + table.getSettings().turnSeconds.getValue() + " seconds" + ChatColor.DARK_PURPLE + " to act!");
        }
        Sound.tableTurnSounds(getTable(), getPlayerName());
        Sound.turn(getPlayer());
        startTurnTimer();
    }

}
