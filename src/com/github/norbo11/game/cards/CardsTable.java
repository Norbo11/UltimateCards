package com.github.norbo11.game.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.util.MapMethods;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.MoneyMethods;
import com.github.norbo11.util.NumberMethods;
import com.github.norbo11.util.config.SavedTables;

public abstract class CardsTable {
    public CardsTable(String owner, String name, int id, Location location) {
        // Set the table core properties
        setOwner(owner);
        setName(name);
        setId(id);
    }

    static {
        ArrayList<String> allowedDetailTypes = new ArrayList<String>();
        allowedDetailTypes.add("settings");
        allowedDetailTypes.add("player");
        allowedDetailTypes.add("other");
        allowedDetailTypes.add("general");
        allowedDetailTypes.add("all");
    }
    private int button; // button (in the list 'players')

    private CardsPlayer ownerPlayer;

    private String owner;

    private String name;
    private CardsPlayer actionPlayer; // The player that is currently supposed to act
    private CardsTableSettings cardsTableSettings;

    private Deck deck = new Deck(1); // Stores the deck assigned to this table
    private PokerPhase currentPhase;
    private int id;
    private int handNumber; // The amount of hands played/the hand number currently being played at this table
    private boolean inProgress; // True if the hand is currently in progress. Its false if the table hasen't even started or is currently in showdown
    private boolean open; // Decides if player can join or not
    private boolean toBeContinued;
    private static ArrayList<String> allowedDetailTypes;
    private ArrayList<CardsPlayer> players = new ArrayList<CardsPlayer>();
    private ArrayList<String> bannedList = new ArrayList<String>();
    private static ArrayList<CardsTable> tables = new ArrayList<CardsTable>();
    private BukkitTask timerTask;

    public static boolean doesTableExist(String tableName) {
        for (CardsTable table : tables) {
            if (table.getName().equalsIgnoreCase(tableName)) return true;
        }
        for (CardsTable table : SavedTables.getSavedTables()) {
            if (table.getName().equalsIgnoreCase(tableName)) return true;
        }
        return false;
    }

    public static ArrayList<String> getAllowedTypes() {
        return allowedDetailTypes;
    }

    public static int getFreeTableID() {
        int newID = 0;
        boolean taken = true;
        whileLoop: while (taken) {
            for (CardsTable table : tables) {
                if (table.getId() == newID) {
                    newID++;
                    continue whileLoop;
                }
            }
            taken = false;
        }
        return newID;
    }

    public static CardsTable getTable(int ID) {
        for (CardsTable cardsTable : CardsTable.getTables())
            if (cardsTable.getId() == ID) return cardsTable;
        return null;
    }

    public static ArrayList<CardsTable> getTables() {
        return tables;
    }

    public static boolean isGameType(String gameType) {
        return gameType.equalsIgnoreCase("poker") || gameType.equalsIgnoreCase("blackjack") || gameType.equalsIgnoreCase("bj");
    }

    public static void setTables(ArrayList<CardsTable> tables) {
        CardsTable.tables = tables;
    }

    public abstract void autoStart();

    public boolean canBeDeleted() {
        return true;
    }

    public abstract boolean canDeal();

    public abstract void clearPlayerVars();

    public abstract void deal();

    public abstract void dealCards();

    public void deleteTable() {
        sendTableMessage("Table ID '" + "&6" + getName() + "&f', ID #" + "&6" + getId() + " &fhas been deleted!");
        MoneyMethods.returnMoney(this);
        for (CardsPlayer player : players)
        {
            if (player.getTurnTimer() != null) player.getTurnTimer().cancel();
        }
        CardsTable.getTables().remove(this);
    }

