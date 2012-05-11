/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: Table.java
 * -This class is created whenever a table is created.
 * -It holds vital properties of the table, such as settings and flags
 * -It holds vital methods for the table
 * ===================================================================================================
 */

package com.github.norbo11.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;

public class Table
{
    public Table(Player owner, String name, int id, Location location, double BuyIN, UltimatePoker p)
    {
        // Set the table core properties
        this.p = p;
        this.owner = new PokerPlayer(owner, this, BuyIN, p);
        this.owner.owner = true;
        this.name = name;
        this.id = id;
        this.location = location;

        // Set all the required settings
        open = false;
        handNumber = 0;
        button = 0;
        deck = new Deck(p);
        inProgress = false;
        stopped = false;
        toBeContinued = false;
        actionPlayer = null;
        pots.add(new Pot(null, this, 0, 0, p));
        pots.get(0).main = true;
        latestPot = pots.get(0);

        // Fetch defaults from config
        elimination = p.getConfig().getBoolean("table.defaults.elimination");
        minBuy = p.getConfig().getDouble("table.defaults.minBuy");
        maxBuy = p.getConfig().getDouble("table.defaults.maxBuy");
        sb = p.getConfig().getDouble("table.defaults.sb");
        bb = p.getConfig().getDouble("table.defaults.bb");
        ante = p.getConfig().getDouble("table.defaults.ante");
        dynamicFrequency = p.getConfig().getInt("table.defaults.dynamicFrequency");
        minRaiseIsAlwaysBB = p.getConfig().getBoolean("table.defaults.minRaiseIsAlwaysBB");
        chatRange = p.getConfig().getInt("table.chatRange");

        // If the min raise is always BB, set it to the BB. If not, make it whatever is in the settings
        if (minRaiseIsAlwaysBB == false) minRaise = p.getConfig().getDouble("table.defaults.minRaise");
        else minRaise = bb;

        // If the rake is allowed, set it. If not, make it 0.
        if (p.getConfig().getBoolean("table.allowRake") == true) rake = p.getConfig().getDouble("table.defaults.rake");
        else rake = 0;

        // Set originals, required for dynamic antes/blinds
        originalSB = sb;
        originalBB = bb;
        originalAnte = ante;

        players.add(this.owner); //Add the owner to the sitting player list
    }

    // Generic vars set by constructor
    UltimatePoker p;
    public PokerPlayer owner;           //Owner of the table. A PokerPlayer object should be created right after the creation of the table
    public String name;
    public int id;
    public Location location;           //The location at which the table was created
    
    // Generic vars
    public boolean inProgress;          //True if the hand is currently in progress. Its false if the table hasen't even started or is currently in showdown
    public boolean open;                //Decides if players can join or not
    public boolean stopped;             //This is set to true only if the table is stopped by another player quitting the game
    public boolean toBeContinued;
    public double currentBet;           //The current bet at the table, in the current phase
    public double rake;                 //A number from 
    public double chatRange;
    public int handNumber;              //The amount of hands played/the hand number currently being played at this table
    public int button;                  //Represents the index of the player that is on the button (in the list 'players')
    public int currentPhase;            //Represents the current phase of the hand. 0 = preflop, 1 = flop, 2 = turn, 3 = river, 4 = showdown, 5 = once everyone has showed their hand
    public Pot latestPot;               //Represents the latest pot that was created. For all in purposes.
    public List<PokerPlayer> players = new ArrayList<PokerPlayer>(); //Stores all players sitting at the table
    public List<String> banned = new ArrayList<String>();            //Stores all banned players from the table
    public List<Card> board = new ArrayList<Card>();                 //Stores the community cards
    public List<Pot> pots = new ArrayList<Pot>();                    //Stores all pots created (index 0 is always the main pot, others are side pots)
    public Deck deck;                                                //Stores the deck assigned to this table
    public PokerPlayer actionPlayer;     //The player that is currently supposed to act
    
