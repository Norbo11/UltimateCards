/* ==================================================================================================
 * UltimatePoker v1.0 - By Norbo11
 * Copyright (C) 2012
 * You may NOT modify this file in any way, or use any of it's code for personal projects. 
 * You may, however, read and learn from it if you like. All rights blah blah and shit. 
 * Basically just respect my hard work, please :)
 * 
 * File notes: MethodsMisc.java
 * -Contains methods that provide various functions that dont fit in any other category.
 * -This includes things like adding to the log, sending global messages, getting online players etc.
 * ===================================================================================================
 */

package com.github.norbo11.methods;

import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.norbo11.UltimatePoker;
import com.github.norbo11.classes.PokerPlayer;
import com.github.norbo11.classes.Table;

public class MethodsMisc
{
    public MethodsMisc(UltimatePoker p)
    {
        this.p = p;
    }

    UltimatePoker p;
    NumberFormat nf = NumberFormat.getCurrencyInstance();

    int task;

    public void addToLog(String message)
    {
        //Only send to the log if it is enabled in the config
        if (p.getConfig().getBoolean("log.enableLog") == true)
        {
            try
            {
                //Attempt to write the supplied message as a new line at the end of the log
                FileWriter writer = new FileWriter(p.fileLog, true);
                writer.write(message + "\r\n");
                writer.flush();
                writer.close();
            } catch (Exception e)
            {
                p.log.info("Something went wrong when trying to write to the log file! " + e.getMessage());
            }
        }
    }

    //Converts the given double into a percentage string
    public String convertToPercentage(double value)
    {
        return Double.toString(value * 100) + '%';
    }

    //Converts things like '31982193' into '31,982,193.00 Dollars'
    public String formatMoney(double amount)
    {
        return p.economy.format(amount);
    }

    //Returns a list of all online players on the server
    public List<Player> getOnlinePlayers()
    {
        List<Player> returnValue = new ArrayList<Player>();
        Player[] players = p.getServer().getOnlinePlayers();
        for (Player player : players)
            returnValue.add(player);
        return returnValue;
    }

    //Logs the supplied command, it's sender and all of it's arguments
    public void logCommand(CommandSender sender, Command command, String args[])
    {
        String arguments = "";
        for (String argument : args) //Goes through the array of given arguments, appends them at the back of the arguments variable
            arguments = arguments + " " + argument;
        addToLog(p.getDate() + " " + sender.getName() + ": /" + command.getName() + arguments); //Adds the timestamped result to the log
    }

    //Replaces [] () | by colouring them. [] = dark gray, () = dark aqua, | = blue.
    public String[] replaceSpecialCharacters(List<String> message)
    {
        String[] returnValue = new String[message.size()];                      //Create a return value which will eventually be returned, with the size of the amount of messages to parse
        Pattern squareBrackets = Pattern.compile("(.*?)(\\[.*?\\])(.*?)");      //Compile the regular expression for square brackets. group 1 = first part of the message. 2 = coloured part (part inside square brackets, including them). 3 = the rest of the message.
        Pattern normalBrackets = Pattern.compile("(.*?)(\\(.*?\\))(.*?)");      //Same as above, but for brackets. \\ is used before []() because those characters are part of regex syntax and need to be escaped.
        Matcher squareBracketMatcher; //These are the matchers that we will     //The question mark makes it so that the expression matches as many times as it can (doesnt really make sense since that makes it relucant, but whatever)
        Matcher normalBracketMatcher; //use to actually match the strings
        int i = 0; //Used to loop through the return value which returns the array
        for (String temp : message) //Go through the list of supplied messages
        {
            //Replace square and normal brackets if they are matched (colour them)
            squareBracketMatcher = squareBrackets.matcher(temp);
            if (squareBracketMatcher.find()) temp = squareBracketMatcher.replaceAll("$1" + ChatColor.DARK_GRAY + "$2" + p.gold + "$3");
            normalBracketMatcher = normalBrackets.matcher(temp);
            if (normalBracketMatcher.find()) temp = normalBracketMatcher.replaceAll("$1" + ChatColor.DARK_AQUA + "$2" + p.gold + "$3");
            
            //Because our plugin tag contains square brackets we need to replace it back if it is replaced
            temp = temp.replace(ChatColor.stripColor(p.pluginTag).replace(" ", ""), p.pluginTag.replace(" ", "")); //We match against the raw plugin tag with no colour and replace it with the colored one, removing an extra space that is created
            temp = temp.replace("|", ChatColor.BLUE + " | " + p.gold);                            //We replace all pipes by adding spaces around them and making them dark aqua
            returnValue[i] = temp;                                                                //We add to the return value
            i++;                                                                                  //Increase our iterator
        }
        return returnValue; //Return the value once we parse through everything
    }