    public void displayDetails(Player player) {
        Messages.sendMessage(player, "&6" + UltimateCards.getLineString());
        Messages.sendMessage(player, ChatColor.BOLD + "&6Settings");
        Messages.sendMessage(player, getSettings().listSettings());

        Messages.sendMessage(player, "&6" + UltimateCards.getLineString());
        Messages.sendMessage(player, ChatColor.BOLD + "&6Players");
        Messages.sendMessage(player, listPlayers());

        Messages.sendMessage(player, "&6" + UltimateCards.getLineString());
        Messages.sendMessage(player,  "&6General Details");
        Messages.sendMessage(player, "Owner: &6" + (getOwner().equals("") == false ? getOwner() : "SERVER"));
        Messages.sendMessage(player, "Hands played: &6" + getHandNumber());
        Messages.sendMessage(player, "Open: &6" + isOpen());
        Messages.sendMessage(player, "In progress: " + "&6" + isInProgress());
    }

    public CardsPlayer getActionPlayer() {
        return actionPlayer;
    }

    public double getAverageStack() {
        double average = 0;
        int players = 0;
        for (CardsPlayer player : getPlayers()) {
            average += player.getMoney();
            players++;
        }
        return NumberMethods.roundDouble(average / players, 2);
    }

    public ArrayList<String> getBannedList() {
        return bannedList;
    }

    public int getButton() {
        return button;
    }

    public CardsPlayer getButtonPlayer() {
        // Exception has to be caught in case the button doesnt exist in the list
        try {
            return getPlayersThisHand().get(button);
        } catch (Exception e) {
            return null;
        }
    }

    public CardsTableSettings getCardsTableSettings() {
        return cardsTableSettings;
    }

    public CardsPlayer getChipLeader() {
        CardsPlayer returnValue = players.get(0);
        for (CardsPlayer cardsPlayer : players)
            if (cardsPlayer.getMoney() > returnValue.getMoney()) {
                returnValue = cardsPlayer;
            }
        return returnValue;
    }

    public PokerPhase getCurrentPhase() {
        return currentPhase;
    }

    public Deck getDeck() {
        return deck;
    }

    // Find the first empty ID that is closest to 0
    public int getEmptyPlayerID() {
        int newID = 0;
        try {
            // Try to get the first player. If that player exists,
            // increment the newID variable
            while (getPlayers().get(newID).getID() == newID) {
                newID++;
            }
        } catch (Exception e) // As soon as you try to get a player that
                              // return null (doesnt exist), this means that
                              // that ID is free. Therefore, return it
        {
            return newID;
        }
        return newID; // This doesnt actually do anything but is required so the
                      // compiler doesnt complain
    }

    public int getHandNumber() {
        return handNumber;
    }

    public int getId() {
        return id;
    }

    public abstract int getMinPlayers();

    public String getName() {
        return name;
    }

    // Method to get the player 1 after the index specified, and loop back to
    // the beginning if the end is reached
    public CardsPlayer getNextPlayer(int index) {
        if (index + 1 >= getPlayers().size()) return getPlayers().get((index + 1) % getPlayers().size()); // If
        else return getPlayers().get(index + 1); // If the end of the
        // players is not reached
        // simply return the player 1
        // after the given index
    }

    // Returns a list of online player sitting at the table
    public ArrayList<CardsPlayer> getOnlinePlayers() {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>();
        for (CardsPlayer player : getPlayers())
            if (player.isOnline() == true) {
                returnValue.add(player);
            }
        return returnValue;
    }

    public String getOwner() {
        return owner;
    }

    public CardsPlayer getOwnerPlayer() {
        return ownerPlayer;
    }

    public ArrayList<CardsPlayer> getPlayers() {
        return players;
    }

    public abstract ArrayList<CardsPlayer> getPlayersThisHand();

    public ArrayList<CardsPlayer> getRearrangedPlayers(CardsPlayer startingPlayer) {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>(getPlayersThisHand());

        for (CardsPlayer player : getPlayersThisHand()) {
            if (getPlayers().indexOf(player) < getPlayersThisHand().indexOf(startingPlayer)) {
                CardsPlayer temp = returnValue.get(0);
                returnValue.remove(0);
                returnValue.add(temp);
            }
        }

        return returnValue;
    }

