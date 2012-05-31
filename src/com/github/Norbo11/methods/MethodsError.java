/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsError.java
 * -Contains LOTS of methods which simply display a single message to the user.
 * -These methods are called throughout the whole plugin to show misuse.
 * ===================================================================================================
 */

package com.github.norbo11.methods;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.classes.Pot;

public class MethodsError
{

    public MethodsError(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    private void availableStatisticTypes(Player player)
    {
        List<String> message = new ArrayList<String>();
        message.add(p.PLUGIN_TAG + p.red + "Available statistic types:");
        message.add(p.PLUGIN_TAG + p.gold + p.LINE_STRING);
        message.add(p.PLUGIN_TAG + p.red + "winnings" + p.gold + " - Total winnings.");
        message.add(p.PLUGIN_TAG + p.red + "losses" + p.gold + " - Total losses.");
        message.add(p.PLUGIN_TAG + p.red + "profit" + p.gold + " - Profit/loss.");
        message.add(p.PLUGIN_TAG + p.red + "biggestWin" + p.gold + " - Biggest pot won.");
        message.add(p.PLUGIN_TAG + p.red + "biggestLoss" + p.gold + " - Most money lost in 1 hand.");
        message.add(p.PLUGIN_TAG + p.red + "played" + p.gold + " - Amount of hands played.");
        message.add(p.PLUGIN_TAG + p.red + "averageBet" + p.gold + " - Average bet/raise size.");
        message.add(p.PLUGIN_TAG + p.red + "aggressionFactor" + p.gold + " - Aggression factor of the player.");
        message.add(p.PLUGIN_TAG + p.red + "vpip" + p.gold + " - Voluntarily put money in play (at preflop).");
        message.add(p.PLUGIN_TAG + p.red + "pfr" + p.gold + " - Pre-flop raising percentage.");
        message.add(p.PLUGIN_TAG + p.red + "wins" + p.gold + " - Percentage of hands won.");
        message.add(p.PLUGIN_TAG + p.red + "winsShowdown" + p.gold + " - Percentage of pots won at showdown.");
        message.add(p.PLUGIN_TAG + p.red + "winsAllin" + p.gold + " - Percentage of wins during all-in.");
        for (String string : message)
            player.sendMessage(string);
    }

    public void cantAllIn(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You have enough money to call/raise the current bet! If you wish to bet all of your stack, simply " + p.gold + "/hand bet [your stack]" + p.red + " (check with " + p.gold + "/hand money" + p.red + ")");
    }

    public void cantCall(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You have already contributed the required amount to this pot!");
    }

    public void cantCheck(PokerPlayer player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You cannot check at this time! You need to call " + p.gold + p.methodsMisc.formatMoney(player.table.currentBet - player.currentBet) + p.red + " more, or raise!");
    }

    public void cantContinue(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You can't continue the hand right now!");
    }

    public void cantRaise(Player player, double minRaise, double currentBet)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You cannot raise that amount. Min Raise: " + p.gold + p.methodsMisc.formatMoney(minRaise) + p.red + " (on top of the current bet of " + p.gold + p.methodsMisc.formatMoney(currentBet) + p.red + ")");
    }

    public void cantReveal(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You cannot reveal your hand right now!");
    }

