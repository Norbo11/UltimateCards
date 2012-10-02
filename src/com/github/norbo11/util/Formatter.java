package com.github.norbo11.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;

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

    // Replaces [] () | by colouring them. [] = dark gray, () = dark aqua, | =
    // blue.
    public static String[] replaceSpecialCharacters(ArrayList<String> message)
    {
        String[] returnValue = new String[message.size()]; // Create a return
                                                           // value which will
                                                           // eventually be
                                                           // returned, with the
                                                           // size of the amount
                                                           // of messages to
                                                           // parse
        Pattern squareBrackets = Pattern.compile("(.*?)(\\[.*?\\])(.*?)"); // Compile
                                                                           // the
                                                                           // regular
                                                                           // expression
                                                                           // for
                                                                           // square
                                                                           // brackets.
                                                                           // group
                                                                           // 1
                                                                           // =
                                                                           // first
                                                                           // part
                                                                           // of
                                                                           // the
                                                                           // message.
                                                                           // 2
                                                                           // =
                                                                           // coloured
                                                                           // part
                                                                           // (part
                                                                           // inside
                                                                           // square
                                                                           // brackets,
                                                                           // including
                                                                           // them).
                                                                           // 3
                                                                           // =
                                                                           // the
                                                                           // rest
                                                                           // of
                                                                           // the
                                                                           // message.
        Pattern normalBrackets = Pattern.compile("(.*?)(\\(.*?\\))(.*?)"); // Same
                                                                           // as
                                                                           // above,
                                                                           // but
                                                                           // for
                                                                           // brackets.
                                                                           // \\
                                                                           // is
                                                                           // used
                                                                           // before
                                                                           // []()
                                                                           // because
                                                                           // those
                                                                           // characters
                                                                           // are
                                                                           // part
                                                                           // of
                                                                           // regex
                                                                           // syntax
                                                                           // and
                                                                           // need
                                                                           // to
                                                                           // be
                                                                           // escaped.
        Matcher squareBracketMatcher; // These are the matchers that we will
                                      // //The question mark makes it so that
                                      // the expression matches as many times as
                                      // it can (doesnt really make sense since
                                      // that makes it relucant, but whatever)
        Matcher normalBracketMatcher; // use to actually match the strings
        int i = 0; // Used to loop through the return value which returns the
                   // array
        for (String temp : message) // Go through the list of supplied messages
        {
            // Replace square and normal brackets if they are matched (colour
            // them)
            squareBracketMatcher = squareBrackets.matcher(temp);
            if (squareBracketMatcher.find())
            {
                temp = squareBracketMatcher.replaceAll("$1" + ChatColor.DARK_GRAY + "$2" + "&6$3");
            }
            normalBracketMatcher = normalBrackets.matcher(temp);
            if (normalBracketMatcher.find())
            {
                temp = normalBracketMatcher.replaceAll("$1" + ChatColor.DARK_AQUA + "$2" + "&6$3");
            }

            // Because our plugin tag contains square brackets we need to
            // replace it back if it is replaced
            temp = temp.replace(ChatColor.stripColor(UltimateCards.getPluginTag()).replace(" ", ""), UltimateCards.getPluginTag().replace(" ", "")); // We
                                                                                                                                                     // match
                                                                                                                                                     // against
                                                                                                                                                     // the
                                                                                                                                                     // raw
                                                                                                                                                     // plugin
                                                                                                                                                     // tag
                                                                                                                                                     // with
                                                                                                                                                     // no
                                                                                                                                                     // colour
                                                                                                                                                     // and
                                                                                                                                                     // replace
                                                                                                                                                     // it
                                                                                                                                                     // with
                                                                                                                                                     // the
                                                                                                                                                     // colored
                                                                                                                                                     // one,
                                                                                                                                                     // removing
                                                                                                                                                     // an
                                                                                                                                                     // extra
                                                                                                                                                     // space
                                                                                                                                                     // that
                                                                                                                                                     // is
                                                                                                                                                     // created
            temp = temp.replace("|", ChatColor.BLUE + " | &6"); // We replace
                                                                // all pipes by
                                                                // adding spaces
                                                                // around them
                                                                // and making
                                                                // them dark
                                                                // aqua
            returnValue[i] = temp; // We add to the return value
            i++; // Increase our iterator
        }
        return returnValue; // Return the value once we parse through everything
    }
}
