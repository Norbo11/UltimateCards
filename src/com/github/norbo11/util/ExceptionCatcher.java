package com.github.norbo11.util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExceptionCatcher {
    public static void catchException(Exception e) {
        Log.addToLog(DateMethods.getDate() + " [ERROR] An error has occured: " + e.getMessage());
        e.printStackTrace();
    }

    public static void catchException(Exception e, Command command, CommandSender sender, String args[]) {
        Messages.sendMessage((Player) sender, "&cAn error has occured: " + e.getMessage());
        Log.addToLog(DateMethods.getDate() + " [ERROR] An error has occured: " + e.getMessage());
        e.printStackTrace();
    }

    public static void catchException(Exception e, Player player, String message) {
        Messages.sendMessage(player, "&cAn error has occured: " + e.getMessage());
        Log.addToLog(message);
        e.printStackTrace();
    }
}
