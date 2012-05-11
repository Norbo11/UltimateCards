/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
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

    public void playerIsPokerPlayer(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are already sat at a table!");
    }

    public void playerIsOwnerGeneral(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You already own a table!");
    }

    public void cantCall(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have already contributed the required amount to this pot!");
    }

    public void cantCheck(PokerPlayer player)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot check at this time! You need to call " + p.gold + p.methodsMisc.formatMoney(player.table.currentBet - player.currentBet) + p.red + " more, or raise!");
    }

    public void cantRaise(Player player, double minRaise, double currentBet)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot raise that amount. Min Raise: " + p.gold + p.methodsMisc.formatMoney(minRaise) + p.red + " (on top of the current bet of " + p.gold + p.methodsMisc.formatMoney(currentBet) + p.red + ")");
    }

    public void displayHelp(Player player, String command)
    {
        List<String> message = new ArrayList<String>();
        if (command.equals("table"))
        {
            message.add(p.pluginTag + p.gold + "/table help|h (command)");
            message.add(p.pluginTag + p.gold + "/table create|new|cr [name] [buyin]");
            message.add(p.pluginTag + p.gold + "/table delete|del");
            message.add(p.pluginTag + p.gold + "/table open|unlock|o");
            message.add(p.pluginTag + p.gold + "/table close|lock|cl");
            message.add(p.pluginTag + p.gold + "/table set [setting] [value]");
            message.add(p.pluginTag + p.gold + "/table listsettings|ls");
            message.add(p.pluginTag + p.gold + "/table start|go|s");
            message.add(p.pluginTag + p.gold + "/table pay|p (pot ID) [player ID]");
            message.add(p.pluginTag + p.gold + "/table kick|boot|k [player ID]");
            message.add(p.pluginTag + p.gold + "/table ban|b [player name]");
            message.add(p.pluginTag + p.gold + "/table unban|pardon|forgive|u [player name]");
            message.add(p.pluginTag + p.gold + "/table continue|cont|next");
            message.add(p.pluginTag + "/table may be replaced with /tbl.");
        }
        if (command.equals("hand"))
        {
            message.add(p.pluginTag + p.gold + "/hand");
            message.add(p.pluginTag + p.gold + "/hand help|h (command)");
            message.add(p.pluginTag + p.gold + "/hand bet|b [amount]");
            message.add(p.pluginTag + p.gold + "/hand fold|muck|f");
            message.add(p.pluginTag + p.gold + "/hand call|match|ca");
            message.add(p.pluginTag + p.gold + "/hand check|ch");
            message.add(p.pluginTag + p.gold + "/hand board|b");
            message.add(p.pluginTag + p.gold + "/hand money|balance|m");
            message.add(p.pluginTag + p.gold + "/hand rebuy|addmoney|addstack [amount]");
            message.add(p.pluginTag + p.gold + "/hand pot|pots|p");
            message.add(p.pluginTag + p.gold + "/hand allin|shove|a");
            message.add(p.pluginTag + p.gold + "/hand reveal|show|display");
        }
        if (command.equals("poker"))
        {
            message.add(p.pluginTag + p.gold + "/poker help|h (command)");
            message.add(p.pluginTag + p.gold + "/poker teleport|tp [table ID]");
            message.add(p.pluginTag + p.gold + "/poker tables|list|all|l|/tables");
            message.add(p.pluginTag + p.gold + "/poker sit|join|s [table ID] [buyin]");
            message.add(p.pluginTag + p.gold + "/poker leave|getup|stand|standup");
            message.add(p.pluginTag + p.gold + "/poker details|info|d (type) (table ID)");
            message.add(p.pluginTag + p.gold + "/poker players|listplayers|lp (table ID)");
            message.add(p.pluginTag + p.gold + "/poker invite|i [player name]");
            message.add(p.pluginTag + p.gold + "/poker check|checkplayer [player name]");
            message.add(p.pluginTag + "/poker may be replaced with /pkr.");
        }
        String parsedMessage[] = p.methodsMisc.replaceSpecialCharacters(message);
        player.sendMessage(parsedMessage);
        player.sendMessage(p.pluginTag + ChatColor.BLUE + "|" + p.white + " represents 'or' (an alias)");
        player.sendMessage(p.pluginTag + ChatColor.DARK_GRAY + "[]" + p.white + " represents 'required'");
        player.sendMessage(p.pluginTag + ChatColor.DARK_AQUA + "()" + p.white + " represents 'optional'");
        player.sendMessage(p.pluginTag + ChatColor.BLUE + "UltimatePoker " + p.gold + "v" + p.version + ChatColor.BLUE + " by " + p.gold + "Norbo11");
    }

    public void tableMultiplePots(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "There is more than one pot on your table, please specify the pot ID!");
    }

    public void tableNoCallers(Player player, String amount, double highestBalance)
    {
        player.sendMessage(p.pluginTag + p.red + "Nobody on the table can call a raise of " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount)) + p.red + "! Raise to " + p.methodsMisc.formatMoney(highestBalance) + " at most, or call.");
    }

    public void noPermission(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You don't have permission to do this.");
    }

    public void noPotsOnTable(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no pot on your table!");
    }

    public void noPotToPay(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have already paid all pots on your table!");
    }

    public void notADetailType(Player player, String type)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no detail type of " + p.gold + type + p.white + "! Available types: settings, players, other|general.");
    }

    public void notANumber(Player player, String value)
    {
        player.sendMessage(p.pluginTag + p.gold + value + p.red + " is not a valid number!");
    }

    public void notAPokerPlayerID(Player player, String id)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no player with the ID of " + p.gold + id + p.red + " sitting on your table.");
    }

    public void notAPotID(Player player, String potID)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no pot on your table with the ID of " + p.gold + potID + p.red + "!");

    }

    public void notATable(Player player, String id)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no table with the ID of " + p.gold + id + p.red + ".");
    }

    public void notEnoughMoney(Player player, String amount, double money)
    {
        player.sendMessage(p.pluginTag + p.red + "You do not have " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount)) + p.red + "! You need " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount) - money) + p.red + " more.");
        return;
    }

    public void notEnoughPlayers(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You need at least " + p.gold + "2" + p.red + " people sat on your table to start it!");
    }

    public void notOpen(Player player, String id)
    {
        player.sendMessage(p.pluginTag + p.red + "Table ID " + p.gold + id + p.red + " is not open!");
    }

    public void notOwnerOfTable(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are not an owner of any table!");
    }

    public void notAPlayer(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + p.red + "Sorry, this command is only for players!");
    }

    public void notAPokerPlayer(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are not currently sitting at any table! Sit with " + p.gold + "/table sit [ID]" + p.red + ".");
    }

    public void notWithinBuyinBounds(Player player, double buyin, double minbuy, double maxbuy)
    {
        player.sendMessage(p.pluginTag + p.red + "Buy-in amount " + p.gold + p.methodsMisc.formatMoney(buyin) + p.red + " is not within the table buy-in boundries. Min: " + p.methodsMisc.formatMoney(minbuy) + ". Max: " + p.methodsMisc.formatMoney(maxbuy) + ".");
    }

    public void notYourTurn(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "It's not your turn to act!");
    }

    public void playerIsBanned(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have been banned from this table!");
    }

    public void playerIsFolded(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot do that, you have folded your hand!");
    }

    public void playerIsAllIn(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are all in, you cannot take any action!");
    }

    public void playerIsOwnerSpecific(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are the owner of this table!");
    }

    public void playerNotFound(Player player, String toInvite)
    {
        player.sendMessage(p.pluginTag + p.red + "Player not found: " + p.gold + toInvite);
    }

    public void potExists(Player player, double pot)
    {
        player.sendMessage(p.pluginTag + p.red + "There is currently a pot of " + p.gold + p.methodsMisc.formatMoney(pot) + p.red + "!");
    }

    public void tableIsInEliminationMode(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "The table is currently in elimination mode, you cannot re-buy!");
    }

    public void tableIsInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Table is currently in progress! Use this command only during showdowns.");
    }

    public void tableIsStopped(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "That table has been stopped!");
    }

    public void tableIsNotInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "The table is currently not in progress!");
    }

    public void usage(Player player, String command)
    {
        String message = "";

        // Help
        if (command.equals("help")) message = (p.pluginTag + p.red + "Usage: /table help | /poker help | /hand help (cmd)" + p.gold + " - Displays list of commands or gives command specific help.");

        // Table
        if (command.equals("create")) message = (p.pluginTag + p.red + "Usage: /table create | new | cr [name] [buyin]" + p.gold + " - Creates a table and sits at it with the specified buyin.");
        if (command.equals("delete")) message = (p.pluginTag + p.red + "Usage: /table delete | del | d" + p.gold + " - Deletes your table.");
        if (command.equals("open")) message = (p.pluginTag + p.red + "Usage: /table open | unlock | o " + p.gold + " - Opens your table.");
        if (command.equals("close")) message = (p.pluginTag + p.red + "Usage: /table close | lock | c" + p.gold + " - Closes your table.");
        if (command.equals("set")) message = (p.pluginTag + p.red + "Usage: /table set [setting] [value]" + p.gold + " - Sets the [setting] to the [value]. List available settings with /table listsettings | ls.");
        if (command.equals("listsettings")) message = (p.pluginTag + p.red + "Usage: /table listsettings | ls" + p.gold + " - Lists all available settings for /table set.");
        if (command.equals("start")) message = (p.pluginTag + p.red + "Usage: /table start | go | s" + p.gold + " - Starts the game at your table.");
        if (command.equals("pay")) message = (p.pluginTag + p.red + "Usage: /table pay | p (pot ID) [player ID]" + p.gold + " - Pays the specified pot to the specified player. If only 1 pot exists, pot ID is optional.");
        if (command.equals("kick")) message = (p.pluginTag + p.red + "Usage: /table kick | boot | k [Player ID]" + p.gold + " - Kicks the player from the table.");
        if (command.equals("ban")) message = (p.pluginTag + p.red + "Usage: /table ban | b [Player name]" + p.gold + " - Bans the player from the table.");
        if (command.equals("unban")) message = (p.pluginTag + p.red + "Usage: /table unban | pardon | forgive | u [Player name]" + p.gold + " - Unbans the player from the table.");
        if (command.equals("continue")) message = (p.pluginTag + p.red + "Usage: /table continue | cont | next" + p.gold + " - Continues the hand after a player won by being last person to not fold.");

        // Hand
        if (command.equals("hand")) message = (p.pluginTag + p.red + "Usage: /hand" + p.gold + " - Displays your hand.");
        if (command.equals("bet")) message = (p.pluginTag + p.red + "Usage: /hand bet | b [amount]" + p.gold + " - Bets or raises to the specified amount.");
        if (command.equals("fold")) message = (p.pluginTag + p.red + "Usage: /hand fold | muck | f" + p.gold + " - Folds your hand.");
        if (command.equals("call")) message = (p.pluginTag + p.red + "Usage: /hand call | match | ca" + p.gold + " - Matches your total with the rest of the table.");
        if (command.equals("check")) message = (p.pluginTag + p.red + "Usage: /hand check | ch" + p.gold + " - Checks your hand.");
        if (command.equals("board")) message = (p.pluginTag + p.red + "Usage: /hand board | b" + p.gold + " - Shows the community cards at your table.");
        if (command.equals("money")) message = (p.pluginTag + p.red + "Usage: /hand money | balance | m" + p.gold + " - Shows you your remaining stack at the table.");
        if (command.equals("rebuy")) message = (p.pluginTag + p.red + "Usage: /hand rebuy | addmoney | addstack | r [amount]" + p.gold + " - Adds more money to your stack.");
        if (command.equals("pot")) message = (p.pluginTag + p.red + "Usage: /hand pot | pots | p" + p.gold + " - Displays all pots at the table.");
        if (command.equals("allin")) message = (p.pluginTag + p.red + "Usage: /hand allin | shove | a" + p.gold + " - Bets the rest of your stack and puts you in all in mode.");
        if (command.equals("reveal")) message = (p.pluginTag + p.red + "Usage: /hand reveal | show | display" + p.gold + " - Shows your hand to everyone around the table.");

        // Poker
        if (command.equals("teleport")) message = (p.pluginTag + p.red + "Usage: /poker teleport | tp [table ID]" + p.gold + " - Teleports you to the specified table.");
        if (command.equals("tables")) message = (p.pluginTag + p.red + "Usage: /poker tables | list | all | l | /tables" + p.gold + " - Lists all created tables.");
        if (command.equals("sit")) message = (p.pluginTag + p.red + "Usage: /poker sit | join | s [table ID] [buyin]" + p.gold + " - Sits down at the specified table with the specified buy-in.");
        if (command.equals("leave")) message = (p.pluginTag + p.red + "Usage: /poker leave | getup | stand | standup" + p.gold + " - Leaves the table you are currently sititng at.");
        if (command.equals("details")) message = (p.pluginTag + p.red + "Usage: /poker details | info | d (type) (table ID)" + p.gold + " - Gives specific details about a table. Allowed types: all, settings, players, general | other.");
        if (command.equals("players")) message = (p.pluginTag + p.red + "Usage: /poker players | listplayers | lp (table ID)" + p.gold + " - Lists all players at the specified table. If no table is specified, lists players at the table you're sitting on.");
        if (command.equals("invite")) message = (p.pluginTag + p.red + "Usage: /poker invite|i " + p.gold + " - Invites the specified player to your table.");
        if (command.equals("playercheck")) message = (p.pluginTag + p.red + "Usage: /poker check|checkplayer [player name]" + p.gold + " - Checks the player's money stack if they are playing poker.");
        
        if (message != "") player.sendMessage(message);
        else player.sendMessage(p.pluginTag + p.red + "That is not a valid command! Use /table help, /hand help, and /poker help for lists of commands.");
    }

    public void potIsEmpty(Player player, Pot pot)
    {
        player.sendMessage(p.pluginTag + p.red + "That pot has already been paid!");
    }

    public void playerAlreadyBanned(Player player, String toBan)
    {
        player.sendMessage(p.pluginTag + p.gold + toBan + p.red + " is already banned from the table!");
    }

    public void tableAlreadyClosed(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Your table is already closed!");
    }

    public void cantAllIn(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have enough money to call/raise the current bet! If you wish to bet all of your stack, simply " + p.gold + "/hand bet [your stack]" + p.red + " (check with " + p.gold + "/hand money" + p.red + ")");
    }

    public void tableAlreadyOpen(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Your table is already open!");
    }

    public void playerNotNearEnough(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are not near enough to the poker table to join it!");
    }

    public void noTablesCreated(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "No tables available.");
    }

    public void playerHasNoHand(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You do not have any cards!");
    }

    public void cantReveal(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot reveal your hand right now!");
    }

    public void tableIsAtShowdown(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "The table is currently at showdown! Reveal your hand with " + p.gold + "/hand reveal");
    }

    public void cantContinue(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You can't continue the hand right now!");
    }

    public void pokerPlayerNotFound(Player player, String toCheck)
    {
        player.sendMessage(p.pluginTag + p.red + "The player " + p.gold + toCheck + p.red + " is not currently playing poker!");
    }
}
