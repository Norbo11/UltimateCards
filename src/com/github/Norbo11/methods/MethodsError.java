package com.github.Norbo11.methods;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.classes.PokerPlayer;

public class MethodsError
{

    public MethodsError(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;

    public void alreadyAtTable(Player player, String tablename, int id)
    {
        player.sendMessage(p.pluginTag + p.red + "You are already sat at table named " + p.gold + tablename + p.red + ", ID #" + p.gold + id + p.red + "!");
    }

    public void alreadyOwnTable(Player player)
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
        if (command.equals("table"))
        {
            player.sendMessage(p.pluginTag + "UltimatePoker v" + p.gold + p.version + p.white + " by " + p.gold + "Norbo11" + p.white + " Help:");
            player.sendMessage(p.pluginTag + "'|' represents 'or' (an alias).");
            player.sendMessage(p.pluginTag + p.gold + "/table list");
            player.sendMessage(p.pluginTag + p.gold + "/table create|new [name]");
            player.sendMessage(p.pluginTag + p.gold + "/table delete|del");
            player.sendMessage(p.pluginTag + p.gold + "/table open|o");
            player.sendMessage(p.pluginTag + p.gold + "/table close|c");
            player.sendMessage(p.pluginTag + p.gold + "/table set [setting] [value]");
            player.sendMessage(p.pluginTag + p.gold + "/table listsettings");
            player.sendMessage(p.pluginTag + p.gold + "/table sit [ID] [Buy-in]");
            player.sendMessage(p.pluginTag + p.gold + "/table getup");
            player.sendMessage(p.pluginTag + p.gold + "/table tp [ID]");
            player.sendMessage(p.pluginTag + p.gold + "/table start");
            player.sendMessage(p.pluginTag + p.gold + "/table details [ID]");
            player.sendMessage(p.pluginTag + p.gold + "/table board [ID]");
            player.sendMessage(p.pluginTag + p.gold + "/table pay [player ID]");
        }
        if (command.equals("hand"))
        {
            player.sendMessage(p.pluginTag + p.gold + "/hand");
            player.sendMessage(p.pluginTag + p.gold + "/hand bet [amount]");
            player.sendMessage(p.pluginTag + p.gold + "/hand fold");
            player.sendMessage(p.pluginTag + p.gold + "/hand call");
            player.sendMessage(p.pluginTag + p.gold + "/hand check");
            player.sendMessage(p.pluginTag + p.gold + "/hand rebuy [amount]");
        }
    }

    public void noPermission(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You don't have permission to do this.");
    }

    public void notANumber(Player player, String value)
    {
        player.sendMessage(p.pluginTag + p.gold + value + p.red + " is not a valid number!");
    }

    public void notAPokerPlayerID(Player player, String id)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no player with the ID of " + p.gold + id + p.red + " sitting on your table.");
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

    public void notPlayer(CommandSender sender)
    {
        sender.sendMessage(p.pluginTag + p.red + "Sorry, this command is only for players!");
    }

    public void notPokerPlayer(Player player)
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

    public void noPotToPay(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have already paid all pots on your table!");
    }

    public void potExists(Player player, double pot)
    {
        player.sendMessage(p.pluginTag + p.red + "There is currently a pot of " + p.gold + p.methodsMisc.formatMoney(pot) + p.red + "!");

    }

