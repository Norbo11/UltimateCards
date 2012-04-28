package com.github.Norbo11.classes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;

public class Table
{

    UltimatePoker p;

    // Generic vars
    public Player owner;
    public String name;
    public int id;
    public Location location;
    public boolean inProgress;

    public boolean open = false;

    // Settings
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
    public double dynamicAnteFreq;

    // Bet related stuff
    public double pot;
    public double currentBet;
    public PokerPlayer actionPlayer;
    public int handNumber;
    public int button;

    // Important shit
    public List<PokerPlayer> players = new ArrayList<PokerPlayer>();
    public List<Card> board = new ArrayList<Card>();
    public Deck deck;
    public int currentPhase;
    public double rake;

    public Table(Player owner, String name, int id, Location location, UltimatePoker p)
    {
        this.p = p;

        this.owner = owner;
        this.name = name;
        this.id = id;

        open = false;
        handNumber = 0;
        button = 0;

        elimination = p.getConfig().getBoolean("table.defaults.elimination");
        minBuy = p.getConfig().getDouble("table.defaults.minBuy");
        maxBuy = p.getConfig().getDouble("table.defaults.maxBuy");
        sb = p.getConfig().getDouble("table.defaults.sb");
        bb = p.getConfig().getDouble("table.defaults.bb");
        ante = p.getConfig().getDouble("table.defaults.ante");
        dynamicAnteFreq = p.getConfig().getDouble("table.defaults.dynamicAnteFreq");
        minRaise = p.getConfig().getDouble("table.defaults.minRaise");
        minRaiseIsAlwaysBB = p.getConfig().getBoolean("table.defaults.minRaiseIsAlwaysBB");

        if (p.getConfig().getBoolean("table.allowRake") == true)
        {
            rake = p.getConfig().getDouble("table.defaults.rake");
        } else
        {
            rake = 0;
        }

        originalSB = sb;
        originalBB = bb;
        originalAnte = ante;

        this.location = location;
        deck = new Deck(p);

        inProgress = false;
        actionPlayer = null;
    }

    public PokerPlayer getNextPlayer(int index)
    {
        if (index + 1 >= players.size()) return players.get(0);
        return players.get(index + 1);
    }
    
    public void deal()
    {
        handNumber++;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Dealing hand number " + p.gold + handNumber);
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
        inProgress = true;
        deck.shuffle();
        board.clear();
        pot = 0;
        
        //If the button is not the last player in the list, increment the button. Otherwise set button to 0.
        if (++button >= players.size()) { button = 0; }
        
        actionPlayer = getNextPlayer(button);
        
        for (PokerPlayer pokerPlayer : players)
        {
            pokerPlayer.clearHand();
            pokerPlayer.folded = false;
            pokerPlayer.addCards(deck.generateCards(2));
        }
        preflop();
    }

    public void displayBoard(Player who)
    {
        if (who == null)
        {
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Community Cards: ");
            for (Card card : board)
            {
                p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + card.toString());
            }
        } else
        {
            who.sendMessage(p.pluginTag + "Community Cards: ");
            for (Card card : board)
            {
                who.sendMessage(p.pluginTag + card.toString());
            }
        }
    }

    public void flop()
    {
        currentPhase = 1;
        currentBet = 0;
        Card[] cards = deck.generateCards(3);
        board.add(cards[0]);
        board.add(cards[1]);
        board.add(cards[2]);
        displayBoard(null);
        actionPlayer.takeAction();
    }

    public void nextPersonTurn(PokerPlayer pokerPlayer)
    {
        //Set the action player to the next player after the current action player
        actionPlayer = getNextPlayer(players.indexOf(actionPlayer));
        //If that player is the starting player (one after the button), or is the player who has just had his turn
        if (actionPlayer == getNextPlayer(button) || actionPlayer == pokerPlayer)
        //Go to show down
        nextPhase();
        //If the player is a valid next player then take his action.
        else actionPlayer.takeAction();
    }

    public void nextPhase()
    {
        actionPlayer = players.get(0);
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

    public void preflop()
    {
        currentPhase = 0;
        currentBet = 0;
        actionPlayer.takeAction();
    }

    public void river()
    {
        currentPhase = 3;
        currentBet = 0;
        Card[] cards = deck.generateCards(1);
        board.add(cards[0]);
        displayBoard(null);
        actionPlayer.takeAction();
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
            player.sendMessage(p.pluginTag + p.red + "'" + value + "' is an invalid value! Please specify 'true' or 'false' only.");
        }
    }

    public void setInProgress(boolean state)
    {
        if (state = true)
        {
            inProgress = true;
            deal();
        } else
        {
            inProgress = false;
        }
    }

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

            if (setting.equalsIgnoreCase("setDynamicAnteFreq"))
            {
                dynamicAnteFreq = value;
                player.sendMessage(p.pluginTag + "The ante + blinds will now raise by themselves every " + p.gold + value + " hands.");
            } 
        } else p.methodsError.notANumber(player, v);
    }

    public void showdown()
    {
        currentPhase = 4;
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Showdown time!");
        for (PokerPlayer player : players)
        {
            if (player.folded == false)
            player.revealHand();
            p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + p.gold + p.lineString);
        }
        p.methodsMisc.sendToAllWithinRange(location, p.pluginTag + "Table owner: Please use " + p.gold + "/table pay [player ID]" + p.white + " to pay the winner.");
        inProgress = false;
    }

    public void turn()
    {
        currentPhase = 2;
        currentBet = 0;
        Card[] cards = deck.generateCards(1);
        board.add(cards[0]);
        displayBoard(null);
        actionPlayer.takeAction();
    }

    public double requiredContribution()
    {
        return pot / players.size();
    }
}
