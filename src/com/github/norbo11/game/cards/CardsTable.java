package com.github.norbo11.game.cards;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.game.poker.PokerPhase;
import com.github.norbo11.util.Formatter;
import com.github.norbo11.util.Messages;
import com.github.norbo11.util.NumberMethods;

public abstract class CardsTable
{
    static
    {
        ArrayList<String> allowedDetailTypes = new ArrayList<String>();
        allowedDetailTypes.add("settings");
        allowedDetailTypes.add("player");
        allowedDetailTypes.add("other");
        allowedDetailTypes.add("general");
        allowedDetailTypes.add("all");
    }

    public static ArrayList<String> getAllowedTypes()
    {
        return allowedDetailTypes;
    }

    private CardsPlayer owner; // Owner of the table. A PokerPlayer object
                               // should be created right after the creation of
                               // the table
    private String name;
    private CardsPlayer actionPlayer; // The player that is currently supposed
                                      // to act
    private CardsTableSettings cardsTableSettings;
    private Location location; // The location at which the table was created
    private Deck deck = new Deck(1); // Stores the deck assigned to this table

    private PokerPhase currentPhase; // Represents the current phase of the
                                     // hand. 0 = preflop, 1 = flop, 2 = turn, 3
                                     // = river, 4 = showdown, 5 = once everyone
                                     // has showed their hand
    private int ID;

    private int handNumber; // The amount of hands played/the hand number
                            // currently being played at this table
    private boolean inProgress; // True if the hand is currently in progress.
                                // Its false if the table hasen't even started
                                // or is currently in showdown
    private boolean open; // Decides if player can join or not

    private boolean toBeContinued;
    private static ArrayList<String> allowedDetailTypes;
    private ArrayList<CardsPlayer> players = new ArrayList<CardsPlayer>(); // Stores
                                                                           // all
                                                                           // player
                                                                           // sitting
                                                                           // at
                                                                           // the
                                                                           // table
    private ArrayList<String> bannedList = new ArrayList<String>(); // Stores
                                                                    // all
                                                                    // banned
                                                                    // player
                                                                    // from the
                                                                    // table

    private static ArrayList<CardsTable> tables = new ArrayList<CardsTable>();

