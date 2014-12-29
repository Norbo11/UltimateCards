package com.github.norbo11.util;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.commands.PluginCommand;
import com.github.norbo11.commands.PluginExecutor;
import com.github.norbo11.game.poker.PokerPlayer;

public class ErrorMessages {

    public static void betBelowCurrentBet(Player player) {
        Messages.sendMessage(player, "&cYou need to bet more than the current table bet! Or simply " + PluginExecutor.pokerCall.getCommandString() + "&f.");
    }

    public static void betBelowMinRaise(Player player, double minRaise, double currentBet) {
        Messages.sendMessage(player, "&cYou cannot raise that amount. Min Raise: " + "&6" + Formatter.formatMoney(minRaise) + "&c (on top of the current bet of " + "&6" + Formatter.formatMoney(currentBet) + "&c)");
    }

    public static void cannotSpecifyHand(Player player) {
        Messages.sendMessage(player, "&cYou cannot specify a hand ID right now!");
    }

    public static void cantCall(Player player) {
        Messages.sendMessage(player, "&cYou have already contributed the required amount to this pot!");
    }

    public static void cantCheck(PokerPlayer player) {
        Messages.sendMessage(player.getPlayer(), "&cYou cannot check! You need to call " + "&6" + Formatter.formatMoney(player.getPokerTable().getCurrentBet() - player.getCurrentBet()) + "&c, or raise!");
    }

    public static void cantContinue(Player player) {
        Messages.sendMessage(player, "&cYou can't continue the hand right now!");
    }

    public static void cantPay(Player player) {
        Messages.sendMessage(player, "&cYou can't pay that player, his pot is 0!");
    }

    public static void cantReveal(Player player) {
        Messages.sendMessage(player, "&cYou cannot reveal your hand right now!");
    }

    public static void dealerHasNotEnoughMoney(Player player, double money) {
        Messages.sendMessage(player, "&cThe dealer has not enough money for you to bet that high! Bet &6" + Formatter.formatMoney(money) + " at maximum.");
    }

    public static void displayHelp(Player player, String command) {
        if (command.equalsIgnoreCase("table") || command.equalsIgnoreCase("t") || command.equalsIgnoreCase("cards") || command.equalsIgnoreCase("c")) {
            for (PluginCommand pluginCommand : PluginExecutor.commandsTable) {
                if (pluginCommand.hasPermission(player)) {
                    Messages.sendMessage(player, "&6/table " + pluginCommand.getAliasesString() + " &b" + pluginCommand.getArgumentsString());
                }
            }

            Messages.sendMessage(player, "&6/table help &b[command]");
            Messages.sendMessage(player, "&6UltimateCards &bv" + UltimateCards.getVersion() + " &fby &6Norbo11");
        } else if (command.equalsIgnoreCase("poker") || command.equalsIgnoreCase("p")) {
            for (PluginCommand pluginCommand : PluginExecutor.commandsPoker) {
                if (pluginCommand.hasPermission(player)) {
                    Messages.sendMessage(player, "&6/poker " + pluginCommand.getAliasesString() + " &b" + pluginCommand.getArgumentsString());
                }
            }

            Messages.sendMessage(player, "&6/poker help &b[command]");
            Messages.sendMessage(player, "&6UltimateCards &bv" + UltimateCards.getVersion() + " &fby &6Norbo11");
        } else if (command.equalsIgnoreCase("blackjack") || command.equalsIgnoreCase("bj")) {
            for (PluginCommand pluginCommand : PluginExecutor.commandsBlackjack) {
                if (pluginCommand.hasPermission(player)) {
                    Messages.sendMessage(player, "&6/blackjack " + pluginCommand.getAliasesString() + " &b" + pluginCommand.getArgumentsString());
                }
            }

            Messages.sendMessage(player, "&6/blackjack help &b[command]");
            Messages.sendMessage(player, "&6/blackjack&b can be replaced with &6/bj&b.");
            Messages.sendMessage(player, "&6UltimateCards &bv" + UltimateCards.getVersion() + " &fby &6Norbo11");
        } else {
            for (ArrayList<PluginCommand> commandGroup : PluginExecutor.commands) {
                for (PluginCommand pluginCommand : commandGroup) {
                    if (pluginCommand.getAlises().contains(command)) {
                        Messages.sendMessage(player, pluginCommand.getUsage());
                        return;
                    }
                }
            }
        }
    }

