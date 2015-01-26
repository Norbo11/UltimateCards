package com.github.norbo11.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.norbo11.commands.blackjack.BlackjackBet;
import com.github.norbo11.commands.blackjack.BlackjackDouble;
import com.github.norbo11.commands.blackjack.BlackjackHit;
import com.github.norbo11.commands.blackjack.BlackjackSplit;
import com.github.norbo11.commands.blackjack.BlackjackStand;
import com.github.norbo11.commands.poker.PokerAllin;
import com.github.norbo11.commands.poker.PokerBet;
import com.github.norbo11.commands.poker.PokerBoard;
import com.github.norbo11.commands.poker.PokerCall;
import com.github.norbo11.commands.poker.PokerCheck;
import com.github.norbo11.commands.poker.PokerFold;
import com.github.norbo11.commands.poker.PokerHand;
import com.github.norbo11.commands.poker.PokerPot;
import com.github.norbo11.commands.poker.PokerReveal;
import com.github.norbo11.commands.table.TableBan;
import com.github.norbo11.commands.table.TableClose;
import com.github.norbo11.commands.table.TableCreate;
import com.github.norbo11.commands.table.TableDelete;
import com.github.norbo11.commands.table.TableDetails;
import com.github.norbo11.commands.table.TableInvite;
import com.github.norbo11.commands.table.TableKick;
import com.github.norbo11.commands.table.TableLeave;
import com.github.norbo11.commands.table.TableListSettings;
import com.github.norbo11.commands.table.TableMoney;
import com.github.norbo11.commands.table.TableOpen;
import com.github.norbo11.commands.table.TablePlayers;
import com.github.norbo11.commands.table.TableRebuy;
import com.github.norbo11.commands.table.TableReload;
import com.github.norbo11.commands.table.TableSave;
import com.github.norbo11.commands.table.TableSet;
import com.github.norbo11.commands.table.TableSit;
import com.github.norbo11.commands.table.TableStart;
import com.github.norbo11.commands.table.TableTables;
import com.github.norbo11.commands.table.TableTeleport;
import com.github.norbo11.commands.table.TableUnban;
import com.github.norbo11.commands.table.TableUnsave;
import com.github.norbo11.commands.table.TableWithdraw;
import com.github.norbo11.util.ErrorMessages;
import com.github.norbo11.util.ExceptionCatcher;
import com.github.norbo11.util.Log;
import com.github.norbo11.util.Messages;

public class PluginExecutor implements CommandExecutor {

    public static TableDetails tableDetails = new TableDetails();
    public static TableInvite tableInvite = new TableInvite();
    public static TableLeave tableLeave = new TableLeave();
    public static TableMoney tableMoney = new TableMoney();
    public static TablePlayers tablePlayers = new TablePlayers();
    public static TableRebuy tableRebuy = new TableRebuy();
    public static TableSit tableSit = new TableSit();
    public static TableTables tableTables = new TableTables();
    public static TableTeleport tableTeleport = new TableTeleport();
    public static TableWithdraw tableWithdraw = new TableWithdraw();
    public static TableReload tableReload = new TableReload();

    public static PokerHand pokerHand = new PokerHand();
    public static PokerAllin pokerAllin = new PokerAllin();
    public static PokerBet pokerBet = new PokerBet();
    public static PokerBoard pokerBoard = new PokerBoard();
    public static PokerCall pokerCall = new PokerCall();
    public static PokerCheck pokerCheck = new PokerCheck();
    public static PokerFold pokerFold = new PokerFold();
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
    public static TableSave tableSave = new TableSave();
    public static TableUnsave tableUnsave = new TableUnsave();

    public static BlackjackHit blackjackHit = new BlackjackHit();
    public static BlackjackStand blackjackStand = new BlackjackStand();
    public static BlackjackBet blackjackBet = new BlackjackBet();
    public static BlackjackSplit blackjackSplit = new BlackjackSplit();
    public static BlackjackDouble blackjackDouble = new BlackjackDouble();

    public static ArrayList<PluginCommand> commandsTable = new ArrayList<PluginCommand>();
    public static ArrayList<PluginCommand> commandsPoker = new ArrayList<PluginCommand>();
    public static ArrayList<PluginCommand> commandsBlackjack = new ArrayList<PluginCommand>();
    public static ArrayList<ArrayList<PluginCommand>> commands = new ArrayList<ArrayList<PluginCommand>>();

