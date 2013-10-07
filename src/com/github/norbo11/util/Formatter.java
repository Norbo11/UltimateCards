package com.github.norbo11.util;

import java.text.DecimalFormat;

import org.bukkit.Location;

import com.github.norbo11.UltimateCards;

public class Formatter {

    // Converts the given double into a percentage string
    public static String convertToPercentage(double value) {
        return Double.toString(NumberMethods.roundDouble(value * 100, 1)) + '%';
    }

    public static String formatLocation(Location location) {
        return "&6X: &f" + Math.round(location.getX()) + "&6 Z: &f" + Math.round(location.getZ()) + "&6 Y: &f" + Math.round(location.getY()) + "&6 World: &f" + location.getWorld().getName();
    }

    // Converts things like '31982193' into '31,982,193.00 Dollars'
    public static String formatMoney(double amount) {
        return "&6" + UltimateCards.getEconomy().format(amount);
    }

    public static String formatMoneyWithoutColor(double amount) {
        return UltimateCards.getEconomy().format(amount);
    }

    public static String formatMoneyWithoutCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("#,###,###");
        return df.format(amount);
    }
}