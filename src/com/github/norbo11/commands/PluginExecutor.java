/* ==================================================================================================
 * UltimatePoker v1.1 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: ListenerCommandExecutor.java
 * -Listens for commands typed by the user and handles them accordingly
 * -Every command is surrounded with a try/catch statement which spits out the error log
 * to the console and logs it to the log.txt file. It also displays the simplified error to the user.
 * ===================================================================================================
 */

package com.github.norbo11.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.norbo11.commands.blackjack.BlackjackBet;
import com.github.norbo11.commands.blackjack.BlackjackDouble;
import com.github.norbo11.commands.blackjack.BlackjackHit;
import com.github.norbo11.commands.blackjack.BlackjackSplit;
import com.github.norbo11.commands.blackjack.BlackjackStand;
import com.github.norbo11.commands.cards.CardsDetails;
import com.github.norbo11.commands.cards.CardsInvite;
import com.github.norbo11.commands.cards.CardsLeave;
import com.github.norbo11.commands.cards.CardsMoney;
import com.github.norbo11.commands.cards.CardsPlayers;
import com.github.norbo11.commands.cards.CardsRebuy;
import com.github.norbo11.commands.cards.CardsReload;
import com.github.norbo11.commands.cards.CardsSit;
import com.github.norbo11.commands.cards.CardsTables;
import com.github.norbo11.commands.cards.CardsTeleport;
import com.github.norbo11.commands.cards.CardsWithdraw;
import com.github.norbo11.commands.poker.PokerAllin;
import com.github.norbo11.commands.poker.PokerBet;
import com.github.norbo11.commands.poker.PokerBoard;
import com.github.norbo11.commands.poker.PokerCall;
import com.github.norbo11.commands.poker.PokerCheck;
import com.github.norbo11.commands.poker.PokerFold;
import com.github.norbo11.commands.poker.PokerHand;
import com.github.norbo11.commands.poker.PokerPay;
import com.github.norbo11.commands.poker.PokerPot;
import com.github.norbo11.commands.poker.PokerReveal;
import com.github.norbo11.commands.table.TableBan;
import com.github.norbo11.commands.table.TableClose;
import com.github.norbo11.commands.table.TableCreate;
import com.github.norbo11.commands.table.TableDelete;
import com.github.norbo11.commands.table.TableKick;
import com.github.norbo11.commands.table.TableListSettings;
import com.github.norbo11.commands.table.TableOpen;
import com.github.norbo11.commands.table.TableSet;
import com.github.norbo11.commands.table.TableStart;
import com.github.norbo11.commands.table.TableUnban;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.ExceptionCatcher;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;

public class PluginExecutor implements CommandExecutor
{

    public static CardsDetails cardsDetails = new CardsDetails();
    public static CardsInvite cardsInvite = new CardsInvite();
    public static CardsLeave cardsLeave = new CardsLeave();
    public static CardsMoney cardsMoney = new CardsMoney();
    public static CardsPlayers cardsPlayers = new CardsPlayers();
    public static CardsRebuy cardsRebuy = new CardsRebuy();
    public static CardsSit cardsSit = new CardsSit();
    public static CardsTables cardsTables = new CardsTables();
    public static CardsTeleport cardsTeleport = new CardsTeleport();
    public static CardsWithdraw cardsWithdraw = new CardsWithdraw();
    public static CardsReload cardsReload = new CardsReload();

    public static PokerHand pokerHand = new PokerHand();
    public static PokerAllin pokerAllin = new PokerAllin();
    public static PokerBet pokerBet = new PokerBet();
    public static PokerBoard pokerBoard = new PokerBoard();
    public static PokerCall pokerCall = new PokerCall();
    public static PokerCheck pokerCheck = new PokerCheck();
    public static PokerFold pokerFold = new PokerFold();
    public static PokerPay pokerPay = new PokerPay();
    public static PokerPot pokerPot = new PokerPot();
    public static PokerReveal pokerReveal = new PokerReveal();