    public void displayHelp(Player player, String command)
    {
        List<String> message = new ArrayList<String>();
        if (command.equals("table"))
        {
            message.add(p.PLUGIN_TAG + p.gold + "/table help|h (command)");
            message.add(p.PLUGIN_TAG + p.gold + "/table create|new|cr [name] [buyin]");
            message.add(p.PLUGIN_TAG + p.gold + "/table delete|del");
            message.add(p.PLUGIN_TAG + p.gold + "/table open|unlock|o");
            message.add(p.PLUGIN_TAG + p.gold + "/table close|lock|cl");
            message.add(p.PLUGIN_TAG + p.gold + "/table set [setting] [value]");
            message.add(p.PLUGIN_TAG + p.gold + "/table listsettings|ls");
            message.add(p.PLUGIN_TAG + p.gold + "/table start|go|s");
            message.add(p.PLUGIN_TAG + p.gold + "/table pay|p (pot ID) [player ID]");
            message.add(p.PLUGIN_TAG + p.gold + "/table kick|boot|k [player ID]");
            message.add(p.PLUGIN_TAG + p.gold + "/table ban|b [player name]");
            message.add(p.PLUGIN_TAG + p.gold + "/table unban|pardon|forgive|u [player name]");
            message.add(p.PLUGIN_TAG + p.gold + "/table continue|cont|next");
            message.add(p.PLUGIN_TAG + "/table may be replaced with /tbl.");
        }
        if (command.equals("hand"))
        {
            message.add(p.PLUGIN_TAG + p.gold + "/hand");
            message.add(p.PLUGIN_TAG + p.gold + "/hand help|h (command)");
            message.add(p.PLUGIN_TAG + p.gold + "/hand bet|b [amount]");
            message.add(p.PLUGIN_TAG + p.gold + "/hand fold|muck|f");
            message.add(p.PLUGIN_TAG + p.gold + "/hand call|match|ca");
            message.add(p.PLUGIN_TAG + p.gold + "/hand check|ch");
            message.add(p.PLUGIN_TAG + p.gold + "/hand board|community");
            message.add(p.PLUGIN_TAG + p.gold + "/hand money|balance|m");
            message.add(p.PLUGIN_TAG + p.gold + "/hand rebuy|addmoney|addstack [amount]");
            message.add(p.PLUGIN_TAG + p.gold + "/hand pot|pots|p");
            message.add(p.PLUGIN_TAG + p.gold + "/hand allin|shove|a");
            message.add(p.PLUGIN_TAG + p.gold + "/hand reveal|show|display");
            message.add(p.PLUGIN_TAG + p.gold + "/hand withdraw|cashin|w [amount]");
        }
        if (command.equals("poker"))
        {
            message.add(p.PLUGIN_TAG + p.gold + "/poker help|h (command)");
            message.add(p.PLUGIN_TAG + p.gold + "/poker teleport|tp [table ID]");
            message.add(p.PLUGIN_TAG + p.gold + "/poker tables|list|all|l|/tables");
            message.add(p.PLUGIN_TAG + p.gold + "/poker sit|join|s [table ID] [buyin]");
            message.add(p.PLUGIN_TAG + p.gold + "/poker leave|getup|stand|standup");
            message.add(p.PLUGIN_TAG + p.gold + "/poker details|info|d (type) (table ID)");
            message.add(p.PLUGIN_TAG + p.gold + "/poker players|listplayers|lp (table ID)");
            message.add(p.PLUGIN_TAG + p.gold + "/poker invite|i [player name]");
            message.add(p.PLUGIN_TAG + p.gold + "/poker check|checkplayer [player name]");
            message.add(p.PLUGIN_TAG + p.gold + "/poker stats (check)|(top)|(rank) (arguments)");
            message.add(p.PLUGIN_TAG + "/poker may be replaced with /pkr.");
        }
        String parsedMessage[] = p.methodsMisc.replaceSpecialCharacters(message);
        player.sendMessage(parsedMessage);
        player.sendMessage(p.PLUGIN_TAG + ChatColor.BLUE + "|" + p.white + " represents 'or' (an alias)");
        player.sendMessage(p.PLUGIN_TAG + ChatColor.DARK_GRAY + "[]" + p.white + " represents 'required'");
        player.sendMessage(p.PLUGIN_TAG + ChatColor.DARK_AQUA + "()" + p.white + " represents 'optional'");
        player.sendMessage(p.PLUGIN_TAG + ChatColor.BLUE + "UltimatePoker " + p.gold + "v" + p.VERSION + ChatColor.BLUE + " by " + p.gold + "Norbo11");
    }

    public void noPermission(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You don't have permission to do this.");
    }

    public void noPotsOnTable(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is no pot on your table!");
    }

    public void noPotToPay(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You have already paid all pots on your table!");
    }

