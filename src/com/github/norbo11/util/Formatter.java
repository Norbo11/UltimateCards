package com.github.norbo11.util;

import java.text.DecimalFormat;

import org.bukkit.Location;

public class Formatter {

    // Converts the given double into a percentage string
    public static String convertToPercentage(double value) {
        return Double.toString(NumberMethods.roundDouble(value * 100, 1)) + '%';
    }

    public static String formatLocation(Location location) {
        if (location == null || location.getWorld() == null) return "Not set";
        return "&6X: &f" + Math.round(location.getX()) + "&6 Z: &f" + Math.round(location.getZ()) + "&6 Y: &f" + Math.round(location.getY()) + "&6 World: &f" + location.getWorld().getName();
    }

    // Converts things like '31982193' into '31,982,193.00'
    public static String formatMoney(double amount) {
        DecimalFormat df = new DecimalFormat("0.00#");
        return "&6" + df.format(amount);
    }

    public static String formatMoneyWithoutColor(double amount) {
        DecimalFormat df = new DecimalFormat("0.00#");
        return df.format(amount);
    }
}