    static {
        commandsTable.add(tableDetails);
        commandsTable.add(tableInvite);
        commandsTable.add(tableLeave);
        commandsTable.add(tableMoney);
        commandsTable.add(tablePlayers);
        commandsTable.add(tableRebuy);
        commandsTable.add(tableSit);
        commandsTable.add(tableTables);
        commandsTable.add(tableTeleport);
        commandsTable.add(tableWithdraw);
        commandsTable.add(tableReload);

        commandsTable.add(tableBan);
        commandsTable.add(tableClose);
        commandsTable.add(tableCreate);
        commandsTable.add(tableDelete);
        commandsTable.add(tableKick);
        commandsTable.add(tableListSettings);
        commandsTable.add(tableOpen);
        commandsTable.add(tableSet);
        commandsTable.add(tableStart);
        commandsTable.add(tableUnban);
        commandsTable.add(tableSave);
        commandsTable.add(tableUnsave);

        commandsPoker.add(pokerHand);
        commandsPoker.add(pokerReveal);
        commandsPoker.add(pokerAllin);
        commandsPoker.add(pokerBet);
        commandsPoker.add(pokerBoard);
        commandsPoker.add(pokerCall);
        commandsPoker.add(pokerCheck);
        commandsPoker.add(pokerFold);
        commandsPoker.add(pokerPot);

        commandsBlackjack.add(blackjackHit);
        commandsBlackjack.add(blackjackStand);
        commandsBlackjack.add(blackjackBet);
        commandsBlackjack.add(blackjackSplit);
        commandsBlackjack.add(blackjackDouble);

        commands.add(commandsTable);
        commands.add(commandsPoker);
        commands.add(commandsBlackjack);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length >= 1) {
                    String action = args[0];
                    Log.logCommand(sender, command, args);

                    if (action.equalsIgnoreCase("help")) {
                        String commandToHelpWith = args.length == 2 ? args[1] : command.getName();
                        ErrorMessages.displayHelp(player, commandToHelpWith);
                        return true;
                    }

                    if (command.getName().equalsIgnoreCase("table") || command.getName().equalsIgnoreCase("cards")) {
                        for (PluginCommand cmd : commandsTable) {
                            if (cmd.containsAlias(action)) {
                                if (cmd.hasPermission(player)) {
                                    performCommand(cmd, args, player);
                                } else {
                                    ErrorMessages.noPermission(player);
                                }
                                return true;
                            }
                        }
                        Messages.sendMessage(player, "&cNo such table command. Check help with &6/table help&c.");
                    }

                    if (command.getName().equalsIgnoreCase("poker")) {
                        for (PluginCommand cmd : commandsPoker) {
                            if (cmd.containsAlias(action)) {
                                if (cmd.hasPermission(player)) {
                                    performCommand(cmd, args, player);
                                } else {
                                    ErrorMessages.noPermission(player);
                                }
                                return true;
                            }
                        }
                        Messages.sendMessage(player, "&cNo such poker command. Check help with &6/poker help&c.");
                    }

                    if (command.getName().equalsIgnoreCase("blackjack") || command.getName().equalsIgnoreCase("bj")) {
                        for (PluginCommand cmd : commandsBlackjack) {
                            if (cmd.containsAlias(action)) {
                                if (cmd.hasPermission(player)) {
                                    performCommand(cmd, args, player);
                                } else {
                                    ErrorMessages.noPermission(player);
                                }
                                return true;
                            }
                        }
                        Messages.sendMessage(player, "&cNo such blackjack command. Check help with &6/blackjack | /bj help&c.");
                    }
                } else {
                    ErrorMessages.displayHelp(player, command.getName());
                }
            } else {
                ErrorMessages.notHumanPlayer(sender);
            }
        } catch (Exception e) {
            ExceptionCatcher.catchException(e, command, sender, args);
        }
        return true;
    }

    public void performCommand(PluginCommand cmd, String[] args, Player player) throws Exception {
        cmd.setArgs(args);
        cmd.setPlayer(player);
        if (cmd.conditions()) {
            cmd.perform();
        }
    }
}