    //Returns money to EVERYONE that is currently at a table. Should be called when a reload happens
    public void returnMoney()
    {
        for (Table table : p.tables)
        {
            //Go through every online player, teleport them to their starting location, display them messages to them, give them their money back and log the event.
            for (PokerPlayer player : table.players)
            {
                if (player.online)
                {
                    player.player.teleport(player.startLocation);
                    player.sendMessage(p.pluginTag + p.red + "Something (an error, plugin reload, etc) has caused all poker tables to be deleted!");
                    player.sendMessage(p.pluginTag + "You have been paid your remaining stack of " + p.gold + p.methodsMisc.formatMoney(player.money + player.totalBet));
                }
                p.economy.depositPlayer(player.name, player.money + player.totalBet);
                p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + player.totalBet + " to " + player.name);
            }
        }
        p.tables.clear();
    }

    //Returns money to all the players in the specified table. Should be called when a table is deleted, etc.
    public void returnMoney(Table table)
    {
        //Go through every online player, teleport them to their starting location, display them messages to them, give them their money back and log the event.
        for (PokerPlayer player : table.players)
        {
            if (player.online)
            {
                player.player.teleport(player.startLocation);
                player.sendMessage(p.pluginTag + "You have been paid your remaining stack of " + p.gold + p.methodsMisc.formatMoney(player.money + player.totalBet));
                p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + Double.toString(player.money + player.totalBet) + " to " + player.name);
            }
            p.economy.depositPlayer(player.name, player.money + player.totalBet);
            p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + player.totalBet + " to " + player.name);
        }
    }

    public void returnMoney(PokerPlayer player)
    {
        if (player.online)
        {
            player.player.teleport(player.startLocation);
            player.sendMessage(p.pluginTag + "You have been paid your remaining stack of " + p.gold + p.methodsMisc.formatMoney(player.money + player.totalBet));
            p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + Double.toString(player.money + player.totalBet) + " to " + player.name);
        }
        p.economy.depositPlayer(player.name, player.money + player.totalBet);
        p.methodsMisc.addToLog(p.getDate() + " [ECONOMY] Depositing " + player.totalBet + " to " + player.name);
    }

    //Sends a list of messages to all players that are close to the specified location. The maximum distance is specified in the config
    public void sendToAllWithinRange(Location location, List<String> message)
    {
        List<Player> players = p.methodsMisc.getOnlinePlayers();
        for (Player player : players)
        {
            if (player.getWorld() == location.getWorld())
            {
                if (player.getLocation().distance(location) <= p.getConfig().getInt("table.chatRange"))
                {
                    for (String temp : message)
                        player.sendMessage(temp);
                }
            }
        }
    }

    //Sends a single message to all players that are close to the specified location. The maximum distance is specified in the config
    public void sendToAllWithinRange(Location location, String message)
    {
        List<Player> players = p.methodsMisc.getOnlinePlayers();
        for (Player player : players)
        {
            if (player.getWorld() == location.getWorld())
            {
                if (player.getLocation().distance(location) <= p.getConfig().getInt("table.chatRange"))
                    player.sendMessage(message);
            }
        }
    }

    //Sends an array of messages to all players that are close to the specified location. The maximum distance is specified in the confi
    public void sendToAllWithinRange(Location location, String[] message)
    {
        List<Player> players = p.methodsMisc.getOnlinePlayers();
        for (Player player : players)
        {
            if (player.getWorld() == location.getWorld())
            {
                if (player.getLocation().distance(location) <= p.getConfig().getInt("table.chatRange"))
                    player.sendMessage(message);
            }
        }
    }
    
    public double roundDouble(double d) 
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
    return Double.valueOf(twoDForm.format(d));
    }

    public void removePlayer(PokerPlayer player)
    {
        for (Table table : p.tables)
        {
            for (PokerPlayer temp : table.players)
            {
                if (temp == player) 
                {
                    table.players.remove(temp);
                    table.shiftIDs();
                    return;
                }
            }
        }
    }
    
    public void catchException(Exception e)
    {
        p.methodsMisc.addToLog(p.getDate() + " [ERROR] An error has occured: " + e.getMessage());
        e.printStackTrace();
    }
    
    public void catchException(Exception e, Player sender, String message)
    {
        sender.sendMessage(p.pluginTag + p.red + "An error has occured: " + e.getMessage());
        p.methodsMisc.addToLog(message);
        e.printStackTrace();
    }
    
    public void catchException(Exception e, Command command, CommandSender sender, String args[])
    {
        p.methodsMisc.logCommand(sender, command, args);
        sender.sendMessage(p.pluginTag + p.red + "An error has occured: " + e.getMessage());
        p.methodsMisc.addToLog(p.getDate() + " [ERROR] An error has occured: " + e.getMessage());
        e.printStackTrace();
    }

}