    // Settings
    public boolean minRaiseIsAlwaysBB;   //If this is true, players wont be able to set their own minraise. The minraise will always be equal to the big blind.
    public boolean elimination;          //If this is true, players cant rebuy
    public double minBuy;
    public double maxBuy;
    public double minRaise;
    public double originalSB;
    public double originalBB;
    public double originalAnte;
    public double sb;
    public double bb;
    public double ante;
    public int dynamicFrequency;         //This represents every how many hands will the antes go up by the amount that they were last set to.


    //Goes through every player and clears their bets/pots/acted status. This is called at the start of every phase too
    public void clearBets()
    {
        for (PokerPlayer player : players)
        {
            player.currentBet = 0;
            player.acted = false;
        }
    }
    
    public void continueHand()
    {
        toBeContinued = false;
        deal();
    }

    //Clears all the pots at the table and adds a new pot main pot
    public void clearPots()
    {
        pots.clear();
        pots.add(new Pot(null, this, 0, 0, p));
        pots.get(0).main = true;
        latestPot = pots.get(0);
    }

    // Method to deal a brand new hand
    public void deal()
    {
        //If there are enough players to play another hand, then do so
        if (eliminatePlayers() == true)
        {
            handNumber++;
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Dealing hand number " + p.gold + handNumber);
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
            inProgress = true;
            deck.shuffle();     //Shuffle the deck
            board.clear();      //Clear the community cards
            
            clearPots();        //Clears all the pots at the table and adds a new pot main pot
            clearBets();        //Goes through every player and clears their bets/pots/acted status
            raiseBlinds();
            moveButton();
            dealCards();
            preflop();
        }
    }

    // Deal cards and clear variables for non-eliminated players
    public void dealCards()
    {
        // Go through all players, clear their hands and add their cards. 
        for (PokerPlayer pokerPlayer : players)
        {
            if (pokerPlayer.eliminated == false)
            {
                pokerPlayer.clearHand();
                pokerPlayer.folded = false;
                pokerPlayer.action = false;
                pokerPlayer.revealed = false;
                pokerPlayer.pot = null;                     //Make sure that the player's all-in status is reset
                pokerPlayer.totalBet = 0;
                pokerPlayer.addCards(deck.generateCards(2));
            }
        }
    }

    //Displays all table details to the specified player
    public void displayAllDetails(Player player)
    {
        displayDetail(player, "settings");
        displayDetail(player, "players");
        displayDetail(player, "other");
    }

