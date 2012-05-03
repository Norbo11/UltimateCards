package com.github.Norbo11.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class Table
{
    public Table(Player owner, String name, int id, Location location, UltimatePoker p)
    {
        //Set the table core properties
        this.p = p;
        this.owner = owner;
        this.name = name;
        this.id = id;
        this.location = location;

        //Set all the required settings
        open = false;
        handNumber = 0;
        button = 0;
        deck = new Deck(p);
        inProgress = false;
        stopped = false;
        actionPlayer = null;
        pots.add(new Pot(null, this, 0, 0, p));
        pots.get(0).main = true;
        latestPot = pots.get(0);
        allPotsPaid = true;

        //Fetch defaults from config
        elimination = p.getConfig().getBoolean("table.defaults.elimination");
        minBuy = p.getConfig().getDouble("table.defaults.minBuy");
        maxBuy = p.getConfig().getDouble("table.defaults.maxBuy");
        sb = p.getConfig().getDouble("table.defaults.sb");
        bb = p.getConfig().getDouble("table.defaults.bb");
        ante = p.getConfig().getDouble("table.defaults.ante");
        dynamicFrequency = p.getConfig().getDouble("table.defaults.dynamicFrequency");
        minRaiseIsAlwaysBB = p.getConfig().getBoolean("table.defaults.minRaiseIsAlwaysBB");
        
        //If the min raise is always BB, set it to the BB. If not, make it 0;
        if (minRaiseIsAlwaysBB == false) minRaise = p.getConfig().getDouble("table.defaults.minRaise");
        else minRaise = bb;

        //If the rake is allowed, set it. If not, make it 0.
        if (p.getConfig().getBoolean("table.allowRake") == true) rake = p.getConfig().getDouble("table.defaults.rake");
        else rake = 0;

        //Set originals, required for dynamic antes/blinds
        originalSB = sb;
        originalBB = bb;
        originalAnte = ante;
        
    }
    //Generic vars set by constructor
    UltimatePoker p;
    public Player owner;
    public String name;
    public int id;
    
    public Location location;
    //Generic vars
    public boolean inProgress;
    public boolean open;
    public boolean stopped;
    public boolean allPotsPaid;
    public double currentBet;
    public int handNumber;
    public int button;
    public int currentPhase;
    public Pot latestPot;
    public List<PokerPlayer> players = new ArrayList<PokerPlayer>();
    public List<String> banned = new ArrayList<String>();
    public List<Card> board = new ArrayList<Card>();
    public List<Pot> pots = new ArrayList<Pot>();
    public Deck deck;

    public PokerPlayer actionPlayer;
    //Settings
    public boolean minRaiseIsAlwaysBB;
    public boolean elimination;
    public double minBuy;
    public double maxBuy;
    public double minRaise;
    public double originalSB;
    public double originalBB;
    public double originalAnte;
    public double sb;
    public double bb;
    public double ante;
    public double dynamicFrequency;
    
    public double rake;

    public void clearBets()
    {
        for (PokerPlayer player : players)
        {
            player.currentBet = 0;
            player.acted = false;
            player.pot = null;
        }
    }
    
    public void clearPots()
    {
        pots.clear();
        pots.add(new Pot(null, this, 0, 0, p));
        pots.get(0).main = true;
        latestPot = pots.get(0);
        allPotsPaid = true;
    }

    //Method to deal a brand new hand
    public void deal()
    {
        handNumber++;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Dealing hand number " + p.gold + handNumber);
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
        inProgress = true;
        deck.shuffle();
        board.clear();
        clearPots();
        clearBets();
        if (eliminatePlayers() == true) 
        {
            raiseBlinds();
            moveButton();
            dealCards();
            preflop();
        } else 
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Less than 2 non-eliminated players left, stopping table!");
            inProgress = false;
        }
    }
    
    //Deal cards and clear variables for non-eliminated players
    public void dealCards()
    {
        //Go through all players, clear their hands and add their cards
        for (PokerPlayer pokerPlayer : players)
        {
            if (pokerPlayer.eliminated == false)
            {
                pokerPlayer.clearHand();
                pokerPlayer.folded = false;
                pokerPlayer.action = false;
                pokerPlayer.acted = false;
                pokerPlayer.totalBet = 0;
                pokerPlayer.addCards(deck.generateCards(2));
            }
        }
    }
    
    //Method to display the board. If who is null, display to everyone around the table. Otherwise, display to just the player specified in "who".
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
    
    //Method to eliminate players with 0 money, return true if more than 2 non eliminated players are left, false if otherwise
    public boolean eliminatePlayers()
    {
        for (PokerPlayer player : players)
        {
            if (player.money == 0)
            {
                player.eliminate();
                players.remove(player);
            }
        }
        if (getEliminatedPlayers().size() > players.size() - 2)
        return false;
        
        return true;
    }

    public void flop()
    {
        currentPhase = 1;
        currentBet = 0;
        clearBets();
        Card[] cards = deck.generateCards(3);
        board.add(cards[0]);
        board.add(cards[1]);
        board.add(cards[2]);
        displayBoard(null);
        nextPersonTurn(players.get(button));
    }
    
    public List<PokerPlayer> getAllInPlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players)
        {
            if (player.money == 0)
            returnValue.add(player);
        }
        return returnValue;
    }
    
    //Method to get the player 1 after the index specified, and loop back to the beginning if the end is reached
    public PokerPlayer getNextPlayer(int index)
    {
        if (index + 1 >= players.size()) return players.get((index + 1) % players.size());
        else return players.get(index+1);
    }

    public List<PokerPlayer> getEliminatedPlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players)
        {
            if (player.eliminated == true)
            returnValue.add(player);
        }
        return returnValue;
    }
    
    public List<PokerPlayer> getNonFoldedPlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players)
        {
            if (player.folded == false) returnValue.add(player);
        }
        return returnValue;
    }
    
    public List<PokerPlayer> getOnlinePlayers()
    {
        List<PokerPlayer> returnValue = new ArrayList<PokerPlayer>();
        for (PokerPlayer player : players)
        {
            if (player.online == true)
            returnValue.add(player);
        }
        return returnValue;
    }

    public String[] listPlayers()
    {
        String[] returnValue = new String[players.size()+1];
        int i = 0;
        for (PokerPlayer player : players)
        {
            if (player.online == true)
            returnValue[i] = p.pluginTag + "[" + player.id + "] " + p.gold + player.name;
            else returnValue[i] = p.pluginTag + "[" + player.id + "] " + p.red + player.name;
            i++;
        }
        returnValue[i] = p.pluginTag + p.red + "RED = offline";
        return returnValue;
    }

    public String[] listPots()
    {
        String[] returnValue = new String[pots.size()]; 
        int i = 0;
        for (Pot pot : pots)
        {
            returnValue[i] = p.pluginTag + pot.convertToString();
            i++;
        }
        return returnValue;
    }
    
    public void moveButton()
    {
        //If the button is not the last player in the list, increment the button. Otherwise set button to 0.
        if (++button >= players.size()) { button = 0; }
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Button moved to " + p.gold + players.get(button).player.getName());
    }
    
    public void nextPersonTurn(PokerPlayer pokerPlayer)
    {
        if (players.size() == getAllInPlayers().size() + 1)
        {
            showdown();
            return;
        }
        if (getNonFoldedPlayers().size() == 1) winner(getNonFoldedPlayers().get(0));    //If there is only 1 player left, announce him the winner
        
        List<PokerPlayer> contributed = new ArrayList<PokerPlayer>();                   //List to hold all the players that have contributed the required amount
        List<PokerPlayer> acted = new ArrayList<PokerPlayer>();
        
        //Go through all players that have not folded
        for (PokerPlayer nonFolded : getNonFoldedPlayers())
        {
            //And add the person to contributed list if they have contributed the right amount
            if (nonFolded.currentBet == currentBet || nonFolded.money == 0) contributed.add(nonFolded);
        }
        
        //If every non-folded player has contributed the right amount
        if (contributed.size() == getNonFoldedPlayers().size())
        {
            if (currentPhase == 0)
            {
                actionPlayer = getNextPlayer(button + 1);
                if (actionPlayer.blind && actionPlayer.acted)
                {
                    nextPhase();
                    return;
                }
                else 
                {
                    actionPlayer = getNextPlayer(button + 1);
                    actionPlayer.takeAction();
                    return;
                }
            }
            else
            {
                for (PokerPlayer player : getNonFoldedPlayers())
                {
                    if (player.acted == true || player.money == 0) acted.add(player);
                }
                if (acted.size() == getNonFoldedPlayers().size())
                {
                    nextPhase();
                    return;
                }
            }
        }
        actionPlayer = getNextPlayer(players.indexOf(pokerPlayer));
        while (actionPlayer.folded == true || actionPlayer.money == 0 || actionPlayer != pokerPlayer)
        {
            actionPlayer = getNextPlayer(players.indexOf(actionPlayer));
            if (actionPlayer.folded == false && actionPlayer.money > 0 && actionPlayer != pokerPlayer) break;
        }
        actionPlayer.takeAction();
    }

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
    }

    public void postBlinds()
    {
        //Post antes
        if (ante > 0)
        {
            for (PokerPlayer player : players)
            {
                player.postBlind("ante");
            }
        }
        getNextPlayer(button).postBlind("small");  //Find the player 1 after the button and post his small blind
        getNextPlayer(button+1).postBlind("big");  //Find the player 2 after the button and post his big blind
        getNextPlayer(button+1).blind = true;

    }

    public void preflop()
    {
        currentPhase = 0;
        currentBet = 0;
        postBlinds();
        p.log.info("Button: " + button);
        nextPersonTurn(players.get(button + 2));
    }

    public void raiseBlinds()
    {
        //If the current hand number is a multiple of the dynamic ante frequency, and dynamic ante frequency is turned on, increase the blinds/ante
        if (handNumber % dynamicFrequency == 0 && dynamicFrequency > 0 && handNumber != 0)
        {
            ante = ante + originalAnte;
            bb = bb + originalBB;
            sb = sb + originalSB;
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "New ante: " + p.gold + ante + p.white + ". New BB: " + p.gold + bb + p.white + ". New SB: " + p.gold + sb + p.white + ".");
        }
    }
    
    public void resumeTable()
    {
        stopped = false;
    }
    
    public void river()
    {
        currentPhase = 3;
        currentBet = 0;
        clearBets();
        board.add(deck.generateCards(1)[0]);
        displayBoard(null);
        nextPersonTurn(players.get(button));
    }

    public void setElimination(Player player, String value)
    {
        if (value.equalsIgnoreCase("true"))
        {
            elimination = true;
            player.sendMessage(p.pluginTag + "Your table is now in elimination mode! Players are unable to re-buy.");
        } else if (value.equalsIgnoreCase("false"))
        {
            elimination = false;
            player.sendMessage(p.pluginTag + "Your table is now in normal (non-elimination) mode. Players are now able to re-buy.");
        } else
        {
            player.sendMessage(p.pluginTag + p.gold + value + p.red + " is an invalid value! Please specify " + p.gold + "true " + p.red + "or" + p.gold + " false " + p.red + "only.");
        }
    }

    //Method to set any of the number settings
    public void setNumberValue(Player player, String setting, String v)
    {
        if (p.methodsCheck.isDouble(v) == true)
        {
            double value = Double.parseDouble(v);

            if (setting.equalsIgnoreCase("minBuy"))
            {
                minBuy = value;
                player.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
            }

            if (setting.equalsIgnoreCase("maxBuy"))
            {
                maxBuy = value;
                player.sendMessage(p.pluginTag + "Maximum buy-in for table set to " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
            }

            if (setting.equalsIgnoreCase("sb"))
            {
                sb = value;
                originalSB = sb;
                player.sendMessage(p.pluginTag + "Small blind for table set to " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
            }

            if (setting.equalsIgnoreCase("bb"))
            {
                bb = value;
                originalSB = bb;
                player.sendMessage(p.pluginTag + "Big blind for table set to " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
            }

            if (setting.equalsIgnoreCase("ante"))
            {
                ante = value;
                originalAnte = ante;
                player.sendMessage(p.pluginTag + "Ante for table set to " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
            }

            if (setting.equalsIgnoreCase("minRaise"))
            {
                if (p.getConfig().getBoolean("table.minRaiseIsAlwaysBB") == false)
                {
                    minRaise = value;
                    player.sendMessage(p.pluginTag + "Minimum buy-in for table set to " + p.gold + p.methodsMisc.formatMoney(value) + p.white + "!");
                } else player.sendMessage(p.pluginTag + p.red + "Because of the plugin configuration, the minimum raise has to always be equals to the big blind. Sorry!");
            }

            if (setting.equalsIgnoreCase("rake"))
            {
                if (p.getConfig().getBoolean("table.allowRake") == true)
                {
                    if (value <= 1 && value >= 0)
                    {
                        rake = value;
                        player.sendMessage(p.pluginTag + "You will now receive " + p.gold + p.methodsMisc.convertToPercentage(rake) + p.white + " of every pot, for hosting your awesome table!");
                    } else player.sendMessage(p.pluginTag + p.red + "Please choose a number from 0 to 1. Example: 0.05 = 5% rake.");
                } else player.sendMessage(p.pluginTag + p.red + "The configuration of the plugin doesn't allow the rake. Sorry!");
            }

            if (setting.equalsIgnoreCase("dynamicFrequency"))
            {
                //Only allow the player to set the dynamic frequency if the blinds increased on the current hand, or the table is not currently in progress
                if (inProgress == false || handNumber % dynamicFrequency == 0)
                {
                    if (p.methodsCheck.isInteger(v))
                    {
                        dynamicFrequency = value;
                        player.sendMessage(p.pluginTag + "The ante + blinds will now raise by themselves every " + p.gold + v + p.white + " hands.");
                    } else p.methodsError.notANumber(player, v);
                } else player.sendMessage(p.pluginTag + p.red + "You may only set the dynamic frequency during a hand where the blinds increased, or if the table is not in progress.");
            } 
        } else p.methodsError.notANumber(player, v);
    }
    
    public void showdown()
    {
        currentPhase = 4;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Showdown time!");
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
        for (PokerPlayer player : players)
        {
            if (player.folded == false)
            player.revealHand();
        }
        if (board.size() != 5)
        {
            Card[] cards = deck.generateCards(5-board.size());
            for (Card card : cards)
            {
                board.add(card);
            }
        }
        displayBoard(null);
        if (pots.size() == 1)
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table owner: Please use " + p.gold + "/table pay [player ID]" + p.white + " to pay the winner. Players: use " + p.gold + "/hand rebuy [amount]" + p.white + " to add more money to your stacks.");
        else
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "List of pots:");
            p.methodsMisc.sendToAllWithinRange(location, listPots());
        }
        inProgress = false;
    }

    public void stopTable()
    {
        stopped = true;
    }

    public void turn()
    {
        currentPhase = 2;
        currentBet = 0;
        clearBets();
        board.add(deck.generateCards(1)[0]);
        displayBoard(null);
        nextPersonTurn(players.get(button));
    }

    //Method used to pay a winner if everyone else has folded
    public void winner(PokerPlayer player)
    {
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Everybody except " + p.gold + player.player.getName() + p.white + " folded!");
        for (Pot pot : pots)
        pot.payPot(player);
    }

    public int getEmptyID()
    {
        int newID = 0;
        try {
            while (players.get(newID).id == newID)
            {
                newID++;
            }
        } catch (Exception e) { return newID; }
        return newID;
    }

    public void listDetails(Player player)
    {
        player.sendMessage(p.pluginTag + p.gold + "Settings:");
        player.sendMessage(p.pluginTag + p.lineString);
        player.sendMessage(listSettings());
        player.sendMessage(p.pluginTag + p.lineString);
        player.sendMessage(p.pluginTag + p.gold + "Players:");
        player.sendMessage(listPlayers());
        player.sendMessage(p.pluginTag + p.lineString);
        player.sendMessage(p.pluginTag + "Owner: " + p.gold + owner.getName());
        player.sendMessage(p.pluginTag + "Hand number: " + p.gold + handNumber);
        player.sendMessage(p.pluginTag + "Open: " + p.gold + open);
        player.sendMessage(p.pluginTag + "In progress: " + p.gold + inProgress);
        player.sendMessage(p.pluginTag + "Location: " + p.gold + "X: " + p.white + Math.round(location.getX()) + p.gold + " Z: " + p.white + Math.round(location.getZ()) + p.gold + " Y: " + p.white + Math.round(location.getY()) + p.gold + " World: " + p.white + location.getWorld().getName());
    }
    
    public String[] listSettings()
    {
        String[] returnValue = new String[9];
        returnValue[0] = p.pluginTag + "Elimination mode: " + p.gold + elimination;
        returnValue[1] = p.pluginTag + "Minimum buy-in: " + p.gold + p.methodsMisc.formatMoney(minBuy);
        returnValue[2] = p.pluginTag + "Maximum buy-in: " + p.gold + p.methodsMisc.formatMoney(maxBuy);
        returnValue[3] = p.pluginTag + "Small blind: " + p.gold + p.methodsMisc.formatMoney(sb);
        returnValue[4] = p.pluginTag + "Big blind: " + p.gold + p.methodsMisc.formatMoney(bb);
        returnValue[5] = p.pluginTag + "Ante: " + p.gold + p.methodsMisc.formatMoney(ante);
        if (dynamicFrequency > 0)
            returnValue[6] = p.pluginTag + "Dynamic frequency: " + p.gold + "every " + dynamicFrequency + " hands.";
        else
            returnValue[7] = p.pluginTag + "Dynamic frequency is turned " + p.gold + "OFF";
            returnValue[8] = p.pluginTag + "Rake percentage: " + p.gold + p.methodsMisc.convertToPercentage(rake);
        return returnValue;
    }
}
