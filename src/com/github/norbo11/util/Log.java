package com.github.norbo11.util;

import java.io.FileWriter;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.github.norbo11.UltimateCards;
import com.github.norbo11.util.config.PluginConfig;

public class Log {
    public static void addToLog(String message) {
        // Only send to the log if it is enabled in the config
        if (PluginConfig.isEnableLog()) {
            try {
                // Attempt to write the supplied message as a new line at the
                // end of the log
                FileWriter writer = new FileWriter(UltimateCards.getFileLog(), true);
                writer.write(DateMethods.getDate() + " " + message + "\r\n");
                writer.flush();
                writer.close();
            } catch (Exception e) {
                UltimateCards.getLog().info("Something went wrong when trying to write to the log file! " + e.getMessage());
            }
        }
    }

    // Logs the supplied command, it's sender and all of it's arguments
    public static void logCommand(CommandSender sender, Command command, String args[]) {
        String arguments = "";
        for (String argument : args) {
            // Goes through the array of given arguments, appends them at the
            // back of the arguments variable
            arguments = arguments + " " + argument;
        }
        addToLog(DateMethods.getDate() + " " + sender.getName() + ": /" + command.getName() + arguments);
    }

}