    public static int getFreeTableID()
    {
        int newID = 0;
        try
        {
            // Try to get the first table. If that table exists, increment the
            // newID variable
            while (tables.get(newID).getID() == newID)
            {
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

    public static CardsTable getTable(int ID)
    {
        for (CardsTable cardsTable : CardsTable.getTables())
            if (cardsTable.getID() == ID) return cardsTable;
        return null;
    }

    public static ArrayList<CardsTable> getTables()
    {
        return tables;
    }

    public static boolean isOwnerOfTable(CardsPlayer toCheck)
    {
        if (toCheck != null) if (toCheck.getTable().getOwner() == toCheck) return true;
        return false;
    }

    public static void setTables(ArrayList<CardsTable> tables)
    {
        CardsTable.tables = tables;
    }

    public abstract void clearPlayerVars();

    public abstract void deal();

    public abstract void dealCards();

    public abstract void deleteTable();

    public void displayDetails(Player player)
    {
        Messages.sendMessage(player, "&6" + UltimateCards.getLineString());
        Messages.sendMessage(player, "&6&nSettings");
        Messages.sendMessage(player, getSettings().listSettings());
        
        Messages.sendMessage(player, "&6" + UltimateCards.getLineString());
        Messages.sendMessage(player, "&6&nPlayers");
        Messages.sendMessage(player, listPlayers());
        Messages.sendMessage(player, "Average stack size: &6" + Formatter.formatMoney(getAverageStack()));
        Messages.sendMessage(player, "&cOFFLINE &f| &6&nACTION&r &f| &6&lCHIP LEADER");
        
        Messages.sendMessage(player, "&6" + UltimateCards.getLineString());
        Messages.sendMessage(player, "&6&nGeneral Details");
        Messages.sendMessage(player, "Owner: &6" + getOwner().getPlayerName());
        Messages.sendMessage(player, "Hands played: &6" + getHandNumber());
        Messages.sendMessage(player, "Open: &6" + isOpen());
        Messages.sendMessage(player, "In progress: " + "&6" + isInProgress());
        Messages.sendMessage(player, "Location: " + "&6X: &f" + Math.round(getLocation().getX()) + "&6 Z: &f" + Math.round(getLocation().getZ()) + "&6 Y: &f" + Math.round(getLocation().getY()) + "&6 World: &f" + getLocation().getWorld().getName());
    }
    
    public abstract boolean canDeal();

    public CardsPlayer getActionPlayer()
    {
        return actionPlayer;
    }

    public double getAverageStack()
    {
        double average = 0;
        int players = 0;
        for (CardsPlayer player : this.players)
            if (!player.isEliminated())
            {
                average = average + player.getMoney();
                players++;
            }
        return NumberMethods.roundDouble(average / players, 2);
    }

    public ArrayList<String> getBannedList()
    {
        return bannedList;
    }

    public CardsTableSettings getCardsTableSettings()
    {
        return cardsTableSettings;
    }

    public CardsPlayer getChipLeader()
    {
        CardsPlayer returnValue = players.get(0);
        for (CardsPlayer cardsPlayer : players)
            if (cardsPlayer.getMoney() > returnValue.getMoney())
            {
                returnValue = cardsPlayer;
            }
        return returnValue;
    }

    public PokerPhase getCurrentPhase()
    {
        return currentPhase;
    }

    public Deck getDeck()
    {
        return deck;
    }

    // Returns a list of player that are eliminated
    public ArrayList<CardsPlayer> getEliminatedPlayers()
    {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>();

        for (CardsPlayer player : players)
            // Go through all player, if their eliminated flag is true, add them
            // to the eventually returned value
            if (player.isEliminated())
            {
                returnValue.add(player);
            }

        return returnValue;
    }

    // Find the first empty ID that is closest to 0
    public int getEmptyPlayerID()
    {
        int newID = 0;
        try
        {
            // Try to get the first player. If that player exists,
            // increment the newID variable
            while (getPlayers().get(newID).getID() == newID)
            {
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

    public int getHandNumber()
    {
        return handNumber;
    }

    public int getID()
    {
        return ID;
    }

    public Location getLocation()
    {
        return location;
    }

    public abstract int getMinPlayers();

    public String getName()
    {
        return name;
    }

    // Method to get the player 1 after the index specified, and loop back to
    // the beginning if the end is reached
    public CardsPlayer getNextPlayer(int index)
    {
        if (index + 1 >= getPlayers().size()) return getPlayers().get((index + 1) % getPlayers().size()); // If
        else return getPlayers().get(index + 1); // If the end of the
        // players is not reached
        // simply return the player 1
        // after the given index
    }

    // Returns a list of online player sitting at the table
    public ArrayList<CardsPlayer> getOnlinePlayers()
    {
        ArrayList<CardsPlayer> returnValue = new ArrayList<CardsPlayer>();
        for (CardsPlayer player : getPlayers())
            if (player.isOnline() == true)
            {
                returnValue.add(player);
            }
        return returnValue;
    }

    public CardsPlayer getOwner()
    {
        return owner;
    }

    public ArrayList<CardsPlayer> getPlayers()
    {
        return players;
    }

    public abstract CardsTableSettings getSettings();

    public boolean isInProgress()
    {
        return inProgress;
    }

    public boolean isOpen()
    {
        return open;
    }

    public boolean isToBeContinued()
    {
        return toBeContinued;
    }

    public abstract void kick(CardsPlayer player);

    public abstract ArrayList<String> listPlayers();

    public abstract void nextPersonTurn(CardsPlayer lastPlayer);

    public abstract void playerLeave(CardsPlayer player);

    public abstract void playerSit(Player player, double buyin) throws Exception;

    public void removePlayer(CardsPlayer cardsPlayer)
    {
        players.remove(cardsPlayer);
    }

    public abstract void returnMoney(CardsPlayer player);

    public void setActionPlayer(CardsPlayer cardsPlayer)
    {
        this.actionPlayer = cardsPlayer;
    }

    public void setBannedList(ArrayList<String> bannedList)
    {
        this.bannedList = bannedList;
    }

    public void setCardsTableSettings(CardsTableSettings cardsTableSettings)
    {
        this.cardsTableSettings = cardsTableSettings;
    }

    public void setCurrentPhase(PokerPhase currentPhase)
    {
        this.currentPhase = currentPhase;
    }

    public void setDeck(Deck deck)
    {
        this.deck = deck;
    }

    public void setHandNumber(int handNumber)
    {
        this.handNumber = handNumber;
    }

    public void setID(int iD)
    {
        ID = iD;
    }

    public void setInProgress(boolean inProgress)
    {
        this.inProgress = inProgress;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setOpen(boolean open)
    {
        this.open = open;
    }

    public void setOwner(CardsPlayer owner)
    {
        this.owner = owner;
    }

    public void setPlayers(ArrayList<CardsPlayer> players)
    {
        this.players = players;
    }

    public void setToBeContinued(boolean toBeContinued)
    {
        this.toBeContinued = toBeContinued;
    }

    // This method makes sure that every player ID is equal to their index in
    // the player list. This should be called whenever a player is removed.
    public void shiftIDs()
    {
        for (int i = 0; i < getPlayers().size(); i++)
            if (getPlayers().get(i).getID() != i)
            {
                getPlayers().get(i).setID(i);
            }
    }

}
