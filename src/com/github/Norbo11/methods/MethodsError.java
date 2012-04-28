package com.github.Norbo11.methods;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.Norbo11.UltimatePoker;
import com.github.Norbo11.classes.PokerPlayer;

public class MethodsError
{

    UltimatePoker p;

    public MethodsError(UltimatePoker p)
    {
        this.p = p;
    }

    public void alreadyAtTable(Player player, String tablename, int id)
    {
        player.sendMessage(p.pluginTag + p.red + "You are already sat at table '" + p.gold + tablename + p.red + "', ID #" + p.gold + id + p.red + "!");
    }

    public void alreadyOwnTable(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You already own a table!");
    }

    public void cantCheck(PokerPlayer player)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot check at this time! There is a bet of " + p.gold + p.methodsMisc.formatMoney(player.table.currentBet) + p.red + " to call!");
    }
    
    public void cantCall(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You have already contributed the required amount to this pot!");
    }
    
    public void cantRaise(Player player, double minRaise)
    {
        player.sendMessage(p.pluginTag + p.red + "You cannot raise that amount. Min Raise: " + p.gold + p.methodsMisc.formatMoney(minRaise) + p.red + " (on top of the current bet)");
    }

    public void displayHelp(Player player, String command)
    {
        if (command.equals("table"))
        {
            player.sendMessage(p.pluginTag + "UltimatePoker v" + p.gold + p.version + p.white + " by " + p.gold + "Norbo11" + p.white + " Help:");
            player.sendMessage(p.pluginTag + "'|' represents 'or' (and alias).");
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
        }
    }

    public void noPermission(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "You don't have permission to do this.");
    }

    public void noPlayersAtTable(Player player, String name, int id)
    {
        player.sendMessage(p.pluginTag + p.red + "There are no players at table " + p.gold + name + p.white + "', ID #" + p.gold + Integer.toString(id) + p.red + ".");
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
        if (p.methodsCheck.isInteger(amount))
        {
            player.sendMessage(p.pluginTag + p.red + "You do not have " + p.gold + amount + p.red + " " + p.economy.currencyNamePlural() + "! You need "
            + (Integer.parseInt(amount) - money) + " " + p.economy.currencyNamePlural() + " more.");
            return;
        }
        if (p.methodsCheck.isDouble(amount))
        {
            player.sendMessage(p.pluginTag + p.red + "You do not have " + p.gold + amount + p.red + " " + p.economy.currencyNamePlural() + "! You need "
            + (Double.parseDouble(amount) - money) + " " + p.economy.currencyNamePlural() + " more. Check " + p.gold + "/hand money");
            return;
        }
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
        player.sendMessage(p.pluginTag + p.red + "Buy-in amount " + p.gold + p.methodsMisc.formatMoney(buyin) + p.red + " is not within the table buy-in boundries. Min: "
        + p.methodsMisc.formatMoney(minbuy) + ". Max: " + p.methodsMisc.formatMoney(maxbuy) + ".");
    }

    public void notYourTurn(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "It's not your turn to act!");
    }

    public void potExists(Player player, double pot)
    {
        player.sendMessage(p.pluginTag + p.red + "There is currently a pot of " + p.gold + p.methodsMisc.formatMoney(pot) + p.red + "!");

    }

    public void tableIsInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "Table is currently in progress!");
    }

    public void usage(Player player, String command)
    {
        // table
        if (command.equals("help"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table help or /table help [cmd].");
        }
        if (command.equals("create"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table create|new [name]");
        }
        if (command.equals("list"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table list");
        }
        if (command.equals("open"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table open|o");
        }
        if (command.equals("close"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table close");
        }
        if (command.equals("delete"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table delete");
        }
        if (command.equals("set"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table set [setting] [value]");
        }
        if (command.equals("sit"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table sit [ID] [buy-in]. Check available tables with /table list.");
        }
        if (command.equals("leave"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table leave");
        }
        if (command.equals("getup"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table getup");
        }
        if (command.equals("listsettings"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table listsettings");
        }
        if (command.equals("teleport"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table tp [ID]");
        }
        if (command.equals("start"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table start");
        }
        if (command.equals("details"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table details [ID]");
        }
        if (command.equals("board"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /table board [ID]");
        }

        // hand
        if (command.equals("call"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /hand call");
        }
        if (command.equals("fold"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /hand fold");
        }
        if (command.equals("bet"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /hand bet");
        }
        if (command.equals("check"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /hand check");
        }
        if (command.equals("money"))
        {
            player.sendMessage(p.pluginTag + p.red + "Usage: /hand money");
        }
    }

    
    public void potDoesntExist(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "There is no pot to pay on your table!");
    }

    
    public void tableNotInProgress(Player player)
    {
        player.sendMessage(p.pluginTag + p.red + "The table is currently not in progress!");

    }


}