    public static TableBan tableBan = new TableBan();
    public static TableClose tableClose = new TableClose();
    public static TableCreate tableCreate = new TableCreate();
    public static TableDelete tableDelete = new TableDelete();
    public static TableKick tableKick = new TableKick();
    public static TableListSettings tableListSettings = new TableListSettings();
    public static TableOpen tableOpen = new TableOpen();
    public static TableSet tableSet = new TableSet();
    public static TableStart tableStart = new TableStart();
    public static TableUnban tableUnban = new TableUnban();

    public static BlackjackHit blackjackHit = new BlackjackHit();
    public static BlackjackStand blackjackStand = new BlackjackStand();
    public static BlackjackBet blackjackBet = new BlackjackBet();
    public static BlackjackSplit blackjackSplit = new BlackjackSplit();
    public static BlackjackDouble blackjackDouble = new BlackjackDouble();

    public static PluginCommand[] commandsCards = { cardsDetails, cardsInvite, cardsLeave, cardsMoney, cardsPlayers, cardsRebuy, cardsSit, cardsTables, cardsTeleport, cardsWithdraw, cardsReload };

    public static PluginCommand[] commandsTable = { tableBan, tableClose, tableCreate, tableDelete, tableKick, tableListSettings, tableOpen, tableSet, tableStart, tableUnban };

    public static PluginCommand[] commandsPoker = { pokerHand, pokerReveal, pokerAllin, pokerBet, pokerBoard, pokerCall, pokerCheck, pokerFold, pokerPay, pokerPot, };

    public static PluginCommand[] commandsBlackjack = { blackjackHit, blackjackStand, blackjackBet, blackjackSplit, blackjackDouble };

    public static PluginCommand[][] commands = { commandsCards, commandsTable, commandsPoker, commandsBlackjack };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        try
        {
            if (sender instanceof Player)
            {
                Player player = (Player) sender;
                
                if (args.length >= 1)
                {
                    String action = args[0];
                    Log.logCommand(sender, command, args);

                    if (action.equalsIgnoreCase("help"))
                    {
                        String commandToHelpWith = args.length == 2 ? args[1] : command.getName();
                        ErrorMessages.displayHelp(player, commandToHelpWith);
                        return true;
                    }

                    if (command.getName().equalsIgnoreCase("cards"))// || command.getName().equalsIgnoreCase("c"))
                    {

                        for (PluginCommand cmd : commandsCards)
                            if (performChecks(cmd, args, player, action)) return true;
                        Messages.sendMessage(player, "&cNo such cards command. Check help with &6/cards help.");
                    }

                    if (command.getName().equalsIgnoreCase("table"))// || command.getName().equalsIgnoreCase("t"))
                    {
                        for (PluginCommand cmd : commandsTable)
                            if (performChecks(cmd, args, player, action)) return true;
                        Messages.sendMessage(player, "&cNo such table command. Check help with &6/table help.");
                    }

                    if (command.getName().equalsIgnoreCase("poker"))// || command.getName().equalsIgnoreCase("p"))
                    {
                        for (PluginCommand cmd : commandsPoker)
                            if (performChecks(cmd, args, player, action)) return true;
                        Messages.sendMessage(player, "&cNo such poker command. Check help with &6/poker help.");
                    }

                    if (command.getName().equalsIgnoreCase("blackjack") || command.getName().equalsIgnoreCase("bj"))
                    {
                        for (PluginCommand cmd : commandsBlackjack)
                            if (performChecks(cmd, args, player, action)) return true;
                        Messages.sendMessage(player, "&cNo such blackjack command. Check help with &6/blackjack help.");
                    }
                } else 
                {
                    ErrorMessages.displayHelp(player, command.getName());
                }
            } else
            {
                ErrorMessages.notHumanPlayer(sender);
            }
        } catch (Exception e)
        {
            ExceptionCatcher.catchException(e, command, sender, args);
        }
        return true;
    }

    private boolean performChecks(PluginCommand cmd, String[] args, Player player, String action) throws Exception
    {
        if (cmd.getAlises().contains(action)) if (cmd.hasPermission(player) || player.hasPermission(PluginCommand.PERMISSIONS_BASE_NODE + "*"))
        {
            cmd.setArgs(args);
            cmd.setPlayer(player);
            if (cmd.conditions())
            {
                cmd.perform();
            }
            return true;
        } else
        {
            ErrorMessages.noPermission(player);
        }
        return false;
    }
}