    public static void holeCardsNotMatching(Player player) {
        Messages.sendMessage(player, "&cYour hole cards (starting two cards) need be of matching rank if you want to split!");
    }

    public static void invalidNumber(Player player, String value) {
        Messages.sendMessage(player, "&6" + value + "&c is not a valid number!");
    }

    public static void invalidPercentage(Player player) {
        Messages.sendMessage(player, "&cPlease specify a value between 0 and 1. Example: &60.05 = 5%");
    }

    public static void needToSpecifyHand(Player player) {
        Messages.sendMessage(player, "&cYou need to specify a hand!");
    }

    public static void noPermission(Player player) {
        Messages.sendMessage(player, "&cYou don't have permission to do this.");
    }

    public static void noPotsOnTable(Player player) {
        Messages.sendMessage(player, "&cThere is no pot on your table!");
    }

    public static void noPotToPay(Player player) {
        Messages.sendMessage(player, "&cYou have already paid all pots on your table!");
    }

    public static void noTablesAvailable(Player player) {
        Messages.sendMessage(player, "&cNo tables available.");
    }

    public static void notEnoughMoney(Player player, double amount, double money) {
        Messages.sendMessage(player, "&cYou do not have " + "&6" + Formatter.formatMoney(amount) + "&c to do this! You need " + "&6" + Formatter.formatMoney(amount - money) + "&c more - or use &6/poker allin.");
    }

    public static void notEnoughPlayers(Player player) {
        Messages.sendMessage(player, "&cYou need at least " + "&62" + "&c people sat on your table to start it!");
    }

    public static void notGameType(Player player) {
        Messages.sendMessage(player, "&cThat is not a valid game type!");
    }

    public static void notHumanPlayer(CommandSender sender) {
        sender.sendMessage("&cSorry, this command is only for players!");
    }

    public static void notPlayerID(Player player, int IDtoKick) {
        Messages.sendMessage(player, "&cThere is no player with the ID of " + "&6" + IDtoKick + "&c sitting on your table.");
    }

    public static void notPotID(Player player, String potID) {
        Messages.sendMessage(player, "&cThere is no pot on your table with the ID of " + "&6" + potID + "&c!");

    }

    public static void notSittingAtTable(Player player) {
        Messages.sendMessage(player, "&cYou are not currently sitting at any table! Sit with " + PluginExecutor.tableSit.getCommandString() + " [id] [buy-in]&c.");
    }

    public static void notTable(Player player, String id) {
        Messages.sendMessage(player, "&cThere is no table with the ID of " + "&6" + id + "&c.");
    }

    public static void notWithinBuyinBounds(Player player, double buyin, double minbuy, double maxbuy) {
        Messages.sendMessage(player, "&cBuy-in amount " + "&6" + Formatter.formatMoney(buyin) + "&c is not within the table buy-in boundries. Min: " + Formatter.formatMoney(minbuy) + ". Max: " + Formatter.formatMoney(maxbuy) + ".");
    }

    public static void notYourTurn(Player player) {
        Messages.sendMessage(player, "&cIt's not your turn to act!");
    }

    public static void playerAlreadyBanned(Player player, String toBan) {
        Messages.sendMessage(player, "&6" + toBan + "&c is already banned from the table!");
    }

    public static void playerAlreadyDoubled(Player player) {
        Messages.sendMessage(player, "&cYou have already doubled down!");
    }

    public static void playerAlreadyHit(Player player) {
        Messages.sendMessage(player, "&cYou have already hit!");
    }

    public static void playerAlreadyPlaying(Player player) {
        Messages.sendMessage(player, "&cYou are already playing this hand!");
    }

    public static void playerHasNoHand(Player player) {
        Messages.sendMessage(player, "&cYou do not have any cards!");
    }

