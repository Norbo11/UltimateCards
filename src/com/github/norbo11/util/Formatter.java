package com.github.norbo11.util;

import java.text.DecimalFormat;

import com.github.norbo11.UltimateCards;

public class Formatter
{

    // Converts the given double into a percentage string
    public static String convertToPercentage(double value)
    {
        return Double.toString(NumberMethods.roundDouble(value * 100, 1)) + '%';
    }

    // Converts things like '31982193' into '31,982,193.00 Dollars'
    public static String formatMoney(double amount)
    {
        return "&6" + UltimateCards.getEconomy().format(amount);
    }

    public static String formatMoneyWithoutCurrency(double amount)
    {
        DecimalFormat df = new DecimalFormat("#,###,###");
        return df.format(amount);
    }
}