    // Method to display the board. If who is null, display to everyone around the table. Otherwise, display to just the player specified in "who".
    public void displayBoard(Player who)
    {
        if (who == null)
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Community Cards: ");
            int i = 1;
            for (Card card : board)
            {
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "[" + i + "] " + card.toString());
                i++;
            }
        } else
        {
            who.sendMessage(p.pluginTag + p.gold + p.lineString);
            who.sendMessage(p.pluginTag + "Community Cards: ");
            int i = 1;
            for (Card card : board)
            {
                who.sendMessage(p.pluginTag + "[" + i + "] " + card.toString());
                i++;
            }
        }
    }

    //Display a specific detail to the player
    public void displayDetail(Player player, String type)
    {
        if (type.equalsIgnoreCase("settings"))
        {
            player.sendMessage(p.pluginTag + "Settings:");
            player.sendMessage(p.pluginTag + p.gold + p.lineString);
            player.sendMessage(p.pluginTag + "Minimum Buy-in: " + p.gold + p.methodsMisc.formatMoney(minBuy));
            player.sendMessage(p.pluginTag + "Maximum Buy-in: " + p.gold + p.methodsMisc.formatMoney(maxBuy));
            player.sendMessage(p.pluginTag + "Ante: " + p.gold + p.methodsMisc.formatMoney(ante));
            player.sendMessage(p.pluginTag + "Small Blind: " + p.gold + p.methodsMisc.formatMoney(sb));
            player.sendMessage(p.pluginTag + "Big Blind: " + p.gold + p.methodsMisc.formatMoney(bb));
            player.sendMessage(p.pluginTag + "Rake: " + p.gold + p.methodsMisc.convertToPercentage(rake));
            if (minRaiseIsAlwaysBB)
                player.sendMessage(p.pluginTag + "Minimum Raise: " + p.gold + "equal to the Big Blind");
            else player.sendMessage(p.pluginTag + "Minimum Raise: " + p.gold + p.methodsMisc.formatMoney(minRaise));
            player.sendMessage(p.pluginTag + "Elimination: " + p.gold + elimination);
            if (dynamicFrequency > 0) player.sendMessage(p.pluginTag + "Dynamic Frequency: " + p.gold + "Every " + dynamicFrequency + " hands");
            else player.sendMessage(p.pluginTag + "Dynamic Frequency: " + p.gold + "OFF");
        }

        if (type.equalsIgnoreCase("players"))
        {
            player.sendMessage(p.pluginTag + "Players:");
            player.sendMessage(p.pluginTag + p.gold + p.lineString);
            for (PokerPlayer pokerPlayer : players) //Display all the players. If the player is offline make their name appear in red
            {
                if (pokerPlayer.online == true) player.sendMessage(p.pluginTag + "[" + pokerPlayer.id + "] " + p.gold + pokerPlayer.name);
                else player.sendMessage(p.pluginTag + "[" + pokerPlayer.id + "] " + p.red + pokerPlayer.name);
            }
            player.sendMessage(p.pluginTag + p.red + "RED = offline");
        }

        if (type.equalsIgnoreCase("other") || type.equalsIgnoreCase("general"))
        {
            player.sendMessage(p.pluginTag + "General Details:");
            player.sendMessage(p.pluginTag + p.gold + p.lineString);
            player.sendMessage(p.pluginTag + "Owner: " + p.gold + owner.name);
            player.sendMessage(p.pluginTag + "Hands played: " + p.gold + handNumber);
            player.sendMessage(p.pluginTag + "Open: " + p.gold + open);
            player.sendMessage(p.pluginTag + "In progress: " + p.gold + inProgress);
            player.sendMessage(p.pluginTag + "Location: " + p.gold + "X: " + p.white + Math.round(location.getX()) + p.gold + " Z: " + p.white + Math.round(location.getZ()) + p.gold + " Y: " + p.white + Math.round(location.getY()) + p.gold + " World: " + p.white + location.getWorld().getName());
        }
        
        if (type.equalsIgnoreCase("all"))
            displayAllDetails(player);
    }

    // Method to eliminate players with 0 money, return true if more than 2 non eliminated players are left, false if otherwise
    public boolean eliminatePlayers()
    {
        //Go through all players, if the player's money is 0, eliminate them and remove them from the player list
        for (PokerPlayer player : players)
        {
            if (player.money == 0)
            {
                player.eliminate();
                if (player.owner == false)
                    p.methodsMisc.removePlayer(player);
            }
        }
        //If there are not enough players to continue the hand (less than 2 non eliminated players are left)
        if (getEliminatedPlayers().size() > players.size() - 2) 
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.red + "Less than 2 non-eliminated players left, stopping table!");
            return false;
        }
        
        if (players.size() >= 23)
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.red + "A poker game of 23+ players!? Are you nuts!? Stopping table!");
            return false;
        }

        return true;
    }

    //Deals the flop
    public void flop()
    {
        currentPhase = 1;
        currentBet = 0;
        clearBets();
        Card[] cards = deck.generateCards(3);
        board.add(cards[0]);
        board.add(cards[1]);
        board.add(cards[2]);
        displayBoard(null);                     //Specifying null in the argument displays the board to everyone
        nextPersonTurn(players.get(button));   //Take the action from the player AFTER the button (that would be the small blind)
    }

    //Returns a list of players that are all in
    public List<PokerPlayer> getAllInPlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players) //Go through all players, if their money is 0, add them to the eventually returned value
        {
            if (player.money == 0) returnValue.add(player);
        }
        return returnValue;
    }

    //Returns a list of players that are eliminated
    public List<PokerPlayer> getEliminatedPlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players) //Go through all players, if their eliminated flag is true, add them to the eventually returned value
        {
            if (player.eliminated == true) returnValue.add(player);
        }
        return returnValue;
    }

    //Find the first empty ID that is closest to 0
    public int getEmptyID()
    {
        int newID = 0;
        try
        {
            //Try to get the first player. If that player exists, increment the newID variable
            while (players.get(newID).id == newID)
                newID++;
        } catch (Exception e) //As soon as you try to get a player that return null (doesnt exist), this means that that ID is free. Therefore, return it
        {
            return newID;
        }
        return newID; //This doesnt actually do anything but is required so the compiler doesnt complain
    }

    // Method to get the player 1 after the index specified, and loop back to the beginning if the end is reached
    public PokerPlayer getNextPlayer(int index)
    {
        if (index + 1 >= players.size()) return players.get((index + 1) % players.size()); //If adding 1 to the index makes it go over the amount of players, divide index + 1 by the size of players.
                                                                                           //This effectively returns the player with the index of how much you went over the size of the list of players.
                                                                                           //For example, if the size of players is 3 (last element is therefore index 2), giving 5 as the index returns the player with the index of 2.
        else return players.get(index + 1); //If the end of the players is not reached simply return the player 1 after the given index
    }

    //Returns a list of non folded players sitting at the table
    public List<PokerPlayer> getNonFoldedPlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players) //Go through all players, if their folded flag is true, add them to the eventually returned value
        {
            if (player.folded == false) returnValue.add(player);
        }
        return returnValue;
    }

    //Returns a list of online players sitting at the table
    public List<PokerPlayer> getOnlinePlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players) //Go through all players, if their online flag is true, add them to the eventually returned value
        {
            if (player.online == true) returnValue.add(player);
        }
        return returnValue;
    }

    //Simple check to see if the specified detail type is a valid type.
    public boolean isADetail(String type)
    {
        List<String> allowedTypes = new ArrayList<String>();
        allowedTypes.add("settings");
        allowedTypes.add("players");
        allowedTypes.add("other");
        allowedTypes.add("general");
        allowedTypes.add("all");

        if (allowedTypes.contains(type)) return true;
        else return false;
    }

    //Returns an array of strings, which represent each pot on this table
    public String[] listPots()
    {
        String[] returnValue = new String[pots.size()];
        int i = 0;
        for (Pot pot : pots)
        {
            returnValue[i] = p.pluginTag + pot.toString();
            i++;
        }
        return returnValue;
    }

    //Lists the settings of the table, returning a string array
    public String[] listSettings()
    {
        String[] returnValue = new String[9];
        returnValue[0] = p.pluginTag + "Elimination mode: " + p.gold + elimination;
        returnValue[1] = p.pluginTag + "Minimum buy-in: " + p.gold + p.methodsMisc.formatMoney(minBuy);
        returnValue[2] = p.pluginTag + "Maximum buy-in: " + p.gold + p.methodsMisc.formatMoney(maxBuy);
        returnValue[3] = p.pluginTag + "Small blind: " + p.gold + p.methodsMisc.formatMoney(sb);
        returnValue[4] = p.pluginTag + "Big blind: " + p.gold + p.methodsMisc.formatMoney(bb);
        returnValue[5] = p.pluginTag + "Ante: " + p.gold + p.methodsMisc.formatMoney(ante);
        if (dynamicFrequency > 0) returnValue[6] = p.pluginTag + "Dynamic frequency: " + p.gold + "every " + dynamicFrequency + " hands.";
        else returnValue[7] = p.pluginTag + "Dynamic frequency is turned " + p.gold + "OFF";
        returnValue[8] = p.pluginTag + "Rake percentage: " + p.gold + p.methodsMisc.convertToPercentage(rake);
        return returnValue;
    }

    //Moves the button to the next player (call this when starting a new hand)
    public void moveButton()
    {
        // If the button is not the last player in the list, increment the button. Otherwise set button to 0.
        if (++button >= players.size())
            button = 0;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Button moved to " + p.gold + players.get(button).player.getName());
    }

    //Move the action to the player after the one specified in the argument
    public void nextPersonTurn(PokerPlayer pokerPlayer)
    {   
        if (currentPhase != 4)
        {
            List<PokerPlayer> contributed = new ArrayList<PokerPlayer>(); // List to hold all the players that have contributed the required amount
            List<PokerPlayer> acted = new ArrayList<PokerPlayer>();       // List to hold all the players that have acted
            
            // Go through all players that have not folded
            for (PokerPlayer nonFolded : getNonFoldedPlayers())
            {
                // And add the person to contributed list if they have contributed the right amount, or if they are all in
                if (nonFolded.currentBet == currentBet || nonFolded.money == 0) contributed.add(nonFolded);
            }
            
            //Go through all players and add them to the acted list or if they are all in
            for (PokerPlayer player : getNonFoldedPlayers())
                if (player.acted == true || player.money == 0) acted.add(player);
            
            if (getNonFoldedPlayers().size() == 1) { winner(getNonFoldedPlayers().get(0)); return; } // If there is only 1 non-folded player left, announce him the winner
    
            //If there is only 1 player left that isn't all in, and everyone has contributed the right amount, go to showdown
            if (players.size() == getAllInPlayers().size() + 1 && contributed.size() - 1 == getAllInPlayers().size())
            {
                showdown();
                return; //If you go to showdown dont do anything else by quitting the method
            }            
    
            // If every non-folded player has contributed the right amount
            if (contributed.size() == getNonFoldedPlayers().size())
            {
                //If it's preflop
                if (currentPhase == 0)
                {
                    actionPlayer = getNextPlayer(button + 1);     //Set the action player to the big blind
                    if (actionPlayer.blind && actionPlayer.acted) //If they are the big blind and they have acted
                    {
                        nextPhase(); //Go to the next phase if the big blind and everyone else have acted
                        return;
                    } else //If the big blind hasn't acted, then take his action and quit the method
                    {
                        actionPlayer = getNextPlayer(button + 1);
                        actionPlayer.takeAction();
                        return;
                    }
                } else //If it's not preflop
                {
                    if (acted.size() == getNonFoldedPlayers().size()) 
                    {
                        nextPhase(); //If every non folded player has acted, go to the next phase
                        return;
                    }
                }
            }
            
            //If its not time to go to the next phase, simply keep trying to choose a player that has not folded and still has money. Then take his/her action
            actionPlayer = getNextPlayer(players.indexOf(pokerPlayer));
            while (actionPlayer.folded == true || actionPlayer.money == 0)
            {
                actionPlayer = getNextPlayer(players.indexOf(actionPlayer));
                if (actionPlayer.folded == false && actionPlayer.money > 0) break;
            }
            actionPlayer.takeAction();
        } else
        {
            List<PokerPlayer> revealed = new ArrayList<PokerPlayer>();
            for (PokerPlayer player : getNonFoldedPlayers())
                if (player.revealed == true) revealed.add(player);
            
            if (revealed.size() == getNonFoldedPlayers().size())
            {
                nextPhase();
                return;
            }
            
            actionPlayer = getNextPlayer(players.indexOf(pokerPlayer));
            while (actionPlayer.revealed == true || actionPlayer.folded == true)
            {
                actionPlayer = getNextPlayer(players.indexOf(actionPlayer));
                if (actionPlayer.revealed == false && actionPlayer.folded == false) break;
            }
            actionPlayer.takeAction();
        }
    }

    //Go to the next phase depending on what the current phase is
    public void nextPhase()
    {
        if (currentPhase == 0)
        {
            flop();
            return;
        }
        if (currentPhase == 1)
        {
            turn();
            return;
        }
        if (currentPhase == 2)
        {
            river();
            return;
        }
        if (currentPhase == 3)
        {
            showdown();
            return;
        }
        if (currentPhase == 4)
        {
            handEnd();
            return;
        }
    }

    //Post the blinds for every player on the table
    public void postBlinds()
    {
        // Post antes if there is one
        if (ante > 0)
        {
            for (PokerPlayer player : players)
            {
                player.postBlind("ante");
            }
        }
        getNextPlayer(button).postBlind("small");   //Find the player 1 after the button and post his small blind
        getNextPlayer(button + 1).postBlind("big"); //Find the player 2 after the button and post his big blind
        getNextPlayer(button + 1).blind = true;     //Set that player's big blind flag to true

    }

    //Deals the preflop
    public void preflop()
    {
        currentPhase = 0;
        currentBet = 0;
        postBlinds();
        nextPersonTurn(getNextPlayer(button + 1));
    }

    //Raise the blinds if the dynamic frequency is set
    public void raiseBlinds()
    {
        //If the current hand number is a multiple of the dynamic ante frequency, and dynamic ante frequency is turned on, increase the blinds/ante by what it was set to most recently
        if (dynamicFrequency > 0)
        {
            if (handNumber % dynamicFrequency == 0 && handNumber != 0)
            {
                ante = ante + originalAnte;
                bb = bb + originalBB;
                sb = sb + originalSB;
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "New ante: " + p.gold + ante + p.white + ". New BB: " + p.gold + bb + p.white + ". New SB: " + p.gold + sb + p.white + ".");
            }
        }
        if (minRaiseIsAlwaysBB) minRaise = bb; //If the min raise is always the big blind, then also change that value.
    }

    //Deals the river
    public void river()
    {
        currentPhase = 3;
        currentBet = 0;
        clearBets();
        board.add(deck.generateCards(1)[0]);
        displayBoard(null);                   //Null in the argument makes it display the board to everyone
        nextPersonTurn(players.get(button)); //Get the action of the player AFTER the button (the small blind)
    }

    //Method to set a boolean setting
    public void setBooleanValue(Player player, String setting, String v)
    {
        boolean value;
        if (v.equalsIgnoreCase("true")) value = true;
            else if (v.equalsIgnoreCase("false")) value = false;
                else { player.sendMessage(p.pluginTag + p.gold + v + p.red + " is an invalid value! Please specify " + p.gold + "true " + p.red + "or" + p.gold + " false " + p.red + "only."); return; }
        
        if (setting.equalsIgnoreCase("elimination"))
        {
            elimination = value;
            if (value == true)
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set elimination to " + p.gold + v + p.white + "! Players cannot rebuy after losing their stack!");
            else p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set elimination to " + p.gold + v + p.white + "! Players can now rebuy!");
            return;
        }
        if (setting.equalsIgnoreCase("minRaiseIsAlwaysBB"))
        {
            minRaiseIsAlwaysBB = value;
            if (value == true)
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has made the " + p.gold + "Minimum Raise" + p.white + " be always equal to the Big Blind!");
            else p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has made the " + p.gold + "Minimum Raise" + p.white + " no longer be equal to the Big Blind!");
            return;
        }
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set " + p.gold + setting + p.white + " to " + p.gold + v);
    }

    // Method to set any of the number settings
    public void setNumberValue(Player player, String setting, String v)
    {
        if (p.methodsCheck.isDouble(v) == true)
        {
            double value = Double.parseDouble(v);

            if (setting.equalsIgnoreCase("minBuy"))
            {
                minBuy = value;
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Minimum Buy-In" + p.white + " to " + p.gold + p.methodsMisc.formatMoney(value));
            }

            if (setting.equalsIgnoreCase("maxBuy"))
            {
                maxBuy = value;
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Maximum Buy-In" + p.white + " to " + p.gold + p.methodsMisc.formatMoney(value));
            }

            if (setting.equalsIgnoreCase("sb"))
            {
                sb = value;
                originalSB = sb;
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Small Blind" + p.white + " to " + p.gold + p.methodsMisc.formatMoney(value));
            }

            if (setting.equalsIgnoreCase("bb"))
            {
                bb = value;
                originalSB = bb;
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Big Blind" + p.white + " to " + p.gold + p.methodsMisc.formatMoney(value));
            }

            if (setting.equalsIgnoreCase("ante"))
            {
                ante = value;
                originalAnte = ante;
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Ante" + p.white + " to " + p.gold + p.methodsMisc.formatMoney(value));
            }

            if (setting.equalsIgnoreCase("minRaise"))
            {
                if (minRaiseIsAlwaysBB == false)
                {
                    minRaise = value;
                    p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Minimum Raise" + p.white + " to " + p.gold + p.methodsMisc.formatMoney(value));
                } else player.sendMessage(p.pluginTag + p.red + "This table's minimum raise is currently set to always be equal to the big blind! Change this with " + p.gold + "/table set minRaiseIsAlwaysBB.");
            }

            if (setting.equalsIgnoreCase("rake"))
            {
                if (p.getConfig().getBoolean("table.allowRake") == true)
                {
                    if (value <= 1 && value >= 0)
                    {
                        rake = value;
                        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "rake" + p.white + " to " + p.gold + p.methodsMisc.convertToPercentage(value));
                        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " will now receive " + p.gold + p.methodsMisc.convertToPercentage(value) + p.white + " of each pot to their own pocket!");
                    } else player.sendMessage(p.pluginTag + p.red + "Please choose a number from 0 to 1. Example: 0.05 = 5% rake.");
                } else player.sendMessage(p.pluginTag + p.red + "The configuration of the plugin doesn't allow the rake. Sorry!");
            }

            if (setting.equalsIgnoreCase("dynamicFrequency"))
            {
                // Only allow the player to set the dynamic frequency if the blinds increased on the current hand, or the table is not currently in progress
                if (inProgress == false || handNumber % dynamicFrequency == 0)
                {
                    if (p.methodsCheck.isInteger(v))
                    {
                        dynamicFrequency = (int) value;
                        if (dynamicFrequency > 0)
                            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has set the " + p.gold + "Dynamic Frequency " + p.white + "to " + p.gold + "'Every " + v + " hands'");
                        else p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + player.getName() + p.white + " has truned the " + p.gold + "Dynamic Frequency " + p.white + p.gold + "off.");
                    } else p.methodsError.notANumber(player, v);
                } else player.sendMessage(p.pluginTag + p.red + "You may only set the dynamic frequency during a hand where the blinds increased, or if the table is not in progress.");
            }
        } else p.methodsError.notANumber(player, v);
    }

    //Showdown method
    public void showdown()
    {
        currentPhase = 4;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Showdown time!");

        if (board.size() != 5) //If somehow the board doesnt have 5 cards (an all in made the hand end early, for example)
        {
            //Generate required cards
            Card[] cards = deck.generateCards(5 - board.size());
            for (Card card : cards)
                board.add(card);
        }

        displayBoard(null);
        
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Use " + p.gold + "/hand reveal" + p.white + " to reveal your hand, or " + p.gold + "/hand muck" + " to muck.");
        nextPersonTurn(players.get(button)); //Get the action of the player AFTER the button (the small blind)
    }
    
    //This is called once everyone has revealed their hand
    public void handEnd()
    {
        currentPhase = 5;
        
        //If there is only 1 pot, display this specific message.
        if (pots.size() == 1) { p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Pot: " + p.gold + p.methodsMisc.formatMoney(pots.get(0).pot)); p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table owner: Please use " + p.gold + "/table pay [player ID]" + p.white + " to pay the winner."); }
        else //If there are side pots, list them all with a different message at the end
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "List of pots:");
            p.methodsMisc.sendToAllWithinRange(location, listPots());
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table owner: Please use " + p.gold + "/table pay [pot ID] [player ID]" + p.white + " to pay the winner(s). You can now also modify settings of the table.");
        }
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Players: use " + p.gold + "/hand rebuy [amount]" + p.white + " to add more money to your stacks.");
        inProgress = false;
    }

    //Deal the turn
    public void turn()
    {
        currentPhase = 2;
        currentBet = 0;
        clearBets();
        board.add(deck.generateCards(1)[0]);
        displayBoard(null);                   //Specifying null displays the board to everyone
        nextPersonTurn(players.get(button));
    }

    // Method used to pay a winner if everyone else has folded
    public void winner(PokerPlayer player)
    {
        inProgress = false;
        toBeContinued = true;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Everybody except " + p.gold + player.player.getName() + p.white + " folded!");
        for (Pot pot : pots)   //Pay all pots to the winner
            pot.payPot(player);
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table owner: use " + p.gold + "/table continue" + p.white + " to deal a new hand.");
    }

    //Adds the specified player to the ban list, and sends a message
    public void ban(String toBan)
    {
        banned.add(toBan);
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + owner.name + p.white + " has banned " + p.gold + toBan + p.white + " from the table!");
    }

    //Closes the table and doesn't allow any more people to sit
    public void close()
    {
        open = false;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table named '" + p.gold + name + p.white + "', ID #" + p.gold + id + p.white + " is now closed! Players now can't join!");
    }

    public void open()
    {
        open = true;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table named '" + p.gold + name + p.white + "', ID #" + p.gold + id + p.white + " is now open! Players can now join!");
    }

    public void start()
    {
        owner.sendMessage(p.pluginTag + "You have started the game at table '" + p.gold + name + p.white + "', ID #" + p.gold + id + p.white + ".");
        deal();
    }

    public void unBan(String toUnBan)
    {
        banned.remove(toUnBan);
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + owner.name + p.white + " has unbanned " + p.gold + toUnBan + p.white + " from the table!");
    }

    public void kick(PokerPlayer pokerPlayer)
    {
        pokerPlayer.player.teleport(pokerPlayer.startLocation);
        pokerPlayer.sendMessage(p.pluginTag + p.gold + owner.name + p.red + " has kicked you from his/her poker table! You receive your remaining stack of " + p.gold + p.methodsMisc.formatMoney(pokerPlayer.money));
        p.economy.depositPlayer(pokerPlayer.name, pokerPlayer.money);
        p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + pokerPlayer.money + " to " + pokerPlayer.name);
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + owner.name + p.white + " has kicked " + p.gold + pokerPlayer.player.getName() + p.white + " from the table!");
        p.methodsMisc.removePlayer(pokerPlayer);
        if (pokerPlayer.table.getOnlinePlayers().size() == pokerPlayer.table.players.size() && pokerPlayer.table.stopped == true)
        {
            p.methodsMisc.sendToAllWithinRange(pokerPlayer.table.location, p.pluginTag + "All players online again, resuming the table!");
            pokerPlayer.table.stopped = false;
        }
    }

    public void leave(PokerPlayer player)
    {
        player.player.teleport(player.startLocation);
        p.economy.depositPlayer(player.name, player.money);
        p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + player.money + " to " + player.name);
        player.sendMessage(p.pluginTag + "You have left table '" + p.gold + name + p.white + "', ID #" + p.gold + id + p.white + ", and received " + p.gold + p.methodsMisc.formatMoney(player.money) + p.white + ".");
        p.methodsMisc.removePlayer(player);
    }

    //This method makes sure that every player ID is equal to their index in the player list. This should be called whenever a player is removed.
    public void shiftIDs()
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).id != i) players.get(i).id = i;
        }
    }

    //Gets the highest balance of all players, excludes the supplied argument
    public double getHighestBalance(PokerPlayer exclude)
    {
        double highestBalance = 0;
        for (PokerPlayer player : players)
        {
            if (player.money > highestBalance && player != exclude) highestBalance = player.money;
        }
        return highestBalance;
    }
}