    public abstract CardsTableSettings getSettings();

    public BukkitTask getTimerTask() {
        return timerTask;
    }

    public boolean isInProgress() {
        return inProgress;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isOwner(String player) {
        return player.equalsIgnoreCase(owner);
    }

    public boolean isToBeContinued() {
        return toBeContinued;
    }

    public abstract void kick(CardsPlayer player);

    public abstract ArrayList<String> listPlayers();

    // Moves the button to the next player (call this when starting a new hand)
    public void moveButton() {
        // If the button is not the last player in the list, increment the
        // button. Otherwise set button to 0.
        if (++button >= getPlayersThisHand().size()) {
            button = 0;
        }
        sendTableMessage("Button moved to &6" + getPlayersThisHand().get(button).getPlayerName());
    }

    public abstract void nextPersonTurn(CardsPlayer lastPlayer);

    public abstract void playerLeave(CardsPlayer player);

    public abstract CardsPlayer playerSit(Player player, double buyin) throws Exception;

    public void removePlayer(CardsPlayer cardsPlayer) {
        players.remove(cardsPlayer);
        if (players.size() == 0) setOpen(true);
    }

    public void restoreAllMaps() {
        for (CardsPlayer player : getPlayers()) {
            MapMethods.restoreMap(player.getPlayer().getName(), true);
        }
    }

    public abstract void returnMoney(CardsPlayer player);

    public void sendTableMessage(String message) {
        sendTableMessage(message, Collections.<String> emptyList());
    }

    public void sendTableMessage(String message, List<String> toIgnore) {
        ArrayList<String> ignore = new ArrayList<String>(toIgnore);
        int range = getSettings().publicChatRange.getValue();

        // Send private message to all table players, also add them to the ignore list
        for (CardsPlayer cardsPlayer : getPlayers()) {
            if (!toIgnore.contains(cardsPlayer.getPlayerName())) cardsPlayer.sendMessage(message);
            ignore.add(cardsPlayer.getPlayerName());
        }

        // Send public message to everyone apart from the table players, if the range setting is enabled
        if (range > 0) Messages.sendToAllWithinRange(getSettings().startLocation.getValue(), range, message, ignore);
    }

    public void sendTableMessage(String message, String ignore) {
        ArrayList<String> ignoreList = new ArrayList<String>();
        ignoreList.add(ignore);
        sendTableMessage(message, ignoreList);
    }

    public void sendTableMessage(String[] messages) {
        for (String message : messages) {
            sendTableMessage(message);
        }
    }

    public void setActionPlayer(CardsPlayer cardsPlayer) {
        actionPlayer = cardsPlayer;
    }

    public void setBannedList(ArrayList<String> bannedList) {
        this.bannedList = bannedList;
    }

    public void setCardsTableSettings(CardsTableSettings cardsTableSettings) {
        this.cardsTableSettings = cardsTableSettings;
    }

    public void setCurrentPhase(PokerPhase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public void setHandNumber(int handNumber) {
        this.handNumber = handNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setOwnerPlayer(CardsPlayer ownerPlayer) {
        this.ownerPlayer = ownerPlayer;
    }

    public void setPlayers(ArrayList<CardsPlayer> players) {
        this.players = players;
    }

    public void setTimerTask(BukkitTask timerTask) {
        this.timerTask = timerTask;
    }

    public void setToBeContinued(boolean toBeContinued) {
        this.toBeContinued = toBeContinued;
    }

    // This method makes sure that every player ID is equal to their index in
    // the player list. This should be called whenever a player is removed.
    public void shiftIDs() {
        for (int i = 0; i < getPlayers().size(); i++)
            if (getPlayers().get(i).getID() != i) {
                getPlayers().get(i).setID(i);
            }
    }

    public void cancelTimerTask() {
        if (getTimerTask() != null) getTimerTask().cancel();
    }
}