    public void noStatsAvailable(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You have no poker stats! Sit down at a poker table to generate stats.");
    }

    public void noStatsAvailable(Player player, String toCheck)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "The player " + p.gold + toCheck + p.red + " doesn't have any poker stats!");
    }

    public void noSuchTopStat(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "That is not a valid statistic type! Check statistic types with " + p.gold + "/poker stats top|rank");
    }

    public void noTablesCreated(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "No tables available.");
    }

    public void notADetailType(Player player, String type)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is no detail type of " + p.gold + type + p.white + "! Available types: settings, players, other|general.");
    }

    public void notANumber(Player player, String value)
    {
        player.sendMessage(p.PLUGIN_TAG + p.gold + value + p.red + " is not a valid number!");
    }

    public void notAPlayer(CommandSender sender)
    {
        sender.sendMessage(p.PLUGIN_TAG + p.red + "Sorry, this command is only for players!");
    }

    public void notAPokerPlayer(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You are not currently sitting at any table! Sit with " + p.gold + "/table sit [ID]" + p.red + ".");
    }

    public void notAPokerPlayerID(Player player, String id)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is no player with the ID of " + p.gold + id + p.red + " sitting on your table.");
    }

    public void notAPotID(Player player, String potID)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is no pot on your table with the ID of " + p.gold + potID + p.red + "!");

    }

    public void notATable(Player player, String id)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is no table with the ID of " + p.gold + id + p.red + ".");
    }

    public void notEnoughChips(Player player, double money, double amount)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You do not have " + p.gold + p.methodsMisc.formatMoney(amount) + p.red + " chips to do this! You need " + p.gold + p.methodsMisc.formatMoney(amount - money) + p.red + " more.");

    }

    public void notEnoughMoney(Player player, String amount, double money)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You do not have " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount)) + p.red + "! You need " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount) - money) + p.red + " more.");
        return;
    }

    public void notEnoughPlayers(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You need at least " + p.gold + "2" + p.red + " people sat on your table to start it!");
    }

    public void notOpen(Player player, String id)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Table ID " + p.gold + id + p.red + " is not open!");
    }

    public void notOwnerOfTable(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You are not an owner of any table!");
    }

    public void notWithinBuyinBounds(Player player, double buyin, double minbuy, double maxbuy)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Buy-in amount " + p.gold + p.methodsMisc.formatMoney(buyin) + p.red + " is not within the table buy-in boundries. Min: " + p.methodsMisc.formatMoney(minbuy) + ". Max: " + p.methodsMisc.formatMoney(maxbuy) + ".");
    }

    public void notYourTurn(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "It's not your turn to act!");
    }

    public void playerAlreadyBanned(Player player, String toBan)
    {
        player.sendMessage(p.PLUGIN_TAG + p.gold + toBan + p.red + " is already banned from the table!");
    }

    public void playerHasNoHand(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You do not have any cards!");
    }

    public void playerIsAllIn(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You are all in, you cannot take any action!");
    }

    public void playerIsBanned(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You have been banned from this table!");
    }

    public void playerIsEliminated(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You have been eliminated from this table!");
    }

    public void playerIsFolded(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You cannot do that, you have folded your hand!");
    }

    public void playerIsOwnerGeneral(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You already own a table!");
    }

    public void playerIsOwnerSpecific(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You are the owner of this table! If you wish to leave, delete the table.");
    }

    public void playerIsPokerPlayer(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You are already sat at a table!");
    }

    public void playerNotFound(Player player, String toInvite)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Player not found: " + p.gold + toInvite);
    }

    public void playerNotNearEnough(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You are not near enough to the poker table to join it!");
    }

    public void pokerPlayerNotFound(Player player, String toCheck)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "The player " + p.gold + toCheck + p.red + " is not currently playing poker!");
    }

    public void potExists(Player player, double pot)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is currently a pot of " + p.gold + p.methodsMisc.formatMoney(pot) + p.red + "!");
    }

    public void potIsEmpty(Player player, Pot pot)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "That pot has already been paid!");
    }

    public void tableAlreadyClosed(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Your table is already closed!");
    }

    public void tableAlreadyOpen(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Your table is already open!");
    }

    public void tableHasPots(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "You need to pay all pots before you delete the table!");
    }

    public void tableIsAtShowdown(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "The table is currently at showdown! Reveal your hand with " + p.gold + "/hand reveal");
    }

    public void tableIsInEliminationMode(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "The table is currently in elimination mode!");
    }

    public void tableIsInProgress(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Table is currently in progress! Use this command only during showdowns.");
    }

    public void tableIsNotInProgress(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "The table is currently not in progress!");
    }

    public void tableIsStopped(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "That table has been stopped!");
    }

    public void tableMultiplePots(Player player)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "There is more than one pot on your table, please specify the pot ID!");
    }

    public void tableNoCallers(Player player, String amount, double highestBalance)
    {
        player.sendMessage(p.PLUGIN_TAG + p.red + "Nobody on the table can call a raise of " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount)) + p.red + "! Raise to " + p.methodsMisc.formatMoney(highestBalance) + " at most, or call.");
    }

    public void usage(Player player, String category, String command)
    {
        List<String> message = new ArrayList<String>();

        // Help
        if (category.equalsIgnoreCase("help"))
        {
            if (command.equalsIgnoreCase("help")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table help | /poker help | /hand help (cmd)" + p.gold + " - Displays list of commands or gives command specific help.");
        }

        // Table
        if (category.equalsIgnoreCase("table"))
        {
            if (command.equalsIgnoreCase("create")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table create | new | cr [name] [buyin]" + p.gold + " - Creates a table and sits at it with the specified buyin.");
            else if (command.equalsIgnoreCase("delete")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table delete | del | d" + p.gold + " - Deletes your table.");
            else if (command.equalsIgnoreCase("open")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table open | unlock | o " + p.gold + " - Opens your table.");
            else if (command.equalsIgnoreCase("close")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table close | lock | c" + p.gold + " - Closes your table.");
            else if (command.equalsIgnoreCase("set")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table set [setting] [value]" + p.gold + " - Sets the [setting] to the [value]. List available settings with /table listsettings | ls.");
            else if (command.equalsIgnoreCase("listSettings")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table listsettings | ls" + p.gold + " - Lists all available settings for /table set.");
            else if (command.equalsIgnoreCase("start")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table start | go | s" + p.gold + " - Starts the game at your table.");
            else if (command.equalsIgnoreCase("pay")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table pay | p (pot ID) [player ID]" + p.gold + " - Pays the specified pot to the specified player. If only 1 pot exists, pot ID is optional.");
            else if (command.equalsIgnoreCase("kick")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table kick | boot | k [Player ID]" + p.gold + " - Kicks the player from the table.");
            else if (command.equalsIgnoreCase("ban")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table ban | b [Player name]" + p.gold + " - Bans the player from the table.");
            else if (command.equalsIgnoreCase("unban")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table unban | pardon | forgive | u [Player name]" + p.gold + " - Unbans the player from the table.");
            else if (command.equalsIgnoreCase("continue")) message.add(p.PLUGIN_TAG + p.red + "Usage: /table continue | cont | next" + p.gold + " - Continues the hand after a player won by being last person to not fold.");
            else message.add(p.PLUGIN_TAG + p.red + "No such /table command! Check help with " + p.gold + "/table help.");
        }

        // Hand
        if (category.equalsIgnoreCase("hand"))
        {
            if (command.equalsIgnoreCase("hand")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand" + p.gold + " - Displays your hand.");
            else if (command.equalsIgnoreCase("bet")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand bet | b [amount]" + p.gold + " - Bets or raises to the specified amount.");
            else if (command.equalsIgnoreCase("fold")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand fold | muck | f" + p.gold + " - Folds your hand.");
            else if (command.equalsIgnoreCase("call")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand call | match | ca" + p.gold + " - Matches your total with the rest of the table.");
            else if (command.equalsIgnoreCase("check")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand check | ch" + p.gold + " - Checks your hand.");
            else if (command.equalsIgnoreCase("board")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand board | community" + p.gold + " - Shows the community cards at your table.");
            else if (command.equalsIgnoreCase("money")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand money | balance | m" + p.gold + " - Shows you your remaining stack at the table.");
            else if (command.equalsIgnoreCase("rebuy")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand rebuy | addmoney | addstack | r [amount]" + p.gold + " - Adds more money to your stack.");
            else if (command.equalsIgnoreCase("pot")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand pot | pots | p" + p.gold + " - Displays all pots at the table.");
            else if (command.equalsIgnoreCase("allin")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand allin | shove | a" + p.gold + " - Bets the rest of your stack and puts you in all in mode.");
            else if (command.equalsIgnoreCase("reveal")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand reveal | show | display" + p.gold + " - Shows your hand to everyone around the table.");
            else if (command.equalsIgnoreCase("withdraw")) message.add(p.PLUGIN_TAG + p.red + "Usage: /hand withdraw | cashin | w [amount]" + p.gold + " - Withdraws the specified amount from your stack.");
            else message.add(p.PLUGIN_TAG + p.red + "No such /hand command! Check help with " + p.gold + "/hand help.");
        }

        // Poker
        if (category.equalsIgnoreCase("poker"))
        {
            if (command.equalsIgnoreCase("teleport")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker teleport | tp [table ID]" + p.gold + " - Teleports you to the specified table.");
            else if (command.equalsIgnoreCase("tables")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker tables | list | all | l | /tables" + p.gold + " - Lists all created tables.");
            else if (command.equalsIgnoreCase("sit")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker sit | join | s [table ID] [buyin]" + p.gold + " - Sits down at the specified table with the specified buy-in.");
            else if (command.equalsIgnoreCase("leave")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker leave | getup | stand | standup" + p.gold + " - Leaves the table you are currently sititng at.");
            else if (command.equalsIgnoreCase("details")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker details | info | d (type) (table ID)" + p.gold + " - Gives specific details about a table. Allowed types: all, settings, players, general | other.");
            else if (command.equalsIgnoreCase("players")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker players | listplayers | lp (table ID)" + p.gold + " - Lists all players at the specified table. If no table is specified, lists players at the table you're sitting on.");
            else if (command.equalsIgnoreCase("invite")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker invite | i " + p.gold + " - Invites the specified player to your table.");
            else if (command.equalsIgnoreCase("check")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker check | checkplayer [player name]" + p.gold + " - Checks the player's money stack if they are playing poker.");
            else message.add(p.PLUGIN_TAG + p.red + "No such /poker command! Check help with " + p.gold + "/poker help.");
        }

        // Poker.stats
        if (category.equalsIgnoreCase("stats"))
        {
            if (command.equalsIgnoreCase("check")) message.add(p.PLUGIN_TAG + p.red + "Usage: /poker stats check [player name]" + p.gold + " - Displays the statistics of the specified user.");
            else if (command.equalsIgnoreCase("top"))
            {
                message.add(p.PLUGIN_TAG + p.red + "Usage: /poker stats top [type]" + p.gold + " - Displays the leaderboards of a particular statistic type.");
                availableStatisticTypes(player);
            } else if (command.equalsIgnoreCase("rank"))
            {
                message.add(p.PLUGIN_TAG + p.red + "Usage: /poker stats rank [type] (player)" + p.gold + " - Displays the rank of yourself or the specified player, on the leaderboard of the specified statistic.");
                availableStatisticTypes(player);
            } else message.add(p.PLUGIN_TAG + p.red + "Usage: /poker stats (check|top|rank) (arguments)" + p.gold + " - Displays poker statistics and leaderboards.");
        }

        boolean sent = false;
        for (String string : message)
        {
            player.sendMessage(string);
            sent = true;
        }

        if (!sent) player.sendMessage(p.PLUGIN_TAG + p.red + "That is not a valid command! Use /table help, /hand help, and /poker help for lists of commands.");
    }
}