    public static void playerIsAllIn(Player player) {
        Messages.sendMessage(player, "&cYou are all in, you cannot take any action!");
    }

    public static void playerIsBanned(Player player) {
        Messages.sendMessage(player, "&cYou have been banned from this table!");
    }

    public static void playerIsBlackjackDealer(Player player) {
        Messages.sendMessage(player, "&cYou are the dealer of this blackjack table, you cannot play!");
    }

    public static void playerIsBust(Player player) {
        Messages.sendMessage(player, "&cThat hand is already bust!");
    }

    public static void playerIsEliminated(Player player) {
        Messages.sendMessage(player, "&cYou have been eliminated from this table!");
    }

    public static void playerIsFolded(Player player) {
        Messages.sendMessage(player, "&cYou cannot do that, you have folded your hand!");
    }

    public static void playerIsOwnerGeneral(Player player) {
        Messages.sendMessage(player, "&cYou already own a table!");
    }

    public static void playerIsSplit(Player player) {
        Messages.sendMessage(player, "&cYou have already split!");
    }

    public static void playerIsStayed(Player player) {
        Messages.sendMessage(player, "&cYou have already stayed!");
    }

    public static void playerNotBanned(Player player, String toUnBan) {
        Messages.sendMessage(player, "&6" + toUnBan + "&c is not banned from this table!");
    }

    public static void playerNotFound(Player player, String toInvite) {
        Messages.sendMessage(player, "&cPlayer not found: " + "&6" + toInvite);
    }

    public static void playerNotNearEnough(Player player) {
        Messages.sendMessage(player, "&cYou are not near enough to the cards table to join it!");
    }

    public static void playerNotOwner(Player player) {
        Messages.sendMessage(player, "&cYou are not the owner of this table!");
    }

    public static void playerSittingAtTable(Player player) {
        Messages.sendMessage(player, "&cYou are already sat at a table!");
    }

    public static void pokerPlayerNotFound(Player player, String toCheck) {
        Messages.sendMessage(player, "&cThe player " + "&6" + toCheck + "&c is not currently playing poker!");
    }

    public static void potEmpty(Player player) {
        Messages.sendMessage(player, "&cThat pot has already been paid!");
    }

    public static void potExists(Player player, double pot) {
        Messages.sendMessage(player, "&cThere is currently a pot of " + "&6" + Formatter.formatMoney(pot) + "&c!");
    }

    public static void tableAlreadyClosed(Player player) {
        Messages.sendMessage(player, "&cYour table is already closed!");
    }

    public static void tableAlreadyOpen(Player player) {
        Messages.sendMessage(player, "&cYour table is already open!");
    }

    public static void tableAtShowdown(Player player) {
        Messages.sendMessage(player, "&cThe table is currently at showdown! Reveal your hand with " + PluginExecutor.pokerReveal.getCommandString() + ".");
    }

    public static void tableDoesntAllowRebuys(Player player) {
        Messages.sendMessage(player, "&cThis table doesn't allow rebuys!");
    }

    public static void tableHasMultiplePots(Player player) {
        Messages.sendMessage(player, "&cThere is more than one pot on your table, please specify the pot ID!");
    }

    public static void tableHasPots(Player player) {
        Messages.sendMessage(player, "&cYou need to pay all pots before you delete the table!");
    }

    public static void tableInProgress(Player player) {
        Messages.sendMessage(player, "&cTable is currently in progress! Use this command only during showdowns.");
    }

    public static void tableNameAlreadyExists(Player player) {
        Messages.sendMessage(player, "&cA table with that name already exists!");
    }

    public static void tableNotInProgress(Player player) {
        Messages.sendMessage(player, "&cThe table is currently not in progress!");
    }

    public static void tableNotOpen(Player player, String id) {
        Messages.sendMessage(player, "&cTable ID " + "&6" + id + "&c is not open!");
    }

    public static void tooSmallBet(Player player, double minBet) {
        Messages.sendMessage(player, "&cThat bet is too small! You need to bet at least " + Formatter.formatMoney(minBet));
    }

}