    public void tableInEliminationMode(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "The table is currently in elimination mode, you cannot re-buy!");
    }
 
    public void tableIsInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Table is currently in progress! Use this command only during showdowns.");
    }

    public void tableNotInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "The table is currently not in progress!");
    }

    public void usage(Player player, String command)
    {
        //Table
        if (command.equals("help")) player.sendMessage(p.pluginTag + p.red + "Usage: /table help or /table help [cmd]." + p.gold + " - Displays list of commands or gives command specific help.");
        if (command.equals("create")) player.sendMessage(p.pluginTag + p.red + "Usage: /table create|new [name]" + p.gold + " - Creates a table.");
        if (command.equals("list")) player.sendMessage(p.pluginTag + p.red + "Usage: /table list|/tables" + p.gold + " - Lists created tables.");
        if (command.equals("open")) player.sendMessage(p.pluginTag + p.red + "Usage: /table open|o " + p.gold + " - Opens your table.");
        if (command.equals("close")) player.sendMessage(p.pluginTag + p.red + "Usage: /table close|c" + p.gold + " - Closes your table.");
        if (command.equals("delete")) player.sendMessage(p.pluginTag + p.red + "Usage: /table delete" + p.gold + " - Deletes your table.");
        if (command.equals("set")) player.sendMessage(p.pluginTag + p.red + "Usage: /table set [setting] [value]" + p.gold + " - Sets the [setting] to the [value]. List available settings with /listsettings.");
        if (command.equals("sit")) player.sendMessage(p.pluginTag + p.red + "Usage: /table sit [ID] [buy-in]" + p.gold + " - Sits at a table with the specified buy-in amount.");
        if (command.equals("getup")) player.sendMessage(p.pluginTag + p.red + "Usage: /table getup|leave" + p.gold + " - Leaves the table and gives you back your stack.");
        if (command.equals("listsettings")) player.sendMessage(p.pluginTag + p.red + "Usage: /table listsettings" + p.gold + " - Lists all available settings for /table set.");
        if (command.equals("teleport")) player.sendMessage(p.pluginTag + p.red + "Usage: /table tp [ID]" + p.gold + " - Teleports you to a table.");
        if (command.equals("start")) player.sendMessage(p.pluginTag + p.red + "Usage: /table start" + p.gold + " - Starts the game at your table.");
        if (command.equals("details")) player.sendMessage(p.pluginTag + p.red + "Usage: /table details [ID]" + p.gold + " - Lists settings, players, and other info about a table.");
        if (command.equals("board")) player.sendMessage(p.pluginTag + p.red + "Usage: /table board [ID]" + p.gold + " - Shows the community cards at the specified table.");
        if (command.equals("kick")) player.sendMessage(p.pluginTag + p.red + "Usage: /table kick [Player ID]" + p.gold + " - Kicks the player from the table.");
        if (command.equals("ban")) player.sendMessage(p.pluginTag + p.red + "Usage: /table ban [Player name]" + p.gold + " - Bans the player from the table.");
        if (command.equals("unban")) player.sendMessage(p.pluginTag + p.red + "Usage: /table unban [Player name]" + p.gold + " - Unbans the player from the table.");

        //Hand
        if (command.equals("call")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand call" + p.gold + " - Calls the last bet.");
        if (command.equals("fold")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand fold" + p.gold + " - Folds your hand.");
        if (command.equals("bet")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand bet [amount]" + p.gold + " - Bets the specified [amount].");
        if (command.equals("check")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand check" + p.gold + " - Checks your turn.");
        if (command.equals("money")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand money" + p.gold + " - Shows you your remaining stack at the table.");
        if (command.equals("rebuy")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand rebuy [amount]" + p.gold + " - Adds more money to your stack.");
        if (command.equals("allin")) player.sendMessage(p.pluginTag + p.red + "Usage: /hand allin" + p.gold + " - Bets all of your stack and puts you in all in mode.");
    }

    public void enoughMoneyToCall(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have enough money to call/raise the current bet! If you wish to bet all of your stack, simply " + p.gold + "/hand bet [your stack]" + p.red + " (check with " + p.gold + "/hand money" + p.red + ")");
    }

    public void playerIsAllIn(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You are all in, you cannot take any action!");
    }

    public void playerHasFolded(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot do that, you have folded your hand!");
    }

    public void playerBanned(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have been banned from this table!");
    }

    public void tableIsStopped(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "That table has been stopped!");
    }

   
    public void moreThanOnePot(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "There is more than one pot on your table, please specify the pot ID!");
    }

    public void notAPotID(Player player, String potID)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no pot on your table with the ID of " + p.gold + potID + p.red + "!");

    }

    public void noPotsOnTable(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no pot on your table!");
    }

    public void nobodyCanCall(Player player, String amount)
    {
        player.sendMessage(p.pluginTag + p.red + "Nobody on the table can call a raise of " + p.gold + p.methodsMisc.formatMoney(Double.parseDouble(amount)) + p.red + "! Bet less, or simpy call!");
    }
}
